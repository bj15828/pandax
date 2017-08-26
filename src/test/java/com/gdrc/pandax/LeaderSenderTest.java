package com.gdrc.pandax;

import java.io.File;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.command.AppendLogCommand;
import com.gdrc.panda.handler.LeaderToFollowerSender_back;
import com.gdrc.panda.util.UUID;

/**
 * Unit test for simple App.
 */
public class LeaderSenderTest

{
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public LeaderSenderTest(String testName) {

	}

	/**
	 * Rigourous Test :-)
	 * @throws PandaException 
	 */
	public static void main(String[] args) throws PandaException {

	  PropertyConfigurator.configure("config/log4j.properties");
      Config config = new Config(new File("config/default.conf"));

      Panda panda = new Panda(config);

      CorePeer peer = new CorePeer("1","",1,1,1,panda);

      peer.setCltPort(8088);
      peer.setSvrPort(8089);
	  
		LeaderToFollowerSender_back  sender = new LeaderToFollowerSender_back(peer,null);
		sender.start();

		AppendLogCommand []  cmd  = new AppendLogCommand [20];
		
		
		for(int i = 0 ; i < 20 ; i++){
		  
		  cmd[i] = new AppendLogCommand(UUID.newUUID());
		}
		
		sender.sendSyn(cmd);
		
		try {
      Thread.sleep(200);
    } catch (InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
		
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					
				  sender.followerReturnBack(cmd[new Random().nextInt(20)]);
					
					
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}).start();
		
		

	}
}
