/**
 * Message passed over the buffer including identifier value
 *
 */
public class Message {

	// Type of message
	private MessageType messageType;
	// Identifier value sent
	private Integer idValue;

	/**
	 * Sets message details
	 * @param msgType
	 * @param idValue
	 */
	public Message(MessageType msgType, int idValue) {
		this.messageType = msgType;
		this.idValue = idValue;
	}

	/**
	 * Returns message details
	 * @return
	 */
	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * Returns identifier value of sender
	 * @return
	 */
	public Integer getIdValue() {
		return idValue;
	}
}