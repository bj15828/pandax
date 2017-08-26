package com.gdrc.panda;

import com.gdrc.panda.command.Command;

public class MemberPeer extends Peer{

	
    private boolean isAvailable;// if memberpeer is not connect ,isAvailable is false
    
    public MemberPeer(){
      
      this.isAvailable = false;
    }
	
	@Override
	public void handlerCommand(Peer peer, Command cmd) throws PandaException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String toString(){
	  
	  new StringBuffer("name:"+this.peerName).append("ip:").append(this.hostIp).append("serverPort:").append(this.svrPort)
	  .append("clientPort:").append(this.cltPort);
	  return null;
	  
	}

	
	
}
