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
public class LoginRequestDto {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "电话")
    private String telephone;
}
