package com.chen.producer_balance.mandatory;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *生产者——失败确认模式
 */
public class ProducerMandatory {
    public final static String EXCHANGE_NAME = "mandatory_test";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        connection.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println("连接关闭，关闭原因：" + e.getReason());
            }
        });

        channel.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println("信道关闭，关闭原因：" + e.getReason());
            }
        });

        //失败通知，回调
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

        String[] routekeys={"king","mark","james"};
        for(int i =0;i < 3;i++){
            String routeKey = routekeys[i%3];
            String message = "Hello World" + (i+1) + ("_" + System.currentTimeMillis());
            //true为设置了mandatory，即失败通知
            channel.basicPublish(EXCHANGE_NAME,routeKey,true,null,message.getBytes());
            System.out.println("----------------------------------");
            System.out.println(" Sent Message: [" + routeKey +"]:'" + message + "'");
            Thread.sleep(200);
        }

        channel.close();
        connection.close();
    }
}
