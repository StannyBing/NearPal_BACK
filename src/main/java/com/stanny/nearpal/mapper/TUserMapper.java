package com.stanny.nearpal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stanny.nearpal.entity.TUser;

import java.util.List;


public interface TUserMapper extends BaseMapper<TUser> {

    List<TUser> selectRandomUser(Integer userid);

}