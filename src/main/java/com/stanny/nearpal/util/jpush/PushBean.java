package com.stanny.nearpal.util.jpush;

import java.util.Map;

public class PushBean {

	private String alert;	// 必填, 通知内容, 内容可以为空字符串，则表示不展示到通知栏。
	private Map<String, String> extras;	// 可选, 附加信息, 供业务使用。
	//android专用
	private String title;	// 可选, 通知标题	如果指定了，则通知里原来展示 App名称的地方，将展示成这个字段。
	
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public Map<String, String> getExtras() {
		return extras;
	}
	public void setExtras(Map<String, String> extras) {
		this.extras = extras;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
