import time


class Process:
    def __init__(self, process_id):
        self.process_id = process_id
        self.state = "ACTIVE"

    def become_passive(self):
        self.state = "PASSIVE"

    def become_active(self):
        self.state = "ACTIVE"


class TerminationDetector:
    def __init__(self, processes):
        self.processes = processes
        self.messages_in_transit = 0

    def send_message(self, sender, receiver):
        self.messages_in_transit += 1

        print(
            f"Process {sender} sent a message to Process {receiver}"
        )

    def receive_message(self, receiver):
        if self.messages_in_transit > 0:
            self.messages_in_transit -= 1

        print(
            f"Process {receiver} received the message"
        )

    def check_termination(self):

        all_passive = all(
            process.state == "PASSIVE"
            for process in self.processes
        )

        no_messages = self.messages_in_transit == 0

        return all_passive and no_messages

    def display_status(self):

        print("\n-----------------------------")
        print("Current System Status")
        print("-----------------------------")

        for process in self.processes:
            print(
                f"Process {process.process_id}: "
                f"{process.state}"
            )

        print(
            f"Messages in transit: "
            f"{self.messages_in_transit}"
        )


# Create processes
p1 = Process(1)
p2 = Process(2)
p3 = Process(3)

processes = [p1, p2, p3]

# Create termination detector
detector = TerminationDetector(processes)


# Initial state
detector.display_status()


# Process 1 sends a message to Process 2
detector.send_message(1, 2)

detector.display_status()


# Process 2 receives the message
detector.receive_message(2)

detector.display_status()


# All processes become passive
print("\nAll processes are becoming passive...")

p1.become_passive()
p2.become_passive()
p3.become_passive()

detector.display_status()


# Check termination
if detector.check_termination():
    print("\nSYSTEM HAS TERMINATED")
else:
    print("\nSYSTEM HAS NOT TERMINATED")