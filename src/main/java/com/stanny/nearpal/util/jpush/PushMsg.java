package com.stanny.nearpal.util.jpush;

import java.util.Map;

/**
 * @author zhachang
 * @description 推送自定义消息
 * @date 2019/3/13 001310:25
 */

public class PushMsg {
    private String msgTitle;//自定义消息标题
    private String msgContent;//自定义消息内容
    private Map<String, String> extras;	// 可选, 附加信息, 供业务使用。

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }
}
