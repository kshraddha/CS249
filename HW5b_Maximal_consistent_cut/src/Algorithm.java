import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Algorithm {
	int noOfProcessors;
	Processor p0, p1; 

	public Algorithm() {
		super();
		this.noOfProcessors = 2;
		// Initialize all the processors
		p0 = new Processor(0, 2);
		p1 = new Processor(1, 2);
	}

	/**
	 * This method performs all executions for p0.
	 */
	public void executionPlanForP0() {

		try {
			System.out.println("Starting execution for p0");
			send(p0, p1, new Message(MessageType.SEND, p0.getVectorClock()));
			compute(p0, new Message(MessageType.COMPUTATION, null));
			compute(p0, new Message(MessageType.COMPUTATION, null));
			send(p0, p1, new Message(MessageType.SEND, p0.getVectorClock()));
			System.out.println("\n*****Completed execution for p0. Vector Clock at p0 is:"+ p0.getVectorClock());
		} catch (Exception e) {
			System.out.println("Exception while executing p0" + e);
			e.printStackTrace();
		}
	}

	/**
	 * This method performs all executions for p1.
	 */
	public void executionPlanForP1() {
		try {
			System.out.println("Starting execution for p1");
			compute(p1, new Message(MessageType.COMPUTATION, null));
			compute(p1, new Message(MessageType.COMPUTATION, null));
			compute(p1, new Message(MessageType.COMPUTATION, null));
			receive(p0, p1, new Message(MessageType.RECEIVE, p1.getVectorClock()));
			compute(p1, new Message(MessageType.COMPUTATION, null));
			receive(p0, p1, new Message(MessageType.RECEIVE, p1.getVectorClock()));
			System.out.println("\n*****Completed execution for p1. Vector Clock at p1 is:"+ p1.getVectorClock());
		} catch (Exception e) {
			System.out.println("Exception while executing p1");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will find the maximal consistent cut by finding the consistent cut for each processor
	 * and getting the maximum value at each index.
	 */
	public void findMaxConsistentCut(){
		VectorClock k = new VectorClock(noOfProcessors);
		VectorClock maxConsistentCut = new VectorClock(noOfProcessors);
		List<VectorClock> vcList = new ArrayList<>();
		
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter cut value at p0: ");
		k.updateAt(0, reader.nextInt());
		System.out.println("Enter cut value at p1: ");
		k.updateAt(1, reader.nextInt());
		reader.close();
		
		vcList.add(p0.getConsistentCutVC(k));
		vcList.add(p1.getConsistentCutVC(k));
	
		//For returned VC for each processors, find max value at each index
		for(int i=0; i<vcList.get(0).vc.length; i++){
			int[] possibleValues = new int[noOfProcessors];
			for(int j=0; j<vcList.size(); j++){
				possibleValues[j] = vcList.get(j).vc[i];
			}
			maxConsistentCut.updateAt(i, findMax(possibleValues));
		}
		
		System.out.println("\n****Maximal Consistent Cut is "+maxConsistentCut);
		
	}
	
	/**
	 * This method will find max value in an array
	 * @param inputArray
	 * @return maxValue
	 */
	public int findMax(int[] inputArray){ 
	    int maxValue = inputArray[0]; 
	    for(int i=1;i < inputArray.length;i++){ 
	      if(inputArray[i] > maxValue){ 
	         maxValue = inputArray[i]; 
	      } 
	    } 
	    return maxValue; 
	  }

	/**
	 * This method carries out COMPUTATION event
	 * @param p
	 * @param computeMessage
	 */
	public void compute(Processor p, Message computeMessage) {
		p.sendMessgeToMyBuffer(computeMessage, p);
	}
	/**
	 *  This method carries out SEND event
	 * @param sender
	 * @param receiver
	 * @param m           
	 */
	public void send(Processor sender, Processor receiver, Message m) {
		sender.sendMessgeToMyBuffer(m, receiver);
	}

	/**
	 *  This method carries out RECEIVE event
	 * @param sender
	 * @param receiver
	 * @param m           
	 */
	public void receive(Processor sender, Processor receiver, Message m) {
		receiver.sendMessgeToMyBuffer(m, sender);
	}

}
