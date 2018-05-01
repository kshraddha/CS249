import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.*;
import java.lang.*;

/**
 * Created by tphadke on 8/29/17.
 */
public class Processor implements Observer {

	Buffer messageBuffer;
	Integer id;
	List<Processor> children;
	Processor parent;
	List<Processor> unexplored;

    /**
     * Initialize the processor with id and unexplored list.
     * Add itself as an observer
     */
	
	public Processor() {
		messageBuffer = new Buffer();
		id = Integer.MIN_VALUE;
		parent = null;
		children = new ArrayList<>();
		unexplored = new ArrayList<>();
		messageBuffer.addObserver(this);
	}

	/**
	 * This method will remove a processor from this processors unexplored list
	 * TODO: implement removing one processor from the list of Children
	 */
	private void removeFromUnexplored(Processor p) {
		this.unexplored.remove(p);
	}

	/**
	 * This method will remove a processor from this processors unexplored list
	 * Other processors will invoke this method to send a message to this Processor
	 */
	public void sendMessgeToMyBuffer(Message message, Processor messageSender) {
		messageBuffer.setMessage(message, messageSender);
	}

	/**
	 * This is analogous to recieve method. Whenever a message is dropped in its
	 * buffer this Pocesssor will respond. Based on the message received, this
	 * method do the further processing. TODO: implement the logic of receive
	 * method here
	 */

	public void update(Observable observable, Object arg) {
		Processor messageSender = (Processor) arg;

		System.out.println("Message Sent by p" + messageSender.id + "==>p" + this.id + " Type Of Message Sent-->"
				+ this.messageBuffer.getMessage());

		switch (this.messageBuffer.getMessage()) {
		case M:
			if (this == messageSender) {
				// This is the first message sent by root to itself
				this.parent = this;
				explore();

			} else if (this.parent != null) {
				// If this processor already has parent set then send ALREADY
				// message
				messageSender.sendMessgeToMyBuffer(Message.ALREADY, this);

			} else {
				// ELse set parent and remove source processor from unexplored
				// list
				this.parent = messageSender;
				removeFromUnexplored(messageSender);
				explore();
			}
			break;

		case ALREADY:
			explore();
			break;

		case PARENT:
			this.children.add(messageSender);
			explore();
			break;

		}
	}

	/**
	 * Processors call this method to explore their unexplored children.
	 */
	private void explore() {
		// If unexplored list is not empty then send message to next processor
		// in the list and remove it from the list.
		if (!this.unexplored.isEmpty()) {
			Processor nextChild = this.unexplored.get(0);
			removeFromUnexplored(nextChild);
			nextChild.sendMessgeToMyBuffer(Message.M, this);

		} else {
			if (this.parent != this) {
				// If unexplored list is empty and this processor is not root
				// then send PARENT message to it's parent.
				this.parent.sendMessgeToMyBuffer(Message.PARENT, this);
			}
		}
	}
}