package com.chen.exchange.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Topic类型的生产者
 * 假设有交换器 topic_course，
 * 讲课老师有king,mark,james，
 * 技术专题有kafka,jvm,redis，
 * 课程章节有 A、B、C，
 * 路由键的规则为 讲课老师+“.”+技术专题+“.”+课程章节，如：king.kafka.A。
 * 生产者--生产全部的消息3*3*3=27条消息
 */
public class TopicProducer {
    public final static String EXCHANGE_NAME = "topic_course";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String[] teachers = {"king","mark","james"};
        for(int i = 0;i < 3;i++){
            String[]  modules={"kafka","jvm","redis"};
            for(int j=0;j<3;j++){
                String[]  servers={"A","B","C"};
                for(int k=0;k<3;k++){
                    // 发送的消息
                    String message = "Hello Topic_["+i+","+j+","+k+"]";
                    String routeKey = teachers[i%3] + "."+modules[j%3] + "."+servers[k%3];
                    channel.basicPublish(EXCHANGE_NAME,routeKey,null, message.getBytes());
                    System.out.println(" [x] Sent '" + routeKey +":'"+ message + "'");
                }
            }
        }
        channel.close();
        connection.close();
    }
}
