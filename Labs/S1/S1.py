from flask import Flask
from flask import request
from flask import jsonify
import uuid


app = Flask(__name__)

messages = []

    
class Message:
    def __init__(self, id, message):
        self.id = id
        self.message = message
        self.readBy = []

    def all_elem(self):
        return {"id":self.id, "message":self.message, "readBy":self.readBy}

    def json_id(self):
        return {"id":self.id}


class QueryReceived:
    def __init__(self, passed, data):
        self.passed = passed
        self.data = data


def query_check(MessageID, func):
    """"
    Checks if the function can find the correct item from all messages in the
    list of messages and then applies the function func. Then set the
    QueryReceived passed to either True or False.
    """
    for message in messages:
        if message.id == MessageID:
            return QueryReceived(True, func(message))
    return QueryReceived(False, None)


@app.route("/messages", methods = ['POST'])
def send_message():
    received = request.json
    if not received['message'] or len(received['message']) > 140:
        return 400
    message = Message(str(uuid.uuid4()), received['message'])
    messages.append(message)
    return jsonify(message.json_id()), 200


@app.route("/messages/<MessageID>", methods=['GET'])
def get_message(MessageID):
    received = query_check(MessageID, lambda x : x)
    return jsonify(received.data.all_elem()), 200 if received.passed else 404


@app.route("/messages/<MessageID>/read/<UserId>", methods=['POST'])
def mark_as_read(MessageID, UserId):
    received = query_check(MessageID, lambda x : x.readBy.append(UserId) if not UserId in messages else 404)
    return "", 200 if received.passed else 404


@app.route("/messages/unread/<UserId>", methods=["GET"])
def unread_messages(UserId):
    unread = [message.all_elem() for message in messages if not UserId in message.readBy]
    return jsonify(unread), 200


@app.route("/messages/<MessageID>", methods = ['DELETE'])
def delete_message(MessageID):
    received = query_check(MessageID, lambda x : messages.remove(x))
    return "", 200 if received.passed else 404


@app.route("/messages", methods=['GET'])
def get_all_messages():
    return jsonify([message.all_elem() for message in messages])


if __name__ == '__main__':
    app.debug = True
    app.run(port=5050)