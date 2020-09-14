package com.stanny.nearpal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stanny.nearpal.entity.TVersion;

import org.springframework.stereotype.Repository;

@Repository
public interface TVersionMapper extends BaseMapper<TVersion> {

    TVersion selectMaxVersion();

}