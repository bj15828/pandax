package com.gdrc.pandax;

import java.io.File;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.store.LogEntry;
import com.gdrc.panda.store.LogStore;

public class LogStoreTestRange {

	public static void main(String[] args) throws InterruptedException, PandaException {

		PropertyConfigurator.configure("config/log4j.properties");
		Config config = new Config(new File("config/default.conf"));

		Panda panda = new Panda(config);

		//CorePeer peer = new CorePeer("1","",1,1,1,panda);
		CorePeer peer = panda.getLocalPeers().get(0);
		
		LogStore store = peer.getStore();
		
		
		
		
		
		try {
			store.start();
			
			
			List<LogEntry> entries = store.getLogEntrysBetween(1, 1, 100, 100);
			System.out.println(entries.size());
			
			
		} catch (PandaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
