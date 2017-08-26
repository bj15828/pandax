package com.gdrc.panda;

import com.gdrc.panda.command.Command;

public abstract   class Peer {

	
	
	
	protected String peerName;
	protected String hostIp;

	
	protected int cltPort;//client connect port
	
	protected int svrPort;//server port
	
	
	public int getCltPort() {
		return cltPort;
	}
	public void setCltPort(int cltPort) {
		this.cltPort = cltPort;
	}
	public int getSvrPort() {
		return svrPort;
	}
	public void setSvrPort(int svrPort) {
		this.svrPort = svrPort;
	}

	protected int shardId;//consensus id .
	
	protected Stats stats;
	
	
	
	public int getShardId() {
		return shardId;
	}
	public void setShardId(int shardId) {
		this.shardId = shardId;
	}
	public String getPeerName() {
		return peerName;
	}
	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}
	
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	
	public Stats getStats() {
		return stats;
	}
	public void setStats(Stats stats) {
		this.stats = stats;
	}
	
	public String toString(){
		
		return new StringBuffer("peerName:").append(this.peerName).append("ip:").append(this.hostIp)
				.append("memberPort:").append(this.svrPort).append("clientPort:").append(this.cltPort).toString();
	}
	
	
	public boolean equals(Object p){
		
		return ((Peer) p).getPeerName() .equalsIgnoreCase(this.peerName)  ? true: false;
		
	}
	
	public abstract void handlerCommand(Peer peer,Command cmd) throws PandaException;
	
	
}
