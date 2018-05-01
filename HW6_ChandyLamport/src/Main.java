
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String args[]) {

		// Channels from P3 to P1 and P2 to P1
		Buffer channelP31 = new Buffer("channelP31");
		Buffer channelP21 = new Buffer("channelP21");

		// Channels from P3 to P2 and P1 to P2
		Buffer channelP32 = new Buffer("channelP32"); // source destination
		Buffer channelP12 = new Buffer("channelP12"); // source Dest

		// Channels from P2 to P3 and P1 to P3
		Buffer channelP23 = new Buffer("channelP23");
		Buffer channelP13 = new Buffer("channelP13");

		List<Buffer> inChannelsP1 = new ArrayList<>();
		inChannelsP1.add(channelP31);
		inChannelsP1.add(channelP21);
		List<Buffer> outChannelsP1 = new ArrayList<>();
		outChannelsP1.add(channelP13);
		outChannelsP1.add(channelP12);
		Processor processor1 = new Processor(1, inChannelsP1, outChannelsP1); 

		List<Buffer> inChannelsP2 = new ArrayList<>();
		inChannelsP2.add(channelP12);
		inChannelsP2.add(channelP32);
		List<Buffer> outChannelsP2 = new ArrayList<>();
		outChannelsP2.add(channelP21);
		outChannelsP2.add(channelP23);
		Processor processor2 = new Processor(2, inChannelsP2, outChannelsP2); 
		
		List<Buffer> inChannelsP3 = new ArrayList<>();
		inChannelsP3.add(channelP13);
		inChannelsP3.add(channelP23);
		List<Buffer> outChannelsP3 = new ArrayList<>();
		outChannelsP3.add(channelP31);
		outChannelsP3.add(channelP32);
		Processor processor3 = new Processor(3, inChannelsP3, outChannelsP3); 
		
		/**
		 * Start execution plans for all 3 processors.
		 */
		Algorithm algo = new Algorithm(processor1, processor2, processor3);

		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {
			public void run() {
				algo.executionPlanForP1();
			}
		});
		executor.execute(new Runnable() {
			public void run() {
				algo.executionPlanForP2();
			}
		});
		executor.execute(new Runnable() {
			public void run() {
				algo.executionPlanForP3();
			}
		});

		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {

		}
	}

}
