package com.gdrc.panda;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.command.Command;
import com.gdrc.panda.dispatcher.InDispatcher;
import com.gdrc.panda.dispatcher.NettyInDispatcher;
import com.gdrc.panda.dispatcher.NettyOutDispatcher;
import com.gdrc.panda.dispatcher.OutDispatcher;
import com.gdrc.panda.event.Event;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventConstants;
import com.gdrc.panda.event.EventConstants.TopicType;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicFactory;
import com.gdrc.panda.event.TopicGroup;
import com.gdrc.panda.handler.ClientHandler;
import com.gdrc.panda.handler.HeartBeatHandler;
import com.gdrc.panda.handler.MemberAppendHandler;
import com.gdrc.panda.handler.VoteHandler;
import com.gdrc.panda.store.LogStore;
import com.gdrc.panda.util.JsonUtil;
import com.gdrc.panda.util.UUID;

public class CorePeer extends Peer implements Stoppable, EventProducer {

  
  Panda panda;

  List<MemberPeer> memberPeers;// member

  List<MemberPeer> observers;// observer



  OutDispatcher outDispatcher;

  InDispatcher inDispatcher;

  MemberAppendHandler memberAppendHandler;// for Member

  // LeaderAppendHandler leaderAppendHandler; // for Leader

  HeartBeatHandler heartHandler;

  VoteHandler voteHandler;

  ClientHandler clientHandler;

  List<Consumer<Stats>> stateChangeListeners = new ArrayList<>();

  volatile AtomicLong currentTerm;
  volatile AtomicLong currentLogIndex;

  volatile Rid preRid;


  ReentrantLock curAndPreLock;

  LogStore store;


  MemberPeer leader;
  
  com.gdrc.panda.event.EventProducer eventProducer ;

  final Logger logger = LoggerFactory.getLogger(CorePeer.class);
  
  


  public CorePeer(String peerName, String ip, int memberPort, int clientPort, int shardId,
      Panda panda) throws PandaException {

    this.eventProducer = EventBus.getProducer0();
    
    
    this.peerName = peerName;
    this.hostIp = ip;
    this.svrPort = memberPort;
    this.cltPort = clientPort;
    this.shardId = shardId;
    
    curAndPreLock = new ReentrantLock();
    


    this.panda = panda;

    initMemberPeer();
    this.store = new LogStore(panda, this);


    this.outDispatcher = new NettyOutDispatcher(this);
    this.inDispatcher = new NettyInDispatcher(this);
    this.memberAppendHandler = new MemberAppendHandler(this, panda);
    // this.leaderAppendHandler = new LeaderAppendHandler(this, panda);
    this.heartHandler = new HeartBeatHandler(this, panda);
    this.voteHandler = new VoteHandler(this, panda);
    this.clientHandler = new ClientHandler(this, panda);
    // init



    this.inDispatcher.start();

    Rid rid = this.store.getLastLogRidFromStore();

    this.currentLogIndex = new AtomicLong(rid.getLogIndex());
    this.currentTerm = new AtomicLong(rid.getTerm());

    preRid = rid;



  }



  public void changeStats(Stats stats) throws PandaException {

    this.stats = stats;

    
    
    notifyStats2All();

  }

  private void notifyStats2All() {
    
    Event e = new Event(UUID.newUUID());
    
    e.setData(JsonUtil.object2Json(this.stats));
    
    Topic t = Topic.newTopic(EventConstants.TopicType.CHANGE_STATE);
    
    
    eventProducer.produce(TopicGroup.newGroup(this.getPeerName()),t, e);
    
   //this.stateChangeListeners.forEach(x -> x.accept(stats));
  }



  @Override
  public void handlerCommand(Peer peer, Command cmd) throws PandaException {


  }

  private void initMemberPeer() {


    this.memberPeers = new ArrayList();
    List<Peer> listPeers = panda.getMemberPeers(this);



    listPeers.forEach(x -> {

      MemberPeer mp = new MemberPeer();
      mp.setCltPort(x.getCltPort());
      mp.setHostIp(x.getHostIp());
      mp.setPeerName(x.getPeerName());
      mp.setShardId(x.getShardId());
      mp.setStats(x.getStats());
      mp.setSvrPort(x.getSvrPort());



      this.memberPeers.add(mp);
    });

  }

  public List<MemberPeer> getMemberPeers() {

    return this.memberPeers;

  }

