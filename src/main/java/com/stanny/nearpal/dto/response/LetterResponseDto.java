package com.stanny.nearpal.dto.response;

import com.stanny.nearpal.entity.TLetter;
import com.stanny.nearpal.entity.TUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Xiangb on 2019/12/25.
 * 功能：
 */
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class LetterResponseDto {
    @ApiModelProperty(value = "信件")
    private TLetter letter;

    @ApiModelProperty(value = "收信人")
    private TUser sendUser;
}
