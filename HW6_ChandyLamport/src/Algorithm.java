
/**
 * This is the simulation of a main algorithm that will run on processors P1,
 * P2, P3 This could be a banking application, payroll application or any other
 * distributed application
 */
public class Algorithm {

	/**
	 * The processors which will participate in a distributed application
	 */
	Processor processor1, processor2, processor3;

	/**
	 * Initialize processors so that they represent the topology of 3 processor
	 * system
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public Algorithm(Processor p1, Processor p2, Processor p3) {

		this.processor1 = p1;
		this.processor2 = p2;
		this.processor3 = p3;
	}

	/**
	 * Execution plan for P1
	 */
	public void executionPlanForP1() {

		// Initiate snapshot
		processor1.initiateSnapShot();
		send(processor1, processor2, processor1.getOutChannels().get(0));
		compute(processor1);
		send(processor1, processor3, processor1.getOutChannels().get(1));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		send(processor1, processor2, processor1.getOutChannels().get(0));

	}

	/**
	 * Execution plan for P2
	 */
	public void executionPlanForP2() {

		send(processor2, processor3, processor2.getOutChannels().get(1));
		compute(processor2);
		send(processor2, processor1, processor2.getOutChannels().get(0));
		compute(processor2);
		send(processor2, processor3, processor2.getOutChannels().get(0));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		send(processor2, processor3, processor2.getOutChannels().get(1));
		send(processor2, processor1, processor2.getOutChannels().get(0));
		compute(processor2);

	}

	/**
	 * Execution plan for P3
	 */
	public void executionPlanForP3() {

		send(processor3, processor1, processor3.getOutChannels().get(0));
		send(processor3, processor2, processor3.getOutChannels().get(1));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		send(processor3, processor2, processor3.getOutChannels().get(1));
		send(processor3, processor1, processor3.getOutChannels().get(0));
		send(processor3, processor2, processor3.getOutChannels().get(1));
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		send(processor3, processor2, processor3.getOutChannels().get(1));
		compute(processor3);
		compute(processor3);
		send(processor3, processor1, processor3.getOutChannels().get(0));

	}

	/**
	 * A dummy computation.
	 * 
	 * @param p
	 */
	public void compute(Processor p) {
		System.out.println("\nDoing some computation on p" + p.getProcessorId());
	}

	/**
	 * Send Algorithm messages
	 * 
	 * @param to processor to which message is sent
	 * @param channel the incoming channel on the to processor that will receive this message
	 */
	public void send(Processor from, Processor to, Buffer channel) {
		Message m = new Message(MessageType.ALGORITHM);
		m.setFrom(from);
		to.sendMessgeTo(m, channel); // ALGORITHM
	}

}
