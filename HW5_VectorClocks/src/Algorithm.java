public class Algorithm {
	int noOfProcessors;
	Processor p0, p1, p2; 

	public Algorithm() {
		super();
		this.noOfProcessors = 3;
		// Initialize all the processors
		p0 = new Processor(0, 3);
		p1 = new Processor(1, 3);
		p2 = new Processor(2, 3);
	}

	/**
	 * This method performs all executions for p0.
	 */
	public void executionPlanForP0() {

		try {
			System.out.println("Starting execution for p0");
			send(p0, p1, new Message(MessageType.SEND, p0.getVectorClock()));
			System.out.println("\n### P0 tread is sleeping for 2sec before sending message to p2(Event Q2)");
			Thread.sleep(2000);
			send(p0, p2, new Message(MessageType.SEND, p0.getVectorClock()));
			compute(p0, new Message(MessageType.COMPUTATION, null));
			System.out.println("\n### P0 tread is sleeping for 1sec before receiving message from p1(Event Q4)");
			Thread.sleep(2000);
			receive(p1, p0, new Message(MessageType.RECEIVE, p0.getVectorClock()));
			compute(p0, new Message(MessageType.COMPUTATION, null));
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
			receive(p0, p1, new Message(MessageType.RECEIVE, p1.getVectorClock()));
			receive(p2, p1, new Message(MessageType.RECEIVE, p1.getVectorClock()));
			send(p1, p2, new Message(MessageType.SEND, p1.getVectorClock()));
			System.out.println("\n### P1 tread is sleeping for 3sec before receiving message to p2 (Event Q9)");
			Thread.sleep(3000);
			receive(p2, p1, new Message(MessageType.RECEIVE, p1.getVectorClock()));
			send(p1, p0, new Message(MessageType.SEND, p1.getVectorClock()));
			System.out.println("\n*****Completed execution for p1. Vector Clock at p1 is:"+ p1.getVectorClock());
		} catch (Exception e) {
			System.out.println("Exception while executing p1");
			e.printStackTrace();
		}
	}

	/**
	 * This method performs all executions for p2.
	 */
	public void executionPlanForP2() {
		try {
			System.out.println("Starting execution for p2");
			compute(p2, new Message(MessageType.COMPUTATION, null));
			System.out.println("\n### P2 tread is sleeping for 1sec before computing (Event Q11)");
			Thread.sleep(1000);
			compute(p2, new Message(MessageType.COMPUTATION, null));
			send(p2, p1, new Message(MessageType.SEND, p2.getVectorClock()));
			receive(p0, p2, new Message(MessageType.RECEIVE,  p2.getVectorClock()));
			send(p2, p1, new Message(MessageType.SEND,  p2.getVectorClock()));
			receive(p1, p2, new Message(MessageType.RECEIVE,  p2.getVectorClock()));
			compute(p2, new Message(MessageType.COMPUTATION, null));
			System.out.println("\n*****Completed execution for p2. Vector Clock at p2 is:"+ p2.getVectorClock());
		} catch (Exception e) {
			System.out.println("Exception while executing p2" + e);
			e.printStackTrace();
		}

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
