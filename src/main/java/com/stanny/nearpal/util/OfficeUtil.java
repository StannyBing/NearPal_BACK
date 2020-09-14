package com.stanny.nearpal.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stanny.nearpal.entity.TLetter;
import com.stanny.nearpal.entity.TUser;
import com.stanny.nearpal.service.LetterService;
import com.stanny.nearpal.service.UMPushService;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Xiangb on 2020/1/6.
 * 功能：
 */
public class OfficeUtil {

    private static OfficeUtil officeUtil;

    public static OfficeUtil getInstance() {
        if (officeUtil == null) {
            officeUtil = new OfficeUtil();
        }
        return officeUtil;
    }

    /**
     * 匹配一份旅行信件
     *
     * @param user
     */
    public boolean addNewLetter(LetterService letterService, TUser user) {
        QueryWrapper<TLetter> query = new QueryWrapper<>();
        query.lambda().eq(TLetter::getIsrandom, 1)
                .le(TLetter::getAccepttime, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 12))
                .ne(TLetter::getSenduserid, user.getId())
                .ne(TLetter::getAcceptuserid, user.getId())
                .ne(TLetter::getMstatus, 3);//将未读的还剩不到12个小时就会过期的不是自己发送或不是自己已经收到的的旅行信件重新分配给新人
        List<TLetter> letters = letterService.list(query);
        if (!letters.isEmpty()) {
            TLetter letter = letters.get((int) (Math.random() * letters.size()));
            letter.setAcceptuserid(user.getId());
            letter.setAccepttime(new Date(System.currentTimeMillis()));
            letter.setMstatus(2);
            letter.updateById();
            return true;
        }
        return false;
    }

    public void sendOfficeLetter(UMPushService pushService, String letterdetail, Integer acceptuserids) {
        TLetter letter = new TLetter();
        letter.setAccepttime(new Date());
        letter.setAcceptuserid(acceptuserids);
        letter.setSenduserid(1);
        letter.setMstatus(2);
        letter.setStampid("1");
        letter.setLettercall("远方的你");
        letter.setLetterinfo(getLetterInfo(letterdetail));
        letter.setLetterdetail(letterdetail);
        if (letter.insert()) {
            letter.setPostcode(StringUtils.leftPad(String.valueOf(letter.getId()), 6, '0'));
            letter.updateById();
        }
        pushService.pushAndroidAlias("提示", "您有新的来信，请注意查收", acceptuserids.toString());
    }

    public String getLetterInfo(String letterDetail) {
        String letterInfo = "";
        if (StringUtils.isNotBlank(letterDetail)) {
            letterInfo = letterDetail.trim().replace("　", "");
            if (letterInfo.length() >= 30) {
                letterInfo = letterInfo.substring(0, 30);
            }
            int index = 0;
            index = Math.max(letterInfo.lastIndexOf("。"), index);
            index = Math.max(letterInfo.lastIndexOf("."), index);
            index = Math.max(letterInfo.lastIndexOf("！"), index);
            index = Math.max(letterInfo.lastIndexOf("!"), index);
            index = Math.max(letterInfo.lastIndexOf("?"), index);
            index = Math.max(letterInfo.lastIndexOf("？"), index);
            if (index == 0) {
                index = Math.max(letterInfo.lastIndexOf("，") - 1, index);
                index = Math.max(letterInfo.lastIndexOf(",") - 1, index);
            }
            if (index > 0) {
                letterInfo = letterInfo.substring(0, index + 1).trim();
            }
        }
        return letterInfo;
    }

}
