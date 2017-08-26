package com.gdrc.panda.dispatcher;

import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stoppable;
import com.gdrc.panda.command.Command;

public abstract class InDispatcher implements Stoppable {

	public abstract void dispatch(Command cmd)  throws PandaException;

	public abstract void addMember(MemberPeer peer) throws PandaException;
	

	
	
	

}
