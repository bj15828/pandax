package com.gdrc.panda.event.inner;

import com.gdrc.panda.event.Event;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventProducer;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicGroup;

public class InnerEventProducer implements EventProducer{

 
  EventBus bus;
  
  public InnerEventProducer(EventBus bus){
    
    this.bus = bus;
    
  }
  
  
  
 

 









  @Override
  public void produce(TopicGroup group, Topic t, Event e) {


    bus.notifyEvent(group,t,e);
    
  }

  

}
