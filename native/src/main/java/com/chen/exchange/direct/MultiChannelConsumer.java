package com.chen.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一个连接多个信道
 */
public class MultiChannelConsumer {

    public final static String EXCHANGE_NAME = "direct_logs";
    public final static String QUEUE_NAME = "queue-king";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();

        for(int i = 0;i < 2;i++){
            Thread thread = new Thread(new Worker(connection));
            thread.start();
        }
    }

    public static class Worker implements Runnable{
        private Connection connection;

        Worker(Connection connection){
            this.connection = connection;
        }

        @Override
        public void run() {
            try{
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(MultiChannelConsumer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                channel.queueDeclare(MultiChannelConsumer.QUEUE_NAME,false,false,false,null);
                String routeKey = "king";
                channel.queueBind(MultiChannelConsumer.QUEUE_NAME,MultiChannelConsumer.EXCHANGE_NAME,routeKey);
                Consumer consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String message = new String(body,"UTF-8");
                        System.out.println(Thread.currentThread().getName() + "Received["+envelope.getRoutingKey()+"]"+message);
                    }
                };
                channel.basicConsume(MultiChannelConsumer.QUEUE_NAME,true,consumer);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
