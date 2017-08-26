package com.gdrc.pandax;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.Panda;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stats;
import com.gdrc.panda.command.AppendLogCommand;
import com.gdrc.panda.util.UUID;

public class VoteTest {

  static CorePeer core;

  public static void main(String[] args) throws InterruptedException, PandaException {

    PropertyConfigurator.configure("config/log4j.properties");
    Config config = new Config(new File("config/default.conf"));

    Panda panda = new Panda(config);

    panda.start();

    Thread.sleep(2000);

    AppendLogCommand cmd = new AppendLogCommand(UUID.newUUID());

    cmd.setData(null);
    
    

    panda.getLocalPeers().forEach(x -> {

      
      if(x.isLeader())
      {
     
        try {
         
          
          
          new Thread (new Runnable(){

            @Override
            public void run() {
              
              while(true){
                
                AppendLogCommand cmd = new AppendLogCommand(UUID.newUUID());
                try {
                  x.getClientHandler().requestAppendLog(cmd);
                } catch (PandaException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
                
                try {
                  Thread.sleep(30);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            }
            
          }).start();
          
          
          
          new Thread (new Runnable(){

            @Override
            public void run() {
              
              while(true){
                
                AppendLogCommand cmd = new AppendLogCommand(UUID.newUUID());
                try {
                  x.getClientHandler().requestAppendLog(cmd);
                } catch (PandaException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
                
                try {
                  Thread.sleep(30);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            }
            
          }).start();
          
          
          
          
         
          
          
          
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      

    });
    
    
    
    
    
  
    
    



   


  }
}
