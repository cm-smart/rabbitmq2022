package com.chen.consumer_balance.getmessage;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 普通生产者
 */
public class GetMessageProducer {
    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
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
        channel.addConfirmListener(new ConfirmListener() {
            //成功
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("send_ACK:"+deliveryTag+",multiple:"+multiple);
            }
            //失败
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Erro----send_NACK:"+deliveryTag+",multiple:"+multiple);
            }
        });

        String routeKey = "error";
        for(int i = 0;i < 3;i++){
            // 发送的消息
            String message = "Hello World_"+(i+1);
            //true为设置了mandatory，即失败通知
            channel.basicPublish(EXCHANGE_NAME,routeKey,true,null,message.getBytes());
            System.out.println(" [x] Sent 'error':'" + message + "'");
        }

        // 关闭频道和连接
        //channel.close();
        //connection.close();
    }
}
