package com.chen.consumer_balance.getmessage;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者----拉取模式
 */
public class GetMessageConsumer {
    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = "focuserror";
        channel.queueDeclare(queueName,false,false,false,null);
        String routeKey = "error";
        channel.queueBind(queueName,EXCHANGE_NAME,routeKey);
        System.out.println(" [*] Waiting for messages......");
        //无限循环拉取
        while (true){
            //拉一条，自动确认的(rabbit 认为这条消息消费 -- 从队列中删除)
            GetResponse getResponse = channel.basicGet(queueName,false);
            if(null != getResponse){
                System.out.println("received[" + getResponse.getEnvelope().getRoutingKey() + "]" + new String(getResponse.getBody()));
            }
            //确认（自动，手动）
            channel.basicAck(0,true);
            Thread.sleep(1000);
        }
    }
}
