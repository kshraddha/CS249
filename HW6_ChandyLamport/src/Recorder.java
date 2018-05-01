import java.util.ArrayList;
import java.util.List;

public class Recorder extends Thread {
	private List<Message> channelMessages;

	// Recorder will have a channel, to record messages from.
	private Buffer channel;

	// Index from which messages needs to be stored.
	private int startIndex;

	/**
	 * Create a new recorder with given channel.
	 * 
	 * @param b
	 */

	public Recorder(Buffer b) {
		channelMessages = new ArrayList<>();
		startIndex = 0;
		channel = b;
	}

	/**
	 * Channel will have messages from before a marker has arrived. Record
	 * messages only after a marker has arrived. Get the current index of
	 * message from which messages needs to be stored in this channelMessages.
	 */

	public void recordChannel() {
		if (channel.getTotalMessageCount() > 0) {
			startIndex = channel.getTotalMessageCount() - 1;
		}
	}

	/**
	 * Processor will invoke this method and then stop this thread Store all the
	 * messages from startIndex to currentIndex in this channelMessages.
	 */

	public void stopChannel() {
		Integer currentIndex = channel.getTotalMessageCount();
		for (int i = startIndex; i < currentIndex - 1; i++) {
			channelMessages.add(channel.getMessage(i));
		}
	}

	/**
	 * @return channelMessages
	 */
	public List<Message> getChannelMessages() {
		return this.channelMessages;
	}
	
	/**
	 * Set channelMessages for this recorder
	 * @param messagesList
	 */
	public void setChannelMessages(List<Message> messagesList) {
		this.channelMessages = messagesList;
	}
	
	/**
	 * Set channel for this recorder
	 * @param channel
	 */
	public void setChannel(Buffer Channel){
		this.channel = Channel;
	}
	
	/**
	 * @return channel
	 */
	public Buffer getChannel(){
		return this.channel;
	}

}
