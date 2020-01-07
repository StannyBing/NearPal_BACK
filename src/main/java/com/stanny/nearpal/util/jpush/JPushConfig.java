package com.stanny.nearpal.util.jpush;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import cn.jpush.api.JPushClient;

@Configuration
public class JPushConfig {
	@Value("${push.appkey}")
	private String appkey;
	@Value("${push.secret}")
	private String secret;

	private JPushClient jPushClient;
	

	/**
	 * 推送客户端
	 * @return
	 */
	@PostConstruct
	public void initJPushClient() {
		jPushClient = new JPushClient(secret, appkey);
	}
	
	/**
	 * 获取推送客户端
	 * @return
	 */
	public JPushClient getJPushClient() {
		return jPushClient;
	}
	
}
