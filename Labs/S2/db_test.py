import tempfile
import os
import pytest
from database import app, db


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


def test_send_message(client):
    rs = client.post("/messages", json = {'message': 'Hello'})
    assert rs.status_code == 200


def test_get_message(client):
    test_send_message(client)
    rs = client.get("/messages/1")
    assert rs.status_code == 200
    assert rs.get_json()['message'] == 'Hello'


def test_delete_message(client):
    test_send_message(client)
    rs = client.delete("/messages/1")
    assert rs.status_code == 200


def test_mark_as_read(client):
    test_send_message(client)
    test_add_user(client)
    rs = client.post("/messages/1/read/1")
    assert rs.status_code == 200
    
    
def test_unread_messages(client):
    test_send_message(client)
    test_send_message(client)
    test_mark_as_read(client)
    rs = client.get("/messages/unread/1")
    assert rs.status_code == 200


def test_add_user(client):
    rs = client.post("/add", json={'username': 'user2'})
    assert rs.status_code == 200, rs.status_code


def test_get_all(client):
    test_send_message(client)
    test_send_message(client)
    rs = client.get("/messages")
    assert rs.status_code == 200