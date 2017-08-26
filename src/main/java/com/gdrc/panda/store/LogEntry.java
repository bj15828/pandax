/*
 * Copyright 2016, YXP.
 * Copyrights licensed under the New BSD License.
 * See the accompanying LICENSE file for terms.
 */

package com.gdrc.panda.store;

import com.gdrc.panda.tran.StateObject;

public class LogEntry {

	public String getPeer() {
		return peer;
	}

	public void setPeer(String peer) {
		this.peer = peer;
	}

	public StateObject getData() {
		return data;
	}

	public void setData(StateObject data) {
		this.data = data;
	}

	public long getTerm() {
		return term;
	}

	public void setTerm(long term) {
		this.term = term;
	}

	public long getPreLogIndex() {
		return preLogIndex;
	}

	public void setPreLogIndex(long preLogIndex) {
		this.preLogIndex = preLogIndex;
	}

	public long getPreLogTerm() {
		return preLogTerm;
	}

	public void setPreLogTerm(long preLogTerm) {
		this.preLogTerm = preLogTerm;
	}

	public long getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(long lastIndex) {
		this.lastIndex = lastIndex;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPk() {

		return this.term + "" + this.lastIndex;
	}

	public String getPreLogPk() {

		return this.preLogTerm + "" + this.preLogIndex;
	}

	public int getHasCommit() {
		return hasCommit;
	}

	public void setHasCommit(boolean hasCommit) {
		if (hasCommit)
			this.hasCommit = 1;
		else
			this.hasCommit = 0;
	}
	
	public void setHasCommit(int hasCommit) {
		this.hasCommit=hasCommit;
	}

	public boolean hasCommit() {

		return this.hasCommit == 1 ? true : false;
	}
	

	public boolean isHasSaved() {
    return hasSaved;
  }

  public void setHasSaved(boolean hasSaved) {
    this.hasSaved = hasSaved;
  }


  private String uuid;
	private String peer;
	private StateObject data;
	private long term;
	private long preLogIndex;//
	private long preLogTerm;

	private long lastIndex;// last leader commit log index

	private int hasCommit;
	
	private boolean hasSaved; 

}
