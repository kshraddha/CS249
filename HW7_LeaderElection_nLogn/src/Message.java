/**
 * Message passed over the buffer including identifier value
 *
 */
public class Message {

	// Type of message
	private MessageType messageType;
	// Identifier value sent
	private Integer idValue;
	// Message phase number
	private Integer iPhase;
	// Message hop counter
	private Integer iHopCounter;
	// Message direction;
	private boolean isSentLeft;

	/**
	 * Sets message details
	 * 
	 * @param msgType
	 * @param idValue
	 * @param iPhase
	 * @param iHopCounter
	 * @param isSentLeft
	 */
	public Message(MessageType msgType, int idValue, int iPhase, int iHopCounter, boolean isSentLeft) {
		this.messageType = msgType;
		this.idValue = idValue;
		this.iPhase = iPhase;
		this.iHopCounter = iHopCounter; 
		this.isSentLeft = isSentLeft;
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

	/**
	 * Returns message phase number
	 * @return
	 */
	public Integer getiPhase() {
		return iPhase;
	}

	/**
	 * Returns message hop counter
	 * @return
	 */
	public Integer getiHopCounter() {
		return iHopCounter;
	}

	/**
	 * Returns check if message sent in left direction
	 * @return
	 */
	public boolean isSentLeft() {
		return isSentLeft;
	}
}