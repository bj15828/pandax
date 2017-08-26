package com.gdrc.pandax;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.spongycastle.util.Arrays.Iterator;


class PoolObject {
  private volatile boolean flag = false;

  public synchronized void setFlag(boolean flag) {

   
      notify();

  }

  public synchronized boolean getFlag() {

  
      try {
        wait();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    return false;
  }


}


class Pool {

  private Map q = new ConcurrentHashMap();


  public int getSize() {
    return q.size();

  }

  public void put(Integer key, PoolObject in) {

    q.put(key, in);



  }
  
  public void delete(Integer key){
    q.remove(key);
  }

  
  public PoolObject get() {

    
    

    return (PoolObject) q.get(new Random().nextInt(50));



  }
  
  public PoolObject get(Integer key) {


    return (PoolObject) q.get(key);



  }

}


class Consumer implements Runnable {

  private Pool pool;

  public Consumer(Pool pool) {
    this.pool = pool;


  }

  @Override
  public void run() {

    
    
    while (true) {

    
      int i = new Random().nextInt(50);
      
      if(pool.get(i) != null)
      {
        System.out.println(Thread.currentThread().getName()+" "+i + " "+pool.get(i).getFlag());
      }
       
      // 
      pool.delete(i);
      

    }

  }


}


class Producer implements Runnable {

  private Pool pool;

  public Producer(Pool pool) {

    this.pool = pool;

  }

  @Override
  public void run() {

    int i = 0;
    while (true) {


      
   
     // System.out.println(Thread.currentThread().getName()+" "+i + " == true");
      
      if(pool.get(i)!= null)
        pool.get(i).setFlag(true);
      
      
      
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      i  = ( i+1 ) % 50;
    }
  }


}


public class WaitTest {

  public static void main(String[] args) {

    Pool pool = new Pool();

    for (int i = 0; i <= 50; i++) {
      ;

      pool.put(i, new PoolObject());

    }

   new Thread(new Consumer(pool)).start();;
   new Thread(new Consumer(pool)).start();;
   new Thread(new Consumer(pool)).start();;
    new Thread(new Producer(pool)).start();
    new Thread(new Producer(pool)).start();
   



  }
}
