package com.chen.producer_balance.producerconfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 批量确认
 */
public class ProducerBatchConfirm {
    public final static String EXCHANGE_NAME = "producer_confirm";
    private final static String ROUTE_KEY = "king";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        connection.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println(e.getMessage());
            }
        });
        channel.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println(e.getMessage());
            }
        });
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routeKey, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                String message = new String(bytes);
                System.out.println("返回的replycode:"+replyCode);
                System.out.println("返回的replyText:"+replyText);
                System.out.println("返回的exchange:"+exchange);
                System.out.println("返回的routeKey:"+routeKey);
            }
        });

        //开启确认模式
        channel.confirmSelect();

        for(int i = 0;i < 2;i++){
            String message = "Hello World_"+(i+1);
            System.out.println(" Sent Message: [" + ROUTE_KEY +"]:'"+ message + "'");
            channel.basicPublish(EXCHANGE_NAME,ROUTE_KEY,true,null,message.getBytes());
        }

        //启用发送者确认模式（批量确认）
        channel.waitForConfirmsOrDie();

        channel.close();
        connection.close();
    }
}
