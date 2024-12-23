from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
import sys
sys.path.append("/Users/dennisjohansson/Skola/Programering/tddd80/my_env/lib/python3.11/site-packages")

app = Flask(__name__)

if not __name__ == "__main__":  
    app.config['SQLALCHEMY_DATABASE_URI'] = "sqlite:///./app.db"
else:
    app.config['SQLALCHEMY_DATABASE_URI'] = "sqlite:///./test.db"

db = SQLAlchemy()
db.init_app(app)


all_readBy = db.Table('readBy', 
                        db.Column('UserId', db.Integer, db.ForeignKey('user.id'), primary_key = True),
                        db.Column('MessageID', db.Integer, db.ForeignKey('message.id'), primary_key = True)
                        )


class Message(db.Model):
    id = db.Column(db.Integer(), primary_key = True)
    message = db.Column(db.String(140), nullable = False)
    
    def to_dict(self):
        return {'message': self.message, 'id': self.id}

    def to_dict_id(self):
        return {'id': self.id}


class User(db.Model):
    id = db.Column(db.Integer(), primary_key = True)
    username = db.Column(db.String(60), nullable = False, unique = True)
    readBy = db.relationship('Message', secondary = all_readBy, backref = 'person', lazy = True)
   
    def to_dict(self):
       return {'username': self.username, 'id': self.id}
   

@app.route("/add", methods = ['POST'])
def add_user():
    user = User(username = request.json['username'])
    db.session.add(user)
    db.session.commit()
    return jsonify(user.to_dict())


@app.route("/messages", methods = ['POST'])
def send_message():
    message_text = request.json['message']
    if len(message_text) > 140:
        return "Message exceeded maximum length.", 400
    message = Message(message = message_text)
    db.session.add(message)
    db.session.commit()
    return jsonify(message.to_dict_id())    


@app.route("/messages/<MessageID>", methods=['GET'])
def get_message(MessageID):
    message = Message.query.filter_by(id = MessageID).first()
    if not message:
        return jsonify("No such message exists."), 404
    return jsonify(message.to_dict())


@app.route("/messages/<MessageID>/read/<UserId>", methods = ['POST'])
def mark_as_read(MessageID, UserId):
    user = User.query.filter_by(id = int(UserId)).first()
    message = Message.query.filter_by(id = int(MessageID)).first()
    if not message:
        return jsonify("Message does not exist."), 404
    elif not user:
        return jsonify("User does not exist."), 404
    else:
        user.readBy.append(message)
        db.session.commit()
        return jsonify("Message has been read.") 


@app.route("/messages/unread/<UserId>", methods = ["GET"])
def unread_messages(UserId):
    user = User.query.filter_by(id = int(UserId)).first()
    messages = Message.query.all()
    readlist = user.readBy
    unread_messages = []
    for message in messages:
        if not message in readlist:
            unread_messages.append(message)
    if not unread_messages:
        return jsonify("No unread messages exist."), 404
    else:
        return jsonify([unread_message.to_dict() for unread_message in unread_messages])


@app.route("/messages/<MessageID>", methods = ["DELETE"])
def delete_message(MessageID):
    message = Message.query.filter_by(id = int(MessageID)).first()
    if not message:
        return jsonify("Message does not exist."), 404
    db.session.delete(message)
    db.session.commit()
    return jsonify("Message has been removed.")


@app.route("/messages", methods = ["GET"])
def get_all_messages():
    messages = Message.query.all()
    if not messages:
        return jsonify("No messages exist."), 404
    else:
        return jsonify([message.to_dict() for message in messages])


if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.debug = True
    app.run(port = 5050)