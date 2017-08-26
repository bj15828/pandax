/*
 * Copyright 2016, YXP.
 * Copyrights licensed under the New BSD License.
 * See the accompanying LICENSE file for terms.
 */

package com.gdrc.pandax;

import java.util.Random;

import com.gdrc.panda.PandaException;
import com.gdrc.panda.QumPool;
import com.gdrc.panda.command.Command;

public class QumPoolTest {

	
	public static void  main(String [] args) {

		QumPool p = new QumPool();

		
		for (int i = 0; i < 4; i++) {
			
			String hashKey = "hashKey"+i;
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					
					
					String key;
					int i = 0;
					while (true && i<= 20 ) {
						
						 key= "key"+new Random().nextInt(100);
						 
						 Command cmd = new Command(Command.Type.APPEND_BLOCK, key);
						 
						try {
							p.appendLog(cmd, hashKey);
						} catch (PandaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						i++;
					}

				}

			});
			t.start();
			
		}

		Thread t1 = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					
					
				//	System.out.println(p);
					
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
		});
		t1.start();
	}
}
