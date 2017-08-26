package com.gdrc.panda.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Rid;
import com.gdrc.panda.Stats;
import com.gdrc.panda.SysClock;
import com.gdrc.panda.command.Command;
import com.gdrc.panda.command.Vote;
import com.gdrc.panda.command.VoteResult;
import com.gdrc.panda.util.UUID;

public class VoteHandler extends AbstractHandler {

  final Logger logger = LoggerFactory.getLogger(VoteHandler.class);

  SysClock voteClock;

  int MAX_CYCLE_Time = 3;// max timeout cycle number ,if big than 3 ,then
                         // writer error log and warning
                         // if CorePeer change stats ,init MAX_CYCLE_TIME to
                         // 1.

  VoteResult hasVoted;

  ReentrantLock votedLock;

  VoteCounter voteCounter;

  public VoteHandler(CorePeer corePeer, Panda panda) throws PandaException {
    super(corePeer, panda);

    voteClock = new SysClock();
    votedLock = new ReentrantLock();

    voteClock.registerEvent(e -> {

      try {
        voteTimeOut(e);
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
        logger.error(e1.getMessage());
      }

    });
    voteCounter = new VoteCounter(corePeer.getMemberPeers().size());


  }


  @Override
  protected void changeStats(Stats x) throws PandaException {


    logger.info("{} change state:{}", this.corePeer.getPeerName(), x);

    // clear vote counter
    voteCounter.clear();
    hasVoted = null;

    if (x == Stats.FOLLOWER || x == Stats.LEADER) {



      this.voteClock.cancel();

      if (x == Stats.LEADER) {

        VoteResult back = new VoteResult(UUID.newUUID());

        back.setTerm(this.corePeer.getCurrentTerm());

        back.setVoteGranted(true);

        back.setCandidateId(this.corePeer.getPeerName());

        back.setLeaderHadSelected(true);

        sendToAll(back);

        logger.info("Leader {} Send Confirm to All ", this.corePeer.getPeerName());



      }



    } else if (x == Stats.CANDIDATER) {



      // send vote for me

      incrCurrentTermAndSend();

      // start clock

      int max = 500;
      int min = 300;

      voteClock.start(new Random().nextInt(max) % (max - min + 1) + min);



    }

  }



  public void handlerVoteResult(Command cmd) throws PandaException {



    if(! this.corePeer.isCandidater() ) return;//
    
    VoteResult vote = (VoteResult) cmd;
    
    

    logger.info(
        "{} receive VoteResult from {} candidateId:{},term is {} ,current term:{}, agree:{} ,isLeader:{}",
        this.corePeer.getPeerName(), vote.getFromPeer(), vote.getCandidateId(), vote.getTerm(),
        this.corePeer.getCurrentTerm(), vote.isVoteGranted(), vote.isLeaderHadSelected());

    if (vote.isLeaderHadSelected() ) {

      setToFollower(vote);

      return;

    }



    if (voteCounter.incrVote(vote)) {// more than half member agree


      // change stat to Follower

      if (corePeer.isSelf(vote.getCandidateId())) {
        
        setToLeader(vote);


      } else {

       // setToFollower(vote);

        logger.info("Leader is {},term is {}", vote.getCandidateId(), vote.getTerm());

      }



      return;

    }


  }



  public void handlerVote(Command cmd) throws PandaException {

    Vote vote = (Vote) cmd;

    if (!this.corePeer.isCandidater()) {

      


      return;
    }



    logger.info("{}  receive vote from {} candidateId:{},term is {} ,current term:{},hasVoted:{}",
        this.corePeer.getPeerName(), vote.getFromPeer(), vote.getCandidateId(), vote.getTerm(),
        this.corePeer.getCurrentTerm(), this.hasVoted);


    if (hasVoted()) {

      if (vote.getTerm() > hasVoted.getTerm()) {
        VoteResult back = new VoteResult(cmd.getUuid());

        back.setTerm(vote.getTerm());

        back.setVoteGranted(true);

        back.setCandidateId(vote.getCandidateId());

        sendToOne(vote.getFromPeer(), back);
        setVoted(back);

        return;
      } else {

        VoteResult back = new VoteResult(cmd.getUuid());

        back.setTerm(vote.getTerm());

        back.setVoteGranted(false);

        back.setCandidateId(hasVoted.getCandidateId());

        sendToOne(vote.getFromPeer(), back);

        return;

      }
    }

    //
    if (vote.getTerm() >= this.corePeer.getCurrentTerm()) {

      VoteResult back = new VoteResult(cmd.getUuid());

      back.setTerm(vote.getTerm());

      back.setVoteGranted(true);

      back.setCandidateId(vote.getCandidateId());

      sendToOne(vote.getFromPeer(), back);

      setVoted(back);
    }

  }

  private void sendToAll(Command cmd) throws PandaException {
    this.corePeer.getOutDispatcher().dispatch2Members(cmd);

  }

  private void sendToOne(String peerName, Command cmd) throws PandaException {
    this.corePeer.getOutDispatcher().dispatch2One(this.panda.getPeerByName(peerName), cmd);

  }


  private void setToFollower(VoteResult cmd) throws PandaException {


    
    corePeer.setLeader(cmd.getCandidateId());
    corePeer.setTerm(cmd.getTerm());
    corePeer.setIndex(0);
    corePeer.changeStats(Stats.FOLLOWER);
    

  }
  private void setToLeader(VoteResult cmd) throws PandaException {


    
    corePeer.setLeader(cmd.getCandidateId());
    corePeer.setTerm(cmd.getTerm());
    corePeer.setIndex(0);
    corePeer.changeStats(Stats.LEADER);

  }


  private void voteTimeOut(Object e) throws PandaException {

    logger.info("Vote time out");
    incrCurrentTermAndSend();


  }

  private void incrCurrentTermAndSend() throws PandaException {
    Vote vote = new Vote(UUID.newUUID());

    Rid rid = this.corePeer.getStore().getLastLogRidFromStore();


    vote.setLastLogIndex(rid.getLogIndex());
    vote.setLastLogTerm(rid.getTerm());

    vote.setCandidateId(this.corePeer.getPeerName());
    vote.setTerm(this.corePeer.incrCurrentTermAndGet());// increase Term


    sendToAll(vote);
  }


  private void setVoted(VoteResult voteResult) {

    votedLock.lock();

    this.hasVoted = voteResult;


    votedLock.unlock();



  }

  private boolean hasVoted() {



    return this.hasVoted != null ? true : false;
  }

  class VoteCounter {

    int candidaterSize = 0;
    Map<String, Integer> voteCounterMap = new HashMap();

    public VoteCounter(int candidaterSize) {
      this.candidaterSize = candidaterSize;
    }


    /**
     * true mean is more than half Member agree Vote
     */
    public boolean incrVote(VoteResult vote) {

      String pk =
          new StringBuffer().append(vote.getCandidateId()).append(vote.getTerm()).toString();

      Integer count = voteCounterMap.get(pk);
      if (null == count) {

        voteCounterMap.put(pk, 1);

        return false;
      } else {

        if (count + 1 >= candidaterSize / 2 + 1) {

          return true;
        } else {

          voteCounterMap.put(pk, count + 1);
          return false;
        }

      }

    }

    public void clear() {
      voteCounterMap.clear();
    }

  }


}
