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
	List<Integer> traversalList;

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
		int max = m.recursive(m.processorList.get(0));
		
		System.out.println("Max. value in tree = " + max);
		System.out.println("Traversal List - " + m.traversalList);		
	}

	/**
	 * Tree structure and processors initialization
	 */
	public void init(){

		processorList = new ArrayList<>();
		traversalList = new ArrayList<>();

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
				p.children.add(processorList.get(childProcessorId));
				childProcessorId = sc.nextInt();
			}		
		}
		// Closer scanner
		sc.close();
	}
	
	/**
	 * Recursive function that returns max processor val of that subtree
	 * @param p
	 * @return max. processor value in the tree
	 */
	public int recursive(Processor p)
	{		
		Iterator<Processor> itr = p.children.iterator();

		int pValue;
		while(itr.hasNext())
		{
			pValue = recursive(itr.next());
			if(p.maxValue < pValue)
			{
				p.maxValue = pValue;
			}
		}
		traversalList.add(p.value);
		return p.maxValue;
	}
}