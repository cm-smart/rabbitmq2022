package com.chen.msgdurable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息的持久化生产者
 */
public class MsgAttrProducer {
    public final static String EXCHANGE_NAME = "msg_durable";

    public static void main(String[] args) throws IOException, TimeoutException {
        /* 创建连接,连接到RabbitMQ*/
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();

        /*创建信道*/
        Channel channel = connection.createChannel();
        /*创建持久化交换器*/
        channel.exchangeDeclare(EXCHANGE_NAME,"direct",true);

        String[] routekeys = {"king","mark","james"};

        for(int i = 0;i < 3;i++){
            String routeKey = routekeys[i%3];
            String msg = "Hellol,RabbitMq"+(i+1);
            //发布持久化消息
            channel.basicPublish(EXCHANGE_NAME,routeKey, MessageProperties.PERSISTENT_TEXT_PLAIN,msg.getBytes());
            System.out.println("sent:[" + routeKey + "]" + msg);
        }

        channel.close();
        connection.close();
    }
}
