package com.gdrc.pandax;

import java.util.Random;

import com.gdrc.panda.command.AppendLogResultCommand;
import com.gdrc.panda.handler.AppendCounter;

/**
 * Unit test for simple App.
 */
public class AppendCounterTest2

{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppendCounterTest2(String testName) {

	}

	/**
	 * Rigourous Test :-)
	 */
	public static void main(String[] args) {

		AppendCounter counter = new AppendCounter();

		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					AppendLogResultCommand cmd = new AppendLogResultCommand(new Random().nextInt(20) + "");
					counter.initCmdCounter(cmd, 3);
					
					
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}).start();
		
		

		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					AppendLogResultCommand cmd = new AppendLogResultCommand(new Random().nextInt(20) + "");
					
					if(counter.countLog(cmd)){
						
					
					}

					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					AppendLogResultCommand cmd = new AppendLogResultCommand(new Random().nextInt(20) + "");
					
					if(counter.countLog(cmd)){
						
						System.out.println("");
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}).start();

		

	}
}
