package com.gdrc.panda.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.QumPool;
import com.gdrc.panda.Rid;
import com.gdrc.panda.Stats;
import com.gdrc.panda.command.AppendLogCommand;
import com.gdrc.panda.command.AppendLogResultCommand;
import com.gdrc.panda.command.Command;
import com.gdrc.panda.command.CommitLogCommand;
import com.gdrc.panda.command.TICommand;
import com.gdrc.panda.event.Event;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventConstants;
import com.gdrc.panda.event.EventConsumer;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicGroup;

public class MemberAppendHandler extends AbstractHandler {

  EventConsumer eventConsumer ;
  
  
  Logger logger = LoggerFactory.getLogger(MemberAppendHandler.class);

  QumPool pool;


  AppendCounter counter;


  LeaderHandler leaderHandler;
  FollowerHandler followerHandler;


  public MemberAppendHandler(CorePeer peer, Panda panda) throws PandaException {

    super(peer, panda);
    pool = new QumPool(peer);
    
    eventConsumer = EventBus.getConsumer0();

    Topic t = Topic.newTopic(EventConstants.TopicType.ADD_NEW_MEMBER);
    eventConsumer.registerEventHandler(TopicGroup.newGroup(peer.getPeerName()),t, x -> {
      
        addNewMember((Event)x );
      
    });
    

    counter = new AppendCounter();



    corePeer.getStore().registerEvent(x -> {

      AppendLogCommand cmd = (AppendLogCommand) x;


      logStoreAppendSuccess(cmd);
      
      

    });

  }


  /**
   * when runtime ,add new member 
   * */
  private void addNewMember(Event x) {
   
    
    
  }


  /***
   * 
   * Follower return
   */
  public void appendLogResult(Command cmd) throws PandaException {

    AppendLogResultCommand appendLogResultCmd = (AppendLogResultCommand) cmd;


    if (appendLogResultCmd.isSuccess()) {


      // if the agreement from Follower is true
      if (countAppendLogResult(appendLogResultCmd)) {// if more than half of member is agree

        // send commit to all followers
        logger.info("leader {} ,more than half members agree with Term : {} index {}",
            this.corePeer.getLeader().getPeerName(), appendLogResultCmd.getTerm(),
            appendLogResultCmd.getLastIndex());


        CommitLogCommand commit = new CommitLogCommand(appendLogResultCmd.getUuid());

        commit.setTerm(appendLogResultCmd.getTerm());
        commit.setLastIndex(appendLogResultCmd.getLastIndex());



        this.corePeer.getStore().commitLog(commit.getUuid(), commit.getTerm(),
            commit.getLastIndex());

        this.corePeer.getOutDispatcher().dispatch2All(commit);



      }
    } else {
      // Follower return last log Term and index
      // get all cmd from Current Term and Current Index to Follower Last Term and index
      // put them to send Queue

      // judge last Term and index from Follower is Contain?

      Rid rid = new Rid(appendLogResultCmd.getTerm(), appendLogResultCmd.getLastIndex());

      if (rid.getTerm() == corePeer.getCurrentTerm()
          && rid.getLogIndex() == corePeer.getCurrentLogIndex()) {
        // if follower return false and term ,index is current ,then return

        /*
         * logger.warn(
         * "follower [{}] don't agree term [{}]  and  index [{}],please contact Adminstrator! ",
         * appendLogResultCmd.getFromPeer(), rid.getTerm(), rid.getLogIndex());
         */

        return;


      }
      
      

      if (corePeer.getStore().hasContainTermAndIndex(rid.getTerm(), rid.getLogIndex())) {



      } else {// send rid term



      }



    }


  }

  private boolean countAppendLogResult(AppendLogResultCommand cmd) {

    return counter.countLog(cmd);

  }



