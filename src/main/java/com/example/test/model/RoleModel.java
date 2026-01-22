package com.example.test.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.*;
import com.example.test.type.JsonbTypeHandler;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;


/**
 * ${comments}
 *
 * @author zuochaojie
 * @email chaojie_zuo@qq.com
 * @date 2025-12-23 16:19:58
 */
@Data
@TableName(value = "tbl_role",autoResultMap = true)
public class RoleModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    @Parameter(required = false)
    private Integer id;

    /**
     * $column.comments
     */
    private String name;
    /**
     * $column.comments
     */
    private String createBy;
    /**
     * $column.comments
     */
    @Parameter(required = false)
    @TableField(fill= FieldFill.INSERT)
    private Date createDate;

//    @JsonAnyGetter
//    public Map<String, Object> getJsonData() {
//        return jsonData;
//    }
//    @JsonAnySetter
//    public void setJsonData(String key,Object value) {
//        this.jsonData.put(key,value);
//    }

    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String,Object> jsonData;
}
