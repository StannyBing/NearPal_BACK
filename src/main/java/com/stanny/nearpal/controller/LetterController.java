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
import com.stanny.nearpal.service.UserService;
import com.stanny.nearpal.util.CopyPropertiesUtil;
import com.stanny.nearpal.util.OfficeUtil;
import com.stanny.nearpal.util.jpush.JPushService;
import com.stanny.nearpal.util.jpush.PushBean;

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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

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
    private JPushService pushService;

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
            if (dto.getAcceptuserid() == null) {
                //设置收信时间，如果是随机信件，等待10分钟后马上转发给某个用户（为了避免遍历失败）
                acceptMills = System.currentTimeMillis() + 1000 * 60 * 10;
                //随机找到一个非当前用户的id
                QueryWrapper<TUser> query = new QueryWrapper<>();
                query.lambda().ne(TUser::getId, user.getId());
                List<TUser> users = userService.list(query);
                letter.setAcceptuserid(users.get((int) (Math.random() * users.size())).getId());
                letter.setIsrandom(1);//设置当前信件为随机信件
            } else {
                //设置收信时间，如果是定向信件，最短为一天，然后在1-3天内随机
                acceptMills = getAcceptMills(dto.getAcceptuserid(), request);
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
                //设置随机信件变为正常信件
                QueryWrapper<TLetter> query = new QueryWrapper<>();
                query.lambda().eq(TLetter::getId, dto.getReplyletterid());
                TLetter replayLetter = letterService.getOne(query);
                replayLetter.setIsrandom(0);
                replayLetter.updateById();
            }
            if (letter.insert()) {
                letter.setPostcode(StringUtils.leftPad(String.valueOf(letter.getId()), 6, '0'));
                letter.updateById();
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }


    private long getAcceptMills(Integer acceptUserId, HttpServletRequest request) {
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
        return System.currentTimeMillis() + 1000 * 60 * 60 * 24 + (long) (Math.random() * (1000 * 60 * 60 * 24 * 2));
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

    /**
     * 每隔1分钟执行一次
     * 查找到达发布时间的信件进行信件发布
     * 查询过期的随机信件重新分配
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
                PushBean pushBean = new PushBean();
                pushBean.setTitle("提示");
                pushBean.setAlert("您有新的来信，请注意查收");
                HashMap<String, String> map = new HashMap();
                map.put("letterid", letter.getId().toString());
                pushBean.setExtras(map);
                pushService.pushAndroid(pushBean, letter.getAcceptuserid().toString());
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
            //查询过期的随机信件重新分配
            QueryWrapper<TLetter> query = new QueryWrapper<>();
            query.lambda().eq(TLetter::getIsrandom, 1)
                    .le(TLetter::getAccepttime, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));
            List<TLetter> letters = letterService.list(query);
            for (TLetter letter : letters) {
                //随机找到一个非发送用户的id
                QueryWrapper<TUser> query1 = new QueryWrapper<>();
                query1.lambda().ne(TUser::getId, letter.getSenduserid());
                List<TUser> users = userService.list(query1);
                letter.setAcceptuserid(users.get((int) (Math.random() * users.size())).getId());
                //设置收信时间，如果是随机信件，等待10分钟后马上转发给某个用户（为了避免遍历失败）
                letter.setAccepttime(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
                letter.updateById();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
