package com.stanny.nearpal.dto.reqest;

import java.util.Date;

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
public class UserInfoRequestDto {

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "登录类别")
    private String logintype;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "生日")
    private Date birthday;

    @ApiModelProperty(value = "余额")
    private Integer balance;

    @ApiModelProperty(value = "头像")
    private Integer headicon;

}
