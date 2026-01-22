package com.example.test.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageData implements Serializable {
    private String id;
    private String content;
    private Date sendTime;
    private String sender;
}
