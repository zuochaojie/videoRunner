package com.example.test.message;

import com.example.test.model.VideoModel;
import com.example.test.service.VideoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Service
@RocketMQMessageListener(
        topic = "${rocketmq-config.topic}",
        consumerGroup = "${rocketmq-config.consumer-group}",
        selectorExpression = "${rocketmq-config.tag}"
)
public class RocketMQConsumer implements RocketMQListener<MessageData> {

    @Autowired
    private VideoService videoService;

    @Override
    public void onMessage(MessageData message) {
        String content = message.getContent();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, List<String>> map = mapper.readValue(content, Map.class);
            map.forEach((k, v) -> {
                VideoModel model = videoService.getById(k);
                if (model.getAddress() == null || model.getAddress().length==0) {
                    model.setAddress(v.toArray(new String[0]));
                }else {
                    List<String> addressList = new ArrayList<>();
                    for (String address : model.getAddress()) {
                        addressList.add(address);
                    }
                    for (String string : v) {
                        addressList.add(string);
                    }
                    model.setAddress(addressList.stream().distinct().toArray(String[]::new));
                }
                videoService.updateById(model);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
