import java.util.*;

/**
 * Created by tphadke on 8/29/17.
 */
public class Main {

	static Map<Processor, List<Processor>> graph;
	Processor p0, p1, p2, p3, p4, p5;

	/**
	 * Initialize graph
	 */
	public Main() {
		init();
	}

	/**
	 * Picks a root processor and sends first message to this processor
	 */
	public static void main(String args[]) {
		Main m = new Main();
		System.out.println("****Input Graph****");
		// Initially graph contains all processors and their unexplored list
		m.printGraph();
		System.out.println("\n");

		//Choose a processor as a Root
		//Send an initial message Message.M to this processor.
		m.p0.sendMessgeToMyBuffer(Message.M, m.p0);

		// Populate the graph with all it's children.
		for (Processor processor : graph.keySet()) {
			graph.put(processor, processor.children);
		}

		System.out.println("\n**** Output Spanning tree****\n");
		m.printGraph();
	}

	/**
	 * TODO: Populate the Graph with processors 0,1,2,3... and their children
	 */
	public void init() {
		graph = new HashMap<Processor, List<Processor>>();

		p0 = new Processor();
		p0.id = 0;
		p1 = new Processor();
		p1.id = 1;
		p2 = new Processor();
		p2.id = 2;
		p3 = new Processor();
		p3.id = 3;
		p4 = new Processor();
		p4.id = 4;
		p5 = new Processor();
		p5.id = 5;

		p0.unexplored = new ArrayList<Processor>(Arrays.asList(p1, p2, p3));
		p1.unexplored = new ArrayList<Processor>(Arrays.asList(p0, p2, p4));
		p2.unexplored = new ArrayList<Processor>(Arrays.asList(p1, p0, p5));
		p3.unexplored = new ArrayList<Processor>(Arrays.asList(p0));
		p4.unexplored = new ArrayList<Processor>(Arrays.asList(p1, p5));
		p5.unexplored = new ArrayList<Processor>(Arrays.asList(p2, p4));

		graph.put(p0, p0.unexplored);
		graph.put(p1, p1.unexplored);
		graph.put(p2, p2.unexplored);
		graph.put(p3, p3.unexplored);
		graph.put(p4, p4.unexplored);
		graph.put(p5, p5.unexplored);

	}

	/**
	 * Method to print the graph/tree. Processor and it's children are printed
	 * in the following way: Processor p0 = [p1 p2 p3 ... ]
	 */
	public void printGraph() {
		for (Processor p : graph.keySet()) {
			System.out.print("Processor p" + p.id + " = [");
			for (Processor child : graph.get(p)) {
				System.out.print("p" + child.id + " ");
			}
			System.out.println("]");
		}

	}
}