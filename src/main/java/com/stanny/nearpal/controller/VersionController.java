package com.stanny.nearpal.controller;

import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TVersion;
import com.stanny.nearpal.service.VersionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Xiangb on 2020/1/13.
 * 功能：
 */
@Slf4j
@RequestMapping("version")
@RestController
@Api(tags = "文件下载")
public class VersionController extends BaseController {

    @Value("${file.upload.path}")
    private String filePath;

    @Autowired
    private VersionService versionService;


    @ApiOperation(value = "获取最新的版本")
    @GetMapping("/getVersion")
    public BaseResult getVersion() {
        try {
            TVersion version = versionService.getVersion();
            if (version != null) {
                return success(version);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "下载最新版本的apk，供外部调用")
    @GetMapping("/getNewApk")
    public void getNewApk(HttpServletResponse resp) {
        try {
            TVersion version = versionService.getVersion();
            File file = new File(filePath + version.getFilepath().substring(version.getFilepath().lastIndexOf("/") + 1));
            if (file.exists()) {
                resp.reset();
                resp.setContentType("application/octet-stream");
//                resp.setCharacterEncoding("utf-8");
                resp.setContentLength((int) file.length());
                resp.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes("GBK"), "ISO-8859-1"));

                byte[] buff = new byte[1024];
                BufferedInputStream bis = null;
                OutputStream os = null;
                try {
                    os = resp.getOutputStream();
                    bis = new BufferedInputStream(new FileInputStream(file));
                    int i = 0;
                    while ((i = bis.read(buff)) != -1) {
                        os.write(buff, 0, i);
                        os.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
