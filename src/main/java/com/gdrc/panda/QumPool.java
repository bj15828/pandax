package com.gdrc.panda;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.command.Command;
import com.gdrc.panda.plugin.IKeyGenerator;
import com.gdrc.panda.util.RefUtil;

/**
 * 
 * pool for Command if Command has same key,then put Command to Key queue. if
 * Command key queue is empty ,then handle it righ now.
 */
public class QumPool implements Stoppable {

	Logger logger = LoggerFactory.getLogger(QumPool.class);
	Map<String, Boolean> index;// for key find
	Map<String, Map<String, Command>> keyMap;// for key find

	CorePeer corePeer;

	ExecutorService threadPool;
	CompletionService<Boolean> pool;

	int MAX_QUM_THREAD_POOL_SIZE = 10;

	boolean isStop = false;
	
	
	IKeyGenerator keyGenerator ; //new cmdKey generator

	public QumPool() {
		index = new ConcurrentHashMap();
		keyMap = new ConcurrentHashMap();
		threadPool = Executors.newFixedThreadPool(MAX_QUM_THREAD_POOL_SIZE);
		pool = new ExecutorCompletionService(threadPool);
		
		try {
			keyGenerator = RefUtil.getKeyGenerator();
		} catch (PandaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public QumPool(CorePeer corePeer) {

		index = new ConcurrentHashMap();
		keyMap = new ConcurrentHashMap();
		this.corePeer = corePeer;
	}
	
	
	public void appendLog(Command cmd) throws PandaException{
		
		appendLog(cmd,keyGenerator.key(cmd));
		
	}

	public void appendLog(Command cmd, String cmdKey) throws PandaException {

		if (isStop)
			return;

		Map<String, Command> q = null;
		if (index.get(cmdKey) != null) {

			q = keyMap.get(cmdKey);

		} else {

			q = new ConcurrentHashMap();

		}

		q.put(cmd.getUuid(), cmd);

		keyMap.put(cmdKey, q);
		index.put(cmdKey, false);

		pool.submit(new CatchTask(cmdKey));
		try {
			pool.take().get();
		} catch (InterruptedException | ExecutionException e) {
			
			e.printStackTrace();
			throw new PandaException(e);
		}

	}

	/**
	 * cmdKey is Complete ,if queue is empty ,then remove cmdKey.
	 */
	public void completeKey(Command cmd,String cmdKey) {

		Map q = keyMap.get(cmdKey);
		if (null == q || q.isEmpty()) {

			keyMap.remove(cmdKey);
			index.remove(cmdKey);
		}

	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		for (Iterator<String> it = index.keySet().iterator(); it.hasNext();) {
			String key = it.next();

			sb.append(key);
			if (it.hasNext())
				sb.append("-");
		}
		return sb.toString();
	}

	@Override
	public void start() throws PandaException {
		this.isStop = false;
	}

	@Override
	public boolean stop() throws PandaException {
		this.isStop = true;
		threadPool.shutdown();
		return true;
	}

	class WatchTask implements Runnable{

		@Override
		public void run() {
			while(!isStop ){
				
				
				
				
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
	}
	
	
	class CatchTask implements Callable<Boolean> {
		String cmdKey;
		boolean yes = false;// find not hanled cmdKey

		public CatchTask(String cmdKey) {
			this.cmdKey = cmdKey;
		}

		public Boolean call() {
			System.out.println(Thread.currentThread().getId());

			if (index.get(cmdKey))
				return false;
			else {
				index.put(cmdKey, true);
			}

			
			//
			if (true) {
				keyMap.get(cmdKey).forEach((x, y) -> {

					try {
						
						//corePeer.store.appendCommand(y);

					} catch (Exception e) {
						logger.error(e.getMessage());

						e.printStackTrace();

					} //

				});
				;

			}

			return true;
		}
	}

}
