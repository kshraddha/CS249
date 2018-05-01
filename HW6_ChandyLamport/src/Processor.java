
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Performs all the processor related tasks
 *
 * @author Sample
 * @version 1.0
 */

public class Processor implements Observer {
	private Integer id;
	private List<Buffer> inChannels;
	private List<Recorder> recorderList;
	private Boolean isInitiator;

	/**
	 * List of output channels
	 */
	private List<Buffer> outChannels;

	/**
	 * This is a map that will record the state of each incoming channel and all
	 * the messages that have been received by this channel since the arrival of
	 * marker and receipt of duplicate marker
	 */
	private Map<Buffer, List<Message>> channelState;

	/**
	 * This map can be used to keep track of markers received on a channel. When
	 * a marker arrives at a channel put it in this map. If a marker arrives
	 * again then this map will have an entry already present from before.
	 * Before doing a put in this map first do a get and check if it is not
	 * null. ( to find out if an entry exists or not). If the entry does not
	 * exist then do a put. If an entry already exists then increment the
	 * integer value and do a put again.
	 */
	private Map<Buffer, Integer> channelMarkerCount;

	/**
	 * @param id of the processor
	 */
	public Processor(int id, List<Buffer> inChannels, List<Buffer> outChannels) {
		this.id = id;
		this.inChannels = inChannels;
		this.outChannels = outChannels;
		recorderList = new ArrayList<>();
		channelState = new HashMap<>();
		isInitiator = false;

		// Make this processor as the observer for each of its inChannel
		// Add all inChannels to the recorder list,
		// so that this processor can trigger recording thread when 1st marker
		// is received
		for (Buffer b : this.inChannels) {
			b.addObserver(this);
			recorderList.add(new Recorder(b));
		}
		channelMarkerCount = new HashMap<>();
	}

	/**
	 * This is a dummy implementation which will record current state of this
	 * processor
	 */
	public void recordMyCurrentState() {
		System.out.println("\nFor Processor p" + this.id);
		System.out.println("Recording my registers...");
		System.out.println("Recording my program counters...");
		System.out.println("Recording my local variables...");
	}

	/**
	 * This method marks the channel as empty
	 * 
	 * @param channel
	 */
	public void recordChannelAsEmpty(Buffer channel) {
		for (Recorder r : recorderList) {
			if (r.getChannel() == channel) {
				r.setChannelMessages(new ArrayList<>());
				channelState.put(channel, r.getChannelMessages());
			}
		}
		System.out.println("\n**** Mark channel as empty on " + channel.label);

	}

	/**
	 * Overloaded method, called with single argument This method will add a
	 * message to this processors buffer. Other processors will invoke this
	 * method to send a message to this Processor
	 *
	 * @param message
	 *            Message to be sent
	 */
	public void sendMessgeTo(Message message, Buffer channel) {
		channel.saveMessage(message);

	}

	/**
	 * @param fromChannel channel where marker has arrived
	 * @return true if this is the first marker false otherwise
	 */
	public boolean isFirstMarker() {
		if (channelMarkerCount.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Gets called when a Processor receives a message in its buffer Processes
	 * the message received in the buffer
	 */
	public void update(Observable observable, Object arg) {
		Message message = (Message) arg;
		Buffer fromChannel = (Buffer) observable;

		if (message.getMessageType().equals(MessageType.MARKER)) {

			// Record from Channel as Empty
			// TODO: add logic here so that if the marker comes back to the
			// initiator then it should stop recording
			if (isFirstMarker()) {
				recordMyCurrentState();
				recordChannelAsEmpty(fromChannel);
				channelMarkerCount.put(fromChannel, 1);

				// From the other incoming Channels (excluding the fromChannel
				// which has sent the marker)
				// start recording messages

				for (Recorder r : recorderList) {
					if (r.getChannel() != fromChannel) {
						r.start();
						r.recordChannel();
					}
				}

				// Send marker messages to all outgoing channels
				for (Buffer b : this.outChannels) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("\n>>>> Sending from p" + this.id + " on " + b.label);
					Message m = new Message(MessageType.MARKER);
					m.setFrom(this);
					sendMessgeTo(m, b);
				}

			} else {

				// If marker comes back to the initiator, means if it is
				// DuplicateMarkerMessage, then stop recording
				channelMarkerCount.put(fromChannel, 1);
				for (Recorder r : recorderList) {
					if (r.getChannel() == fromChannel) {
						r.stopChannel();
						channelState.put(fromChannel, r.getChannelMessages());
						r.interrupt();
					}
				}
				System.out.println(
						"\n**** Messages Recorded on " + fromChannel.label + " are: " + channelState.get(fromChannel));
			}
		} else {
			// If Message Type is ALGORITHM
			if (message.getMessageType().equals(MessageType.ALGORITHM)) {
				System.out.println("\nProcessing Algorithm message for p" + this.id + " from " + fromChannel.label);
			}
		}

	}

	/**
	 * Initiate snapshot by sending out a marker message on outgoing channels
	 * Start recording messages on each incoming channels
	 */
	public void initiateSnapShot() {
		recordMyCurrentState();
		
		// Dummy record to avoid sending message from P1 to P2/P3 again.
		channelMarkerCount.put(new Buffer(), 1);
		
		ExecutorService executor = Executors.newFixedThreadPool(2);

		for (Recorder r : recorderList) {
			r.recordChannel();
		}
		for (Buffer b : this.outChannels) {
			System.out.println("\n>>>> Sending from p" + this.id + " on " + b.label);
			Message m = new Message(MessageType.MARKER);
			m.setFrom(this);
			
			executor.execute(new Runnable() {
				public void run() {
					sendMessgeTo(m, b);
				}
			});

		}
		executor.shutdown();
	}
	
	/**
	 * Get processor Id
	 * @return id
	 */
	public Integer getProcessorId(){
		return this.id;
	}
	
	/**
	 * Get method to return list of outChannels for this processor
	 */
	public List<Buffer> getOutChannels() {
		return this.outChannels;
	}

}
