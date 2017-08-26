package com.gdrc.pandax;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.store.LogEntry;
import com.gdrc.panda.store.LogStore;

public class StorageTest {

	public static void main(String[] args) throws InterruptedException, PandaException {

		PropertyConfigurator.configure("config/log4j.properties");
		Config config = new Config(new File("config/default.conf"));

		Panda panda = new Panda(config);

		CorePeer peer = new CorePeer("1","",1,1,1,panda);

		peer.setCltPort(8088);
		peer.setSvrPort(8089);
		LogStore store = new LogStore(panda,peer);
		
		
		try {
			
			store.start();
			store.getLastLogRidFromStore();
		} catch (PandaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
