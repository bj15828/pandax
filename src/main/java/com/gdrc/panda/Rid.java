package com.gdrc.panda;



/**
 * current term and index
 * */
public class Rid {

	long term = 0;
	long logIndex = 0;

	public Rid(){}
	public Rid(long term ,long logIndex){
		this.term =term
			;
		this.logIndex = logIndex;
	}
	
	public boolean isInit() {
		return term == 0 && logIndex == 0 ? true : false;
	}

	public long getTerm() {
		return term;
	}

	public void setTerm(long term) {
		this.term = term;
	}

	public long getLogIndex() {
		return logIndex;
	}

	public void setLogIndex(long logIndex) {
		this.logIndex = logIndex;
	}

	/**
	 * term and index is equal to rid ,return true
	 */
	public boolean equals(Rid rid) {

		return (rid.getTerm() == this.term && rid.getLogIndex() == this.logIndex) ? true : false;
	}

	/**
	 * term and index is equal to rid ,return true
	 */
	public boolean equals(long term, long logIndex) {

		return (term == this.term && logIndex == this.logIndex) ? true : false;
	}

	/**
	 * in is the Object next logIndex
	 */
	public boolean isNextIndex(long term, long logIndex) {
		
		
		
		return (term == this.term && logIndex == this.logIndex + 1) ? true : false;
	}
	
	

}
