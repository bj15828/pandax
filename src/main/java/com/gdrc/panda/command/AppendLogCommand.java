package com.gdrc.panda.command;

import com.gdrc.panda.tran.StateObject;

public class AppendLogCommand extends TICommand {

	public AppendLogCommand(String uuid) {
		super(Command.Type.APPEND_BLOCK, uuid);

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

	public long getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(long lastIndex) {
		this.lastIndex = lastIndex;
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

	public String getPeer() {
		return peer;
	}

	public void setPeer(String peer) {
		this.peer = peer;
	}


	private String peer;
	private StateObject data;
	private long term;
	private long preLogIndex;//
	private long preLogTerm;

	private long lastIndex;// last leader commit log index

}
