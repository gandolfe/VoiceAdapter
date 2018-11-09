package com.zhonghong.iflyplatformadapter;

import android.app.Application;
import android.os.Handler;

import com.iflytek.platformservice.PlatformHelp;
/***
 * 强烈建议客户，可以参考demo的接口实现。但是不要直接，基于此demo开发工程。
 * @author li
 * 主要参考TestPlatformAdapterClient类和 PlatformAdapterApp类的实现
 * 
 * 其他类为助理自身测试的类，没有太大的参考意义
 */
public class PlatformAdapterApp extends Application{
	private PlatformAdapterClient platformClient;
	@Override
	public void onCreate(){
		super.onCreate();
		/**
		 * 给助理传递 实现 PlatformClientListener 接口的对象
		 */
		platformClient=new PlatformAdapterClient(this);
		PlatformHelp.getInstance().setPlatformClient(platformClient);
		TTSController.getInstance(this);
	}
	
	//获取当前平台实例，用于调用平台适配器接口
	public static PlatformAdapterClient getPlatformClientInstance(){
		return (PlatformAdapterClient) PlatformHelp.getInstance().getPlatformClient();
	}
	
	public static Handler getUiHandler() {
		return uiHandler;
	}

	public static void setUiHandler(Handler uiHandler) {
		PlatformAdapterApp.uiHandler = uiHandler;
	}

	//当前页面的handler，用于更新ui
	private static Handler uiHandler;
	
}
