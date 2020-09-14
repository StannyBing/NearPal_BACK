package com.stanny.nearpal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bihe0832.packageinfo.bean.ApkInfo;
import com.bihe0832.packageinfo.utils.ApkUtil;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TDiary;
import com.stanny.nearpal.entity.TLetter;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.entity.TVersion;
import com.stanny.nearpal.service.DiaryService;
import com.stanny.nearpal.service.LetterService;
import com.stanny.nearpal.service.PenPalService;
import com.stanny.nearpal.service.UMPushService;
import com.stanny.nearpal.service.UserService;
import com.stanny.nearpal.util.OfficeUtil;
import com.stanny.nearpal.util.umpush.UMMessageBean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Xiangb on 2020/1/17.
 * 功能：
 */
@RequestMapping("system")
@RestController
@Api(tags = "系统")
@EnableScheduling
public class SystemController extends BaseController {
    @Autowired
    private LetterService letterService;

    @Autowired
    private UserService userService;

    @Autowired
    private PenPalService penPalService;

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private UMPushService pushService;


    @Value("${file.upload.path}")
    private String filePath;

    @ApiOperation(value = "新增官方信件")
    @PostMapping("/sendOfficeLetter")
    public BaseResult sendOfficeLetter(String letterdetail, String acceptuserids) {
        try {
            if (StringUtils.isNotEmpty(acceptuserids)) {//发给某人
                String[] userids = acceptuserids.split(",");
                for (String id : userids) {
                    OfficeUtil.getInstance().sendOfficeLetter(pushService, letterdetail, Integer.parseInt(id));
                }
            } else {//发给全部
                List<TUser> users = userService.list();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getId() != 1) {
                        OfficeUtil.getInstance().sendOfficeLetter(pushService, letterdetail, users.get(i).getId());
                    }
                }
            }
            return success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "测试")
    @PostMapping("/office")
    public void office() {
        String detail = "　　你好，我是楮先生。\n" +
                "　　之前有不少用户都遇到了信件发送失败的问题，经过一个热心用户（十分感谢你）的反馈，终于发现了是因为添加了Emoji的原因，" +
                "今天下午楮先生对这个问题进行了修复，大家可以尝试添加表情符号,如果仍然不行，可能是手机输入法的Emoji过于刁钻（我是绝对不会承认" +
                "是因为我技术不够的），请大家删除Emoji后再试。\n" +
                "　　此外，关于楮先生的使用，这里有几个小的东西需要说一下。\n" +
                "　　首先是写信的暂存功能，楮先生一直主张，信件不需要一蹴而就的写完，而是需要慢慢的写，在不同的时间写下不同的心情，写多一点，" +
                "真诚一点，慢一点，所以写信的暂存功能是很重要的，但是由于我没有详细的描述，导致很多的用户不知道如何保存信件。信件的保存有两种方式" +
                "1.自动保存，自动保存功能会在每隔一段时间后进行一次自动保存。2.点击发送->暂存按钮，也可以进行手动保存。保存成功后，可以直接退出，" +
                "主页的“书”，会变成“书（续）”，点击即可续写。\n" +
                "　　然后是回信的问题，也有朋友有反馈到，旅行信件发出去几天了，一直没有回信，这里我可以大概给大家描述一下随机信件的原理，随机信件初次发出后" +
                "，会到达某个用户的邮箱里，等待24小时后就会消失，再去到下一个用户的邮箱里，随着信件旅行次数的增多，停留的时间会慢慢的变短，流转的频率会加快，" +
                "这样，通过不断的流转旅行，总有一天会有一个愿意回复你的朋友，写下一封回信，回到你身边，希望大家耐心等候，你写的信，绝对不会消失在人群中。\n" +
                "　　最后是删除信件，长按信件后，会有弹框，可以删除信件哦，不过现在没有恢复信件的功能，希望大家谨慎操作，一定要恢复的话，可以直接联系我。\n" +
                "　　楮先生于2020年3月27日敬上。\uD83D\uDE01";
        List<TUser> users = userService.list();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() != 1) {
                OfficeUtil.getInstance().sendOfficeLetter(pushService, detail, users.get(i).getId());
            }
        }
