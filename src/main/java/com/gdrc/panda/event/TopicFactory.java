package com.gdrc.panda.event;

import com.gdrc.panda.event.EventConstants.TopicType;

public class TopicFactory {

  
  public static Topic getTopic(TopicType type){
    
    
    return  new Topic ("from factory",type);
    
    
    
   }
}
