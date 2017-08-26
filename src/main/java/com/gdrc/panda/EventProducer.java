package com.gdrc.panda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * public class ,crate new event
 * 
 * @param <T>
 * 
 */
public  interface EventProducer<T> {

	
	
	

	public void notifyListener(T t) ;

	public void registerEvent(Consumer<T> listener);
	
	
	
	

}
