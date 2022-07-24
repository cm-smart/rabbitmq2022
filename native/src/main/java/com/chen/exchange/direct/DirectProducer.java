package com.chen.exchange.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * direct类型交换器的生产者
 */
public class DirectProducer {
    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建连接，连接到Rabbitmq
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        //2.创建链接
        Connection connection = connectionFactory.newConnection();
        //3.创建信道
        Channel channel = connection.createChannel();
        //4.在信道中声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //5.声明路由键
        String[] routeKeys = {"king","mark","james"};
        for(int i = 0;i < 6;i++){
            String routeKey = routeKeys[i%3];
            String msg = "Hello Rabbitmq" + (i+1);
            //6.发布消息
            channel.basicPublish(EXCHANGE_NAME,routeKey,null,msg.getBytes());
            System.out.println("Sent:" + routeKey + ":" + msg);
        }
        //7.关闭信道和连接
        channel.close();
        connection.close();
    }
}
