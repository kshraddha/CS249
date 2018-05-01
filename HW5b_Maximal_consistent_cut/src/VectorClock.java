import java.util.Arrays;

public class VectorClock implements Comparable<VectorClock>{
	
	int[] vc;
	int index;

	/**
	 * This method will create a VC with number of processors.
	 * @param noOfProcesses
	 */
	public VectorClock( int noOfProcesses ) {
		vc = new int [noOfProcesses];
	}
	
	/**
	 * This method will create a new VC by cloning the given VC
	 * @param VectorClock v
	 */
	public VectorClock(VectorClock v){
		vc = new int [v.vc.length];
		for (int i = 0; i < v.vc.length; i++) {
			vc[i] = v.vc[i];
		}
	}

	/**
	 * This method will compare two vector clocks.
	 * It will return 0 if both are equal.
	 * It will return -1 if this is smaller.
	 * It will return 1 if this is greater.
	 * It will return 2 if incomparable.
	 * @param VectorClock
	 */
	@Override
	public int compareTo(VectorClock o) {
		boolean smaller = false;
		boolean greater = false;
		for (int i = 0; i < this.vc.length; i++) {
			if (this.vc[i] < o.vc[i]) {
				smaller = true;
			} else if (this.vc[i] > o.vc[i]) {
				greater = true;
			}
		}

		if (smaller && greater) {
			return 2;
		} else if (smaller) {
			return -1;
		} else if (greater) {
			return 1;
		} else {
			return 0;
		}
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
