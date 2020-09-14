package com.stanny.nearpal.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.stanny.nearpal.dto.reqest.DiaryRequestDto;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TDiary;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.service.DiaryService;
import com.stanny.nearpal.service.UMPushService;
import com.stanny.nearpal.service.UserService;
import com.stanny.nearpal.util.CopyPropertiesUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@RequestMapping("diary")
@RestController
@Api(tags = "日记")
@EnableScheduling
public class DiaryController extends BaseController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UMPushService pushService;


    @ApiOperation(value = "新增日记")
    @PostMapping("/addDiary")
    public BaseResult addDiary(@RequestBody DiaryRequestDto dto) {
        Assert.notNull(dto, "请传入必要参数");
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        TDiary diary = new TDiary();
        try {
            BeanUtils.copyProperties(dto, diary, CopyPropertiesUtil.getNullPropertyNames(dto));
            diary.setUserid(user.getId());
            if (diary.insert()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "更新日记")
    @PostMapping("/updateDiary")
    public BaseResult updateDiary(@RequestBody DiaryRequestDto dto) {
        Assert.notNull(dto, "请传入必要参数");
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        QueryWrapper<TDiary> query = new QueryWrapper<>();
        query.lambda().eq(TDiary::getId, dto.getId());
        TDiary diary = diaryService.getOne(query);
        if (Objects.isNull(diary)) {
            return fail("该日记不存在");
        }
        try {
            BeanUtils.copyProperties(dto, diary, CopyPropertiesUtil.getNullPropertyNames(dto));
            if (diary.updateById()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "删除日记")
    @GetMapping("/deleteDiary")
    public BaseResult deleteDiary(Integer id) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            QueryWrapper<TDiary> query = new QueryWrapper<>();
            query.lambda().eq(TDiary::getId, id);
            TDiary diary = diaryService.getOne(query);
            if (Objects.isNull(diary)) {
                return fail("该日记不存在");
            }
            if (diary.deleteById()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取我的所有日记")
    @GetMapping("/getMyDiaryList")
    public BaseResult getMyDiaryList(@RequestParam(value = "myfeeling", defaultValue = "-1") Integer myfeeling) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            QueryWrapper<TDiary> query = new QueryWrapper<>();
            LambdaQueryWrapper<TDiary> wrapper = query.lambda();
            wrapper.eq(TDiary::getUserid, user.getId());
            if (myfeeling >= 0) {
                wrapper.eq(TDiary::getMyfeeling, myfeeling);
            }
            wrapper.orderByDesc(TDiary::getCreatetime);
            List<TDiary> diary = diaryService.list(query);
            return success(diary);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }
}
