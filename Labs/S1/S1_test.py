import requests
import json

base_url = "http://127.0.0.1:5050"

def test_server():
    """Tests the server from S1.py"""
    
    send = requests.post(f"{base_url}/add/messages", json={"message": "First message"})
    assert send.status_code == 200
    
    MsgId = json.loads(send.text)['id']
    
    send_no_json = requests.post(f"{base_url}/messages")
    assert send_no_json.status_code == 400
    
    get = requests.get(f"{base_url}/messages/{MsgId}")
    assert json.loads(get.text) == {'id':MsgId ,'message':'First message','readBy':[]}
    
    get_all = requests.get(f"{base_url}/messages")
    assert json.loads(get_all.text) == [{'id':MsgId ,'message':'First message','readBy':[]}]
    
    read = requests.post(f"{base_url}/messages/{MsgId}/read/400")
    assert read.status_code == 200
    
    get_read = requests.get(f"{base_url}/messages/{MsgId}/read/400")
    assert get_read.status_code == 405
    
    unread = requests.get(f"{base_url}/messages/unread/4020")
    assert json.loads(unread.text) == [{'id':MsgId ,'message':'First message','readBy':['400']}]
    
    send_second = requests.post(f"{base_url}/messages", json={"message": "Second message"})
    assert send_second.status_code == 200
    
    invalid_path = requests.get(f"{base_url}/messages/invalid/path")
    assert invalid_path.status_code == 404

    print("The code passed all the tests!")

test_server()
