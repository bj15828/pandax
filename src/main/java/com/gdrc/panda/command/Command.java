package com.gdrc.panda.command;

import java.io.Serializable;

public class Command implements Serializable{
	 protected static final long serialVersionUID = 1L;
	 
	 
	 protected String uuid ;
	 protected Command.Type type ;
	 protected String fromPeer;//client or member peer
	 
	 
	 
	 
	 
	
	 public  enum Type   {
		
		 //init
		 PEER_CONNECT,CLIENT_CONNECT,
		 
		 
		 // vote channel
		VOTE,        
		VOTE_RESULT,
		FIND_LEADER,
		FIND_LEARDER_CONFIRM,
		NEW_LEADER,
		NEW_LEADER_CONFIRM,
		HEARTBEAT,
		
		//tran channel
		
		REQUEST_BLOCK, // from follower to leader
		REQUEST_BLOCK_SUCCESS,////send to client
		
		PREPARE_BLOCK,// from leader to follower
		PREPARE_BLOCK_CONFIRM,// agree or disagree
		
		APPEND_BLOCK,//append
		APPEND_BLOCK_RESULT,
		COMMIT_BLOCK,
		
		
		//syn channel
		BLOCK_SYN,
		BLOCK_SYN_ING,
		BLOCK_SYN_COMPLETE,
			
		
		
		
	};
	
	
	
	





	





	public String getFromPeer() {
		return fromPeer;
	}





	public void setFromPeer(String fromPeer) {
		this.fromPeer = fromPeer;
	}





	public Command(Command.Type type,String uuid){
		this.uuid = uuid;
		
		this.type = type;
	}


	


	public String getUuid() {
		return uuid;
	}





	public void setUuid(String uuid) {
		this.uuid = uuid;
	}





	public Command.Type getType() {
		return type;
	}


	public void setType(Command.Type type) {
		this.type = type;
	}

	

	
	
	
}
