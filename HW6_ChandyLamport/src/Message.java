
public class Message {

	private MessageType messageType;
	private Processor from;

	/**
	 * THe processor that is sending a message
	 * @return
	 */
	public Processor getFrom() {
		return from;
	}

	public void setFrom(Processor from) {
		this.from = from;
	}
	
	public Message(MessageType mt) {
		this.messageType=mt;
	}

	public MessageType getMessageType() {
		return messageType;
	}
	
	@Override
	public String toString() {
		return "Message [messageType = " + messageType + ", from = p" + from.getProcessorId() + "]";
	}
}
