package com.chen.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "topic.woman")
public class TopicAllReceiver {
    @RabbitHandler
    public void process(Map message){
        System.out.println("AllTopicManReceiver消费者收到消息  : " + message.toString());
    }
}
