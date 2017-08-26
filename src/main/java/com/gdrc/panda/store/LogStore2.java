package com.gdrc.panda.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.EventProducer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Rid;
import com.gdrc.panda.Stoppable;
import com.gdrc.panda.command.AppendLogCommand;
import com.gdrc.panda.command.CommitLogCommand;
import com.gdrc.panda.command.TICommand;
import com.gdrc.panda.util.RefUtil;
import com.gdrc.panda.util.TimeUtil;

/**
 * term ,index
 * 
 */
public class LogStore2 implements Stoppable, EventProducer {

  Logger logger = LoggerFactory.getLogger(LogStore2.class);

  int CACHE_ENTRY_LIVE_CYCLE = 3 * 1000;

  private Map<String, CacheEntry> logCache;

  private Map<String, CacheEntry> termIndexCache;



  Thread appendWriter = new Thread(new Writer());

  List<Consumer> listeners;// complete log store



  ILogStorage store;

  CorePeer corePeer;

  Panda panda;

  volatile boolean isStop = false;

  volatile Rid rid;

  ReentrantLock ridLock;

  public LogStore2(Panda panda, CorePeer cp) throws PandaException {
    this.corePeer = cp;
    this.panda = panda;
    rid = new Rid();

    logCache = new ConcurrentHashMap();
    termIndexCache = new ConcurrentHashMap();
    listeners = new ArrayList<>();
    ridLock = new ReentrantLock();

    logInit();

  }

  private void logInit() throws PandaException {

    Config cfg = panda.getConfig();

    String pre = cfg.get("log_storage.impl");

    String storeCls = cfg.get(pre + ".class");

    Object[] args = new Object[2];
    args[0] = corePeer;
    args[1] = panda.getConfig();

    try {
      store = (ILogStorage) RefUtil.newInstance(storeCls, args);
    } catch (Exception e) {
      throw new PandaException(e);
    }

  }

  public Rid getLastLogRidFromStore() throws PandaException {

    if (!this.rid.isInit())
      return this.rid;
    else {

      LogEntry lastLogEntry = this.store.getLastLog();
      if (null == lastLogEntry)
        return rid;
      this.setRid(lastLogEntry.getTerm(), lastLogEntry.getLastIndex());

      return rid;
    }

  }

  public boolean hasContainTermAndIndex(long term, long index) throws PandaException {

    Rid rid = new Rid(term, index);
    if (rid.isInit())
      return true;


    LogEntry log = new LogEntry();
    log.setTerm(term);
    log.setLastIndex(index);

    // is contain in Cache

    if (null != logCache.get(log.getPk()))
      return true;
    else {


      // is contain in Store

      return this.store.isContain(term, index);
    }


  }

  public boolean appendCommand(AppendLogCommand cmd) throws PandaException {

    return appendLog(this.apdCmd2entry(cmd));


  }

  public boolean appendLog(LogEntry log) throws PandaException {

    if (isStop)
      return false;

    // if size > 10,000,000 then return false;

    CacheEntry ce = new CacheEntry();
    ce.data = log;
    ce.dueTime = TimeUtil.currentTimeMillis();

    logCache.put(log.getPk(), ce);

    setRid(ce.data.getTerm(), ce.data.getLastIndex());// set
    // Rid

    return true;
  }

  private void setRid(long term, long logIndex) throws PandaException {

    ridLock.lock();

    rid.setLogIndex(logIndex);
    rid.setTerm(term);

    ridLock.unlock();

  }


  public boolean commitLog(String uuid, long term, long logIndex) throws PandaException {



    LogEntry log = new LogEntry();
    log.setTerm(term);
    log.setLastIndex(logIndex);

    return commitLog(log);
  }



  public boolean commitLog(LogEntry log) throws PandaException {

    // find in the cache

    if (isStop)
      return false;

    CacheEntry cacheE = this.logCache.get(log.getPk());
    if (null != cacheE) {

      cacheE.data.setHasCommit(true);

      logCache.put(cacheE.data.getPk(), cacheE);
      // logCache.remove(cacheE.data.getPk());

      // logger.info("commit {}", logCache.size());

    }

    // if not in cache ,finde in ILogStoreage

    return false;
  }

  @Override
  public void start() throws PandaException {

    //
    // Get configs
    Config cfg = panda.getConfig();

    String pre = cfg.get("log_storage.impl");

    String storeCls = cfg.get(pre + ".class");

    Object[] args = new Object[2];
    args[0] = this.corePeer;
    args[1] = panda.getConfig();

    try {
      store = (ILogStorage) RefUtil.newInstance(storeCls, args);

    } catch (Exception e) {
      throw new PandaException(e);
    }

    // load last LogEntry into cache

    appendWriter.start();
    // cleaner.start();

  }

  @Override
  public boolean stop() throws PandaException {
    appendWriter.interrupt();

    return false;
  }

  class CacheEntry {

    long dueTime;
    LogEntry data;

  }

  class WriterWatch implements Runnable {

    @Override
    public void run() {

      while (true && isStop) {

      }

    }

  }

  class Writer implements Runnable {

    @Override
    public void run() {

      while (true && !isStop)
        for (Iterator it = logCache.keySet().iterator(); it.hasNext();) {

          CacheEntry log = logCache.get(it.next());

          try {

            if (log.data.hasCommit()) {

              logger.info("save {}", log.data.getPk());
              if (save(log.data)) {



                logCache.remove(log.data.getPk());

                logger.info("remove {}", log.data.getPk());


                notifyListener(entry2apdCmd(log.data));
              }

            }

          } catch (PandaException e) {

            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
          }

        }
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

  }

  public boolean save(LogEntry log) throws PandaException {

    store.appendLog(log);
    return true;

  }

  @Override
  public void notifyListener(Object t) {

    listeners.forEach(l -> l.accept(t));

  }

  @Override
  public void registerEvent(Consumer listener) {

    listeners.add(listener);

  }

  private AppendLogCommand entry2apdCmd(LogEntry log) {

    AppendLogCommand cmd = new AppendLogCommand(log.getUuid());

    cmd.setData(log.getData());

    cmd.setLastIndex(log.getLastIndex());
    cmd.setPeer(log.getPeer());
    cmd.setPreLogIndex(log.getPreLogIndex());
    cmd.setPreLogTerm(log.getPreLogTerm());
    cmd.setTerm(log.getTerm());



    return cmd;
  }

  private LogEntry apdCmd2entry(AppendLogCommand cmd) {

    LogEntry log = new LogEntry();
    log.setData(cmd.getData());
    log.setHasCommit(false);
    log.setLastIndex(cmd.getLastIndex());
    log.setTerm(cmd.getTerm());
    log.setPeer(this.corePeer.getPeerName());
    log.setPreLogIndex(cmd.getPreLogIndex());
    log.setPreLogTerm(cmd.getPreLogTerm());
    log.setUuid(cmd.getUuid());


    return log;
  }

  private LogEntry apdCmd2entry(TICommand cmd) {

    LogEntry log = new LogEntry();

    log.setHasCommit(false);
    log.setLastIndex(cmd.getLastIndex());
    log.setTerm(cmd.getTerm());

    log.setUuid(cmd.getUuid());


    return log;
  }

}
