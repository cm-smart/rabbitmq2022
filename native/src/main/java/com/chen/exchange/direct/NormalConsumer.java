package com.chen.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 普通的消费者
 */
public class NormalConsumer {
    public final static String EXCHANGE_NAME = "direct_logs";
    public final static String QUEUE_NAME = "queue-king";
    public final static String routeKey = "king";

    public static void main(String[] args) throws IOException, TimeoutException {
        //1.创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        //2.创建连接
        Connection connection = connectionFactory.newConnection();
        //3.创建信道
        Channel channel = connection.createChannel();
        //4.声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //5.声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //6.绑定：将队列queue-king与交换器通过路由键绑定
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,routeKey);
        System.out.println("waiting for message ......");
        //7.申明一个消费者
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body,"UTF-8");
                System.out.println("Received["+envelope.getRoutingKey()+"]"+message);
            }
        };
        //消息者正是开始在指定队列上消费。(queue-king)
        //TODO 这里第二个参数是自动确认参数，如果是true则是自动确认
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }
}
