package com.gdrc.panda.command;

public class Vote extends Command{

	public Vote( String uuid) {
		super(Command.Type.VOTE, uuid);
		
	}
	
	
	
	
	
	public String getCandidateId() {
		return candidateId;
	}
	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	public long getTerm() {
		return term;
	}
	public void setTerm(long term) {
		this.term = term;
	}
	public long getLastLogTerm() {
		return lastLogTerm;
	}
	public void setLastLogTerm(long lastLogTerm) {
		this.lastLogTerm = lastLogTerm;
	}
	public long getLastLogIndex() {
		return lastLogIndex;
	}
	public void setLastLogIndex(long lastLogIndex) {
		this.lastLogIndex = lastLogIndex;
	}
	



	String candidateId;
	long term;
	long lastLogTerm;
	long lastLogIndex;
	
	
	
	
}
