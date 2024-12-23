from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from datetime import timedelta, timezone, datetime
import os
from flask_jwt_extended import JWTManager, jwt_required, create_access_token, get_jwt, get_jwt_identity


app = Flask(__name__)
bcrypt = Bcrypt(app)


if 'WEBSITE_HOSTNAME' in os.environ:
    database = os.environ['DBNAME']
    host_root = '.postgres.database.azure.com'
    host = os.environ['DBHOST'] + host_root
    user = os.environ['DBUSER']
    password = os.environ['DBPASS']
    db_uri = f'postgresql+psycopg2://{user}:{password}@{host}/{database}'
    debug_flag = False
    app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
else:
    db_path = os.path.join(os.path.dirname(__file__), 'app.db')
    db_uri = 'sqlite:///{}'.format(db_path)
    debug_flag = True
    app.config['SQLALCHEMY_DATABASE_URI'] = db_uri


ACCES_EXPIRES = timedelta(hours = 1)
app.config['JWT_SECRET_KEY'] = "secret key"
app.config['JWT_ACCES_TOKEN_EXPIRES'] = ACCES_EXPIRES


jwt = JWTManager(app)
db = SQLAlchemy()
db.init_app(app)


class Follow(db.Model):
    follower_username = db.Column(db.String(255), db.ForeignKey('user.username'), primary_key=True)
    followed_username = db.Column(db.String(255), db.ForeignKey('user.username'), primary_key=True)

    follower = db.relationship('User', foreign_keys=[follower_username], backref=db.backref('following', lazy='dynamic'))
    followed = db.relationship('User', foreign_keys=[followed_username], backref=db.backref('followers', lazy='dynamic'))


class Likes(db.Model):    
    user_id = db.Column(db.Integer(), db.ForeignKey('user.id'), primary_key=True)
    message_id = db.Column(db.Integer(), db.ForeignKey('message.id'), primary_key=True)
    
    liker = db.relationship('User', foreign_keys=[user_id], backref = db.backref('liking', lazy = 'dynamic'))
    message_liked = db.relationship('Message', foreign_keys=[message_id], backref = db.backref('message_being_liked', lazy = 'dynamic'))


class User(db.Model):
    id = db.Column(db.Integer(), primary_key = True, unique = True)
    username = db.Column(db.String(20), nullable = False, unique = True)
    password = db.Column(db.String(25), nullable = False, unique = False)
    
    messages = db.relationship('Message', backref='user', lazy=True)

    def __init__(self, username, password):
        self.username = username
        self.password = bcrypt.generate_password_hash(password).decode('utf-8')

    def to_dict(self):
        return {'username' : self.username, 'id' : self.id}
    
    def to_dict_username(self):
        return {'username' : self.username}
    
    def to_dict_id(self):
        return {'id' : self.id}
    
    def is_following(self, user):
        return self.following.filter(Follow.followed_username == user.username).count() > 0
   

class Message(db.Model):
    id = db.Column(db.Integer(), primary_key = True, unique = True)
    message = db.Column(db.String(150), nullable = False)
    username = db.Column(db.String(20), nullable = False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))  # Foreign key column

    def to_dict(self):
        return {'message' : self.message, 'id' : self.id, 'username' : self.username}

    def to_dict_message(self):
        return {'message' : self.message}
    
    def to_dict_id(self):
        return {'id' : self.id}
    
    def to_dict_username(self):
        return {'username' : self.username}


class Comment(db.Model):
    id = db.Column(db.Integer(), primary_key = True, unique = True)
    comment = db.Column(db.String(150), nullable = False)
    message_id = db.Column(db.Integer(), db.ForeignKey('message.id'), nullable = False)
    user_id = db.Column(db.Integer(), db.ForeignKey('user.id'), nullable = False)
    username = db.Column(db.String(20), nullable = False)

    def to_dict(self):
        return {'id': self.id, 'comment': self.comment, 'message_id': self.message_id, 'user_id': self.user_id, 'username': self.username}


class TokenBlocklist(db.Model):
    id = db.Column(db.Integer(), primary_key = True, unique = True)
    jti = db.Column(db.String(500), nullable = False, index = True)
    created_at = db.Column(db.DateTime(), nullable = False)
    

@jwt.token_in_blocklist_loader
def check_if_token_is_revoked(jwt_header, jwt_payload : dict):
    jti = jwt_payload['jti']
    token = db.session.query(TokenBlocklist.id).filter_by(jti = jti).scalar()
    return token is not None


@app.route("/", methods = ['POST'])
def welcome_screen():
    return "Welcome to BeerApp!"


@app.route("/register", methods=['POST'])
def register():
    username = request.json['username']
    password = request.json['password']
    existing_user = User.query.filter_by(username=username).first()
    if existing_user is None:
        new_user = User(username=username, password=password)
        db.session.add(new_user)
        db.session.commit()
        return jsonify({'success': True, 'message': 'User ' + username + ' registered'})
    return jsonify({'success': False, 'message': 'Not a valid user'}), 409


@app.route("/login", methods = ['POST'])
def login():
    username = request.json['username']
    password = request.json['password']
    user = User.query.filter_by(username = username).first()
    if user is not None and bcrypt.check_password_hash(user.password, password):
        token = create_access_token(identity = user.username)
        return jsonify(access_token = token)
    return jsonify({'Message' : 'Incorrect details'}), 401


