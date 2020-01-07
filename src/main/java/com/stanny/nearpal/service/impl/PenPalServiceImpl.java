package com.stanny.nearpal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.mapper.TPenPalMapper;
import com.stanny.nearpal.entity.TPenPal;
import com.stanny.nearpal.service.PenPalService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@Service
class PenPalServiceImpl extends ServiceImpl<TPenPalMapper, TPenPal> implements PenPalService {


    @Override
    public List<TUser> selectMyPenpal(Integer userid) {
        return baseMapper.selectMyPenpal(userid);
    }
}
