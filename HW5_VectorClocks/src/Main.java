
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Main{
	Algorithm algo = new Algorithm();
	public static void main(String[] args) {
		Main m = new Main();
		m.start();	
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
		executor.execute(new Runnable() {
		    public void run() {
		       algo.executionPlanForP2();
		    }
		});
		executor.shutdown();
	}

}