  @Override
  public void start() throws PandaException {
    // init all



    // start all
    // this.inDispatcher.start();



    this.store.start();

    this.outDispatcher.start();
    //
    Rid rid = this.store.getLastLogRidFromStore();
    this.currentTerm = new AtomicLong(rid.getTerm());
    this.currentLogIndex = new AtomicLong(rid.getLogIndex());

    // set stat to candidate


    this.changeStats(Stats.CANDIDATER);


  }

  @Override
  public boolean stop() throws PandaException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void notifyListener(Object t) {

    this.stateChangeListeners.forEach(l -> l.accept((Stats) t));

  }

  @Override
  public void registerEvent(Consumer listener) {

    this.stateChangeListeners.add(listener);
  }

  public long incrCurrentTermAndGet() {

    return this.currentTerm.incrementAndGet();
  }

  public long incrCurrentLogIndexAndGet() {


    this.preRid.setLogIndex(this.currentLogIndex.get());

    long l = this.currentLogIndex.incrementAndGet();


    return l;
  }


  public Rid[] inrCurIndexAndGetPre() {

    curAndPreLock.lock();
    Rid[] d = new Rid[2];

    d[1] = new Rid();
    d[1].setTerm(this.preRid.getTerm());
    d[1].setLogIndex(this.preRid.getLogIndex());

    d[0] = new Rid();
    d[0].setTerm(this.currentTerm.get());
    d[0].setLogIndex(this.currentLogIndex.incrementAndGet());

    this.preRid.setTerm(this.currentTerm.get());
    this.preRid.setLogIndex(this.currentLogIndex.get());

    curAndPreLock.unlock();

    return d;
  }


  public long getCurrentTerm() {
    return this.currentTerm.get();
  }

  public long getCurrentLogIndex() {
    return this.currentLogIndex.get();
  }

  public Stats getCurrentStats() {
    return this.stats;
  }

  public MemberAppendHandler getMemberAppendHandler() {
    return memberAppendHandler;
  }



  public HeartBeatHandler getHeartHandler() {
    return heartHandler;
  }

  public VoteHandler getVoteHandler() {
    return voteHandler;
  }

  public ClientHandler getClientHandler() {
    return clientHandler;
  }

  public LogStore getStore() {
    return store;
  }

  public void setStore(LogStore store) {
    this.store = store;
  }

  public OutDispatcher getOutDispatcher() {
    return outDispatcher;
  }

  public void setOutDispatcher(OutDispatcher outDispatcher) {
    this.outDispatcher = outDispatcher;
  }

  public InDispatcher getInDispatcher() {
    return inDispatcher;
  }

  public void setInDispatcher(InDispatcher inDispatcher) {
    this.inDispatcher = inDispatcher;
  }


  public boolean isSelf(String peerName) {

    return (this.peerName.equals(peerName)) ? true : false;

  }

  public void setLeader(String peerName) {


    this.panda.getAllPeer().forEach(x -> {
      if (x.getPeerName().equals(peerName))
        leader = (MemberPeer) x;

    });

    logger.info(this.peerName + " set leader " + leader.getPeerName());

    
  }

  public MemberPeer getLeader() {
    return this.leader;

  }

  public boolean isLeader() {

    // return this.leader != null && this.leader.getPeerName().equals(this.peerName) ? true : false;

    return this.stats == Stats.LEADER ? true : false;
  }

  public boolean isCandidater() {

    return (this.stats == Stats.CANDIDATER) ? true : false;

  }



  public void setTerm(long term) {

    this.currentTerm = new AtomicLong(term);

  }

  public List<MemberPeer> getFollowerPeers() {

    if (null == leader)
      return null;
    List<MemberPeer> follows = new ArrayList();

    this.memberPeers.forEach(x -> {

      if (!x.getPeerName().equals(leader.getPeerName()))
        follows.add(x);


    });

    return follows;

  }



  public void setIndex(int i) {

    this.currentLogIndex = new AtomicLong(0);

  }


  public void addMember(MemberPeer peer) throws PandaException {

    
    this.memberPeers.add(peer);
     Event e = new Event(UUID.newUUID());
     
     e.setData(JsonUtil.object2Json(peer));
     
     Topic t = new Topic("corePeer",TopicType.ADD_NEW_MEMBER);
     
     eventProducer.produce(TopicGroup.newGroup(this.getPeerName()),t, e );
    
  }


}
