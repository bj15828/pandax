package com.gdrc.panda;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 
 * 
 * 
 * */

public class SysClock  {

	final static Logger logger = LoggerFactory.getLogger(SysClock.class);

	List<Consumer<Object>> listeners = new ArrayList<>();

	private Timer timer;

	/**
	 * cycle flag
	 * 
	 */
	boolean flag = true;

	Lock lock = new ReentrantLock();

	private long interval;

	public SysClock() {

	}

	/**
	 * start Timer to count interval if interval is 0 , trigger a new
	 * TimeoutEvent
	 * 
	 */
	public synchronized void start(long interval) {

		this.interval = interval;

		if (timer != null)
			timer.cancel();

		lock.lock();
		timer = new Timer("Panda");

		timer.schedule(new TimeoutHandler(), interval);
		lock.unlock();
	}

	/***
	 * 
	 * 
	 * */
	public synchronized void cancel() {

		flag = false;
		timer.cancel();

	}

	class TimeoutHandler extends TimerTask {

		@Override
		public void run() {

			notifyListener(null);

			if (flag) {

				timer.schedule(new TimeoutHandler(), interval);

			}

		}

	}

	
	public void notifyListener(Object c) {
		listeners.forEach(l -> l.accept(c));

	}

	
	public void registerEvent(Consumer listener) {

		listeners.add(listener);
		
	}

}
