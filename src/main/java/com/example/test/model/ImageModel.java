package com.example.test.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("tbl_image")
@Data
public class ImageModel {
    private String id;
    private String filePath;
}
