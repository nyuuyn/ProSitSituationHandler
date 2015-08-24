package main;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    public static void main(String[] args) {

	/** The thread executor. */
	ExecutorService threadExecutor = Executors
		.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	for (int i = 0; i < 20; i++) {
	    threadExecutor.submit(new Callable<Map<String, String>>() {

		@Override
		public Map<String, String> call() throws Exception {
		    System.out.println(Math.random());
		    return null;
		}
	    });
	}
	
	threadExecutor.shutdown();

    }
}
