package com.stanny.nearpal.dto.reqest;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Xiangb on 2019/12/23.
 * 功能：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitLoginRequestDto {

    @ApiModelProperty(value = "用户id")
    private String id;
}
