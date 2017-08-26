package com.gdrc.pandax.event;

import com.gdrc.panda.event.Event;
import com.gdrc.panda.event.EventBus;
import com.gdrc.panda.event.EventConstants;
import com.gdrc.panda.event.EventConsumer;
import com.gdrc.panda.event.EventProducer;
import com.gdrc.panda.event.Topic;
import com.gdrc.panda.event.TopicGroup;
import com.gdrc.panda.event.inner.InnerEventConsumer;
import com.gdrc.panda.event.inner.InnerEventProducer;
import com.gdrc.panda.util.UUID;

public class EventTest {



  public static void main(String[] args) {

    EventBus bus = EventBus.build().
    loadConsumer(InnerEventConsumer.class).loadProducer(InnerEventProducer.class);

    try {
      EventProducer p = bus.getProducer();
      EventProducer p1 = bus.getProducer();
      EventConsumer c = bus.getConsumer();
      EventConsumer c1 = bus.getConsumer();
      
      

      Event e = new Event(UUID.newUUID());

      Topic t1 = new Topic("t1",EventConstants.TopicType.ADD_NEW_MEMBER);
      
      Topic t2 = new Topic("t2",EventConstants.TopicType.ADD_NEW_OBSERVER);

      
      
      c.registerEventHandler(TopicGroup.newGroup("1"),t1, x -> {
          
        System.out.println("c");
        
      });
      c1.registerEventHandler(TopicGroup.newGroup("12"),t2, x -> {
        
        System.out.println("c2");
        
      });




      new Thread(new Runnable() {

        TopicGroup group = TopicGroup.newGroup("1");
        TopicGroup group1 = TopicGroup.newGroup("12");
        @Override
        public void run() {
          while (true) {
            p.produce(group, t1 , e);
            p.produce(group1, t2 , e);
            try {
              Thread.sleep(10);
            } catch (InterruptedException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        }


      }).start();

      


    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }



  }
}
