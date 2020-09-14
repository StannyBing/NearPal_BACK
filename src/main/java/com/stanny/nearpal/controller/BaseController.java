package com.stanny.nearpal.controller;


import com.stanny.nearpal.constant.ResultInfo;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.util.RedisClientUtil;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Xiangb on 2019/12/23.
 * 功能：
 */
public class BaseController {

    @Autowired
    RedisClientUtil redisClientUtil;

    @Autowired
    HttpServletRequest request;

    protected TUser currentUser() {
        Object obj = null;
        request.getSession();
        try {
            if (redisClientUtil.isEnable()) {
                String token = request.getHeader("cookie");
                obj = redisClientUtil.getObject(token);
            }
            if (obj == null) {
                obj = request.getSession().getAttribute("user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj = request.getSession().getAttribute("user");
        }
        if (Objects.isNull(obj) || !(obj instanceof TUser)) {
            return null;
        }
        return (TUser) obj;
    }

    protected void updateUser(TUser user) {
        request.getSession();
        try {
            if (redisClientUtil.isEnable()) {
                String token = request.getHeader("cookie");
                if (token.isEmpty()) {
                    token = request.getSession().getId();
                }
                redisClientUtil.expire(token, 60 * 60 * 48);
                redisClientUtil.setobj(token, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String token = request.getSession().getId();
        request.getSession().setAttribute(token, user);
    }

    protected BaseResult success(Object data, String message) {
        BaseResult<Object> result = new BaseResult<>();
        result.setCode(ResultInfo.SUCCESS.getCode());
        result.setMsg(message);
        result.setData(data);
        return result;
    }

    protected BaseResult success(Object data) {
        BaseResult<Object> result = new BaseResult<>();
        result.setCode(ResultInfo.SUCCESS.getCode());
        result.setMsg(ResultInfo.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    protected BaseResult success() {
        BaseResult result = new BaseResult();
        result.setCode(ResultInfo.SUCCESS.getCode());
        result.setMsg(ResultInfo.SUCCESS.getMsg());
        return result;
    }

    protected BaseResult timeOut() {
        BaseResult result = new BaseResult();
        result.setCode(ResultInfo.TIMEOUT.getCode());
        result.setMsg(ResultInfo.TIMEOUT.getMsg());
        return result;
    }

    protected BaseResult fail(String msg) {
        BaseResult result = new BaseResult();
        result.setCode(ResultInfo.FAILED.getCode());
        result.setMsg(msg);
        return result;
    }

    protected BaseResult fail() {
        BaseResult result = new BaseResult();
        result.setCode(ResultInfo.FAILED.getCode());
        result.setMsg(ResultInfo.FAILED.getMsg());
        return result;
    }
}
