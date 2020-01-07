package com.stanny.nearpal.dto.reqest;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Xiangb on 2019/12/23.
 * 功能：
 */
@Data
@ApiModel
public class LetterRequestDto {

    @ApiModelProperty("收信人id")
    private Integer acceptuserid;

    @ApiModelProperty("邮票id")
    @NotNull
    private String stampid;

    @ApiModelProperty("信件称呼")
    private String lettercall;

    @ApiModelProperty("信件详情")
    private String letterdetail;

    @ApiModelProperty("回信id")
    private Integer replyletterid;

}
