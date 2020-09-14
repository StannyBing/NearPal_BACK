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
public class DiaryRequestDto {

    @ApiModelProperty(value = "日记id")
    private Integer id;

    @ApiModelProperty(value = "日记标题")
    private String diarytitle;

    @ApiModelProperty(value = "日记内容")
    private String diarydetail;

    @ApiModelProperty(value = "我的心情")
    private Integer myfeeling;
}
