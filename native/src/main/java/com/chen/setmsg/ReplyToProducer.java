package com.chen.setmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 消息的属性的控制
 */
public class ReplyToProducer {
    public final static String EXCHANGE_NAME = "replyto";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //false:创建持久化交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT,false);
        //响应QueueName ，消费者将会把要返回的信息发送到该Queue
        String responseQueue = channel.queueDeclare().getQueue();
        //消息的唯一id
        String msgId = UUID.randomUUID().toString();
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .replyTo(responseQueue)
                .messageId(msgId)
                .build();
        //声明一个消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received["+envelope.getRoutingKey() +"]"+message);
            }
        };

        channel.basicConsume(responseQueue,true,consumer);

        //发送消息
        String routeKey = "error";
        String msg = "hello rabbitmq";
        channel.basicPublish(EXCHANGE_NAME,routeKey,properties,msg.getBytes());
        System.out.println("Sent error:"+msg);
    }
}
