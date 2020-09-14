package com.stanny.nearpal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName("tb_share")
@Data
public class TShare extends Model<TShare> {
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "信件id")
    private Integer letterid;

    @ApiModelProperty(value = "用户id")
    private Integer userid;

    @ApiModelProperty(value = "分享文字")
    private String sharetext;

    @ApiModelProperty(value = "主键")
    @TableField(value = "sharetime", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sharetime;

}