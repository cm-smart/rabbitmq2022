package com.chen.consumer_balance.ackfalse;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息者不对消息进行确认会怎样？
 */
public class AckFalseConsumerA {

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
                    //TODO
                    //确认
                }catch (Exception e){
                    e.printStackTrace();
                    //拒绝
                }
            }
        };
        //这里第二个参数是自动确认参数，如果是false则是手动确认
        channel.basicConsume(queueName,false,consumer);
    }
}
