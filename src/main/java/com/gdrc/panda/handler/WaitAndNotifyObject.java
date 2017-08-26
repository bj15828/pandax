package com.gdrc.panda.handler;

import com.gdrc.panda.PandaException;
import com.gdrc.panda.util.TimeUtil;

public class WaitAndNotifyObject {
  
  private String key ;
  private long timestamp;
  
  public WaitAndNotifyObject(String key ){
    
    this.key = key;
    this.timestamp = TimeUtil.currentTimeMillis();
  }
  
  
  
  
  public synchronized void waitForReturnBack() throws PandaException{
    this.timestamp = TimeUtil.currentTimeMillis();
   
    try {
      wait();
    } catch (InterruptedException e) {
      
      
      e.printStackTrace();
      throw  new PandaException(e);
    }
    
  }
  
  public synchronized void returnBack(){
    
    
    
     notify();
  }
  

}
