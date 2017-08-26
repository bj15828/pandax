package com.gdrc.panda.event;

import com.google.gson.Gson;

public class Event {

  public static enum Type{
      
    PEER_CONNECT_EXCEPTION,
    STORE_EXCEPTION,   
    
    
  };
  
  String seq;
  
  Type type;
  
  Topic topic;
  
  String data;
  
  public Event(String seq){
    
    this.seq = seq;
  }

  public String getSeq() {
    return seq;
  }

  public void setSeq(String seq) {
    this.seq = seq;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Topic getTopic() {
    return topic;
  }

  public void setTopic(Topic topic) {
    this.topic = topic;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
  
  
  
  
}
