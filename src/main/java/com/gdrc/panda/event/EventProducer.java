package com.gdrc.panda.event;

import java.util.function.Consumer;

public interface EventProducer {

 
  //public void register(Topic t);
  //public void produce(Topic t,   Event e);

  public void produce(TopicGroup group,Topic t,   Event e);
  
  
}
