package com.gdrc.pandax.account;

import java.util.concurrent.locks.ReentrantLock;

import com.gdrc.panda.util.JsonUtil;
import com.gdrc.pandax.annotation.Aggregate;
import com.gdrc.pandax.annotation.ObjectIdentify;

@Aggregate
public class Account {
  
  @ObjectIdentify
  String id;
  
  double balance;
  
  ReentrantLock lock = new ReentrantLock();
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public double getBalance() {
    return balance;
  }
  public void setBalance(double balance) {
    this.balance = balance;
  }
  public void sub(double in){
    
    lock.lock();
    System.out.print("before"+before());
      this.balance = this.balance - in ;
      
    System.out.println("after"+after());
    lock.unlock();
    
  }
  
  public String before(){
    return JsonUtil.object2Json(this);
  }
  
  public String after(){
    
    return JsonUtil.object2Json(this);
  }
  
}
