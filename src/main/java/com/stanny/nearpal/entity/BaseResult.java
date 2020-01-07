package com.stanny.nearpal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResult<T> {

    private int code;

    private String msg;

    private T data;
}
