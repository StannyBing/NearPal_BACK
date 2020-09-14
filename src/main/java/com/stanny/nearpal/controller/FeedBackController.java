package com.stanny.nearpal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TFeedBack;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.service.FeedBackService;

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
@RequestMapping("feedback")
@RestController
@Api(tags = "意见反馈")
public class FeedBackController extends BaseController {

    @Autowired
    private FeedBackService feedBackService;

    @ApiOperation(value = "添加意见反馈")
    @PostMapping("/addFeedBack")
    public BaseResult addFeedBack(String feedcontent) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        TFeedBack feedBack = new TFeedBack();
        try {
            feedBack.setFeedcontent(feedcontent);
            feedBack.setUserid(user.getId());
            if (feedBack.insert()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取意见反馈")
    @GetMapping("/getFeedBack")
    public BaseResult getFeedBack() {
        try {
            QueryWrapper<TFeedBack> wrapper = new QueryWrapper<>();
            wrapper.lambda().orderByDesc(TFeedBack::getFeeddate);
            List<TFeedBack> feedBackList = feedBackService.list(wrapper);
            return success(feedBackList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }
}
