package com.chen.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RejectProducer {
    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long l, boolean b) throws IOException {
                System.out.println(l);
            }

            @Override
            public void handleNack(long l, boolean b) throws IOException {

            }
        });
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int i, String s, String s1, String s2, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {

            }
        });
        channel.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println(e.getMessage());
            }
        });
        connection.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println(e.getMessage());
            }
        });

        String routeKey = "error";

        for(int i = 0;i < 10;i++){
            String message = "Hello World_"+(i+1);
            channel.basicPublish(EXCHANGE_NAME,routeKey,null,message.getBytes());
            System.out.println(" [x] Sent 'error':'" + message + "'");
        }

    }
}
