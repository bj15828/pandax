package com.gdrc.panda.event;

/**
 * topic group
 * */
public class TopicGroup {
  
  
  
  String name;

  public static TopicGroup newGroup(String name){
    return new TopicGroup(name);
  }
  
  public TopicGroup(String name){
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public boolean equals(TopicGroup p){
    return this.name.equals(p.getName())  ? true : false;
  }
  
  
  
}
