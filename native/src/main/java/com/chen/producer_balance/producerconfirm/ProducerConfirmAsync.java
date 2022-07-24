package com.chen.producer_balance.producerconfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者——发送方确认模式--异步监听确认
 */
public class ProducerConfirmAsync {
    public final static String EXCHANGE_NAME = "producer_confirm";
    private final static String ROUTE_KEY = "king";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

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

        //添加失败者通知
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routeKey, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                String message = new String(bytes);
                System.out.println("RabbitMq路由失败:  "+routeKey+"."+message);
            }
        });

        //开启确认模式
        channel.confirmSelect();
        // 添加发送者确认监听器
        channel.addConfirmListener(new ConfirmListener() {
            //TODO 成功
            public void handleAck(long deliveryTag, boolean multiple)
                    throws IOException {
                System.out.println("send_ACK:"+deliveryTag+",multiple:"+multiple);
            }
            //TODO 失败
            public void handleNack(long deliveryTag, boolean multiple)
                    throws IOException {
                System.out.println("Erro----send_NACK:"+deliveryTag+",multiple:"+multiple);
            }
        });

        String[] routekeys={"king","mark"};
        //TODO 6条
        for(int i=0;i<20;i++){
            String routekey = routekeys[i%2];
            //String routekey = "king";
            // 发送的消息
            String message = "Hello World_"+(i+1)+("_"+System.currentTimeMillis());
            channel.basicPublish(EXCHANGE_NAME, routekey, true, MessageProperties.PERSISTENT_BASIC, message.getBytes());
            System.out.println(" Sent Message: [" + ROUTE_KEY +"]:'"+ message + "'");
        }
        // 关闭频道和连接
        //channel.close();
        //connection.close();

    }
}
