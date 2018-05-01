
import javafx.beans.InvalidationListener;

import java.util.Observable;

/**
 * Created by tphadke on 8/29/17.
 */
public class Buffer extends Observable {
	private Message message;

	/**
     * Create an empty Buffer
     */
	public Buffer() {
	}
	
	/**
     * Create a buffer
     * @param  Message to be stored in buffer
     */
	public Buffer(Message message) {
		this.message = message;
	}

	/**
     * @return Message from the processors buffer
     */
	public Message getMessage() {
		return message;
	}

	/**
     * Notify the processor after setting sender information and message.
     * @param message		  Message to be stored in receivers buffer
     * @param SourceProcessor Processor who sent the message
     */
	public void setMessage(Message message, Processor SourceProcessor) {
		this.message = message;
		setChanged();
		notifyObservers(SourceProcessor);
	}
}