@app.route("/logout", methods = ['POST'])
@jwt_required()
def logout():
    jti = get_jwt()['jti']
    now = datetime.now(timezone.utc)
    db.session.add(TokenBlocklist(jti = jti, created_at = now))
    db.session.commit()
    return jsonify({'Message' : 'Logged out'})


@app.route("/post/message", methods = ['POST'])
@jwt_required()
def post_message():
    message_text = request.json['message']
    if len(message_text) > 150:
        return jsonify({'Message' : 'Message exceeded maximun length'}), 400
    username = get_jwt_identity()
    message = Message(message = message_text, username = username)
    db.session.add(message)
    db.session.commit()
    return jsonify(message.to_dict())


@app.route("/post/comment", methods = ['POST'])
@jwt_required()
def post_comment():
    message_id = request.json['messageId']
    comment_text = request.json['comment']
    user = get_jwt_identity()
    user_id = User.query.filter_by(username = user).first().id
    if len(comment_text) > 150:
        return jsonify({'Message' : 'Message exceeded maximun length'}), 400
    username = get_jwt_identity()
    comment = Comment(comment = comment_text, message_id = message_id, user_id = user_id, username = username)
    db.session.add(comment)
    db.session.commit()
    return jsonify(comment.to_dict())


@app.route("/post/like", methods=['POST'])
@jwt_required()
def post_like():
    message_id = request.json['messageId']
    user = get_jwt_identity()
    user_object = User.query.filter_by(username=user).first()
    message_object = db.session.get(Message, message_id)
    if message_object is None:
        return jsonify({'Message': 'Message not found'}), 404
    like = Likes.query.filter_by(user_id=user_object.id, message_id=message_id).first()
    if like is not None:
        db.session.delete(like)
        db.session.commit()
        return jsonify({'Message': 'Like removed successfully'})
    like = Likes(user_id=user_object.id, message_id=message_id)
    db.session.add(like)
    db.session.commit()
    return jsonify({'Message': 'Like added successfully'})


@app.route("/get/likes/<int:message_id>", methods=['GET'])
@jwt_required()
def get_likes(message_id):
    message = db.session.get(Message, message_id)
    if message is None:
        return jsonify({'Message': 'Message not found'}), 404
    likes_count = Likes.query.filter_by(message_id=message_id).count()
    return jsonify({"likes_count": likes_count})


@app.route("/get/message/<int:message_id>", methods = ['GET'])
@jwt_required()
def get_message(message_id):
    message = db.session.get(Message, message_id)
    if message is None:
        return jsonify({'Message' : 'Message not found'}), 404
    return jsonify(message.to_dict())



@app.route("/get/allmessages", methods = ['GET'])
@jwt_required()
def get_messages():
    messages = Message.query.all()
    messages_dict = [message.to_dict() for message in messages]
    return jsonify(messages_dict)


@app.route("/get/allusers", methods=['GET'])
@jwt_required()
def get_all_users():
    users = User.query.all()
    users_dict = [user.to_dict_username() for user in users]
    return jsonify(users_dict)


@app.route("/get/allcomments/<int:message_id>", methods = ['GET'])
@jwt_required()
def get_comments(message_id):
    comments = Comment.query.filter_by(message_id = message_id).all()
    if not comments:
        return jsonify({'Comments' : 'No comments found for this message'}), 404
    comments_dict = [comment.to_dict() for comment in comments]
    return jsonify(comments_dict)


@app.route("/follow/<username>", methods=['POST'])
@jwt_required()
def follow_user(username):
    current_user = get_jwt_identity()
    follower = User.query.filter_by(username=current_user).first()
    followed = User.query.filter_by(username=username).first()
    if followed is None:
        return jsonify({'Message': 'User not found'}), 404
    if followed in follower.following:
        return jsonify({'Message': 'Already following this user'}), 409
    follow = Follow(follower_username=current_user, followed_username=username)
    db.session.add(follow)
    db.session.commit()
    return jsonify({'Message': f'You are now following {username}'})


@app.route("/unfollow/<username>", methods=['DELETE'])
@jwt_required()
def unfollow_user(username):
    current_user = get_jwt_identity()
    follower = User.query.filter_by(username=current_user).first()
    followed = User.query.filter_by(username=username).first()
    if followed in follower.following:
        return jsonify({'Message': 'Not following this user'}), 409
    follow = Follow.query.filter_by(follower_username=current_user, followed_username=username).first()
    db.session.delete(follow)
    db.session.commit()
    return jsonify({'Message': f'You have unfollowed {username}'})


@app.route("/get/following", methods=['GET'])
@jwt_required()
def get_following():
    """Get the users you are following."""
    current_user = get_jwt_identity()
    user = User.query.filter_by(username=current_user).first()
    following = user.following.all()
    following_usernames = [followed.followed_username for followed in following]
    return jsonify({'Following': following_usernames})


@app.route("/get/followedmessages", methods=['GET'])
@jwt_required()
def get_followed_messages():
    """Get the messages from the users that you follow."""
    current_user = get_jwt_identity()
    user = User.query.filter_by(username=current_user).first()
    if user is None:
        return jsonify({'Message': 'User not found'}), 404
    following_usernames = [followed.followed_username for followed in user.following]
    messages = Message.query.filter(Message.username.in_(following_usernames)).all()
    if not messages:
        return jsonify({'Message': 'No messages found from followed users'}), 404
    messages_dict = [message.to_dict() for message in messages]
    return jsonify(messages_dict)


if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.debug = True
    app.run(host='0.0.0.0', port=5050)