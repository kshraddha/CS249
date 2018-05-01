
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class Main{
	Algorithm algo = new Algorithm();
	public static void main(String[] args) {
		Main m = new Main();
		m.start();	
		m.algo.findMaxConsistentCut();
	}
	
	public void start(){
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.execute(new Runnable() {
		    public void run() {
		       algo.executionPlanForP0();
		    }
		});
		executor.execute(new Runnable() {
		    public void run() {
		       algo.executionPlanForP1();
		    }
		});
	
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {}
	}

}
