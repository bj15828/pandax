package com.gdrc.pandax;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTest {

	

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		CompletionService<String> pool = new ExecutorCompletionService<String>(threadPool);

		for (int i = 0; i < 10; i++) {
			pool.submit(new StringTask(i));
		}

		for (int i = 0; i < 10; i++) {
			String result = pool.take().get();
			//System.out.println(result);
			// Compute the result
		}
		
		
		
		
		
		for (int i = 0; i < 10; i++) {
			pool.submit(new StringTask(i));
		}
		for (int i = 0; i < 10; i++) {
			//String result = pool.take().get();
			
			//System.out.println(result);
			// Compute the result
		}

		//threadPool.shutdown();

	}
}
class StringTask implements Callable<String> {
	int i ; 
	public StringTask(int i){
		this.i = i;
	}
	public String call() {
		// Long operations
System.out.println("ss");
		return "Run"+i;
	}
}