//        OfficeUtil.getInstance().sendOfficeLetter(pushService, detail, 43);
    }

    @ApiOperation(value = "新增App版本")
    @PostMapping("/addVersion")
    public BaseResult addVersion(String path, String content, @RequestParam(defaultValue = "false") Boolean isForce) {
        try {
            TVersion version = new TVersion();
//            version.setVersioncode(Integer.parseInt(info.versionCode));
//            version.setVersionname(info.versionName);
            version.setContent(content);
            version.setFilepath(path);
            version.setIsforce(isForce ? 1 : 0);
            version.insert();
            return success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "上传apk")
    @PostMapping("/uploadApk")
    public BaseResult uploadApk(@RequestParam(value = "file") MultipartFile file,
                                String content, @RequestParam(defaultValue = "false") Boolean isForce) {
        String fileUploadPath = "";
        try {
            if (file.isEmpty()) {
                return fail("文件不能为空");
            }
            // 文件名
            String fileName = file.getOriginalFilename();
            File dest = new File(filePath + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            fileUploadPath = "file/temp/" + fileName;
//            fileUploadPath = "nearpal/" + fileName;

            ApkInfo info = new ApkInfo();
            ApkUtil.getApkInfo(dest.getPath(), info);

            TVersion version = new TVersion();
            version.setVersioncode(Integer.parseInt(info.versionCode));
            version.setVersionname(info.versionName);
            version.setContent(content);
            version.setFilepath(fileUploadPath);
            version.setIsforce(isForce ? 1 : 0);
            version.insert();

            return success(fileUploadPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "用户信息统计")
    @GetMapping("/getUserAnalysis")
    public BaseResult getUserAnalysis(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            QueryWrapper<TUser> wrapper = new QueryWrapper<>();
            wrapper.lambda().orderByDesc(TUser::getRegisttime);
            Page<TUser> page = new Page<>(pageNo, pageSize);
            IPage<TUser> users = userService.page(page, wrapper);
            return success(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "信件信息统计")
    @GetMapping("/getLetterAnalysis")
    public BaseResult getLetterAnalysis(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            QueryWrapper<TLetter> query = new QueryWrapper<>();
            query.lambda().ne(TLetter::getSenduserid, 1)
                    .orderByDesc(TLetter::getSendtime);
            Page<TLetter> page = new Page<>(pageNo, pageSize);
            IPage<TLetter> letters = letterService.page(page, query);
            return success(letters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "日记信息统计")
    @GetMapping("/getDiaryList")
    public BaseResult getDiaryList(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            QueryWrapper<TDiary> query = new QueryWrapper<>();
            query.lambda().orderByDesc(TDiary::getCreatetime);
            Page<TDiary> page = new Page<>(pageNo, pageSize);
            IPage<TDiary> letters = diaryService.page(page, query);
            return success(letters);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "手动补齐豆豆")
    @GetMapping("/makeUpBanlance")
    public BaseResult makeUpBalance() {
        try {
            UpdateWrapper<TUser> query = new UpdateWrapper<>();
            query.lambda()
                    .lt(TUser::getBalance, 10)
                    .set(TUser::getBalance, 10);
            if (userService.update(query)) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "旅行信件重定向")
    @GetMapping("/redirectRandomLetter")
    public BaseResult redirectRandomLetter() {
        try {
            UpdateWrapper<TUser> query = new UpdateWrapper<>();
            query.lambda()
                    .lt(TUser::getBalance, 10)
                    .set(TUser::getBalance, 10);
            if (userService.update(query)) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "推送广播")
    @PostMapping("/pushBroadCast")
    public BaseResult pushBroadCast(String title, String content, String alias) {
        try {
            //alias必须中文逗号分隔
            if (StringUtils.isEmpty(alias)) {
                pushService.pushAndroidBroadcast(title, content);
            } else {
                pushService.pushAndroidAlias(title, content, alias);
            }
            return success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "推送消息")
    @PostMapping("/pushMessage")
    public BaseResult pushMessage(UMMessageBean bean, String alias) {
        try {
            //alias必须中文逗号分隔
            if (StringUtils.isEmpty(alias)) {
                pushService.pushAndroidMessageAll(bean);
            } else {
                pushService.pushAndroidMessageAlias(bean, alias);
            }
            return success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }
}
