package com.gdrc.pandax;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.store.LogEntry;
import com.gdrc.panda.store.LogStore;
import com.gdrc.panda.util.UUID;

public class LogStoreTest {

	public static void main(String[] args) throws InterruptedException, PandaException {

		PropertyConfigurator.configure("config/log4j.properties");
		Config config = new Config(new File("config/default.conf"));

		Panda panda = new Panda(config);

		//CorePeer peer = new CorePeer("1","",1,1,1,panda);
		CorePeer peer = panda.getLocalPeers().get(0);
		
		LogStore store = peer.getStore();

		new Thread(new Runnable() {

			@Override
			public void run() {

				long i = 1;
				while (true ) {
					LogEntry log = new LogEntry();
					log.setUuid(UUID.newUUID());
					log.setPeer(peer.getPeerName());
					log.setTerm(1);
					log.setLastIndex(i);

					try {
						store.appendLog(log);
						
					} catch (PandaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}) {

		}.start();
		
		Thread.sleep(1000);
		new Thread(new Runnable() {

			@Override
			public void run() {

				long i = 1;
				while (true ) {
					LogEntry log = new LogEntry();
					log.setUuid(UUID.newUUID());
					log.setPeer(peer.getPeerName());
					log.setTerm(1);
					log.setLastIndex(i);

					try {
						store.commitLog(log);
						
					} catch (PandaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}) {

		}.start();
		
		
		
		try {
			store.start();
		} catch (PandaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
