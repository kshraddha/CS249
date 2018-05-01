
public class Message {
	MessageType messageType;
	VectorClock vc;
	
	
	public Message(MessageType mt, VectorClock originalVc) {
		this.messageType=mt;
		if (originalVc != null) {
			this.vc = new VectorClock(originalVc.vc.length);
			for (int i = 0; i < originalVc.vc.length; i++) {
				this.vc.vc[i] = originalVc.vc[i];
			}
		}
	}


	@Override
	public String toString() {
		return "Message [messageType=" + messageType + ", vc=" + vc + "]";
	}
}
