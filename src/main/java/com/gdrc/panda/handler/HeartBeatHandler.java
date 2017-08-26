package com.gdrc.panda.handler;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stats;
import com.gdrc.panda.Stoppable;

public class HeartBeatHandler extends AbstractHandler implements Stoppable {

	public HeartBeatHandler(CorePeer corePeer, Panda panda) throws PandaException {
		super(corePeer, panda);
	}

	@Override
	protected void changeStats(Stats x) {
		
		
	}

  @Override
  public void start() throws PandaException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean stop() throws PandaException {
    // TODO Auto-generated method stub
    return false;
  }

	

}
