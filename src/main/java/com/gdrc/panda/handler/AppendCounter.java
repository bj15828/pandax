package com.gdrc.panda.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gdrc.panda.command.Command;

/**
 * every time will new Counter
 * count member peer's result
 * if agreement  more than half member peers ,remove it from counterCache,and callback handler   
 * 
 * */
public class AppendCounter {
 
	
	
	Map<String ,Counter> counterMap;
		
	public AppendCounter(){
		counterMap = new ConcurrentHashMap();
	}
	
	
	public void initCmdCounter(Command cmd,int maxMember){
		
	    
		if(counterMap.containsKey(cmd.getUuid()))return ;
		
		Counter c =  new Counter();
		
		c.key = cmd.getUuid();
		c.maxMemberSize = maxMember;
		c.returnSize = 1;
		
		counterMap.put(c.key, c);
		
		
		
	}
	
	/**
	 * if return had completed , Counter will null.
	 * if return is more than maxMemberSize / 2 , return true
	 * 
	 * */
	public boolean  countLog(Command cmd){
	  
	
		
		Counter c = counterMap.get(cmd.getUuid());
		
		if( null != c){
			
			if (  c.returnSize + 1 > c.maxMemberSize  / 2  ){
				
				counterMap.remove(c.key);
				
				return true;
				
			}else{
				
				c.returnSize = c.returnSize + 1;
				
				counterMap.put(c.key, c);
				
				return false;
			}
			
			
		}
		return false;
		
	}
	
	public int size(){
	  return this.counterMap.size();
	}
	
	class Counter{
		String key ;//key 
		int maxMemberSize;//max member size 
		int returnSize;//follower return size
		
		
	}
	
}
