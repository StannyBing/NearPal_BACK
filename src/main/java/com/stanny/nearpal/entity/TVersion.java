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

@TableName("tb_version")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value = "TVersion对象", description = "版本表")
public class TVersion extends Model<TVersion> {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "版本号")
    private Integer versioncode;

    @ApiModelProperty(value = "版本名")
    private String versionname;

    @ApiModelProperty(value = "详情")
    private String content;

    @ApiModelProperty(value = "文件路径")
    private String filepath;

    @ApiModelProperty(value = "强制更新")
    private Integer isforce;

    @ApiModelProperty(value = "注册时间")
    @TableField(value = "updatetime", fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatetime;

}