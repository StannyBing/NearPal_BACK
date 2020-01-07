package com.stanny.nearpal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@TableName("tb_letter")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value = "TLetter对象", description = "信件表")
public class TLetter extends Model<TUser> {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "发件时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "sendtime", fill = FieldFill.INSERT)
    private Date sendtime;

    @ApiModelProperty(value = "收信时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date accepttime;

    @ApiModelProperty(value = "邮编")
    private String postcode;

    @ApiModelProperty(value = "邮票id")
    private String stampid;

    @ApiModelProperty(value = "状态")//状态（1已发送 2已收到 3已拆封）
    private Integer mstatus;

    @ApiModelProperty(value = "称呼")
    private String lettercall;

    @ApiModelProperty(value = "信件头部信息")
    private String letterinfo;

    @ApiModelProperty(value = "信件详情")
    private String letterdetail;

    @ApiModelProperty(value = "发件人id")
    private Integer senduserid;

    @ApiModelProperty(value = "收件人id")
    private Integer acceptuserid;

    @ApiModelProperty(value = "是否为随机信件")
    private Integer israndom;
}