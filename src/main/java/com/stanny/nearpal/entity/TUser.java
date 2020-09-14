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

@TableName("tb_user")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value = "TUser对象", description = "用户表")
public class TUser extends Model<TUser> {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "登录类别 0注册 1微信 2QQ 3微博 4 手机号 5游客")
    private Integer logintype;

    @ApiModelProperty(value = "注册时间")
    @TableField(value = "registtime", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registtime;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "生日")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date birthday;

    @ApiModelProperty(value = "余额")
    private Integer balance;

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "头像")
    private Integer headicon;

    @ApiModelProperty(value = "已删除信件（不显示）")
    private String deleteletter;

    @ApiModelProperty(value = "用户坐标（用于计算收信时间）")
    private String userlocation;

    @ApiModelProperty(value = "用户app版本")
    private String appversion;

    @ApiModelProperty(value = "用户简介")
    private String introduction;
}