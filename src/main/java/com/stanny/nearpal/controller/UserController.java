package com.stanny.nearpal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.stanny.nearpal.dto.reqest.ChangePwdRequestDto;
import com.stanny.nearpal.dto.reqest.LoginRequestDto;
import com.stanny.nearpal.dto.reqest.RegisterRequestDto;
import com.stanny.nearpal.dto.reqest.UserForgetRequestDto;
import com.stanny.nearpal.dto.reqest.UserInfoRequestDto;
import com.stanny.nearpal.dto.reqest.VisitLoginRequestDto;
import com.stanny.nearpal.entity.BaseResult;
import com.stanny.nearpal.entity.TPenPal;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.service.LetterService;
import com.stanny.nearpal.service.PenPalService;
import com.stanny.nearpal.service.UMPushService;
import com.stanny.nearpal.service.UserService;
import com.stanny.nearpal.util.CopyPropertiesUtil;
import com.stanny.nearpal.util.OfficeUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@RequestMapping("user")
@RestController
@Api(tags = "用户")
@EnableScheduling
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private LetterService letterService;

    @Autowired
    private PenPalService penPalService;

    @Autowired
    private UMPushService pushService;

    @Autowired
    HttpServletRequest request;

    @ApiOperation(value = "使用用户名密码登录")
    @PostMapping("/loginByUsername")
    public BaseResult loginByPassword(@Valid @RequestBody LoginRequestDto dto) {
        Assert.notNull(dto, "请传入必要参数");
        Assert.notNull(dto.getPassword(), "请传入必要参数");
        QueryWrapper<TUser> query = new QueryWrapper<>();
        query.lambda().eq(TUser::getPassword, dto.getPassword())
                .and(wrapper -> wrapper.eq(TUser::getUsername, dto.getUsername()).or()
                        .eq(TUser::getTelephone, dto.getUsername()));
        TUser tempUser = userService.getOne(query);
        if (Objects.isNull(tempUser)) {
            return fail("账户或密码错误");
        }
        setLocation(tempUser);
        updateUser(tempUser);

        return success(tempUser);
    }


    @ApiOperation(value = "用户注册-用户名密码")
    @PostMapping("/registerByUsername")
    public BaseResult register(@RequestBody RegisterRequestDto dto) {
        Assert.notNull(dto, "请传入必要参数");
        Assert.notNull(dto.getPassword(), "请传入必要参数");
        Assert.notNull(dto.getUsername(), "请传入必要参数");
        TUser user;
        try {
            if (dto.getUserid() != null && dto.getUserid() != 0) {//游客账户转普通账户
                user = userService.getById(dto.getUserid());
                if (Objects.isNull(user)) {
                    return fail("该游客账户不存在");
                }
                if (user.getLogintype() != 5) {
                    return fail("该用户不是游客账户");
                }
                QueryWrapper<TUser> query = new QueryWrapper<>();
                query.lambda().eq(TUser::getUsername, dto.getUsername())
                        .or(wrapper -> wrapper.eq(TUser::getTelephone, dto.getTelephone())
                                .ne(TUser::getTelephone, "")
                                .isNotNull(TUser::getTelephone));
                TUser tempUser = userService.getOne(query);
                if (Objects.nonNull(tempUser)) {
                    return fail("该用户名/手机号已存在");
                }
                BeanUtils.copyProperties(dto, user);
                user.setLogintype(0);
                if (user.updateById()) {
                    updateUser(user);
                    setLocation(user);
                    return success(user);
                }
            } else {
                user = new TUser();
                BeanUtils.copyProperties(dto, user);
                user.setBalance(10);
                user.setNickname(dto.getUsername());
                user.setLogintype(0);
                QueryWrapper<TUser> query = new QueryWrapper<>();
                query.lambda().eq(TUser::getUsername, dto.getUsername())
                        .or(wrapper -> wrapper.eq(TUser::getTelephone, dto.getTelephone())
                                .ne(TUser::getTelephone, "")
                                .isNotNull(TUser::getTelephone));
                TUser tempUser = userService.getOne(query);
                if (Objects.nonNull(tempUser)) {
                    return fail("该用户名/手机号已存在");
                }
                if (user.insert()) {
                    updateUser(user);
                    setLocation(user);
                    sendNearPalLetter(user);
//                    addNewLetter(user);
//                    OfficeUtil.getInstance().addNewLetter(letterService, user);
                    addPenPal(user.getId(), 1);
                    return success(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "游客注册")
    @PostMapping("/registerByVisit")
    public BaseResult visitRegister() {
        TUser user = new TUser();
        try {
            user.setLogintype(5);
            user.setNickname("游客");
            if (user.insert()) {
                setLocation(user);
                updateUser(user);
                sendNearPalLetter(user);
//                addNewLetter(user);
//                OfficeUtil.getInstance().addNewLetter(letterService, user);
                addPenPal(user.getId(), 1);
                return success(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "游客登录")
    @PostMapping("/loginByVisit")
    public BaseResult loginByVisit(@Valid @RequestBody VisitLoginRequestDto dto) {
        Assert.notNull(dto, "请传入必要参数");
        Assert.notNull(dto.getId(), "请传入必要参数");
        QueryWrapper<TUser> query = new QueryWrapper<>();
        query.lambda().eq(TUser::getId, dto.getId())
                .eq(TUser::getLogintype, 5);
        TUser tempUser = userService.getOne(query);
        if (Objects.isNull(tempUser)) {
            return fail("该游客账户不存在或已转为注册用户");
        }
        setLocation(tempUser);
        updateUser(tempUser);
        return success(tempUser);
    }

    @ApiOperation(value = "修改用户信息")
    @PostMapping("/modifyUserInfo")
    public BaseResult modifyUserInfo(@RequestBody UserInfoRequestDto dto) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            BeanUtils.copyProperties(dto, user, CopyPropertiesUtil.getNullPropertyNames(dto));
            if (user.updateById()) {
                updateUser(user);
                return success(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "根据个人信息找回用户")
    @PostMapping("/forgetPwd")
    private BaseResult forgetPwd(@RequestBody UserForgetRequestDto dto) {
        try {
            QueryWrapper<TUser> query = new QueryWrapper<>();
            query.lambda()
                    .and(wrapper -> wrapper.eq(TUser::getUsername, dto.getUsername()).or().isNull(TUser::getUsername))
                    .and(wrapper -> wrapper.eq(TUser::getNickname, dto.getNickname()).or().isNull(TUser::getNickname))
                    .and(wrapper -> wrapper.eq(TUser::getTelephone, dto.getTelephone()).or().isNull(TUser::getTelephone));
            List<TUser> users = userService.list(query);
            if (users.size() == 1) {
                return success(users.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "修改密码")
    @PostMapping("/changePwd")
    private BaseResult changePwd(@RequestBody ChangePwdRequestDto dto) {
        try {
            TUser user = userService.getById(dto.getId());
            user.setPassword(dto.getPassword());
            user.setLogintype(5);
            if (user.updateById()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "根据用户id或用户名增加楮豆豆")
    @PostMapping("/addChuBeans")
    public BaseResult addChuBeans(@RequestParam(defaultValue = "0") Integer userid, @RequestParam(defaultValue = "") String nickname, @RequestParam(defaultValue = "0")Integer num) {
        try {
            if (num <= 0) {
                return fail("楮豆豆增加量不能小于0");
            }
            TUser tempUser = null;
            if (userid == 0) {
                QueryWrapper<TUser> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(TUser::getNickname, nickname);
                tempUser = userService.getOne(wrapper);
                if (Objects.isNull(tempUser)) {
                    return fail("该用户不存在");
                }
            } else {
                QueryWrapper<TUser> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(TUser::getId, userid);
                tempUser = userService.getOne(wrapper);
                if (Objects.isNull(tempUser)) {
                    return fail("该用户不存在");
                }
            }
            tempUser.setBalance(tempUser.getBalance() + num);
            if (tempUser.updateById()) {
                return success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    @ApiOperation(value = "获取我的信息")
    @GetMapping("/getMyInfo")
    public BaseResult getMyInfo(String appversion) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        user = user.selectById();//查找新的
//        UpdateWrapper<TUser> query = new UpdateWrapper<TUser>();
//        query.lambda().eq(TUser::getId, user.getId())
//                .set(TUser::getAppversion, appversion);
        user.setAppversion(appversion);
//        user.update(query);
        user.updateById();
        user = user.selectById();//查找新的
        updateUser(user);
        return success(user);
    }

    @ApiOperation(value = "根据id获取用户信息")
    @GetMapping("/getInfoById")
    public BaseResult getInfoById(String id) {
        TUser user = currentUser();
        if (user == null) {
            return timeOut();
        }
        try {
            TUser tempuser = userService.getById(id);
            if (Objects.isNull(tempuser)) {
                return fail("该用户不存在");
            }
            return success(tempuser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fail();
    }

    private void setLocation(TUser user) {
//        if (StringUtils.isEmpty(user.getUserlocation())) {
//            String ip = IpLocationUtil.getAddress(request);
//            user.setUserlocation(ip);
//            user.updateById();
//        }
    }

//    /**
//     * 匹配一份旅行信件
//     *
//     * @param user
//     */
//    private void addNewLetter(TUser user) {
//        QueryWrapper<TLetter> query = new QueryWrapper<>();
//        query.lambda().eq(TLetter::getIsrandom, 1)
//                .le(TLetter::getAccepttime, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 12))
//                .ne(TLetter::getMstatus, 3);//将未读的还剩不到12个小时就会过期的旅行信件重新分配给新人
//        List<TLetter> letters = letterService.list(query);
//        if (!letters.isEmpty()) {
//            TLetter letter = letters.get((int) (Math.random() * letters.size()));
//            letter.setAcceptuserid(user.getId());
//            letter.setAccepttime(new Date(System.currentTimeMillis()));
//            letter.setMstatus(2);
//            letter.updateById();
//        }
//    }

    /**
     * 发送官方欢迎信件
     */
    private void sendNearPalLetter(TUser user) {
        OfficeUtil.getInstance().sendOfficeLetter(pushService, "　　远方的你，见字如面，祝你安好。\n" +
                "　　你好，初次通过这样的方式与你聊天，我是楮（chǔ）先生的开发者，楮先生是一款早早就存在于我的想象中的一款应用，相比于即时聊天工具，它显得更加的慵懒，" +
                "在互联网发展的越来越快的当下，聊天、见面变得越来越简单，问候方式也变得越来越多，可越是这样，一句简单的“最近还好吗？”就变得越来越无足轻重，" +
                "无法舍弃，也就无法逃离。\n" +
                "　　说来有些奇怪，作为一个互联网中的开发者，却会成天想着做出这样的一个应用出来，可能，我心里也有一个文艺青年的心？其实我并不太想用“文艺”这个词" +
                "来定下楮先生的基调，它显得太矫情了，我只是想慢一点，让交流慢一点，让心慢一点，让思想慢一点，试着去等待，像写信一样，在某个晴朗的午后，铺开信纸，" +
                "慢慢写下自己的思念，自己的生活，写下天气变化，写下发生的趣事，写下听到的笑话，写下枝头小鸟的叫声，写下楼下市场的叫卖，写下对明年的期许，写下对" +
                "友人的安慰，写下对世界的看法，写下今天天气很好，心情很好，希望你也很好，写下绵绵细雨，心中哀愁，写下心中情义，你过得可好，写下案台上猫咪捣乱，" +
                "按下了点点梅花印，写下期望收到你的来信。\n" +
                "　　楮先生，原来其实叫做尺书，是我想了很久才想出来的名字，尺书，在古语中指书籍、书信、诏书，但是，被抢注了，没办法，我只能重新找，就找到了这里的楮先生，" +
                "楮先生是古人将文房四宝中的宣纸进行拟人化后出现的一个词语。通过楮先生寄出去的信件，并不会马上被收信人收到，而是会根据发信人与收信人的地理位置" +
                "（地理位置取自注册时的网络地址，根据该地址仅能定位到所属省份，不会获取到您的具体位置）计算出距离，再根据天气状况、写信时间、回信时间等因素，计算出来的" +
                "时间，我试着尽可能的去贴合写信的流程。（是的。我这里走的是EMS，而不是京东次日达，靠的是随缘）\n" +
                "　　偶尔你还会收到一些旅行信件，当然你也可以直接写一封，我们会将信件发给某一位笔友，他会有24小时的时间决定是否回信，成为你的笔友，24小时后，信件" +
                "会继续它的旅程，走到下一位的信箱里。所以你不必担心自己的信件消失在路上，总有一天，它会带着另一封信，带着一个愿意走近你的灵魂，倾听你的心声的人，来到" +
                "你的身边。\n" +
                "　　只要你，愿意等待。\n" +
                "　　我一直相信，等待这件事，本身就代表了很多的可能，希望楮先生能带给你更多的一种可能。希望在这里，你能找到真正懂你的人。\n" +
                "　　这里，有几条楮先生公约，嗯，是的，没错，我想的，为了能更好的保持楮先生的慵懒，为了不让这个氛围被打乱，我希望你能看看。\n" +
                "　　1、请不要在信中留下微信号，当然QQ号也不要，如果你想要更加快速的通讯方式，请使用微信摇一摇，或者附近的人，楮先生崇尚慢交流，适当的隐秘，" +
                "能更好的达成心灵上的交流，请勿留下即时通讯软件，希望你能理解。\n" +
                "　　2、请不要在信中打广告、做微商等等，因为我实在想不通，一个要在几天后才能被看到的广告，有这个必要吗，当然，如果被发现，是会进行封号处理的\n" +
                "　　3、请不要轻易邀请或同意进行线下见面，楮先生希望你能安全。\n" +
                "　　4、请不要一次只发几个字。。。，毕竟这是写信，要几天后才收的到，你可以多写一点，我们提供了暂存功能，你也可以一天写一点，当然，这只是建议。\n" +
                "　　\n" +
                "　　楮先生目前处于才开始的状态，可能会存在很多不尽人意的地方，甚至有些问题的存在，如果你有发现任何问题，欢迎向我进行反馈，我一直在这里等待着你。\n" +
                "　　我对楮先生一直有一个期望，就是有一天，楮先生能不仅仅存在于我们的国家，能走向更多的国家，由我来提供翻译功能，让你能和这个世界各个角落的人进行交流。我，想和这个世界说说话。" +
                "我希望，这并不是一件需要等待很久的事情。\n" +
                "　　啰啰嗦嗦了这么久，希望你不要厌烦，回复这封信，将默认发送一封旅行信件。初次写信将获得3颗楮豆豆。希望在楮先生，你能找到那个与你心灵相通的人。\n" +
                "　　祝好！", user.getId());
        addPenPal(user.getId(), 1);
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

    /**
     * 每月1号0点0分0秒触发
     * 给不足10颗楮豆豆的用户补齐10颗
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void addBalance() {
        try {
            UpdateWrapper<TUser> update = new UpdateWrapper<>();
            update.lambda().lt(TUser::getBalance, 10)
                    .set(TUser::getBalance, 10);
            userService.update(update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
