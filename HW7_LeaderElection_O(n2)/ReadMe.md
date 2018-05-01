## Homework 7

### Asynchronous ring leader election algorithm

* All the processors will send their own identifier value to the neighbour on their left.
* Any incoming value message from the neigbour to the right will be compared with own identifier value.
* If the value is less than their own value the incoming message is swallowed. If the value is greater than their own value then this message is forwarded to the neighbour on the left.
* When processor recevies its own value, it declares itself as the leader and sends termination message.
* Others declare themselves as non leaders and terminate.

### Input
* Enter the number of processors
* Enter the id value for each processor

### Steps to run
* Create a new java project in Eclipse IDE with path to src folder.
* Run as java project

### Output
Refer Output.txt file for sample output.