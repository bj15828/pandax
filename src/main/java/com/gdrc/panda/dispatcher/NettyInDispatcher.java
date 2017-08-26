package com.gdrc.panda.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stoppable;
import com.gdrc.panda.command.Command;
import com.gdrc.panda.command.TICommand;
import com.gdrc.panda.netty.NettyClient;
import com.gdrc.panda.netty.NettyServer;
import com.gdrc.panda.netty.NettyServerHandler;

public class NettyInDispatcher extends InDispatcher implements Stoppable {

	
	final  Logger logger = LoggerFactory.getLogger(NettyInDispatcher.class);

	
	NettyServer clientServer;// for client connect
	NettyServer peerServer;// for peer connect

	CorePeer corePeer;

	public NettyInDispatcher(CorePeer peer) {
		this.corePeer = peer;

		NettyServerHandler handler = new NettyServerHandler(this);

		this.peerServer = new NettyServer(corePeer.getSvrPort(), handler);

		this.clientServer = new NettyServer(corePeer.getCltPort(), handler);
		
		
		
	}

	@Override
	public void dispatch(Command cmd) throws PandaException {
		
		if(Command.Type.VOTE == cmd.getType()){
			
			corePeer.getVoteHandler().handlerVote(cmd);
		}
		if(Command.Type.VOTE_RESULT == cmd.getType()){
          
          corePeer.getVoteHandler().handlerVoteResult(cmd);
      }
		if(Command.Type.APPEND_BLOCK == cmd.getType() ){
			
			corePeer.getMemberAppendHandler().appendLog(cmd);
			
		}
		if(Command.Type.APPEND_BLOCK_RESULT == cmd.getType() ){
			
			corePeer.getMemberAppendHandler().appendLogResult(cmd);
		}
		if(Command.Type.COMMIT_BLOCK == cmd.getType() ){
          
          corePeer.getMemberAppendHandler().commitLog((TICommand) cmd);
      }
		
				
		
	}

	@Override
	public void start() throws PandaException {

		this.peerServer.start();
		this.clientServer.start();
		
	}

	@Override
	public boolean stop() throws PandaException {

		this.clientServer.stop();
		this.peerServer.stop();
		
		return false;
	}

  @Override
  public void addMember(MemberPeer peer) throws PandaException {
    
    
    
  }

}
