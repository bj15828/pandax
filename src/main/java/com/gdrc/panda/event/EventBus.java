package com.gdrc.panda.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.gdrc.panda.PandaException;
import com.gdrc.panda.event.EventConstants.TopicType;
import com.gdrc.panda.util.RefUtil;

public class EventBus {

  static EventBus factory;

  Map<TopicType, EventConsumer> consumerMap;
  Map<TopicType, EventConsumer> producerMap;

  Map<TopicType, List<EventConsumer>> topicConsumerMap;// ever topic every list

  Map<TopicType, List<EventProducer>> topicProducerMap;// ever topic every list


  Class consumerClass;

  Class producerClass;

  Map<TopicType, TopicConsumerProducerNotify> topicMap;


  private EventBus() {

    topicMap = new ConcurrentHashMap();


  }



  public EventConsumer getConsumer() throws PandaException {



    Object[] args = new Object[1];
    args[0] = factory;

    try {
      return (EventConsumer) RefUtil.newInstance(consumerClass, args);
    } catch (Exception e) {

      e.printStackTrace();
      throw new PandaException(e);
    }
  }



  public void regsiterTopic(TopicGroup group, EventConsumer c, Topic topic, Consumer cb) {

    TopicConsumerProducerNotify l = topicMap.get(topic.getType());
    if (l == null) {
      l = new TopicConsumerProducerNotify(topic);


    }
    ConsumerCallBack cc = new ConsumerCallBack(group, c, cb);

    l.consumers.add(cc);
    topicMap.put(topic.getType(), l);

  }



  public static EventConsumer getConsumer0() throws PandaException {

    return factory.getConsumer();
  }

  public static EventProducer getProducer0() throws PandaException {

    return factory.getProducer();
  }


  public EventProducer getProducer() throws PandaException {



    EventProducer p = null;
    Object[] args = new Object[1];
    args[0] = factory;
    try {
      p = (EventProducer) RefUtil.newInstance(producerClass, args);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new PandaException(e);
    }



    return p;

  }



  public void notifyEvent(Topic t, Event e) {


    TopicConsumerProducerNotify tcpn = topicMap.get(t.getType());

    if (null == tcpn)
      return;
    tcpn.consumers.forEach(x -> {
      e.setTopic(t);
      x.callback.accept(e);


    });

  }

  public void notifyEvent(TopicGroup group, Topic t, Event e) {

    
    TopicConsumerProducerNotify tcpn = topicMap.get(t.getType());

    if (null == tcpn)
      return;
    tcpn.consumers.forEach(x -> {
     if (x.group.equals(group)) {
        e.setTopic(t);
        x.callback.accept(e);
      }

    });

  }



  public static synchronized EventBus build() {

    if (factory != null)
      return factory;

    factory = new EventBus();
    return factory;

  }

  public static EventBus loadProducer(Class cls) {

    factory.producerClass = cls;
    return factory;
  }

  public static EventBus loadConsumer(Class cls) {


    factory.consumerClass = cls;
    return factory;
  }

  class TopicConsumerProducerNotify {
    Topic t;
    List<EventProducer> producers;
    List<ConsumerCallBack> consumers;

    public TopicConsumerProducerNotify(Topic t) {
      this.t = t;
      this.producers = new ArrayList();
      this.consumers = new ArrayList();
    }


  }

  class ConsumerCallBack {
    EventConsumer consumer;
    Consumer callback;
    TopicGroup group;

    public ConsumerCallBack(TopicGroup group, EventConsumer consumer, Consumer callback) {
      this.group = group;
      this.consumer = consumer;
      this.callback = callback;
    }


  }



}
