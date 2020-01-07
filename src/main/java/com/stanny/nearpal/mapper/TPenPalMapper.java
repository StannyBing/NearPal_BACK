package com.stanny.nearpal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stanny.nearpal.entity.TPenPal;
import com.stanny.nearpal.entity.TUser;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TPenPalMapper extends BaseMapper<TPenPal> {

    List<TUser> selectMyPenpal(Integer userid);

}