package com.stanny.nearpal.service;

import com.stanny.nearpal.util.umpush.AndroidBroadcast;
import com.stanny.nearpal.util.umpush.AndroidCustomizedcast;
import com.stanny.nearpal.util.umpush.AndroidNotification;
import com.stanny.nearpal.util.umpush.UMMessageBean;
import com.stanny.nearpal.util.umpush.UMPushClient;

import org.json.JSONObject;
import org.springframework.stereotype.Service;


/**
 * Created by Xiangb on 2020/1/9.
 * 功能：
 */
@Service
public class UMPushService {

    private UMPushClient client = new UMPushClient();
    private String appkey = "5df046144ca3577bb3000f68";
    private String appMasterSecret = "ita4hfpqqaeyoxdbkpmpxdehzqxgxd6x";


    /**
     * 发送Android广播-所有人
     *
     * @param title 标题
     * @param text  文字
     */
    public void pushAndroidBroadcast(String title, String text) {
        try {
            AndroidBroadcast broadcast = new AndroidBroadcast(appkey, appMasterSecret);
            broadcast.setTicker("楮先生");//通知栏提示文字
            broadcast.setTitle(title);//通知栏标题
            broadcast.setText(text);//通知栏文字描述
            broadcast.goAppAfterOpen();//点击后启动app
            broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            broadcast.setProductionMode();
            client.send(broadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送Android广播-指定用户
     *
     * @param title
     * @param text
     * @param alias
     */
    public void pushAndroidAlias(String title, String text, String... alias) {
        try {
            StringBuilder aliases = new StringBuilder();
            for (String s : alias) {
                aliases.append(s).append(",");
            }
            AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(appkey, appMasterSecret);
            customizedcast.setAlias(aliases.toString(), "id");
            customizedcast.setTicker("楮先生");
            customizedcast.setTitle(title);
            customizedcast.setText(text);
            customizedcast.goAppAfterOpen();
            customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            customizedcast.setProductionMode();
            client.send(customizedcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送自定义调戏-所有人
     *
     * @param bean
     */
    public void pushAndroidMessageAll(UMMessageBean bean) {
        try {
            AndroidBroadcast broadcast = new AndroidBroadcast(appkey, appMasterSecret);
            broadcast.setCustomField(new JSONObject(bean));
            broadcast.setDisplayType(AndroidNotification.DisplayType.MESSAGE);
            broadcast.setProductionMode();
            client.send(broadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送自定义消息-指定用户
     *
     * @param bean
     * @param alias
     */
    public void pushAndroidMessageAlias(UMMessageBean bean, String... alias) {
        try {
            StringBuilder aliases = new StringBuilder();
            for (String s : alias) {
                aliases.append(s).append(",");
            }
            AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(appkey, appMasterSecret);
            customizedcast.setAlias(aliases.toString(), "id");
            customizedcast.setCustomField(new JSONObject(bean));
            customizedcast.setDisplayType(AndroidNotification.DisplayType.MESSAGE);
            customizedcast.setProductionMode();
            client.send(customizedcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
