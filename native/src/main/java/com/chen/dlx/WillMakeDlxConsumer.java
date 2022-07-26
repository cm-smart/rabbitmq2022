package com.chen.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 普通的消费者,但是自己无法消费的消息，将投入死信队列
 */
public class WillMakeDlxConsumer {
    public final static String EXCHANGE_NAME = "dlx_make";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(DlxProducer.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //TODO 绑定死信交换器
        /*声明一个队列，并绑定死信交换器*/
        String queueName = "dlx_make";
        Map<String,Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange",DlxProcessConsumer.DLX_EXCHANGE_NAME);
        channel.queueDeclare(queueName,false,true,false,params);
        channel.queueBind(queueName,EXCHANGE_NAME,"#");
        System.out.println("waiting for message........");

        /*声明了一个消费者*/
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                //TODO
                //TODO 如果是king的消息确认
                if(envelope.getRoutingKey().equals("king")){
                    System.out.println("Received[" +envelope.getRoutingKey() +"]"+message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }else{
                    //TODO 如果是其他的消息拒绝（queue=false），成为死信消息
                    System.out.println("Will reject[" +envelope.getRoutingKey() +"]"+message);
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }

            }
        };
        /*消费者正式开始在指定队列上消费消息*/
        channel.basicConsume(queueName,false,consumer);
    }
}
