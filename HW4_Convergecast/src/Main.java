import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Convergecast algorithm
 */
public class Main {

	// List of processors in the tree
	List<Processor> processorList;

	/**
	 * Default Constructor
	 */
	public  Main(){

		init();
	}

	/**
	 * Entry point function
	 * @param args
	 */
	public static void main ( String args[]){

		Main m = new Main();

		// Starts the algorithm
		m.start();
	}

	/**
	 * Tree structure and processors initialization
	 */
	public void init(){

		processorList = new ArrayList<>();

		// Scanner for tree details input
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter number of processors - ");
		int processorCount = sc.nextInt();

		// Initialize all processors with id's
		for(int i=0; i<processorCount; i++)
		{
			Processor p = new Processor();
			p.id = i;
			processorList.add(p);
		}

		// Set p0 as root
		processorList.get(0).setParent(processorList.get(0));

		// Set processor value and children
		Iterator<Processor> itr = processorList.iterator();
		int childProcessorId;
		// Iterate over list of processors
		while(itr.hasNext())
		{
			Processor p = itr.next();
			System.out.print("Enter processor value for processor " + p.id + " - ");
			p.value = sc.nextInt();
			p.maxValue = p.value;

			System.out.println("Enter id's of child processors - (End list with -1)");
			childProcessorId = sc.nextInt();
			while(childProcessorId != -1)
			{
				p.children.add(childProcessorId);
				processorList.get(childProcessorId).setParent(p);
				childProcessorId = sc.nextInt();
			}		
			p.pending.addAll(p.children);
		}
		// Closer scanner
		sc.close();
	}

	/**
	 * Starts convergecast algorithm
	 */
	public void start(){

		Iterator<Processor> itr = processorList.iterator();

		// Print input tree details
		System.out.println("Tree Details");
		while(itr.hasNext())
		{
			Processor p = itr.next();
			System.out.println("Processor - " + p.id);
			System.out.println("Value - " + p.value);
			System.out.println("Children - " + p.children);
		}

		// Iterate over list of processors and send max value to parent for each
		itr = processorList.iterator();
		while(itr.hasNext())
		{
			Processor p = itr.next();
			if(p.children.isEmpty())
			{
				p.maxValue = p.value;
				p.parentProcessor.sendMessgeToMyBuffer(Message.VALUE, p);
			}
		}
	}
}