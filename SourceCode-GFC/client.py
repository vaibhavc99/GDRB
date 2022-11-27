import socket
import json
from csv import DictReader

def client_interface():
    
    port = 4011  # port exposed to 'GFC'

    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect(('0.0.0.0', port))  # connection to the docker exposed to 'port'

    request1 = json.dumps({"type":"call", "content":"type"})
    client_socket.send(request1.encode())  # send request to approach
    response1 = client_socket.recv(1024).decode() # receive response

    request2 = json.dumps({"type":"call", "content":"training_start"}) 
    client_socket.send(request2.encode())
    response2 = client_socket.recv(1024).decode()

    with open('sample_data/bpdp_data.csv', 'r') as read_obj:
        csv_dict_reader = DictReader(read_obj)
        for row in csv_dict_reader:
            request3 = json.dumps({"type":"train","subject":row["subject"],"predicate":row["predicate"],"object":row["object"],"score":row["score"]})
            client_socket.send(request3.encode())
            response3 = client_socket.recv(1024).decode()

    request4 = json.dumps({"type":"call", "content":"training_complete"})
    client_socket.send(request4.encode())
    response4 = client_socket.recv(1024).decode()

    request5 = json.dumps({"type":"test"})
    client_socket.send(request5.encode())
    response5 = client_socket.recv(1024).decode()

    print('Output: \n' + response1 + '\n' + response2 + '\n' + response3 + '\n' + response4 + '\n' + response5)

    client_socket.close()  # close the connection


if __name__ == '__main__':
    client_interface()
