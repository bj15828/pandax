package com.gdrc.panda.handler;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stoppable;
import com.gdrc.panda.command.Command;

/**
 * leader send to Follower
 */
public class LeaderToFollowerSender_back implements Stoppable {

  Logger logger = LoggerFactory.getLogger(LeaderToFollowerSender_back.class);

  int MAX_SENDER_THREAD_SIZE = 1;

  Queue<Command> followerSynCmdQueue;// for follower syn

  Queue<Command> followerConsensusCmdQueue;// leader send to follower

  Map<String, WaitAndNotifyObject> sentMap;// the command'key had sent to follower and not return back


  ExecutorService threadPool;
  CompletionService<Boolean> pool;


  boolean stop = false;

  boolean cycle = true;


  MemberPeer whichFollower;

  CorePeer corePeer;


  public LeaderToFollowerSender_back(CorePeer corePeer, MemberPeer whichFollower) {

    this.whichFollower = whichFollower;
    this.corePeer = corePeer;

    threadPool = Executors.newFixedThreadPool(MAX_SENDER_THREAD_SIZE);
    pool = new ExecutorCompletionService<Boolean>(threadPool);
    sentMap = new ConcurrentHashMap();
    followerSynCmdQueue = new ConcurrentLinkedQueue();
    followerConsensusCmdQueue = new ConcurrentLinkedQueue();

  }



  public void sendSyn(Command[] cmds) throws PandaException {

    if (stop)
      return;

    int len = cmds.length;

    for (int i = 0; i < len; i++) {

      if (logger.isInfoEnabled())
        logger.info("add cmd :{}", cmds[i].getUuid());



      putCmdTosentMap(cmds[i]);
      followerSynCmdQueue.add(cmds[i]);


    }

  }

  private void putCmdTosentMap(Command cmd) {
    sentMap.put(cmd.getUuid(), new WaitAndNotifyObject(cmd.getUuid()));
  }


  public void sendConsensus(Command cmd) throws PandaException {
    if (stop)
      return;

    
    putCmdTosentMap(cmd);

  }


  /**
   * receive ReturnBack synchronized , if followerSynCmdMap is not null, send to Follower then send
   * followerConsensusCmdMap to Follower Command which had sent should be deleted from Maps.
   * 
   */
  public void followerReturnBack(Command cmd) {

    // update map and notify thread
    logger.info("in cmd :{} {}", Thread.currentThread().getName(), cmd.getUuid());
    WaitAndNotifyObject o = sentMap.get(cmd.getUuid());

    if (null != o) {
      o.returnBack();

      sentMap.remove(cmd.getUuid());
      
      
      
      
    }


  }



  private void sendCmd(Command cmd) {

    
      logger.info("send cmd:{} {}", Thread.currentThread().getName(), cmd.getUuid());
  }



  @Override
  public void start() throws PandaException {

    for (int i = 0; i < MAX_SENDER_THREAD_SIZE; i++) {

      pool.submit(new GetTask());

    }


  }



  @Override
  public boolean stop() throws PandaException {

    // check queue



    return true;
  }
  
  
  
  


  class GetTask implements Callable<Boolean>

  {



    @Override
    public Boolean call() throws Exception {

      int idleTime = 0;
      while ( true ) {

        if( ! cycle  && followerSynCmdQueue.isEmpty() &&  followerConsensusCmdQueue.isEmpty() )return true;
        


        Command cmd = null;
        if (!followerSynCmdQueue.isEmpty()) {

          cmd = followerSynCmdQueue.poll();

          // send cmd



        } else {

          if (!followerConsensusCmdQueue.isEmpty()) {
            cmd = followerSynCmdQueue.poll();
          }

          else {// all is empty

            Thread.sleep(100 + (idleTime * 100));


            idleTime++;
            continue;
          }

        }

        if (null != cmd) {
          idleTime = 0;



          // send cmd

          sendCmd(cmd);

          // wait return
          sentMap.get(cmd.getUuid()).waitForReturnBack();

        }



      }

      


    }


  }



}
