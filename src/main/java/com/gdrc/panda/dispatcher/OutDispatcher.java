package com.gdrc.panda.dispatcher;

import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Peer;
import com.gdrc.panda.Stoppable;
import com.gdrc.panda.command.Command;

public abstract class OutDispatcher implements Stoppable {

	public abstract void dispatch2All(Command cmd) throws PandaException;

	public abstract void dispatch2Members(Command cmd) throws PandaException;
	
	public abstract void dispatch2Follower(Command cmd) throws PandaException;

	public abstract void dispatch2One(Peer peer, Command cmd) throws PandaException;

	
	
}
