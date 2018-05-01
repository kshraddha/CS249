## Homework 6

Chandy Lamport Algorithm

* One of the processor will initiate the snapshot and send messages to outgoing channels. 
* It will also start recording messages from incoming channels.
* When the processors receive the 1st Marker message it will start recording messages on other incoming channels.
* After receiving 2nd Marker message the processor will stop recording and will store all the messages in a Map.
* And this Map is printed after recording is stopped. 

### Input

* 3 processors
* Channels
* Execution plan for all processors

### Steps to run

* Create a new java project in Eclipse IDE with path to src folder.
* Run as java project

### Output 

* Refer Output.txt file for sample output.
