package com.example.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test.model.RoleModel;
import java.io.Serializable;
import java.util.Map;
import java.util.Collection;
/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2025-12-23 16:19:58
 */
public interface RoleService extends IService<RoleModel> {
     Map<String,Object> queryPage(Map<String,Object> params);
}

