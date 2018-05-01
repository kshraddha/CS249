import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

/**
 * Contains processor details and functions
 */
public class Processor implements Observer, Runnable {

	// Processor id
	Integer id;

	// Processor buffer to receive messages
	// In Channels
	Buffer leftInChannel;
	Buffer rightInChannel;
	// Out Channels
	Buffer leftOutChannel;	
	Buffer rightOutChannel;

	// Leader flag
	boolean isLeader;
	Map<Integer, Integer> phaseReplyMap;
	
	// CountDown latches for synchronizing threads
	CountDownLatch startSignal;

	/**
	 * Default constructor initializes processor details
	 */
	public Processor() {
		this.id = Integer.MIN_VALUE;
		this.isLeader = false;
		leftInChannel = null;
		leftOutChannel = null;
		rightInChannel = null;
		rightOutChannel = null;
		phaseReplyMap = null;
	}

	/**
	 * Parameterized constructor setting CountDown latch values
	 * @param startSignal
	 */
	public Processor(CountDownLatch startSignal) {
		this.id = Integer.MIN_VALUE;
		this.isLeader = false;
		
		leftInChannel = null;
		leftOutChannel = null;
		rightInChannel = null;
		rightOutChannel = null;
		
		phaseReplyMap = new HashMap<Integer, Integer>();
		phaseReplyMap.put(0, 0);
		
		this.startSignal = startSignal;
	}

	/**
	 * Setting incoming channels for this processor 
	 * @param leftChannel
	 * @param rightChannel
	 */
	public void setInChannels(Buffer leftChannel, Buffer rightChannel) {

		this.leftInChannel = leftChannel;
		this.leftInChannel.addObserver(this);
		
		this.rightInChannel = rightChannel;
		this.rightInChannel.addObserver(this);
	}

	/**
	 * Setting outgoing channels for this processor
	 * @param leftChannel
	 * @param rightChannel
	 */
	public void setOutChannels(Buffer leftChannel, Buffer rightChannel) {
		this.leftOutChannel = leftChannel;
		this.rightOutChannel = rightChannel;
	}

	/**
	 * Adds a message to this message buffer
	 * Processors observing this buffer receive the message
	 * @param msgType
	 * @param idValue
	 * @param iPhase
	 * @param iHopCounter
	 * @param doSendLeft
	 * @param doSendRight
	 */
	public void send(MessageType msgType, int idValue, int iPhase, int iHopCounter, boolean doSendLeft, boolean doSendRight) {

		// Sends message using outgoing left buffer
		if(doSendLeft)
		{
			Message msg = new Message(msgType, idValue, iPhase, iHopCounter, true);
			System.out.println("Sender\t-\t" + this.id + "\tMessage\t-\t" + msgType.name() + "\t\tValue\t-\t" + idValue + "\t\tDirection\t-\tLEFT");
			this.leftOutChannel.sendMessage(msg);
		}

		// Sends message using outgoing right buffer
		if(doSendRight)
		{
			Message msg = new Message(msgType, idValue, iPhase, iHopCounter, false);
			System.out.println("Sender\t-\t" + this.id + "\tMessage\t-\t" + msgType.name() + "\t\tValue\t-\t" + idValue + "\t\tDirection\t-\tRIGHT");
			this.rightOutChannel.sendMessage(msg);
		}
	}

	/**
	 * Called when a message is sent to this processor using its incoming buffer
	 * @param Observable
	 * @param arg
	 */
	public void update(Observable observable, Object arg) {

		Buffer pBuffer = (Buffer) observable;
		Message message = pBuffer.getMessage();

		//Source processor identifier value
		int idValue = message.getIdValue();
		//Source processor phase value
		int iPhase = message.getiPhase();
		//Message hop counter
		int iHopCounter = message.getiHopCounter();

		// Check type of message received
		switch(message.getMessageType())
		{

		case PROBE: 
			if(idValue == this.id){
				// Terminate as leader
				this.isLeader = true;
				System.out.println("Leader elected - " + this.id);
				System.out.println("Processing completed");
				System.exit(0);
			}
			else if((idValue > this.id))
			{
				if(iHopCounter < Math.pow(2, iPhase))
				{
					if(message.isSentLeft())
					{
						// Received from right
						this.send(MessageType.PROBE, idValue, iPhase, iHopCounter+1, true, false);
					}
					else{
						// Received from left
						this.send(MessageType.PROBE, idValue, iPhase, iHopCounter+1, false, true);
					}	
				}
				else
				{
					if(message.isSentLeft())
					{
						// Received from right
						this.send(MessageType.REPLY, idValue, iPhase, 0, false, true);
					}
					else{
						// Received from left
						this.send(MessageType.REPLY, idValue, iPhase, 0, true, false);
					}	
				}
			}
			else if((idValue < this.id))
			{
				//Do nothing
				System.out.println("Processor\t" + this.id + "\tswallowed message\t" + message.getMessageType() + "\treceived with value\t" + idValue);	
			}
			break;
			
		case REPLY:
			if(idValue != this.id)
			{
				if(message.isSentLeft())
				{
					// Received from right
					this.send(MessageType.REPLY, idValue, iPhase, 0, true, false);
				}
				else{
					// Received from left
					this.send(MessageType.REPLY, idValue, iPhase, 0, false, true);
				}	
			}
			else
			{
				int replyCount = this.phaseReplyMap.get(iPhase);
				if(replyCount > 0)
				{
					iPhase++;
					this.phaseReplyMap.put(iPhase, 0);
					this.send(MessageType.PROBE, this.id, iPhase, 1, true, false);
					this.send(MessageType.PROBE, this.id, iPhase, 1, false, true);
				}
				else
				{
					replyCount++;
					this.phaseReplyMap.put(iPhase, replyCount);
				}
			}
			break;
		}	
	}

	/**
	 * Starts the thread for this processor
	 */
	@Override
	public void run() {
		try {
			startSignal.await();
			this.send(MessageType.PROBE, this.id, 0, 1, true, true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}