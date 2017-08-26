/*
 * Copyright 2016, YXP.
 * Copyrights licensed under the New BSD License.
 * See the accompanying LICENSE file for terms.
 */

package com.gdrc.panda.tran;

public class State {

	
	String a;
	double money;
	
	public State(){}
	
	public State(String a,double money){
		this.a = a;
		this.money = money;
	}

	public String toString() {

		return new StringBuffer(a).append("-->").append(getMoney()).toString();
	}

	public String getAccount() {
		return this.a;
	}

	public double getMoney() {
		
		
		this.money 
		 = Double.parseDouble(new java.text.DecimalFormat("#.000000").format(this.money));
		return money;
	}

}
