package com.gdrc.panda.event;

import com.gdrc.panda.event.EventConstants.TopicType;

public class Topic {

  String name;
  
  TopicType type;

  
  public static Topic newTopic(String name,TopicType type){
    return new Topic(name,type);
  }
  public static Topic newTopic(TopicType type){
    return new Topic("",type);
  }
  
  public Topic(String name,TopicType type) {
    
    this.name = name;
    this.type = type;
  }

 
  public TopicType getType() {
    return type;
  }

 
  

}
