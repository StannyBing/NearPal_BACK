package com.stanny.nearpal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stanny.nearpal.entity.TUser;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@Service
public interface UserService extends IService<TUser> {

    List<TUser> getRandomUser(Integer userid);

}
