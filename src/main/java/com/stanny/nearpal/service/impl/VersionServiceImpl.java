package com.stanny.nearpal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stanny.nearpal.entity.TVersion;
import com.stanny.nearpal.mapper.TVersionMapper;
import com.stanny.nearpal.service.VersionService;

import org.springframework.stereotype.Service;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@Service
class VersionServiceImpl extends ServiceImpl<TVersionMapper, TVersion> implements VersionService {


    @Override
    public TVersion getVersion() {
        return baseMapper.selectMaxVersion();
    }
}
