package com.stanny.nearpal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TShare;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.service.ShareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@RequestMapping("share")
@RestController
@Api(tags = "分享")
public class ShareController extends BaseController {

    @Autowired
    private ShareService shareService;

    @ApiOperation(value = "发布分享")
    @PostMapping("/shareLetter")
    public BaseResult shareLetter(Integer letterid, String shareText) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        TShare share = new TShare();
        try {
            share.setUserid(user.getId());
            share.setSharetext(shareText);
            share.setLetterid(letterid);
            if (share.insert()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取分享")
    @GetMapping("/getShareList")
    public BaseResult getShareList() {
        try {
            QueryWrapper<TShare> wrapper = new QueryWrapper<>();
            wrapper.lambda().orderByDesc(TShare::getSharetime);
            List<TShare> shareList = shareService.list(wrapper);
            return success(shareList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

}
