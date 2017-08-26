package com.gdrc.panda.dispatcher;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Peer;
import com.gdrc.panda.command.Command;
import com.gdrc.panda.event.Event;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventConstants;
import com.gdrc.panda.event.EventConsumer;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicFactory;
import com.gdrc.panda.event.TopicGroup;
import com.gdrc.panda.netty.NettyClient;
import com.gdrc.panda.util.JsonUtil;

public class NettyOutDispatcher extends OutDispatcher  {
	
	
	Logger logger = LoggerFactory.getLogger(NettyOutDispatcher.class);

	Map<MemberPeer, NettyClient> memberClientNettyMap = new ConcurrentHashMap();// all
																			// include
																			// member
																			// peer
																			// and
																			// observer
	ConcurrentLinkedQueue<NotConnectionMember> connectionNotSuccessMembers = new ConcurrentLinkedQueue();
	
	int maxRetryTimes = 5;//thread max retry time 
	
	Thread reconnectThread ;
	
	CorePeer corePeer;
	
	EventConsumer eventConsumer ;
	
	

	public NettyOutDispatcher(CorePeer peer) throws PandaException {

		this.corePeer = peer;
	
		for (Iterator<MemberPeer> it = corePeer.getMemberPeers().iterator(); it.hasNext();) {

			MemberPeer tpeer = it.next();
			
			//logger.info("peer:{}",tpeer);
			
			this.memberClientNettyMap.put(tpeer, new NettyClient(tpeer));

		}
		
		reconnectThread = new Thread(new ReconnectThread());

		eventConsumer = EventBus.getConsumer0();
		eventConsumer.registerEventHandler(TopicGroup.newGroup(this.corePeer.getPeerName()),Topic.newTopic(EventConstants.TopicType.ADD_NEW_MEMBER), x -> {
		  
		  
		  addMember((Event) x );
		  
		});
		eventConsumer.registerEventHandler(TopicGroup.newGroup(this.corePeer.getPeerName()),Topic.newTopic(EventConstants.TopicType.MEMBER_DISCONNECTION), x -> {
          
          
          memberDisconnection((Event) x );
          
        });
		
	}

	
	//member disconnection
  private void memberDisconnection(Event x) {
   
    
    
  }



  private void addMember(Event x) {
    
    //if containe ,return ;
    
    //else add to map
    MemberPeer tpeer = (MemberPeer) JsonUtil.json2Object( x.getData(),MemberPeer.class);
    this.memberClientNettyMap.put(tpeer, new NettyClient(tpeer));
    
  }



  @Override
	public void start() throws PandaException {

		
		
		this.memberClientNettyMap.forEach( (x,y)  -> {
			
			try {
				y.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				NotConnectionMember   m = new NotConnectionMember();
				m.nettyClient = y;
				m.peer = x;
				m.retryCount = 1;
				
				connectionNotSuccessMembers.add(m);
				
				e.printStackTrace();
				logger.error(e.getMessage());
				
				
			}
		});
		
		this.reconnectThread.start();

	}

	@Override
	public boolean stop() throws PandaException {

		for (Iterator it = this.memberClientNettyMap.keySet().iterator(); it.hasNext();) {

			this.memberClientNettyMap.get(it.next()).stop();

		}
		this.reconnectThread.interrupt();

		return true;
	}

	@Override
	public void dispatch2All(Command cmd) throws PandaException {
	  cmd.setFromPeer(this.corePeer.getPeerName());
	  for (Iterator it = this.memberClientNettyMap.keySet().iterator(); it.hasNext();) {

        this.memberClientNettyMap.get(it.next()).send(cmd);
    }

	}

	@Override
	public void dispatch2Members(Command cmd) throws PandaException {
		
	    cmd.setFromPeer(this.corePeer.getPeerName());
		for (Iterator it = this.memberClientNettyMap.keySet().iterator(); it.hasNext();) {

			this.memberClientNettyMap.get(it.next()).send(cmd);
		}

	}

	@Override
	public void dispatch2One(Peer peer, Command cmd) throws PandaException {
	  
	  cmd.setFromPeer(this.corePeer.getPeerName());
	  
	  
	  for (Iterator<MemberPeer> it = this.memberClientNettyMap.keySet().iterator(); it.hasNext();) {
	    MemberPeer p = it.next();
	    if(peer.getPeerName().equals(p.getPeerName()))
            this.memberClientNettyMap.get(p).send(cmd);
    }
	  
	}
	
	class NotConnectionMember 
	{
		MemberPeer peer;
		NettyClient nettyClient;
		int retryCount;
		
		
		
	}
	
	class ReconnectThread implements Runnable{

		@Override
		public void run() {
			
			while(!connectionNotSuccessMembers.isEmpty()){
				
				NotConnectionMember notConnectMember = connectionNotSuccessMembers.poll();
				if(notConnectMember.retryCount    >=  maxRetryTimes) continue;
				notConnectMember.retryCount = notConnectMember.retryCount  + 1;
				
				try {
				  
					notConnectMember.nettyClient.start();
					
					memberClientNettyMap.put(notConnectMember.peer, notConnectMember.nettyClient);
					
				} catch (PandaException e) {
					
				  
				   
					connectionNotSuccessMembers.add(notConnectMember);
					
					
					
					
					
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
		}
		
		
	}

	@Override
	public void dispatch2Follower(Command cmd) throws PandaException {
		// TODO Auto-generated method stub
		
	}

  

}