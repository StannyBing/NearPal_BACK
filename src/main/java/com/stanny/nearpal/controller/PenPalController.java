package com.stanny.nearpal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stanny.nearpal.dto.response.LetterUserResponseDto;
import com.stanny.nearpal.dto.response.PenpalRelationResponseDto;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TLetter;
import com.stanny.nearpal.entity.TPenPal;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.service.LetterService;
import com.stanny.nearpal.service.PenPalService;
import com.stanny.nearpal.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@RequestMapping("penpal")
@RestController
@Api(tags = "笔友")
public class PenPalController extends BaseController {

    @Autowired
    private PenPalService penPalService;

    @Autowired
    private LetterService letterService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "删除笔友关系")
    @PostMapping("/deletePenPal")
    public BaseResult deletePenPal(Integer penpalid) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            QueryWrapper<TPenPal> query = new QueryWrapper<>();
            query.lambda().eq(TPenPal::getUserid, user.getId())
                    .eq(TPenPal::getPenpalid, penpalid);
            TPenPal penPal = penPalService.getOne(query);
            if (Objects.isNull(penPal)) {
                return fail("笔友关系不存在");
            }
            //获取和用户的所有往来
            List<LetterUserResponseDto> letterList = letterService.getLettersWithId(user.getId(), penpalid);
            StringBuilder deleteLetter = new StringBuilder(user.getDeleteletter());
            for (int i = 0; i < letterList.size(); i++) {
                if (!deleteLetter.toString().contains("," + letterList.get(i).getId())) {
                    deleteLetter.append(",").append(letterList.get(i).getId());
                }
            }
            user.setDeleteletter(deleteLetter.toString());
            if (penPal.deleteById() && user.updateById()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取与他的笔友关系")
    @GetMapping("/getPenpalRelation")
    public BaseResult getPenpalRelation(Integer id) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            QueryWrapper<TPenPal> penpalQuery = new QueryWrapper<>();
            penpalQuery.lambda().eq(TPenPal::getUserid, user.getId())
                    .eq(TPenPal::getPenpalid, id);
            TPenPal penPal = penPalService.getOne(penpalQuery);
            QueryWrapper<TLetter> letterQuery = new QueryWrapper<>();
            letterQuery.lambda()
                    .and(wrapper -> wrapper.eq(TLetter::getSenduserid, user.getId()).eq(TLetter::getAcceptuserid, id))
                    .or((wrapper -> wrapper.eq(TLetter::getSenduserid, id).eq(TLetter::getAcceptuserid, user.getId())));
            List<TLetter> letters = letterService.list(letterQuery);
            if (!Objects.isNull(penPal) || !Objects.isNull(letters)) {
                return success(new PenpalRelationResponseDto(letters, penPal));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取我的笔友列表")
    @GetMapping("/getPenpalList")
    public BaseResult getPenpalList() {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            List<TUser> users = penPalService.selectMyPenpal(user.getId());
            if (Objects.isNull(users)) {
                return fail("未获取到笔友");
            }
            return success(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

}
