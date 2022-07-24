package com.chen.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一个队列多个消费者，则会表现出消息在消费者之间的轮询发送。
 */
public class MultiConsumerOneQueue {
    public final static String EXCHANGE_NAME = "direct_logs";
    public final static String QUEUE_NAME = "queue-king";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        String routeKey = "king";
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,routeKey);
        for(int i = 0;i < 2;i++){
            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body,"UTF-8");
                    System.out.println(Thread.currentThread().getName() + "Received["+envelope.getRoutingKey()+"]"+message);
                }
            };
            Thread thread = new Thread(new ConsumerWorker(consumer,channel));
            thread.start();
        }
    }

    public static class ConsumerWorker implements Runnable{

        private Consumer consumer;
        private Channel channel;

        ConsumerWorker(Consumer consumer,Channel channel){
            this.consumer = consumer;
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                channel.basicConsume(MultiConsumerOneQueue.QUEUE_NAME,true,consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
