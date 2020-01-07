package com.stanny.nearpal.util.jpush;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;

/**
 * 极光推送
 * 封装第三方api相关
 *
 * @author zhachang [2019/03/12 下午3:47:55]
 */
@Service
public class JPushService {

    private final Logger logger = LoggerFactory.getLogger(JPushService.class);

    @Autowired
    private JPushConfig jPushConfig;

    /**
     * 广播 (所有平台，所有设备, 不支持附加信息)
     *
     * @param pushBean 推送内容
     * @return
     * @author zhachang [2019/03/12 下午4:12:08]
     */
    public boolean pushAll(PushBean pushBean) {
        return sendPush(PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                .setNotification(Notification.alert(pushBean.getAlert()))
                .setOptions(Options.newBuilder().build())
                .build());
    }

    /**
     * ios广播
     *
     * @param pushBean 推送内容
     * @return
     * @author zhachang [2019/03/12 下午3:59:21]
     */
    public boolean pushIos(PushBean pushBean) {
        return sendPush(PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.all())
                .setNotification(Notification.ios(pushBean.getAlert(), pushBean.getExtras()))
                .setOptions(Options.newBuilder().build())
                .build());
    }

    /**
     * ios通过registid推送 (一次推送最多 1000 个)
     *
     * @param pushBean  推送内容
     * @param registids 推送id
     * @return
     * @author zhachang [2019/03/12 下午3:59:21]
     */
    public boolean pushIos(PushBean pushBean, String... registids) {
        return sendPush(PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(registids))
                .setNotification(Notification.ios(pushBean.getAlert(), pushBean.getExtras()))
                .setOptions(Options.newBuilder().build())
                .build());
    }


    /**
     * android广播
     *
     * @param pushBean 推送内容
     * @return
     * @author zhachang [2019/03/12 下午3:59:21]
     */
    public boolean pushAndroid(PushBean pushBean) {
        return sendPush(PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.all())
                .setNotification(Notification.android(pushBean.getAlert(), pushBean.getTitle(), pushBean.getExtras()))
                .setOptions(Options.newBuilder().build())
                .build());
    }

    /**
     * android通过registid推送 (一次推送最多 1000 个)
     *
     * @param pushBean  推送内容
     * @param registids 推送id
     * @return
     * @author zhachang [2019/03/12 下午3:59:21]
     */
    public boolean pushAndroid(PushBean pushBean, String... registids) {
        return sendPush(PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(registids == null ? Audience.newBuilder().build()
                        : Audience.newBuilder().addAudienceTarget(AudienceTarget.alias(registids)).build())
                .setNotification(Notification.android(pushBean.getAlert(), pushBean.getTitle(), pushBean.getExtras()))
                .setOptions(Options.newBuilder().build())
                .build());
    }

    /**
     * android通过registid推送 (一次推送最多 1000 个)
     *
     * @param pushMsg   推送内容
     * @param registids 推送id
     * @return
     * @author zhachang [2019/03/12 下午3:59:21]
     */
    public boolean pushAndroidMsg(PushMsg pushMsg, String... registids) {
        Message.Builder msg = Message.newBuilder();

        msg.setTitle(pushMsg.getMsgTitle());
        msg.setMsgContent(pushMsg.getMsgContent());
        if (null != pushMsg.getExtras()) {
            msg.addExtras(pushMsg.getExtras());
        }

        return sendPush(PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(registids == null ? Audience.newBuilder().build()
                        : Audience.newBuilder().addAudienceTarget(AudienceTarget.alias(registids)).build())
                .setMessage(msg.build())
                .build());
    }

    public boolean pushAndroidMsgAll(PushMsg pushMsg) {
        Message.Builder msg = Message.newBuilder();

        msg.setTitle(pushMsg.getMsgTitle());
        msg.setMsgContent(pushMsg.getMsgContent());
        if (null != pushMsg.getExtras()) {
            msg.addExtras(pushMsg.getExtras());
        }

        return sendPush(PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.all())
                .setMessage(msg.build())
                .build());
    }

    /**
     * 调用api推送
     *
     * @param pushPayload 推送实体
     * @return
     * @author zhachang [2019/03/12 下午4:19:03]
     */
    private boolean sendPush(PushPayload pushPayload) {
        logger.info("发送极光推送请求: {}", pushPayload);
        PushResult result = null;
        try {
            result = jPushConfig.getJPushClient().sendPush(pushPayload);
        } catch (APIConnectionException e) {
            logger.error("极光推送连接异常: ", e);
        } catch (APIRequestException e) {
            logger.error("极光推送请求异常: ", e);
        }
        if (result != null && result.isResultOK()) {
            logger.info("极光推送请求成功: {}", result);
            return true;
        } else {
            logger.info("极光推送请求失败: {}", result);
            return false;
        }
    }

}