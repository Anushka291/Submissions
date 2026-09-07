import socket

# Name-to-address mapping
name_server = {
    "google": "142.250.195.14",
    "youtube": "142.250.72.14",
    "amazon": "205.251.242.103",
    "facebook": "157.240.241.35"
}

# Create TCP socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind server to localhost and port 5000
server_socket.bind(("127.0.0.1", 5000))

# Start listening
server_socket.listen(5)

print("Name Server started...")
print("Waiting for client connection...")

while True:

    # Accept client connection
    client_socket, client_address = server_socket.accept()

    print("\nClient connected:", client_address)

    # Receive name from client
    name = client_socket.recv(1024).decode().lower()

    print("Name requested:", name)

    # Search for name
    if name in name_server:
        response = name_server[name]
    else:
        response = "Name not found"

    # Send response
    client_socket.send(response.encode())

    # Close connection
    client_socket.close()