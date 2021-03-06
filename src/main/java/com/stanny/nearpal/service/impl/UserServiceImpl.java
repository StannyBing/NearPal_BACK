package com.stanny.nearpal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.mapper.TUserMapper;
import com.stanny.nearpal.service.UserService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@Service
class UserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements UserService {

    @Override
    public List<TUser> getRandomUser(Integer userid) {
        return baseMapper.selectRandomUser(userid);
    }
}
