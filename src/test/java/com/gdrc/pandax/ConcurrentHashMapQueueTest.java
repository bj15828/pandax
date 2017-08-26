package com.gdrc.pandax;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentHashMapQueueTest {

  
  public static void main(String [] args){
    
    Map<String ,Queue>      followerMatchIndexMap  = new ConcurrentHashMap<>();
    
    Queue<String> queue = new ConcurrentLinkedQueue<String>();
    
    queue.add("1");
    
    followerMatchIndexMap.put("1", queue);
    
    System.out.println(followerMatchIndexMap.get("1").poll());
    queue.add("2");
    
    
    System.out.println(followerMatchIndexMap.get("1").poll());
    
    
    
    
    
    
    
    
  }
}
