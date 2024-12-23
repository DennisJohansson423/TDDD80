import requests
import json

#base_url = "https://s3-dennis-antti.azurewebsites.net"
base_url = "http://127.0.0.1:5050"

def test_server():
    """Tests the server from app.py"""
    
    send = requests.post(base_url + "/messages", json={"message": "First message"})
    assert send.status_code == 200
    
    user = requests.post(base_url + "/add", json = {"username":"user5"})
    assert user.status_code == 200

    get = requests.get(base_url + "/messages/1")
    assert get.status_code == 200
    
    get_all = requests.get(base_url + "/messages")
    assert get_all.status_code == 200
    
    read = requests.post(base_url + "/messages/1/read/1")
    assert read.status_code == 200
    
    unread = requests.get(base_url + "/messages/unread/1")
    assert unread.status_code == 404
    
    invalid_path = requests.get(base_url + "/messages/invalid/path")
    assert invalid_path.status_code == 404

    print("The code passed all the tests!")

test_server()