package com.example.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test.model.CompanyModel;
import java.io.Serializable;
import java.util.Map;
import java.util.Collection;
/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-10 15:22:33
 */
public interface CompanyService extends IService<CompanyModel> {
     Map<String,Object> queryPage(Map<String,Object> params);

    void getNewMovie();
    String getMovieTaskProcess();
}

