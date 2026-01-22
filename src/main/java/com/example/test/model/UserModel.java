package com.example.test.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.test.type.ArrayTypeHandler;
import com.example.test.type.UUIDTypeHandler;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;


/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2025-12-23 16:19:58
 */
@Data
@TableName(value = "tbl_user",autoResultMap = true)
public class UserModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableField(typeHandler = UUIDTypeHandler.class)
    @Parameter(required = false)
    private String id;
    /**
     * $column.comments
     */
    private String loginName;
    /**
     * $column.comments
     */
    private String mail;
    /**
     * $column.comments
     */
    private String pass;
    /**
     * $column.comments
     */
    @Parameter(required = false)
    private Boolean delete;
    /**
     * $column.comments
     */
    @Parameter(required = false)
    @TableField(fill= FieldFill.INSERT)
    private Date createDate;
    /**
     * $column.comments
     */
    private String phone;
    /**
     * $column.comments
     */
    @TableField(jdbcType = JdbcType.ARRAY, typeHandler = ArrayTypeHandler.class)
    private Integer[] roleIds;
}
