import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Contains processor details and functions
 */
public class Processor implements Observer {

	// Processor id
	Integer id;
	// Processor value
	Integer value;
	// Max value within subtree with this processor as root
	Integer maxValue;

	// Parent processor to this processor
	Processor parentProcessor;
	// Processor buffer to receive messages
	Buffer messageBuffer;
	// List of child processors
	List<Integer> children;
	// List of neighbors yet to send values
	List<Integer> pending;
	// Traversal order in child processors
	List<Integer> traversalList;	

	/**
	 * Default constructor initializes processor details
	 */
	public Processor() {

		id = Integer.MIN_VALUE; 
		value = Integer.MIN_VALUE; 
		maxValue = Integer.MIN_VALUE;

		messageBuffer = new Buffer();
		children = new ArrayList<>();
		pending = new ArrayList<>();
		traversalList = new ArrayList<>();
		// Processor observers its buffer to check messages received
		messageBuffer.addObserver(this);
	}

	/**
	 * Removes processor from list of neighbors yet to send value
	 * @param p
	 */
	private void removeFromPending(Integer p) {
		this.pending.remove(p);
	}

	/**
	 * Compares processor values
	 * @param value
	 */
	private void checkMaxValue(Integer value)
	{
		if(this.maxValue < value)
		{
			this.maxValue = value;
		}
	}

	/**
	 * Adds a processor to this message buffer
	 * Used by other processors to send a message to this processor
	 * @param message
	 * @param sourceProcessor
	 */
	public void sendMessgeToMyBuffer(Message message, Processor sourceProcessor) {
		System.out.println("Sender\t-\t" + sourceProcessor.id + "\tReceiver\t-\t" + this.id + "\tMessage\t-\t" + message.name() + "\t-\t" + sourceProcessor.maxValue);
		messageBuffer.setMessage(message, sourceProcessor);
	}

	/**
	 * Called when a message is sent to this processor using its buffer
	 */
	public void update(Observable observable, Object arg) {

		Processor sourceProcessor = (Processor) arg;

		// Check type of message received
		switch(this.messageBuffer.getMessage())
		{

		case VALUE:
			// Remove source processor from list of pending neighbors
			removeFromPending(sourceProcessor.id);
			// Add subtree traversal to processor traversal list
			this.traversalList.addAll(sourceProcessor.traversalList);
			this.traversalList.add(sourceProcessor.value);
			// Compare processor values
			checkMaxValue(sourceProcessor.maxValue);
			if(this.pending.isEmpty())
			{
				if(this.parentProcessor == this)
				{
					this.traversalList.add(this.value);
					// Print results & stop the algorithm
					System.out.println("Max value of the tree - " + this.maxValue);
					System.out.println("Traversal List - " + this.traversalList);
					System.exit(0);
				}
				// Send value to parent processor
				this.parentProcessor.sendMessgeToMyBuffer(Message.VALUE, this);
			}
		}
	}

	/**
	 * Set parent processor for this processor
	 * @param p
	 */
	public void setParent(Processor p) {
		this.parentProcessor = p;
	}
}