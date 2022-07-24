package com.chen.producer_balance.backupexchange;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者，绑定备用交换器队列的消费者
 */
public class BackupExConsumer {
    public final static String BAK_EXCHANGE_NAME = "ae";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(BAK_EXCHANGE_NAME, BuiltinExchangeType.FANOUT,true,false,null);
        String queueName = "fetechother";
        channel.queueDeclare(queueName,false,false,false,null);
        String routeKey = "#";
        channel.queueBind(queueName,BAK_EXCHANGE_NAME,routeKey);
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                //记录日志到文件：
                System.out.println( "Received [" + envelope.getRoutingKey() + "] "+message);
            }
        };
        channel.basicConsume(queueName,true,consumer);
    }
}
