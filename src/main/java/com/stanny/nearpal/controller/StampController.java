package com.stanny.nearpal.controller;

import com.stanny.nearpal.service.StampService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@RequestMapping("stamp")
@RestController
@Api(tags = "邮票")
public class StampController  extends BaseController{

    @Autowired
    private StampService stampService;


}
