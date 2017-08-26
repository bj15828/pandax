package com.gdrc.pandax.account;


public class Test {
  public static void main(String [] args){
    
    Account a = new Account();
    a.setBalance(10000);
    
     new Thread(new Runnable(){

      @Override
      public void run() {
        while(true){
          a.sub(0.5);
          try {
            Thread.sleep(20);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
        }
        
      }
      
      
    }).start();
    
     
     new Thread(new Runnable(){

       @Override
       public void run() {
         while(true){
           a.sub(0.5);
           try {
             Thread.sleep(20);
           } catch (InterruptedException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
           }
           
         }
         
       }
       
       
     }).start();
     
    
  }
}
