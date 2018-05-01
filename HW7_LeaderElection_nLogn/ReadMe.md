## Homework 7

### Asynchronous ring leader election algorithm 

* The complexity for this algo is O(n Log n)
* Each processor sends message to both its neighbours and repeats it if it wins that phase of election.
* Every processor may or may not send a reply message depending on the value it recevies.
* If a processor receives reply from both neighbours in that phase then it wins that phase.
* In the next phase it sends the message further away from it and continues until a processor receives its own probe message.
* This processor is the leader of the given topology.

### Input
* Enter the number of processors
* Enter the id value for each processor

### Steps to run
* Create a new java project in Eclipse IDE with path to src folder.
* Run as java project

### Output
Refer Output.txt file for sample output.