  public void appendLog(Command cmd) throws PandaException {
    
    
   

    // pool.appendLog(cmd);


    AppendLogCommand appendLogCmd = (AppendLogCommand) cmd;

    
     /* logger.info(
      "{} state {} recieve AppendLogCommand  term :{} index :{} from {},cur Term {} index {}",
      corePeer.getPeerName(), this.corePeer.getStats(), appendLogCmd.getTerm(),
      appendLogCmd.getLastIndex(), appendLogCmd.getFromPeer()
     ,corePeer.getCurrentTerm(),corePeer.getCurrentLogIndex());*/
     
    if (corePeer.getStats() != Stats.LEADER) {// is not leader,find the
                                              // last store Rid
      // if last store Rid is the next Rid from Leader, then store the
      // Command ,
      // if is not , return last store Rid

      Rid localRid = corePeer.getStore().getLastLogRidFromStore();

      Rid inRid = new Rid();


      inRid.setLogIndex(appendLogCmd.getLastIndex());
      inRid.setTerm(appendLogCmd.getTerm());


      if (localRid.isInit()) {


        corePeer.getStore().appendCommand(appendLogCmd);

        AppendLogResultCommand result = new AppendLogResultCommand(cmd.getUuid());

        result.setPeer(corePeer.getPeerName());
        result.setTerm(inRid.getTerm());
        result.setLastIndex(inRid.getLogIndex());
        result.setSuccess(true);

        corePeer.getOutDispatcher().dispatch2One(corePeer.getLeader(), result);


        return;
      }
      
      
      // Reply false if term < currentTerm
      if (inRid.getTerm() > corePeer.getCurrentTerm()) {



        // return current Term and false
        return;

      }
      // Reply false if log doesn’t contain an entry at prevLogIndex
      // whose term matches prevLogTerm
      
      logger.info("judge hasContain Term and Idex");

      if (!corePeer.getStore().hasContainTermAndIndex(appendLogCmd.getPreLogTerm(),
          appendLogCmd.getPreLogIndex())) {



        /// return current term and false
        return;
      }

      if (inRid.getTerm() < localRid.getTerm()) {
        // local term big than in term ,delete Log until in'preTerm and
        // preLogIndex
        // delete

        return;
      }

      // store
      /*
       * logger.info("{} recieve AppendLogCommand  term :{} index :{} from {} ,all check passed",
       * corePeer.getPeerName(), appendLogCmd.getTerm(), appendLogCmd.getLastIndex(),
       * appendLogCmd.getFromPeer());
       */

      corePeer.getStore().appendCommand(appendLogCmd);

      AppendLogResultCommand result = new AppendLogResultCommand(cmd.getUuid());

      result.setPeer(corePeer.getPeerName());
      result.setTerm(inRid.getTerm());
      result.setLastIndex(inRid.getLogIndex());
      result.setSuccess(true);

      // return true
      corePeer.getOutDispatcher().dispatch2One(corePeer.getLeader(), result);



    }


  }

  /**
   * 
   * for LogStore call back anyn
   **/
  public void logStoreAppendSuccess(AppendLogCommand cmd) {



    if (logger.isInfoEnabled())
      logger.info("LogStore call back MemberAppend {}",
          new String(cmd.getTerm() + " " + cmd.getLastIndex()));



    // send to all follower and self



  }


  public void commitLog(String uuid, Rid rid) throws PandaException {


    corePeer.getStore().commitLog(uuid, rid.getTerm(), rid.getLogIndex());

  }

  public void commitLog(TICommand cmd) throws PandaException {



    corePeer.getStore().commitLog(cmd.getUuid(), cmd.getTerm(), cmd.getLastIndex());

  }

  public void updateLog(Command cmd) {}

  public void appendState(Command cmd) {}

  public void appendStateSuccess(Command cmd) {}

  public void updateState(Command cmd) {



  }

  /**
   * client send request Append log
   */
  public void requestAppendLog(AppendLogCommand appendLogCmd) throws PandaException {

    //logger.info("request Append in ,UUID : {}", cmd.getUuid());
    // uuid is handled ?
    
    if(this.counter.size() > 300) return ;

    if (uuidHadRequest(appendLogCmd)) {


      return;
    }
    
   


    counter.initCmdCounter(appendLogCmd, corePeer.getMemberPeers().size());


    corePeer.getStore().appendCommand(appendLogCmd);// leader store

    
    leaderHandler.sendToAllFollowers(appendLogCmd);



  }

  private boolean uuidHadRequest(Command cmd) {

    return false;
  }


 

  @Override
  protected void changeStats(Stats x) {

    if (x == Stats.LEADER) {
      try {
      leaderHandler = new LeaderHandler(corePeer, corePeer.getFollowerPeers());
      
        leaderHandler.start();
      } catch (PandaException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        logger.error(e.getLocalizedMessage());
      }

    } else if (leaderHandler != null) {
      try {
        leaderHandler.stop();
      } catch (PandaException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        logger.error(e.getLocalizedMessage());
      }
      leaderHandler = null;
    }

  }


 
  


}
