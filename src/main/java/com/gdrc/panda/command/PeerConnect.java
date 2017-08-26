package com.gdrc.panda.command;

public class PeerConnect extends Command{

	public PeerConnect(String uuid) {
		super(Type.PEER_CONNECT, uuid);
		
		
	}
	
	
	
	public String getPn() {
		return pn;
	}



	public PeerConnect setPn(String pn) {
		this.pn = pn;
		return this;
	}



	String pn;//peer's name
	
	
	
}
