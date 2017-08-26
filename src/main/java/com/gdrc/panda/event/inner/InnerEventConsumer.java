package com.gdrc.panda.event.inner;

import java.util.function.Consumer;

import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventConsumer;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicGroup;

public class InnerEventConsumer implements EventConsumer {



  EventBus bus;

  public InnerEventConsumer(EventBus bus) {

   this.bus  = bus;
  }

  



  @Override
  public void registerEventHandler(TopicGroup group,Topic t,Consumer callback) {
    
    bus.regsiterTopic( group,this, t, callback);

  }




 

}
