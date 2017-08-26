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
import com.gdrc.panda.util.TimeUtil;

/**
 * leader send to Follower
 */
public class LeaderToFollowerSender2 implements Stoppable {

  Logger logger = LoggerFactory.getLogger(LeaderToFollowerSender2.class);

  int MAX_SENDER_THREAD_SIZE = 4;

  Queue<Command> followerSynCmdQueue;// for follower syn

  Queue<Command> followerConsensusCmdQueue;// leader send to follower



  ExecutorService threadPool;
  CompletionService<Boolean> pool;


  boolean stop = false;

  boolean cycle = true;


  MemberPeer whichFollower;

  CorePeer corePeer;


  public LeaderToFollowerSender2(CorePeer corePeer, MemberPeer whichFollower) {

    this.whichFollower = whichFollower;
    this.corePeer = corePeer;

    threadPool = Executors.newFixedThreadPool(MAX_SENDER_THREAD_SIZE);
    pool = new ExecutorCompletionService<Boolean>(threadPool);

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


      followerSynCmdQueue.add(cmds[i]);
      putCmdTosentMap(cmds[i]);



    }

  }

  private void putCmdTosentMap(Command cmd) {


    pool.submit(new GetTask(cmd));

  }


  public void sendConsensus(Command cmd) throws PandaException {

    if (stop)
      return;

    if (this.followerConsensusCmdQueue.offer(cmd)) {

      putCmdTosentMap(cmd);
    }

  }



  private void sendCmd(Command cmd) throws PandaException {


    /*logger.info("{} {} send cmd: {} to {}",this.corePeer.getPeerName(), Thread.currentThread().getName(), cmd.getType(),
        this.whichFollower.getPeerName());*/
    this.corePeer.getOutDispatcher().dispatch2One(this.whichFollower, cmd);
  }



  @Override
  public void start() throws PandaException {



  }



  @Override
  public boolean stop() throws PandaException {

    // check queue


    threadPool.shutdown();
    return true;
  }



  class GetTask implements Callable<Boolean>

  {

    long timeStamp;


    public GetTask(Command cmd) {

      this.timeStamp = TimeUtil.currentTimeMillis();


    }

    @Override
    public Boolean call() throws Exception {



      if (!cycle)
        return true;


      Command cmd = null;
      if (!followerSynCmdQueue.isEmpty()) {

        cmd = followerSynCmdQueue.poll();

        // send cmd



      } else

      if (!followerConsensusCmdQueue.isEmpty()) {
        cmd = followerConsensusCmdQueue.poll();
      }



      if (null != cmd) {
        // send cmd

        sendCmd(cmd);



      }
      return true;



    }


  }



}
