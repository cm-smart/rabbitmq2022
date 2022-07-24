package com.chen.exchange.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * fanout生产者
 */
public class FanoutProducer {
    public final static String EXCHANGE_NAME = "fanout_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String[] routekeys = {"king","mark","james"};
        for(int i = 0;i < 3;i++){
            String routeKey = routekeys[i%3];
            String message = "Hello World_"+(i+1);
            channel.basicPublish(EXCHANGE_NAME,routeKey,null,message.getBytes());
            System.out.println(" [x] Sent '" + routeKey +"':'" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
