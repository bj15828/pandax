package com.gdrc.pandax;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.command.Vote;
import com.gdrc.panda.dispatcher.NettyInDispatcher;
import com.gdrc.panda.dispatcher.NettyOutDispatcher;
import com.gdrc.panda.util.UUID;

public class InDispatcherTest {

	public static void main(String[] args) throws PandaException {

		PropertyConfigurator.configure("config/log4j.properties");
		
		Config config = new Config(new File("config/default.conf"));

		Panda panda = new Panda(config);

		

		
		
		try {
			panda.start();
			
			
			
			
		} catch (PandaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
