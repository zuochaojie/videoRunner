package com.example.test.model;

import java.io.Serializable;
import java.sql.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.test.type.ArrayTypeHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;


/**
 * ${comments}
 * 
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2026-01-07 21:00:08
 */
@Data
@TableName(value = "tbl_video", autoResultMap = true) //,autoResultMap = true数组时加上
public class VideoModel  implements Serializable {
	private static final long serialVersionUID = 1L;
    private String id;

    /**
    * $column.comments
    */
    private String title;
    /**
    * $column.comments
    */
    private Date releaseDate;
    /**
    * $column.comments
    */
    private String producedBy;
    /**
    * $column.comments
    */
    private String actress;
    /**
    * $column.comments
    */
    @TableField(jdbcType = JdbcType.ARRAY, typeHandler = ArrayTypeHandler.class)
    private String[] tags;
    /**
    * $column.comments
    */
    private String releaseName;
    /**
    * $column.comments
    */
    @TableField(jdbcType = JdbcType.ARRAY, typeHandler = ArrayTypeHandler.class)
    private String[] address;
    /**
    * $column.comments
    */
    private String imgPath;
}
