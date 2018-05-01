import java.util.Arrays;

public class VectorClock implements Comparable<VectorClock>{
	
	int[] vc;
	int index;

	public VectorClock( int noOfProcesses ) {
		vc = new int [noOfProcesses];
	}
	
	/**
	 * Get index of VC, where values will be compared.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Set index of VC.
	 */
	public void setIndex(int compareIndex) {
		this.index = compareIndex;
	}

	/**
	 * This method will compare vector clock value at given index only.
	 * @param VectorClock
	 */
	@Override
	public int compareTo(VectorClock o) {
		return (this.vc[index]-o.vc[index]);
	}

	/**
	 * Based on a event vector clock will be incremented, changed or updated.
	 * Which index should be updated will be decided by a processor
	 * @param index
	 * @param value
	 */
	public void updateAt(int index, int value){
		//TODO : Apply Vector clock algorithm 
		vc[index]= value;
	}
	
	/**
	 * Increment the value by one at given index.
	 * @param index
	 */
	public void incrementByOne(int index){
		vc[index] +=1;
	}

	@Override
	public String toString() {
		return "VectorClock [vc=" + Arrays.toString(vc) + "]";
	}
	
	
	
}
