package com.stanny.nearpal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.stanny.nearpal.dto.reqest.LetterRequestDto;
import com.stanny.nearpal.dto.response.LetterResponseDto;
import com.stanny.nearpal.dto.response.LetterUserResponseDto;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TLetter;
import com.stanny.nearpal.entity.TPenPal;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.service.LetterService;
import com.stanny.nearpal.service.PenPalService;
import com.stanny.nearpal.service.UMPushService;
import com.stanny.nearpal.service.UserService;
import com.stanny.nearpal.util.CopyPropertiesUtil;
import com.stanny.nearpal.util.OfficeUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@RequestMapping("letter")
@RestController
@Api(tags = "信件")
@EnableScheduling
public class LetterController extends BaseController {

    @Autowired
    private LetterService letterService;

    @Autowired
    private UserService userService;

    @Autowired
    private PenPalService penPalService;

    @Autowired
    private UMPushService pushService;


    @ApiOperation(value = "新增信件")
    @PostMapping("/sendLetter")
    public BaseResult sendLetter(@RequestBody LetterRequestDto dto) {
        Assert.notNull(dto, "请传入必要参数");
        Assert.notNull(dto.getStampid(), "请传入必要参数");
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        TLetter letter = new TLetter();
        try {
            BeanUtils.copyProperties(dto, letter, CopyPropertiesUtil.getNullPropertyNames(dto));
            letter.setSenduserid(user.getId());
            long acceptMills;
            if (dto.getAcceptuserid() == null || dto.getAcceptuserid() == 1) {
                //设置收信时间，如果是旅行信件，等待2分钟后马上转发给某个用户（为了避免遍历失败）
                acceptMills = System.currentTimeMillis() + 1000 * 60 * 2;
                //旅行找到一个非当前用户的id
                List<TUser> users = userService.getRandomUser(user.getId());
                letter.setAcceptuserid(users.get((int) (Math.random() * users.size())).getId());
                letter.setIsrandom(1);//设置当前信件为旅行信件
                letter.setRandomuserids(letter.getAcceptuserid() + ",");
            } else {
                acceptMills = getAcceptMills(dto.getAcceptuserid());
//                acceptMills = System.currentTimeMillis() + 1000 * 60 * 60 * 24 + (long) (Math.random() * (1000 * 60 * 60 * 24 * 2));
            }
            letter.setAccepttime(new Date(acceptMills));
            String letterInfo = OfficeUtil.getInstance().getLetterInfo(dto.getLetterdetail());
            letter.setLetterinfo(letterInfo);
            if (dto.getAcceptuserid() != null) {
                //建立笔友关系
                addPenPal(user.getId(), dto.getAcceptuserid());
            }
            if (dto.getReplyletterid() != null && dto.getReplyletterid() != 0) {
                //设置旅行信件变为正常信件
                QueryWrapper<TLetter> query = new QueryWrapper<>();
                query.lambda().eq(TLetter::getId, dto.getReplyletterid());
                TLetter replayLetter = letterService.getOne(query);
                replayLetter.setIsrandom(0);
                replayLetter.updateById();
            }
            QueryWrapper<TLetter> query2 = new QueryWrapper<>();
            query2.lambda().eq(TLetter::getSenduserid, user.getId());
            boolean isFirstLetter = letterService.count(query2) == 0;
            if (letter.insert()) {
                letter.setPostcode(StringUtils.leftPad(String.valueOf(letter.getId()), 6, '0'));
                letter.updateById();
                user.setBalance(user.getBalance() - 1);
                int changeNum = 0;
                boolean resetFindChange = false;
                if (isFirstLetter) {
                    //初次写信，赠送3颗楮豆豆
                    user.setBalance(user.getBalance() + 3);
                } else {
                    //几率重置寻的机会
                    boolean isFindUsed = false;//是否已经寻过
                    try {
                        if (redisClientUtil.isEnable()) {
                            String status = redisClientUtil.get(user.getId() + "_findLetter");
                            isFindUsed = "true".equals(status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //如果是发出的旅行信件，并且今日的寻机会已用掉，将有几率重置寻
                    if (letter.getIsrandom() != null && letter.getIsrandom() == 1 && isFindUsed) {
                        if (Math.random() < 0.4) {
                            resetFindChange = true;
                            resetFindById(user.getId());
                        }
                    }
                    //如果已重置寻，就不再获取豆豆
                    if (!resetFindChange) {
                        //根据字数增加抽中楮豆豆的几率
                        double chance = 0.10;
                        if (letter.getLetterdetail().length() > 1000) {
                            chance += 0.07;
                        } else if (letter.getLetterdetail().length() > 500) {
                            chance += 0.05;
                        } else if (letter.getLetterdetail().length() > 300) {
                            chance += 0.03;
                        } else if (letter.getLetterdetail().length() > 100) {
                            chance += 0.01;
                        }
                        //根据用户剩余豆豆增加抽中的几率
                        if (user.getBalance() < 5) {
                            chance += 0.03;
                        }
                        //确认抽中
                        if (Math.random() < chance) {
                            changeNum = 1;
                            if (Math.random() < 0.3) {
                                changeNum = 2;
                            }
                        }
                        user.setBalance(user.getBalance() + changeNum);
                    }
                }
                user.updateById();
                updateUser(user);
                if (resetFindChange) {
                    return success("恭喜，你本日的“寻”机会已重置");
                } else if (changeNum == 0) {
                    return success("发送成功");
                } else {
                    return success("恭喜你获得" + changeNum + "颗楮豆豆！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }


    private long getAcceptMills(Integer acceptUserId) {
        //根据地理坐标设置接收时间
//        try {
//            TUser acceptUser = userService.getById(acceptUserId);
//            if (StringUtils.isNotEmpty(acceptUser.getUserlocation()) && StringUtils.isNotEmpty(currentUser(request).getUserlocation())) {
//                double lon1 = IpLocationUtil.MillierConvertion(Double.parseDouble(acceptUser.getUserlocation().split(",")[0]), Double.parseDouble(acceptUser.getUserlocation().split(",")[1])).getX();
//                double lat1 = IpLocationUtil.MillierConvertion(Double.parseDouble(acceptUser.getUserlocation().split(",")[0]), Double.parseDouble(acceptUser.getUserlocation().split(",")[1])).getY();
//                double lon2 = IpLocationUtil.MillierConvertion(Double.parseDouble(currentUser(request).getUserlocation().split(",")[0]), Double.parseDouble(currentUser(request).getUserlocation().split(",")[1])).getX();
//                double lat2 = IpLocationUtil.MillierConvertion(Double.parseDouble(currentUser(request).getUserlocation().split(",")[0]), Double.parseDouble(currentUser(request).getUserlocation().split(",")[1])).getY();
//                IpLocationUtil.getDistance(lon1, lat1, lon2, lat2);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //设置收信时间，如果是定向信件，最短为0.25天，然后在0.25-1.75天内旅行
        return System.currentTimeMillis() + 1000 * 60 * 60 * 6 + (long) (Math.random() * (1000 * 60 * 60 * 36));
    }

    /**
     * 添加笔友关系
     *
     * @param userid
     * @param penpalid
     */
    private void addPenPal(Integer userid, Integer penpalid) {
        QueryWrapper<TPenPal> query = new QueryWrapper<>();
        query.lambda().eq(TPenPal::getUserid, userid)
                .eq(TPenPal::getPenpalid, penpalid);
        if (Objects.isNull(penPalService.getOne(query))) {
            TPenPal penPal = new TPenPal();
            penPal.setUserid(userid);
            penPal.setPenpalid(penpalid);
            penPal.insert();
        }
    }

    @ApiOperation(value = "获取我收到的信件")
    @GetMapping("/getAcceptLetters")
    public BaseResult getAcceptLetters() {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            List<LetterUserResponseDto> letterList = letterService.getAcceptLettes(user.getId());
            if (letterList != null) {
                return success(letterList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取一封旅行信件")
    @GetMapping("/findRandomLetter")
    public BaseResult findRandomLetter() {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            boolean isSuccess = false;
            try {
                if (redisClientUtil.isEnable()) {
                    //限制一天只能访问一次
                    if (redisClientUtil.get(user.getId() + "_findLetter") == null || redisClientUtil.get(user.getId() + "_findLetter").equals("false")) {
                        redisClientUtil.set(user.getId() + "_findLetter", "true");
                        redisClientUtil.expire(user.getId() + "_findLetter", 60 * 60 * 48);
                        //限制寻的概率
                        if (Math.random() < 0.3) {
                            isSuccess = OfficeUtil.getInstance().addNewLetter(letterService, user);
                        }
                    } else {
                        return fail("本日的寻已使用，发出旅行信件有几率重置哦");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (isSuccess) {
                user.setBalance(user.getBalance() - 1);
                user.updateById();
                return success("恭喜你，找到一封旅行信件!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail("没有寻到>-<，明天再试试吧");
    }

    @ApiOperation(value = "获取我发出的信件")
    @GetMapping("/getSendLetters")
    public BaseResult getSendLetters() {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
//            QueryWrapper<TLetter> query = new QueryWrapper<>();
//            query.lambda()
//                    .eq(TLetter::getSenduserid, user.getId())
//                    .ne(TLetter::getIsrandom, 1);
            List<LetterUserResponseDto> letterList = letterService.getSendLettes(user.getId());
            if (letterList != null) {
                return success(letterList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "判断是否尚未发送信件")
    @GetMapping("/isNotSend")
    public BaseResult isNotSend() {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            QueryWrapper<TLetter> query = new QueryWrapper<>();
            query.lambda().eq(TLetter::getSenduserid, user.getId());
            return success(letterService.count(query) == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取与指定用户的信件往来")
    @GetMapping("/getLettersWithId")
    public BaseResult getLettersWithId(Integer userid) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        if (Objects.isNull(userService.getById(userid))) {
            return fail("该用户不存在");
        }
        try {
            List<LetterUserResponseDto> letterList = letterService.getLettersWithId(user.getId(), userid);
            if (letterList != null) {
                return success(letterList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取我的旅行信件")
    @GetMapping("/getMyRandomLetter")
    public BaseResult getMyRandomLetter() {
        try {
            TUser user = currentUser();
            if (user == null) {
                return timeOut();
            }
            List<LetterUserResponseDto> letters = letterService.getRandomLetters(user.getId());
            return success(letters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取和我有关的所有信件")
    @GetMapping("/getAllLetters")
    public BaseResult getAllLetters() {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        List<LetterResponseDto> responseDtos = new ArrayList<>();
        try {
            QueryWrapper<TLetter> query = new QueryWrapper<>();
            query.lambda().eq(TLetter::getSenduserid, user.getId())
                    .or(wrapper -> wrapper.eq(TLetter::getAcceptuserid, user.getId()).le(TLetter::getAccepttime, new Date(System.currentTimeMillis())))
                    .orderByDesc(TLetter::getAccepttime);

            List<TLetter> letterList = letterService.list(query);
            if (!letterList.isEmpty()) {
                for (TLetter letter : letterList) {
                    responseDtos.add(new LetterResponseDto(letter, userService.getById(letter.getSenduserid())));
                }
            }
            return success(responseDtos);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "根据id获取信件信息")
    @GetMapping("/getInfoById")
    public BaseResult getInfoById(String id) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            TLetter templetter = letterService.getById(id);
            if (Objects.isNull(templetter)) {
                return fail("该信件不存在");
            }
            return success(templetter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "拆开信件")
    @PostMapping("/readLetter")
    public BaseResult readLetter(String id) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            TLetter templetter = letterService.getById(id);
            if (Objects.isNull(templetter)) {
                return fail("该信件不存在");
            }
            templetter.setMstatus(3);
            if (templetter.updateById()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "删除信件(不显示)")
    @PostMapping("/deleteLetter")
    public BaseResult deleteLetter(String id) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            user.setDeleteletter(user.getDeleteletter() + "," + id);
            if (user.updateById()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "放弃信件")
    @PostMapping("/giveupLetter")
    public BaseResult giveupLetter(String id) {
        try {
            TLetter letter = letterService.getById(id);
            if (!Objects.isNull(letter)) {
                redirectLetter(letter);
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    /**
     * 每天0点0分0秒触发
     * 清除“寻”的状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetFind() {
        try {
            List<TUser> users = userService.list();
            for (TUser user : users) {
                resetFindById(user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetFindById(Integer userid) {
        try {
            if (redisClientUtil.isEnable()) {
                redisClientUtil.set(userid + "_findLetter", "false");
                redisClientUtil.expire(userid + "_findLetter", 60 * 60 * 48);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每隔1分钟执行一次
     * 查找到达发布时间的信件进行信件发布
     * 查询过期的旅行信件重新分配
     */
    @Scheduled(fixedDelay = 1000 * 60)
    public void pulishLetter() {
        try {
            //查找到达发布时间的信件进行信件发布
            QueryWrapper<TLetter> query = new QueryWrapper<>();
            query.lambda().le(TLetter::getAccepttime, new Date())
                    .eq(TLetter::getMstatus, 1);
            List<TLetter> letters = letterService.list(query);
            for (int i = 0; i < letters.size(); i++) {
                TLetter letter = letters.get(i);
                pushService.pushAndroidAlias("提示", "您有新的来信，请注意查收", letter.getAcceptuserid().toString());
            }
            //数据更新
            UpdateWrapper<TLetter> update = new UpdateWrapper<>();
            update.lambda().le(TLetter::getAccepttime, new Date())
                    .eq(TLetter::getMstatus, 1)
                    .set(TLetter::getMstatus, 2);
            letterService.update(update);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //查询过期的旅行信件重新分配
            QueryWrapper<TLetter> query = new QueryWrapper<>();
            query.lambda().eq(TLetter::getIsrandom, 1)
                    .le(TLetter::getAccepttime, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
            List<TLetter> letters = letterService.list(query);
            for (TLetter letter : letters) {
                redirectLetter(letter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 信件重定向
     *
     * @param letter
     */
    private void redirectLetter(TLetter letter) {
        //随机找到一个非发送用户的id
        List<TUser> users = userService.getRandomUser(letter.getSenduserid());
        if (!users.isEmpty()) {
            letter.setAcceptuserid(users.get((int) (Math.random() * users.size())).getId());
            letter.setRandomuserids(letter.getRandomuserids() + letter.getAcceptuserid() + ",");
            //设置收信时间，如果是旅行信件，等待2分钟后马上转发给某个用户（为了避免遍历失败）同时为了方便测试
            //根据旅行信件流转次数决定收信的时间，避免等待过久
            String[] ids = letter.getRandomuserids().split(",");
            long randomTime;
            if (ids.length > 5) {
                randomTime = 1000 * 60 * 60 * 16;//意味着只有8小时了，而不是还有16个小时
            } else {
                randomTime = 1000 * 60 * 60 * 4 * (ids.length - 1);
            }
            letter.setAccepttime(new Date(System.currentTimeMillis() - randomTime + 1000 * 60 * 2));
            letter.setMstatus(1);
            letter.updateById();
        }
    }

}
