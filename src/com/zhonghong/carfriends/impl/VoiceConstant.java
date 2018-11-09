package com.zhonghong.carfriends.impl;

import android.zhonghong.mcuservice.McuConstant;
/**
 * 常量定义
 * @author yys
 *
 */
public class VoiceConstant {

	/**
	 * 蓝牙电话
	 */
	public static final int BT_CALL = McuConstant.SYS_STATE_BT_TALKING;  //3
	
	/**
	 * 第三方源
	 */
	public static final int OTHER_APP = McuConstant.SYS_SOURCE_OTHER_APP;//16
	
	/**
	 * 本地音乐
	 */
	public static final int MP3 = McuConstant.SYS_SOURCE_MP3; // 10
	
	/**
	 * 蓝牙电话
	 */
	public static final int BT_MUSIC = McuConstant.SYS_SOURCE_BTMUSIC;  //13
	
	/**
	 * 收音
	 */
	public static final int RADIO = McuConstant.SYS_SOURCE_RADIO;//0
	
	/**
	 * 视频
	 */
	public static final int MEDIAPLAY = McuConstant.SYS_SOURCE_MEDIAPLAY; // 11
	
	/**
	 * carlife
	 */
	public static final int CARLIFE = McuConstant.SYS_SOURCE_IPOD;//8
	
	/**
	 * TTS语音播报
	 */
	public static final int TTS = McuConstant.SYS_STATE_TTS;//15
}
