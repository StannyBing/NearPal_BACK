package com.stanny.nearpal.util.umpush;

/**
 * Created by Xiangb on 2020/1/9.
 * 功能：
 */
public class UMMessageBean {

    private Integer type;//0 普通弹窗  1链接

    private String info;

    private String linkUrl;

    private String userId;

    private String letterId;

    public UMMessageBean(Integer type, String info, String linkUrl, String userId, String letterId) {
        this.type = type;
        this.info = info;
        this.linkUrl = linkUrl;
        this.userId = userId;
        this.letterId = letterId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLetterId() {
        return letterId;
    }

    public void setLetterId(String letterId) {
        this.letterId = letterId;
    }
}
