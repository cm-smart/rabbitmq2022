package com.chen.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 队列和交换器的多重绑定
 */
public class MultiBindConsumer {
    public final static String EXCHANGE_NAME = "direct_logs";
    public final static String QUEUE_NAME = "queue-king";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        String[] routeKeys = {"king","mark","james"};
        for(String routeKey:routeKeys){
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,routeKey);
        }
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body,"UTF-8");
                System.out.println("Received["+envelope.getRoutingKey()+"]"+message);
            }
        };
        //
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }
}
