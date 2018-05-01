import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

/**
 * Contains processor details and functions
 */
public class Processor implements Observer, Runnable {

	// Processor id
	Integer id;
	// Processor buffer to receive messages
	// In Channel Buffer
	Buffer channel0;
	// Out Channel Buffer
	Buffer channel1;
	// Leader flag
	boolean isLeader;
	// CountDown latches for synchronizing threads
	CountDownLatch startSignal;
	CountDownLatch doneSignal;

	/**
	 * Default constructor initializes processor details
	 */
	public Processor() {
		id = Integer.MIN_VALUE;
		isLeader = false;
	}

	/**
	 * Parameterized constructor setting CountDown latch values
	 * @param startSignal
	 * @param doneSignal
	 */
	public Processor(CountDownLatch startSignal, CountDownLatch doneSignal) {
		id = Integer.MIN_VALUE;
		isLeader = false;
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
	}

	/**
	 * Setting incoming channel for this processor
	 * @param channel0
	 */
	public void setInChannel(Buffer channel0) {
		this.channel0 = channel0;
		this.channel0.addObserver(this);
	}

	/**
	 * Setting outgoing channel for this processor
	 * @param channel1
	 */
	public void setOutChannel(Buffer channel1) {
		this.channel1 = channel1;
	}

	/**
	 * Adds a processor to this message buffer
	 * Used by other processors to send a message to this processor
	 * @param message
	 * @param sourceProcessor
	 */
	public void send(MessageType msgType, int id_value) {
		System.out.println("Sender\t-\t" + this.id + "\tMessage\t-\t" + msgType.name() + "\t\tValue\t-\t" + id_value);
		Message msg = new Message(msgType, id_value);
		this.channel1.sendMessage(msg);
	}

	/**
	 * Called when a message is sent to this processor using its buffer
	 * @param Observable
	 * @param arg
	 */
	public void update(Observable observable, Object arg) {

		Buffer pBuffer = (Buffer) observable;
		Message message = pBuffer.getMessage();
		// Check type of message received
		switch(message.getMessageType())
		{

		case VALUE: 
			//Source processor identifier value
			int idValue = message.getIdValue();
			if(idValue > this.id)
			{
				this.send(MessageType.VALUE, idValue);
			}else if(idValue < this.id){
				//Do nothing
			}else if(idValue == this.id){
				this.isLeader = true;
				send(MessageType.TERMINATE, this.id);
			}
			break;

		case TERMINATE: 
			if(!this.isLeader){
				System.out.println("Non Leader - " + this.id);
				send(MessageType.TERMINATE, message.getIdValue());				
			}else{
				System.out.println("Leader - " + this.id);
			}
			doneSignal.countDown();
			break;
		}
	}

	/**
	 * Starts the thread for this processor
	 */
	@Override
	public void run() {
		try {
			startSignal.await();
			this.send(MessageType.VALUE, this.id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}