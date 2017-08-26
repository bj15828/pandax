package com.gdrc.panda.command;

import com.gdrc.panda.tran.StateObject;

public class CommitLogCommand extends TICommand {

	public CommitLogCommand(String uuid) {
		super(Command.Type.COMMIT_BLOCK, uuid);

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

	



	private long term;
	

	private long lastIndex;// last leader commit log index

}
