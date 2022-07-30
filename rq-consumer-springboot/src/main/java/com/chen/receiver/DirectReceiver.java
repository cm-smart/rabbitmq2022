package com.chen.receiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "testDirectQueue")
public class DirectReceiver {

    @RabbitHandler
    public void process(Map testMessage){
        System.out.println("第一-----DirectReceiver消费者收到消息  : " + testMessage.toString());
    }
}
