package com.chen.consumer_balance.qos;

import com.chen.exchange.direct.DirectProducer;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
public class QosConsumerMain {
    public final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        // 打开连接和创建频道，与发送端一样
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, "direct");

        /*声明一个队列*/
        String queueName = "focuserror";
        channel.queueDeclare(queueName,false,false, false,null);

        /*绑定，将队列和交换器通过路由键进行绑定*/
        String routekey = "error";/*表示只关注error级别的日志消息*/
        channel.queueBind(queueName,DirectProducer.EXCHANGE_NAME,routekey);

        System.out.println("waiting for message........");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try{
                    String message = new String(body, "UTF-8");
                    System.out.println("Received["+envelope.getRoutingKey() +"]"+message);
                    //单条确认
                    //channel.basicAck(envelope.getDeliveryTag(),true);
                }catch (Exception e){
                    //拒绝

                }
            }
        };

        //TODO 如果是两个消费者(QOS ,批量)则轮询获取数据
        //TODO 150条预取(150都取出来 150， 210-150  60  )
        channel.basicQos(500,true);
        /*消费者正式开始在指定队列上消费消息*/
        channel.basicConsume(queueName,false,consumer);
        //TODO 自定义消费者批量确认
        //BatchAckConsumer batchAckConsumer = new BatchAckConsumer(channel);
        //channel.basicConsume(queueName,false,batchAckConsumer);

    }
}
