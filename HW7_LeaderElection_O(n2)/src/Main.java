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
	List<Buffer> channelList;
	// CountDown latches to synchronize threads
	CountDownLatch startSignal;
	CountDownLatch doneSignal; 
	
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

		init();

		try {	
			for(int i=0; i< processorCount; i++)
			{
				new Thread(processorList.get(i)).start();
			}
			startSignal.countDown();
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Processing completed");
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
		doneSignal = new CountDownLatch(processorCount);

		processorList = new ArrayList<Processor>();
		for(int i=0; i< processorCount; i++)
		{
			Processor p = new Processor(startSignal, doneSignal);
			System.out.print("Enter processor identifier for p" + i + " - ");
			p.id = sc.nextInt();
			processorList.add(p);
		}

		sc.close();

		initializeChannels();

		for(int i=0; i< processorCount; i++)
		{
			processorList.get(i).setOutChannel(channelList.get(i));
			if(i==0)
			{
				processorList.get(0).setInChannel(channelList.get(processorCount-1));				
			}else{
				processorList.get(i).setInChannel(channelList.get(i-1));								
			}
		}
	}

	/**
	 * Initializes channels connecting all processors
	 */
	private void initializeChannels()
	{
		channelList = new ArrayList<Buffer>();
		for(int i=0; i< processorCount; i++)
		{
			Buffer bf = new Buffer();
			channelList.add(bf);
		}
	}
}