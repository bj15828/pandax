package com.gdrc.panda.util;

public class UUID {

	
	public static String newUUID(){
		
		return java.util.UUID.randomUUID().toString().replace("-", "");
		
		 
	}
}
