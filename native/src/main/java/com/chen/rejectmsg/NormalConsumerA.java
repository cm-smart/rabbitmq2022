package com.chen.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NormalConsumerA {
    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = "focuserror";
        channel.queueDeclare(queueName,false,false,false,null);
        String routeKey = "error";
        channel.queueBind(queueName,EXCHANGE_NAME,routeKey);
        System.out.println("waiting for message........");
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try{
                    String message = new String(body, "UTF-8");
                    System.out.println("Received["+envelope.getRoutingKey() +"]"+message);
                    //确认,false表示不是批量确认
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }catch (Exception e){
                    e.printStackTrace();
                    //拒绝
                }
            }
        };
        //false表示不是自动确认
        channel.basicConsume(queueName,false,consumer);
    }
}
