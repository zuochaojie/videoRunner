package com.example.test.message;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class RocketMQProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq-config.topic}")
    private String topic;

    @Value("${rocketmq-config.tag}")
    private String tag;

    /**
     * 发送同步消息
     */
    public SendResult sendSyncMessage(MessageData message) {
        String destination = String.format("%s:%s", topic, tag);
        Message<MessageData> msg = MessageBuilder.withPayload(message).build();
        SendResult sendResult = rocketMQTemplate.syncSend(destination, msg);
        System.out.println("发送同步消息成功"+message.getContent());
        return sendResult;
    }

    /**
     * 发送异步消息
     */
    public void sendAsyncMessage(MessageData message) {
        String destination = String.format("%s:%s", topic, tag);
        Message<MessageData> msg = MessageBuilder.withPayload(message).build();
        rocketMQTemplate.asyncSend(destination, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送异步消息成功"+message.getContent());
            }
            @Override
            public void onException(Throwable e) {
                System.out.println("发送失败"+message.getContent());
            }
        });
    }

    /**
     * 发送单向消息（不关心结果）
     */
    public void sendOneWayMessage(MessageData message) {
        String destination = String.format("%s:%s", topic, tag);
        Message<MessageData> msg = MessageBuilder.withPayload(message).build();
        rocketMQTemplate.sendOneWay(destination, msg);
        System.out.println("发送成功"+message.getContent());
    }

    /**
     * 发送延迟消息
     * @param delayLevel 延迟级别：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public SendResult sendDelayMessage(MessageData message, int delayLevel) {
        String destination = String.format("%s:%s", topic, tag);
        Message<MessageData> msg = MessageBuilder.withPayload(message).build();
        SendResult sendResult = rocketMQTemplate.syncSend(destination, msg, 3000, delayLevel);
        return sendResult;
    }
}
