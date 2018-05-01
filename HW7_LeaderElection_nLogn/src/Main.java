import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Simulating leader election for an asynchronous ring topology 
 *
 */
public class Main {

	// Number of processor in the network
	int processorCount;

	// List of processors in the network
	List<Processor> processorList;
	// List of channels in the network
	// Channels numbered from left of 1st processor
	List<Buffer> leftChannelsList;	
	List<Buffer> rightChannelsList;

	// CountDown latches to synchronize thread start
	CountDownLatch startSignal;
	
	/**
	 * Entry function
	 * @param args
	 */
	public static void main(String[] args) {

		Main m = new Main();
		m.start();
	}

	/**
	 * Starts the asynchronous ring election algorithm
	 */
	private void start()
	{
		// Initializes the network topology
		init();

		// Starts each processor thread and synchronizes using CountdownLatch
		for(int i=0; i< processorCount; i++)
		{
			new Thread(processorList.get(i)).start();
		}
		startSignal.countDown();
	}

	/**
	 * Dynamically initializes the ring network topology 
	 */
	private void init()
	{
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter number of processors - ");
		processorCount = sc.nextInt();

		startSignal = new CountDownLatch(1);

		processorList = new ArrayList<Processor>();
		for(int i=0; i< processorCount; i++)
		{
			Processor p = new Processor(startSignal);
			System.out.print("Enter processor identifier for p" + i + " - ");
			p.id = sc.nextInt();
			processorList.add(p);
		}

		sc.close();

		// Initializes channels for the topology
		initializeChannels();
		// Adds channels to the processors
		setChannels();
	}

	/**
	 * Set In and Out channels for each processor
	 */
	private void setChannels()
	{
		for(int i=0; i< processorCount; i++)
		{
			Buffer leftOutChannel;
			Buffer rightInChannel;
			Buffer rightOutChannel;
			Buffer leftInChannel;
			
			// l of i
			leftOutChannel = leftChannelsList.get(i);
			// r of i
			leftInChannel = rightChannelsList.get(i);
							
			if(i==0)
			{
				// r of i-1
				rightOutChannel = rightChannelsList.get(processorCount-1);
				// l of i-1
				rightInChannel = leftChannelsList.get(processorCount-1);
			}else{
				// r of i-1
				rightOutChannel = rightChannelsList.get(i-1);
				// l of i-1
				rightInChannel = leftChannelsList.get(i-1);
			}
			
			processorList.get(i).setInChannels(leftInChannel, rightInChannel);
			processorList.get(i).setOutChannels(leftOutChannel, rightOutChannel);
		}	
	}
	
	/**
	 * Initializes channels connecting all processors
	 */
	private void initializeChannels()
	{
		leftChannelsList = new ArrayList<Buffer>();
		for(int i=0; i< processorCount; i++)
		{
			Buffer bf = new Buffer();
			leftChannelsList.add(bf);
		}
		
		rightChannelsList = new ArrayList<Buffer>();
		for(int i=0; i< processorCount; i++)
		{
			Buffer bf = new Buffer();
			rightChannelsList.add(bf);
		}
	}
}