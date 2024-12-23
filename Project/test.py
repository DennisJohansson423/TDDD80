from app import app, db
import tempfile
import pytest
import os


@pytest.fixture
def client():
    db_fd, name = tempfile.mkstemp()
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///'+str(name)
    app.config['TESTING'] = True
    with app.test_client() as client:
        with app.app_context():
            db.create_all()
        yield client
        db.drop_all()
    os.close(db_fd)
    os.unlink(name)


#-- Test cases
def test_welcome_screen(client):
    response = client.post("/")
    assert response.status_code == 200


def test_register(client):
    response = client.post("/register", json = {'username' : 'User1', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/register", json = {'username' : 'User2', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/register", json = {'username' : 'User1', 'password' : '1234'})
    assert response.status_code == 409


def test_login(client):
    response = client.post("/register", json = {'username' : 'User20', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User20', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'Incorrect user', 'password' : '1234'})
    assert response.status_code == 401

    response = client.post("/login", json = {'username' : 'User20', 'password' : 'Incorrect password'})
    assert response.status_code == 401


def test_logout(client):
    response = client.post("/register", json = {'username' : 'User5', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User5', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']
    
    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/logout", headers = headers)
    assert response.status_code == 200

    response = client.post("/post/message", json = {'message' : 'Hello World!'}, headers = headers)
    assert response.status_code == 401


def test_post_message(client):
    response = client.post("/register", json = {'username' : 'User6', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User6', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/post/message", json = {'message' : 'Hello World!'}, headers = headers)
    assert response.status_code == 200

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/post/message", json = {'message' : 'To long message, To long message, To long message, \
    To long message, To long message, To long message, To long message, To long message, To long message,'}, headers = headers)
    assert response.status_code == 400


def test_post_comment(client):
    response = client.post("/register", json = {'username': 'User7', 'password': '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username': 'User7', 'password': '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']
    headers = {'Authorization': f'Bearer {access_token}'}

    response = client.post("/post/message", json = {'message': 'Test message'}, headers = headers)
    assert response.status_code == 200
    message_id = response.json['id']

    response = client.post("/post/comment", json = {'messageId': message_id, 'comment': 'This is a test comment'}, headers = headers)
    assert response.status_code == 200


def test_post_like(client):
    response = client.post("/register", json = {'username': 'User8', 'password': '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username': 'User8', 'password': '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization': f'Bearer {access_token}'}
    response = client.post("/post/message", json = {'message': 'Test message'}, headers = headers)
    assert response.status_code == 200
    message_id = response.json['id']

    response = client.post("/post/like", json = {'messageId': message_id}, headers = headers)
    assert response.status_code == 200


def test_get_message(client):
    response = client.post("/register", json = {'username' : 'User10', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User10', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/post/message", json = {'message' : 'Hello World!'}, headers = headers)
    assert response.status_code == 200
    message_id = response.json['id']

    response = client.get(f"/get/message/{message_id}", headers = sheaders)
    assert response.status_code == 200

    response = client.get(f"/get/message/{123456789}", headers = headers)
    assert response.status_code == 404


def test_get_all_users(client):
    response = client.post("/register", json = {'username' : 'User11', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User11', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.get("/get/allusers", headers = headers)
    assert response.status_code == 200
    assert len(response.json) >= 1


def test_get_comments(client):
    response = client.post("/register", json = {'username' : 'User12', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User12', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/post/message", json = {'message' : 'Hello World!'}, headers = headers)
    assert response.status_code == 200
    message_id = response.json['id']

    response = client.post("/post/comment", json = {'messageId': message_id, 'comment': 'This is a test comment'}, headers = headers)
    assert response.status_code == 200

    response = client.get(f"/get/allcomments/{message_id}", headers = headers)
    assert response.status_code == 200

    response = client.get(f"/get/allcomments/{123456789}", headers = headers)
    assert response.status_code == 404


def test_get_likes(client):
    response = client.post("/register", json = {'username' : 'User13', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User13', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/post/message", json = {'message' : 'Hello World!'}, headers = headers)
    assert response.status_code == 200
    message_id = response.json['id']

    response = client.post("/post/like", json = {'messageId': message_id}, headers = headers)
    assert response.status_code == 200

    response = client.get(f"/get/likes/{message_id}", headers = headers)
    assert response.status_code == 200
    assert response.json['likes_count'] >= 1

    response = client.get(f"/get/likes/{123456789}", headers = headers)
    assert response.status_code == 404


def test_get_following(client):
    response = client.post("/register", json = {'username' : 'User1', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/register", json = {'username' : 'User2', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User1', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/follow/User2", headers = headers)
    assert response.status_code == 200

    response = client.get("/get/following", headers = headers)
    assert response.status_code == 200
    assert 'User2' in response.json['Following']


def test_get_followed_messages(client):
    response = client.post("/register", json = {'username' : 'User1', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/register", json = {'username' : 'User2', 'password' : '1234'})
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User1', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']

    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/follow/User2", headers = headers)
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User2', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']
    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.post("/post/message", json = {'message' : 'Hello from User2!'}, headers = headers)
    assert response.status_code == 200

    response = client.post("/login", json = {'username' : 'User1', 'password' : '1234'})
    assert response.status_code == 200
    access_token = response.json['access_token']
    headers = {'Authorization' : f'Bearer {access_token}'}
    response = client.get("/get/followedmessages", headers = headers)
    assert response.status_code == 200
    assert all(message['username'] == 'User2' for message in response.json)