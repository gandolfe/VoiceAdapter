package com.zhonghong.iflyplatformadapter;

import android.app.Application;
import android.os.Handler;

import com.iflytek.platformservice.PlatformHelp;
/***
 * ǿ�ҽ���ͻ������Բο�demo�Ľӿ�ʵ�֡����ǲ�Ҫֱ�ӣ����ڴ�demo�������̡�
 * @author li
 * ��Ҫ�ο�TestPlatformAdapterClient��� PlatformAdapterApp���ʵ��
 * 
 * ������Ϊ����������Ե��࣬û��̫��Ĳο�����
 */
public class PlatformAdapterApp extends Application{
	private PlatformAdapterClient platformClient;
	@Override
	public void onCreate(){
		super.onCreate();
		/**
		 * �������� ʵ�� PlatformClientListener �ӿڵĶ���
		 */
		platformClient=new PlatformAdapterClient(this);
		PlatformHelp.getInstance().setPlatformClient(platformClient);
		TTSController.getInstance(this);
	}
	
	//��ȡ��ǰƽ̨ʵ�������ڵ���ƽ̨�������ӿ�
	public static PlatformAdapterClient getPlatformClientInstance(){
		return (PlatformAdapterClient) PlatformHelp.getInstance().getPlatformClient();
	}
	
	public static Handler getUiHandler() {
		return uiHandler;
	}

	public static void setUiHandler(Handler uiHandler) {
		PlatformAdapterApp.uiHandler = uiHandler;
	}

	//��ǰҳ���handler�����ڸ���ui
	private static Handler uiHandler;
	
}
