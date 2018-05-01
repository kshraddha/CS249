
import java.util.Observable;

/**
 * Observable Buffer of each node
 * @author Sample
 * @version 1.0
 */
public class Buffer extends Observable {
    private Message message;
    private Processor senderProcessor;

    /**
     * 
     * Creates empty buffer
     */
    public Buffer(){
    	this.message = null;
    }

    /**
     * Creates buffer with message
     * @param message Message to be stored
     */
    public Buffer(Message message) {
        this.message = message;
    }
    
    /**
     * @return Message from the buffer
     */
    public Message  getMessage() {
        return message;
    }
    
    /**
     * Get senderProcessor
     */
	public Processor getSenderProcessor(){
		return senderProcessor;
	}
	
	 /**
     * Set senderProcessor
     */
	public void setSenderProcessor(Processor sourceProcessor){
		this.senderProcessor = sourceProcessor;
	}

    /**
     * Sets the message and notifies the observers with the sender node's information
     * @param message		Message to be stored in the buffer
     * @param fromProcessor Node who sent the message
     */
    public void setMessage(Message message, Processor p ) {
        this.message = message;
        setChanged();
        setSenderProcessor(p);
        notifyObservers();
    }
}

