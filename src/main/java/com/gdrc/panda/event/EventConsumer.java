package com.gdrc.panda.event;

import java.util.function.Consumer;

public interface EventConsumer {

  
  
  public void registerEventHandler(TopicGroup group,Topic t,Consumer consumer);
  
  
  
  
}
