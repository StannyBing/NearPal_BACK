package com.stanny.nearpal.util;

import com.stanny.nearpal.entity.TLetter;
import com.stanny.nearpal.util.jpush.JPushService;
import com.stanny.nearpal.util.jpush.PushBean;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;

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

    public void sendOfficeLetter(JPushService pushService, String letterdetail, Integer acceptuserids) {
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
        PushBean pushBean = new PushBean();
        pushBean.setTitle("提示");
        pushBean.setAlert("您有新的来信，请注意查收");
        HashMap<String, String> map = new HashMap();
        map.put("letterid", letter.getId().toString());
        pushBean.setExtras(map);
        pushService.pushAndroid(pushBean, acceptuserids.toString());
    }

    public String getLetterInfo(String letterDetail) {
        String letterInfo = "";
        if (StringUtils.isNotBlank(letterDetail)) {
            letterInfo = letterDetail.trim().replace("　", "");
            if (letterInfo.length() >= 30) {
                letterInfo = letterInfo.substring(0, 30);
            }
            int index = 0;
            index = Math.max(letterInfo.indexOf("。"), index);
            index = Math.max(letterInfo.indexOf("."), index);
            index = Math.max(letterInfo.indexOf("?"), index);
            index = Math.max(letterInfo.indexOf("？"), index);
            if (index > 0) {
                letterInfo = letterInfo.substring(0, index + 1).trim();
            }
        }
        return letterInfo;
    }

}
