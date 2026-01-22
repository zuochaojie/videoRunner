package com.example.test.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * ${comments}
 * 
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-07 21:00:08
 */
@Data
@TableName("tbl_actress") //,autoResultMap = true数组时加上
public class ActressModel  implements Serializable {
	private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
    * $column.comments
    */
    private String name;
    /**
    * $column.comments
    */
    private Date birthday;
    /**
    * $column.comments
    */
    private String height;
    /**
    * $column.comments
    */
    private String cup;
}
