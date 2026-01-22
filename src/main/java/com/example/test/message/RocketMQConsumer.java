package com.example.test.message;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
@RocketMQMessageListener(
        topic = "${rocketmq-config.topic}",
        consumerGroup = "${rocketmq-config.consumer-group}",
        selectorExpression = "${rocketmq-config.tag}"
)
public class RocketMQConsumer implements RocketMQListener<MessageData> {

    @Override
    public void onMessage(MessageData message) {

    }
}
