package com.stanny.nearpal.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        if (metaObject.hasSetter("sendtime")) this.setFieldValByName("sendtime", new Date(System.currentTimeMillis()), metaObject);
        if (metaObject.hasSetter("createtime")) this.setFieldValByName("createtime", new Date(System.currentTimeMillis()), metaObject);
        if (metaObject.hasSetter("registtime")) this.setFieldValByName("registtime", new Date(System.currentTimeMillis()), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
    }
}
