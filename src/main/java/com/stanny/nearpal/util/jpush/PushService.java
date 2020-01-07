package com.stanny.nearpal.util.jpush;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 推送服务
 * 封装业务功能相关
 * @author zhachang [2019/03/12 下午3:47:55]
 */
@Service
public class PushService {
	
	/** 一次推送最大数量 (极光限制1000) */
	private static final int max_size = 800;
	
	@Autowired
	private JPushService jPushService;
	
	
	/**
	 * 推送全部, 不支持附加信息
	 * @author zhachang [2019/03/12 下午5:17:45]
	 * @return
	 */
	public boolean pushAll(PushBean pushBean){
		return jPushService.pushAll(pushBean);
	}
	
	/**
	 * 推送全部ios
	 * @author zhachang [2019/03/12 下午5:18:22]
	 * @return
	 */
	public boolean pushIos(PushBean pushBean){
		return jPushService.pushIos(pushBean);
	}
	
	/**
	 * 推送ios 指定id
	 * @author zhachang [2019/03/12 下午5:18:22]
	 * @return
	 */
	public boolean pushIos(PushBean pushBean, String ... registids){
		registids = checkRegistids(registids); // 剔除无效registed
		while (registids.length > max_size) { // 每次推送max_size个
			jPushService.pushIos(pushBean, Arrays.copyOfRange(registids, 0, max_size));
			registids = Arrays.copyOfRange(registids, max_size, registids.length);
		}
		return jPushService.pushIos(pushBean, registids);
	}
	
	/**
	 * 推送全部android
	 * @author zhachang [2019/03/12 下午5:18:30]
	 * @return
	 */
	public boolean pushAndroid(PushBean pushBean){
		return jPushService.pushAndroid(pushBean);
	}
	/**
	 * 自定义消息推送全部android
	 * @author zhachang [2019/03/12 下午5:18:30]
	 * @return
	 */
	public boolean pushAndroid(PushMsg pushMsg){
		return jPushService.pushAndroidMsgAll(pushMsg);
	}

	/**
	 * 推送android 指定id
	 * @author zhachang [2019/03/12 下午5:18:30]
	 * @return
	 */
	public boolean pushAndroidMsg(PushMsg pushMsg, String ... registids){
		registids = checkRegistids(registids); // 剔除无效registed
		while (registids.length > max_size) { // 每次推送max_size个
			jPushService.pushAndroidMsg(pushMsg, Arrays.copyOfRange(registids, 0, max_size));
			registids = Arrays.copyOfRange(registids, max_size, registids.length);
		}
		return jPushService.pushAndroidMsg(pushMsg, registids);
	}

	/**
	 * 推送android 指定id
	 * @author zhachang [2019/03/12 下午5:18:30]
	 * @return
	 */
	public boolean pushAndroid(PushBean pushBean, String ... registids){
		registids = checkRegistids(registids); // 剔除无效registed
		while (registids.length > max_size) { // 每次推送max_size个
			jPushService.pushAndroid(pushBean, Arrays.copyOfRange(registids, 0, max_size));
			registids = Arrays.copyOfRange(registids, max_size, registids.length);
		}
		return jPushService.pushAndroid(pushBean, registids);
	}

	/**
	 * 剔除无效registed
	 * @author zhachang [2016年7月15日 下午4:03:31]
	 * @param registids
	 * @return
	 */
	private String[] checkRegistids(String[] registids) {
		List<String> regList = new ArrayList<String>(registids.length);
		for (String registid : registids) {
			if (registid!=null && !"".equals(registid.trim())) {
				regList.add(registid);
			}
		}
		return regList.toArray(new String[0]);
	}
	
}