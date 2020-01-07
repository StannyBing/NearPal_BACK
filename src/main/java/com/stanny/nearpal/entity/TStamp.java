package com.stanny.nearpal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import lombok.Data;

@TableName("tb_stamp")
@Data
public class TStamp extends Model<TStamp> {
    @TableField("id")
    private String id;

    @TableField("stampname")
    private String stampname;

    @TableField("stamppath")
    private String stamppath;

    @TableField("createtime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createtime;

    @TableField("cost")
    private Integer cost;

    public TStamp() {
    }
}