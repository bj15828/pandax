package com.gdrc.panda.command;


/**
 * contain Term and index
 * */
public class TICommand extends Command{

  public TICommand(Type type, String uuid) {
    super(type, uuid);
    
  }
  
  
  
  
  public long getTerm() {
    return term;
  }
  public void setTerm(long term) {
    this.term = term;
  }
  public long getLastIndex() {
    return lastIndex;
  }
  public void setLastIndex(long lastIndex) {
    this.lastIndex = lastIndex;
  }




  long term ;
  long lastIndex;
  
	
	 

  

	


	

	
	
	
}
