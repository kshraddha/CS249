import java.util.ArrayList;
import java.util.List;

/**
 * Contains processor details and functions
 */
public class Processor{

	// Processor id
	Integer id;
	// Processor value
	Integer value;
	// Max value within subtree with this processor as root
	Integer maxValue;

	// List of child processors
	List<Processor> children;

	/**
	 * Default constructor initializes processor details
	 */
	public Processor() {

		id = Integer.MIN_VALUE; 
		value = Integer.MIN_VALUE; 
		maxValue = Integer.MIN_VALUE;

		children = new ArrayList<>();
	}
}