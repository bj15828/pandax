package com.gdrc.panda.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stats;
import com.gdrc.panda.event.Event;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventConstants;
import com.gdrc.panda.event.EventConsumer;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicFactory;
import com.gdrc.panda.event.TopicGroup;
import com.gdrc.panda.util.JsonUtil;

public abstract class AbstractHandler {


  Logger logger = LoggerFactory.getLogger(AbstractHandler.class);
  CorePeer corePeer;
  Panda panda;

  EventConsumer eventConsumer;

  public AbstractHandler(CorePeer corePeer, Panda panda) throws PandaException {
    this.corePeer = corePeer;
    this.panda = panda;
    
    
   
    eventConsumer= EventBus.getConsumer0();

    eventConsumer.registerEventHandler(TopicGroup.newGroup(this.corePeer.getPeerName()),Topic.newTopic(EventConstants.TopicType.CHANGE_STATE),
        x -> {

          eventChangeState((Event) x);


        });

    
    
    corePeer.registerEvent(x -> {

      if (x instanceof Stats) {

        try {
          changeStats((Stats) x);
        } catch (PandaException e) {

          e.printStackTrace();
          logger.error(e.getMessage());
        }
      }

    });

  }

  private void eventChangeState(Event e) {

    Stats stats = (Stats) JsonUtil.json2Object(e.getData(), Stats.class);

    
    try {
      changeStats(stats);
    } catch (PandaException e1) {
      
      e1.printStackTrace();
      logger.error(e1.getMessage());
    }
  }



  protected abstract void changeStats(Stats x) throws PandaException;

}
