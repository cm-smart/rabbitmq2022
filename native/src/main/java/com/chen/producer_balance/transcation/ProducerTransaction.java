package com.chen.producer_balance.transcation;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者事务模式
 */
public class ProducerTransaction {
    public final static String EXCHANGE_NAME = "producer_transaction";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.DIRECT);

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

        String[] routekeys={"king","mark","james"};
        //TODO
        //加入事务
        channel.txSelect();
        try{
            for(int i = 0;i < 3;i++){
                String routekey = routekeys[i%3];
                // 发送的消息
                String message = "Hello World_"+(i+1)+("_"+System.currentTimeMillis());
                channel.basicPublish(EXCHANGE_NAME,routekey,true,null,message.getBytes());
                System.out.println("----------------------------------");
                System.out.println(" Sent Message: [" + routekey +"]:'" + message + "'");
                Thread.sleep(200);
            }
            //事务提交
            channel.txCommit();
        }catch (Exception e){
            e.printStackTrace();
            //事务回滚
            channel.txRollback();
        }

        channel.close();
        connection.close();
    }
}
