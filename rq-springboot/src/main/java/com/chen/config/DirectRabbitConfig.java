package com.chen.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {
    //队列 起名：TestDirectQueue
    @Bean
    public Queue testDirectQueue(){
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        // return new Queue("TestDirectQueue",true,true,false);
        return new Queue("testDirectQueue",true);
    }

    //Direct交换机 起名：TestDirectExchange
    @Bean
    public DirectExchange testDirectExchange(){
        return new DirectExchange("testDirectExchange",true,false);
    }

    //绑定 将队列和交换机绑定
    @Bean
    public Binding bindingDirect(){
        String routeKey = "testDirectRouting";
        return BindingBuilder.bind(testDirectQueue()).to(testDirectExchange()).with(routeKey);
    }

    @Bean
    public DirectExchange lonelyDirectExchange(){
        return new DirectExchange("lonelyDirectExchange");
    }
}
