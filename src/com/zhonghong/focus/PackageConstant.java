package com.zhonghong.focus;

import android.os.SystemProperties;
import android.util.Log;

public class PackageConstant {
	/** 收音*/
	public static String RADIO_FOCUS;
	public static String RADIO_SERVER_ACTION;
	/** 蓝牙音乐*/
	public static String BT_FOCUS;
	/** 蓝牙电话*/
	public static String BT_TALKING_FOCUS;
	/** 视频*/
	public static String VIDEO_FOCUS;
	/** 音乐*/
	public static String MUSIC_FOCUS;
	/** 音乐2*/
	public static String MUSIC_FOCUS_MAINT;
	/** 手机互联*/
	public static String INTERCONECETED_FOCUS;
	/** 视频监控*/
	public static final String VIDEOSUR_FOCUS 	= "com.zhonghong.videosur";
	/**carlife 音乐*/
	public static final String CARLIFE_MUSIC_FOCUS 	= "com.zhonghong.carlife.audio.FocusMusic";
	/**carlife tts 不需要处理此焦点 */
	public static final String CARLIFE_TTS_FOCUS 	= "com.zhonghong.carlife.audio.FocusTts";
	/**carlife vr 不需要处理此焦点*/
	public static final String CARLIFE_VR_FOCUS 	= "com.zhonghong.carlife.audio.FocusVr";
	
	/** 设置服务*/
	public static  String SETTINGS;
	
	//第三方应用
	/** 讯飞语音*/
	public static final String IFLYTEK = "com.zhonghong.iflyplatformadapter.PlatformAdapterClient";
	/** 酷我*/
	public static final String KW_FOCUS 		= "cn.kuwo.service.kwplayer.PlayManager";
	/** 喜马拉雅电台 com.ximalaya.ting.android.car*/
	public static final String XIMALAYA_RADIO	= "com.ximalaya.ting.android.opensdk.player.service";
	/** 考拉电台*/
	public static final String KAOLA_FOCUS  = "com.kaolafm.sdk.vehicle.KlSdkVehicle";
	/** 语音助手本身抢占焦点*/
	public static final String SOUGOU_SIRI 	= "com.sogou.siri";
	
	public static String PRODUCT_MODEL;
	
	static {
		try {
			RADIO_FOCUS = "com.zhonghong.radio.manager.AudioFocusManager";
			RADIO_SERVER_ACTION = "com.zhonghong.radio.startService";
			BT_FOCUS = "com.zhonghong.btmusic.manager.BTMusicManager";
			VIDEO_FOCUS = "com.zhonghong.media.player.MeidaPlayerManager";
			MUSIC_FOCUS = "com.zhonghong.music.manger.MediaPlayManager";
			MUSIC_FOCUS_MAINT = "com.zhonghong.music.MainActivity";
			INTERCONECETED_FOCUS = "com.zhonghong.mobileconnect.bean.ThreeAppBean";
			BT_TALKING_FOCUS = "com.zhonghong.bluetooth.manager.CallManager";
			SETTINGS = "com.zhonghong.settingshelper.service.start";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
