package com.example.test.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * ${comments}
 * 
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-10 15:22:33
 */
@Data
@TableName("tbl_company") //,autoResultMap = true数组时加上
public class CompanyModel  implements Serializable {
	private static final long serialVersionUID = 1L;

    @TableId
    private String code;

    /**
    * $column.comments
    */
    private String companyName;
    /**
    * $column.comments
    */
    private String url;
    /**
    * $column.comments
    */
    private String newMoviePage;
    /**
    * $column.comments
    */
    private String movieDetails;
    /**
    * $column.comments
    */
    private String movieImage;
}
