import socket

# Create TCP socket
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect to Name Server
client_socket.connect(("127.0.0.1", 5000))

# Take name from user
name = input("Enter name: ")

# Send name to server
client_socket.send(name.encode())

# Receive response
response = client_socket.recv(1024).decode()

print("Server response:", response)

# Close connection
client_socket.close()