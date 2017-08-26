
/*
 * Copyright 2015, YXP.
 * Copyrights licensed under the New BSD License.
 * See the accompanying LICENSE file for terms.
 */

package com.gdrc.panda;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class supplies all of the configuration information for the entire raft
 * system. This implementation reads a config file but can be subclassed to pull
 * configuration data from elsewhere.
 */
public class Config implements Stoppable {

	final static Logger logger = LoggerFactory.getLogger(Config.class);

	String file;

	public Config(File file) {

		process(getTypesafeConf(file));

	}

	class ConfigPeer {

		String peer;
		String ip;
		int memberPort;
		int clientPort;
		int shardId;

		ConfigPeer(String peer, String ip, int memberPort, int clientPort, int shardId) {
			this.peer = peer;
			this.ip = ip;
			this.memberPort = memberPort;
			this.clientPort = clientPort;
			this.shardId = shardId;

		}
		
		public int getShardIdt() {
			return shardId;
		}
		public String getPeer() {
			return peer;
		}

		public String getIp() {
			return ip;
		}

		
		

	}

	class ConfigData {

		com.typesafe.config.Config cfg;

		Map<String, ConfigPeer> allPeers = new HashMap<String,ConfigPeer>();

		Map<String, ConfigPeer> corePeers = new HashMap<String,ConfigPeer>();

		Map<String, ConfigPeer> obseverPeers = new HashMap<String,ConfigPeer>();
		
		Map<String ,ConfigPeer> localPeers = new HashMap<String,ConfigPeer>();

	}

	ConfigData configData;

	public boolean getBoolean(String property) {

		return configData.cfg.getBoolean(property);
	}

	public List<String> getList(String path) {
		return configData.cfg.getList(path).stream().map(configValue -> String.valueOf(configValue.unwrapped()))
				.collect(Collectors.toList());
	}

	private void process(com.typesafe.config.Config cfg) {

		ConfigData cd = new ConfigData();
		cd.cfg = cfg;

		// Parse the shardid, hostid, and memberid information
		for (com.typesafe.config.Config v : cfg.getConfigList("panda.peers")) {
			ConfigPeer cm = new ConfigPeer(v.getString("peername"), v.getString("ip"), v.getInt("memberport"),
					v.getInt("clientport"),v.getInt("shardId"));
			
			//logger.info("config peer :{},{},{},{}",v.getString("peername"),v.getString("ip"),v.getInt("memberport"),v.getInt("clientport"));
			// update host to members
			ConfigPeer cmembers = (ConfigPeer) cd.allPeers.get(cm.getPeer());
			
			if (cmembers == null) {

				cd.allPeers.put(cm.getPeer(), cm);
				
			}
		}
		
		

		for (String v : cfg.getStringList("panda.core_peers")) {
		
			if (null != cd.allPeers.get(v)) {
				
				ConfigPeer cm = (ConfigPeer) cd.allPeers.get(v);
				// update host to members
				
				ConfigPeer cmembers = cd.corePeers.get(cm.getPeer());
				if (cmembers == null) {

					cd.corePeers.put(cm.getPeer(), cm);

				}
			}
		}
		
		for (String v : cfg.getStringList("panda.observer_peers")) {
			if (null != cd.allPeers.get(v)) {

				ConfigPeer cm = (ConfigPeer) cd.allPeers.get(v);
				// update host to members
				ConfigPeer cmembers = cd.obseverPeers.get(cm.getPeer());
				if (cmembers == null) {

					cd.obseverPeers.put(cm.getPeer(), cm);

				}
			}
		}
		//local peer
		
		for (String v : cfg.getStringList("panda.local_peers")) {
			
			
			if (null != cd.allPeers.get(v)) {
				
				ConfigPeer cm = (ConfigPeer) cd.allPeers.get(v);
				
				ConfigPeer cmembers = cd.localPeers.get(cm.getPeer());
				if (cmembers == null) {//not in localPeers
					
					cd.localPeers.put(cm.getPeer(), cm);

				}
			}
		}
		
		configData= cd;
		

	}

	/*
	 * Returns the config file merged with the settings in the default.conf
	 * file.
	 */
	com.typesafe.config.Config getTypesafeConf(File file) {
		com.typesafe.config.Config cfg = com.typesafe.config.ConfigFactory.parseFile(file).resolve();

		/*InputStream resourceStream = Panda.class.getClassLoader().getResourceAsStream("default.conf");
		if (resourceStream == null) {
			throw new IllegalStateException("default.conf not found");
		}
		com.typesafe.config.Config defaultCfg = com.typesafe.config.ConfigFactory
				.parseReader(new InputStreamReader(resourceStream)).resolve();*/
		return cfg.withFallback(cfg);
	}

	@Override
	public void start() throws PandaException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean stop() throws PandaException {

		return false;
	}

	public int getInt(String string) {
		return configData.cfg.getInt(string);
	}

	public String get(String string) {
		return configData.cfg.getString(string);
	}

	public boolean has(String urlKey) {
		return configData.cfg.hasPath(urlKey);
	}

}
