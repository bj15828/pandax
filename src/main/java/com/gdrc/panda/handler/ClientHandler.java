package com.gdrc.panda.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Rid;
import com.gdrc.panda.command.AppendLogCommand;
import com.gdrc.panda.command.Command;



/**
 * handle Client request Command
 * 
 * */
public class ClientHandler  {
  
    CorePeer corePeer;
    Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	public ClientHandler(CorePeer corePeer, Panda panda) {
	  this.corePeer  = corePeer;
	  
		
	}
	
	

	  /**
	   * client send request Append log
	   */
	  public void requestAppendLog(Command cmd) throws PandaException {

	    //logger.info("request Append in ,UUID : {}", cmd.getUuid());
	    // uuid is handled ?
	    
	    



	    AppendLogCommand appendLogCmd = (AppendLogCommand) cmd;

	    // new term and index

	    Rid[] rids = this.corePeer.inrCurIndexAndGetPre();
	    appendLogCmd.setTerm(rids[0].getTerm());
	    appendLogCmd.setLastIndex(rids[0].getLogIndex());


	    appendLogCmd.setPreLogTerm(rids[1].getTerm());
	    appendLogCmd.setPreLogIndex(rids[1].getLogIndex());

	    
	      logger.info("curTerm :{} index {},pre term {},index {}", rids[0].getTerm(),
	     rids[0].getLogIndex(), rids[1].getTerm(), rids[1].getLogIndex());
	     

	    corePeer.getStore().appendCommand(appendLogCmd);// leader store



	    corePeer.getMemberAppendHandler().requestAppendLog(appendLogCmd);



	  }

	
	

}
