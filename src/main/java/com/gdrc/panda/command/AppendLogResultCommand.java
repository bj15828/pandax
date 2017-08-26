package com.gdrc.panda.command;

import com.gdrc.panda.tran.StateObject;

public class AppendLogResultCommand extends TICommand {

  public AppendLogResultCommand(String uuid) {
    super(Command.Type.APPEND_BLOCK_RESULT, uuid);

  }



  public String getPeer() {
    return peer;
  }

  public void setPeer(String peer) {
    this.peer = peer;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
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



  private String peer;
  private boolean success;

  private long term;

  private long lastIndex;// last follower commit log index

}
