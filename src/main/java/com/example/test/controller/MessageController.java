package com.example.test.controller;

import com.example.test.message.MessageData;
import com.example.test.message.RocketMQProducer;
import com.github.f4b6a3.uuid.UuidCreator;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private RocketMQProducer rocketMQProducer;

    @PostMapping("/send/sync")
    public SendResult sendSyncMessage(@RequestBody MessageData message) {
        message.setId(UuidCreator.getTimeOrdered().toString());
        message.setSendTime(Calendar.getInstance().getTime());
        return rocketMQProducer.sendSyncMessage(message);
    }

    /**
     * 发送异步消息
     */
    @PostMapping("/send/async")
    public String sendAsyncMessage(@RequestBody MessageData message) {
        message.setId(UuidCreator.getTimeOrdered().toString());
        message.setSendTime(Calendar.getInstance().getTime());
        rocketMQProducer.sendAsyncMessage(message);
        return "异步消息已发送";
    }

    /**
     * 发送单向消息
     */
    @PostMapping("/send/oneway")
    public String sendOneWayMessage(@RequestBody MessageData message) {
        message.setId(UuidCreator.getTimeOrdered().toString());
        message.setSendTime(Calendar.getInstance().getTime());
        rocketMQProducer.sendOneWayMessage(message);
        return "单向消息已发送";
    }

    /**
     * 发送延迟消息
     */
    @PostMapping("/send/delay/{delayLevel}")
    public SendResult sendDelayMessage(
            @RequestBody MessageData message,
            @PathVariable int delayLevel) {
        message.setId(UuidCreator.getTimeOrdered().toString());
        message.setSendTime(Calendar.getInstance().getTime());
        return rocketMQProducer.sendDelayMessage(message, delayLevel);
    }

    /**
     * 发送测试消息
     */
    @GetMapping("/send/test")
    public SendResult sendTestMessage() {
        MessageData message = new MessageData();
        message.setId(UuidCreator.getTimeOrdered().toString());
        message.setSendTime(Calendar.getInstance().getTime());
        message.setContent("测试消息内容");
        message.setSender("TestUser");
        return rocketMQProducer.sendSyncMessage(message);
    }
}
