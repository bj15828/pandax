package com.gdrc.panda.tran;

import java.io.Serializable;
import java.util.List;

import com.gdrc.panda.PandaException;

public interface StateObject extends  Serializable {

	
	public String toJson();
	public byte [] toByte();
	public void fromJson(String json);
	public void fromByte(byte [] bt);
	
	
	
	
	public interface Formula{
		
		List doCalc(Object... in) throws PandaException;
	}
	
	
	
	
	
}
