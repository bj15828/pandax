package com.gdrc.panda.command;

public class VoteResult extends Command{

	public VoteResult( String uuid) {
		super(Command.Type.VOTE_RESULT, uuid);
		
	}
	
	
	
	
	public long getTerm() {
		return term;
	}
	public void setTerm(long term) {
		this.term = term;
	}
	public boolean isVoteGranted() {
		return voteGranted;
	}
	public void setVoteGranted(boolean voteGranted) {
		this.voteGranted = voteGranted;
	}


	
	public String getCandidateId() {
    return candidateId;
  }




  public void setCandidateId(String candidateId) {
    this.candidateId = candidateId;
  }

  
  



    public boolean isLeaderHadSelected() {
    return leaderHadSelected;
  }




  public void setLeaderHadSelected(boolean leaderHadSelected) {
    this.leaderHadSelected = leaderHadSelected;
  }






    String candidateId;
	long term;//currentTerm ,for candidate to update itself
	boolean voteGranted;//true means candidate received vote
	boolean leaderHadSelected ;//when leader is selected
	
	
}
