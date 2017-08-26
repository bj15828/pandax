package com.gdrc.panda.handler;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stats;
import com.gdrc.panda.command.Command;


/**
 * if corepeer change to leader ,create new LeaderHandler
 * if change to follower ,LeaderHandler should be destroyed.
 * */
public class FollowerHandler extends AbstractHandler{

	public FollowerHandler(CorePeer corePeer, Panda panda) throws PandaException {
		super(corePeer,panda);
	}

	@Override
	protected void changeStats(Stats x) {
		// TODO Auto-generated method stub
		
	}


	public void handler(Command cmd) {
		// TODO Auto-generated method stub
		
	}

}
