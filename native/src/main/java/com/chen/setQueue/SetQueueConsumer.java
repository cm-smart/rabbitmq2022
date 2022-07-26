package com.chen.setQueue;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class SetQueueConsumer {
    public final static String EXCHANGE_NAME = "set_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //自动过期队列--参数需要Map传递
        String queueName = "setQueue";
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-expires",10*1000);//10秒被删除
        //durable,exclusive,autoDelete
        channel.queueDeclare(queueName,false,false,false,arguments);
        String routeKey = "error";
        channel.queueBind(queueName,EXCHANGE_NAME,routeKey);
        System.out.println("waiting for message........");
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received["+envelope.getRoutingKey() +"]"+message);
            }
        };
        channel.basicConsume(queueName,true,consumer);
    }
}
