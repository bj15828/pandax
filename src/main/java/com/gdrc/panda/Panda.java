package com.gdrc.panda;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.Config.ConfigPeer;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.inner.InnerEventConsumer;
import com.gdrc.panda.event.inner.InnerEventProducer;

public class Panda implements Stoppable {

  final Logger logger = LoggerFactory.getLogger(Panda.class);
  Config config;

  List<Peer> allPeers;


  List<Peer> consensusPeers;

  List<CorePeer> localCorePeers;// local CorePeer
  
  EventBus bus;

  public Panda(Config config) throws PandaException {

    this.config = config;
    this.consensusPeers = new ArrayList();
    this.allPeers = new ArrayList();
    this.localCorePeers = new ArrayList();
    
    bus = EventBus.build().
        loadConsumer(InnerEventConsumer.class).
        loadProducer(InnerEventProducer.class);
    init();

  }

  public List<Peer> getAllPeer() {

    return allPeers;
  }

  public List<CorePeer> getLocalPeers() {
    return this.localCorePeers;
  }

  public List<Peer> getMemberPeers(CorePeer peer) {



    List<Peer> memberPeers = new ArrayList();

    // logger.info(" consensus size :{}", this.consensusPeers.size());
    // logger.info("corePeer :{} get memberpeer",peer.getPeerName());
    this.consensusPeers.forEach(x -> {

      if (!x.getPeerName().equals(peer.getPeerName())) {
        memberPeers.add(x);
      }

    });

    return memberPeers;
  }

  public Peer getPeerByName(String name) {

    for (Iterator<Peer> it = allPeers.iterator(); it.hasNext();) {

      Peer p = it.next();
      if (p.getPeerName().equals(name)) {

        return p;
      }
    }
    return null;

  }

  @Override
  public void start() throws PandaException {

    // start core peer
    for (Iterator<CorePeer> it = this.localCorePeers.iterator(); it.hasNext();) {

      it.next().start();

    }

  }

  private void init() throws PandaException {

    for (Iterator<String> it = this.config.configData.corePeers.keySet().iterator(); it
        .hasNext();) {

      ConfigPeer cfgPeer = config.configData.corePeers.get(it.next());
      MemberPeer peer = new MemberPeer();

      peer.setCltPort(cfgPeer.clientPort);
      peer.setHostIp(cfgPeer.ip);
      peer.setPeerName(cfgPeer.peer);
      peer.setShardId(cfgPeer.shardId);
      peer.setSvrPort(cfgPeer.memberPort);

      this.consensusPeers.add(peer);
    }

    for (Iterator<String> it = this.config.configData.allPeers.keySet().iterator(); it.hasNext();) {

      ConfigPeer cfgPeer = config.configData.allPeers.get(it.next());
      MemberPeer peer = new MemberPeer();

      peer.setCltPort(cfgPeer.clientPort);
      peer.setHostIp(cfgPeer.ip);
      peer.setPeerName(cfgPeer.peer);
      peer.setShardId(cfgPeer.shardId);
      peer.setSvrPort(cfgPeer.memberPort);


      this.allPeers.add(peer);
    }

    // init CorePeer

    for (Iterator<String> it = this.config.configData.localPeers.keySet().iterator(); it
        .hasNext();) {

      ConfigPeer cfgPeer = config.configData.localPeers.get(it.next());
      CorePeer peer = new CorePeer(cfgPeer.peer, cfgPeer.ip, cfgPeer.memberPort, cfgPeer.clientPort,
          cfgPeer.shardId, this);


      this.localCorePeers.add(peer);
    }

  }

  @Override
  public boolean stop() throws PandaException {
    // TODO Auto-generated method stub
    return false;
  }

  public Config getConfig() {

    return this.config;
  }

  public void addMember(MemberPeer mp) {
    
    
    
  }
  public void removeMember(MemberPeer peer){
    
  }
  


}
