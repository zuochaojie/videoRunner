package com.example.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test.model.ImageModel;
import java.io.Serializable;
import java.util.Map;
import java.util.Collection;
/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-18 16:28:29
 */
public interface ImageService extends IService<ImageModel> {
     Map<String,Object> queryPage(Map<String,Object> params);

     void downloadImg();
}

