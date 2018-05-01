## Homework 3

This program takes a graph as an input and builds a DFS spanning tree.

### Input


* 6 Processors with Id and unexplored list is populated.
* Created a Graph using Map <Processor, List<Processor> >

p0 = [p1, p2, p3]  
p1 = [p0, p2, p4]  
p2 = [p1, p0, p5]  
p3 = [p0]  
p4 = [p1, p5]  
p5 = [p2, p4]  

* Here Root is p0 and message M is sent to first processor in the list of unexplored.

### Output


* Source and target Processors along with the type of message (M, ALREADY, PARENT) are printed in the console.
* Map(Processors and its children) which represents the spanning tree.

Message Sent by p0==>p0 Type Of Message Sent-->M  
Message Sent by p0==>p1 Type Of Message Sent-->M  
Message Sent by p1==>p2 Type Of Message Sent-->M  
Message Sent by p2==>p0 Type Of Message Sent-->M  
Message Sent by p0==>p2 Type Of Message Sent-->ALREADY  
Message Sent by p2==>p5 Type Of Message Sent-->M  
Message Sent by p5==>p4 Type Of Message Sent-->M  
Message Sent by p4==>p1 Type Of Message Sent-->M  
Message Sent by p1==>p4 Type Of Message Sent-->ALREADY  
Message Sent by p4==>p5 Type Of Message Sent-->PARENT  
Message Sent by p5==>p2 Type Of Message Sent-->PARENT  
Message Sent by p2==>p1 Type Of Message Sent-->PARENT  
Message Sent by p1==>p4 Type Of Message Sent-->M  
Message Sent by p4==>p1 Type Of Message Sent-->ALREADY  
Message Sent by p1==>p0 Type Of Message Sent-->PARENT  
Message Sent by p0==>p2 Type Of Message Sent-->M  
Message Sent by p2==>p0 Type Of Message Sent-->ALREADY  
Message Sent by p0==>p3 Type Of Message Sent-->M  
Message Sent by p3==>p0 Type Of Message Sent-->PARENT  

**** Output Spanning tree****    
p0 = [p1 p3 ]  
p3 = []  
p5 = [p4 ]  
p2 = [p5 ]  
p4 = []  
p1 = [p2 ]  
