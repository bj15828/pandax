package com.gdrc.panda.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stoppable;
import com.gdrc.panda.command.Command;
import com.gdrc.panda.event.Event;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventConstants;
import com.gdrc.panda.event.EventConsumer;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicGroup;
import com.gdrc.panda.util.JsonUtil;


/*
 * if change to follower ,LeaderHandler should be destroyed.
 */
public class LeaderHandler implements Stoppable{


  Logger logger = LoggerFactory.getLogger(LeaderHandler.class);

  Map<String, LeaderToFollowerSender2> followersMap;

  CorePeer corePeer;

  EventConsumer eventConsumer ;

  public LeaderHandler(CorePeer corePeer, List<MemberPeer> followers) throws PandaException {

    this.corePeer = corePeer;

    followersMap = new ConcurrentHashMap();
    
    eventConsumer = EventBus.getConsumer0();
    
    eventConsumer.registerEventHandler(TopicGroup.newGroup(this.corePeer.getPeerName()),Topic.newTopic(EventConstants.TopicType.ADD_NEW_MEMBER), x -> {
      
      addNewMember((Event)x);
      
    });
    

    followers.forEach(x -> {

      LeaderToFollowerSender2 sender = new LeaderToFollowerSender2(corePeer, x);

      followersMap.put(x.getPeerName(), sender);

    });


  }
  
  
  
  
  private void addNewMember(Event x) {
   
    MemberPeer  peer = (MemberPeer) JsonUtil.json2Object(x.getData(),MemberPeer.class);
    
    LeaderToFollowerSender2 sender = new LeaderToFollowerSender2(corePeer, peer);
    this.followersMap.put(peer.getPeerName(), sender);
    logger.info("add new member from event : {}",peer,x);
    
  }




  public void addFollower(){
    
      
    
  }
  

  public boolean sendToAllFollowers(Command cmd) {
    
    
    followersMap.forEach((x,y) -> {

        try {
          y.sendConsensus(cmd);
        } catch (Exception e) {
         
          e.printStackTrace();
          new PandaException(e);
          logger.error(e.getMessage());
        }

    });
    
   

    return false;
  }
  
  
  public boolean sendToFollower(String peerName,Command cmd){
    
    return false;
    
  }

  public boolean distory() {


    return false;



  }




  @Override
  public void start() throws PandaException {


    followersMap.forEach((x,y) -> {
        
      try {
        y.start();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    });
    
  }




  @Override
  public boolean stop() throws PandaException {
    // TODO Auto-generated method stub
    return false;
  }
}
