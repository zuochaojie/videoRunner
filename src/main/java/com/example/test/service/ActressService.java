package com.example.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test.model.ActressModel;
import java.io.Serializable;
import java.util.Map;
import java.util.Collection;
/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-07 21:00:08
 */
public interface ActressService extends IService<ActressModel> {
     Map<String,Object> queryPage(Map<String,Object> params);
}

