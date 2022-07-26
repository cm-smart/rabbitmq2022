package com.chen.setmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReplyToConsumer {
    public final static String EXCHANGE_NAME = "replyto";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT,false);
        String queueName = "replyto";
        channel.queueDeclare(queueName,false,false,false,null);
        String routeKey = "error";
        channel.queueBind(queueName,EXCHANGE_NAME,routeKey);
        System.out.println("waiting for message........");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received["+envelope.getRoutingKey() +"]"+message);
                AMQP.BasicProperties respProperties = new AMQP.BasicProperties.Builder()
                        .replyTo(properties.getReplyTo())
                        .correlationId(properties.getMessageId())
                        .build();
                channel.basicPublish("",respProperties.getReplyTo(),respProperties,("Hi,"+message).getBytes("UTF-8"));
            }
        };

        channel.basicConsume(queueName,true,consumer);

    }
}
