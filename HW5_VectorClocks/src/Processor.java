import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Performs all the processor related tasks
 * 
 * @author 
 * @version 1.0
 *
 */
public class Processor implements Observer {
	private Buffer messageBuffer;
	private Integer id;
	private VectorClock vc; // This is the current vector clock
	// Add entry to this queue whenever a processor P sends message to this
	// processor.
	// Use it when receive event is executed on this processor.
	private ConcurrentMap<Processor, List<Message>> receivedMessageQueue;

	/**
	 * Initializes the processor with id, children and unexplored lists. Adds
	 * himself in the observers list.
	 * @param id of the processor
	 */
	public Processor(int id, int totalProcessors) {
		messageBuffer = new Buffer();
		this.id = id;
		messageBuffer.addObserver(this);
		vc = new VectorClock(totalProcessors);
		receivedMessageQueue = new ConcurrentHashMap<>();
	}

	/**
	 * Overloaded method, called with single argument This method will add a
	 * message to this processors buffer. Other processors will invoke this
	 * method to send a message to this Processor
	 * 
	 * @param message Message to be sent
	 * @param Sender Processor
	 */
	public void sendMessgeToMyBuffer(Message message, Processor p) {
		messageBuffer.setMessage(message, p);
	}

	/**
	 * Gets called when a node receives a message in it buffer Processes the
	 * message received in the buffer
	 */
	public void update(Observable observable, Object arg) {
		Processor p = this.messageBuffer.getSenderProcessor();
		
		switch (this.messageBuffer.getMessage().messageType) {

		case COMPUTATION:
			calculateVectorClocks(observable, arg);
			
			System.out.println("\n---Compute by p" + this.id + " " + this.messageBuffer.getMessage() + "\n---VC for p"
					+ this.id + " is:" + this.vc);
			break;

		case SEND:
			// Send VC after incrementing this processors VC.
			VectorClock updatedVC = calculateVectorClocks(observable, arg);
			Message updatedMsg = new Message(this.messageBuffer.getMessage().messageType, updatedVC);
			notifyReceiver(p, updatedMsg);
			
			System.out.println("\n---Message sent by p" + this.id + " to p" + p.id + " " + updatedMsg + "\n---VC for p"
					+ this.id + " is:" + this.vc);
			break;

		case RECEIVE:
			synchronized (this) {
				try {

					while (this.receivedMessageQueue.get(p) == null || this.receivedMessageQueue.get(p).isEmpty()) {
						System.out.println("\nReceive initiated: p" + this.id + " is waiting for p" + p.id);
						wait();
					}
				} catch (Exception e) {
					System.out.println("Exception while receiving");
					e.printStackTrace();
				}
			}

			calculateVectorClocks(observable, arg);
			System.out.println("\n---Message received by p" + this.id + " from p" + p.id + " "
					+ this.messageBuffer.getMessage() + "\n---VC for p" + this.id + " is:" + this.vc);

			try {
				this.receivedMessageQueue.get(p).remove(0);

			} catch (Exception e) {
				System.out.println("\nException while removing message from queue of p" + this.id + " for p" + p.id);
				e.printStackTrace();
			}
			break;
		}

	}

	/**
	 * Logic to check based on the current vector clocks and the vector clock message received in the buffer. 
	 * Returned VC is used by processors who are sending messages with updated VC.
	 */
	public VectorClock calculateVectorClocks(Observable observable, Object arg) {
		Processor p = this.messageBuffer.getSenderProcessor();
		try {
			// For all types of event VC for this will be incremented.
			this.vc.incrementByOne(this.id);

			if (this.messageBuffer.getMessage().messageType == MessageType.RECEIVE) {
				List<Message> messages = this.receivedMessageQueue.get(p);
				VectorClock senderVc = messages.get(0).vc;

				for (int i = 0; i < this.vc.vc.length; i++) {
					this.vc.setIndex(i);
					if (this.vc.compareTo(senderVc) < 0) {
						this.vc.updateAt(i, senderVc.vc[i]);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("\nException while computing VC for p" + this.id + " Message:"
					+ this.messageBuffer.getMessage() + " with p" + p.id);
			e.printStackTrace();
		}
		return this.vc;
	}

	/**
	 * Notify receiver by adding message to it's queue. And resume the execution
	 * for receiver if it's waiting.
	 */
	public void notifyReceiver(Processor p, Message M) {
		List<Message> messages = p.receivedMessageQueue.get(this.id);
		if (messages == null) {
			messages = new ArrayList<>();
			List<Message> oldList = p.receivedMessageQueue.putIfAbsent(this, messages);
			if (oldList != null) {
				messages = oldList;
			}
		}
		messages.add(M);
		p.resume();
	}

	/**
	 * Notify receiver (It can be waiting for this processor or still processing
	 * previous requests)
	 */
	synchronized void resume() {
		synchronized (this) {
			notify();
		}
	}
	
	/**
     * Get this processors Vector Clock
     */
	public VectorClock getVectorClock() {
		return this.vc;
	}

	@Override
	public String toString() {
		return "Processor [messageBuffer=" + messageBuffer + ", id=" + id + ", receivedMessageQueue="
				+ receivedMessageQueue + ", vc=" + vc + "]";
	}
}
