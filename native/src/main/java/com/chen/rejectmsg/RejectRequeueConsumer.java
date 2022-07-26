package com.chen.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class RejectRequeueConsumer {
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
                    System.out.println("Received[" +envelope.getRoutingKey() + "]"+message);
                    throw new RuntimeException("处理异常"+message);
                }catch (Exception e){
                    e.printStackTrace();
                    //TODO Reject方式拒绝(这里第2个参数决定是否重新投递)
                    channel.basicReject(envelope.getDeliveryTag(),true);

                    //TODO Nack方式的拒绝（第2个参数决定是否批量）
                    //channel.basicNack(envelope.getDeliveryTag(), false, false);
                }
            }
        };

        channel.basicConsume(queueName,false,consumer);
    }
}
