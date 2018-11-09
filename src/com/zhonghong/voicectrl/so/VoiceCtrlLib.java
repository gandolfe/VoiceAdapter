package com.zhonghong.voicectrl.so;
/**
 * 讯飞降噪模块工作模式设置 
 * @author ZH-SW-Guxb
 * @since 2016.10.28
 * 
 * e.g:
 * 1. add VoiceCtrlLib.java to your project, push libVoiceCtrlJni.so to device system/lib
 * 2. call initVoiceCtrl method
 * 
 * */

public class VoiceCtrlLib {

	//----------------------模式定义
	/**录音模式*/
	public static final int FUNC_MODE_PASSBY = 0x0;
	/**降噪模式*/
	public static final int FUNC_MODE_NOISECLEAN = 0x1;
	/**回声消除*/
	public static final int FUNC_MODE_PHNOE = 0x2;
	/**唤醒模式*/
	public static final int FUNC_MODE_WAKEUP = 0x3;
	
	/**
	 * set func_mode
	 * @param new mode
	 * @return new mode if set success, else -1
	 * */
	public native int setVoiceCtrlMode(int mode);
	
	/**
	 * get func_mode
	 * @return func_mode if get success, else -1
	 * */
	public native int getVoiceCtrlMode();
	
	/**
	 * init
	 * */
	public native void initVoiceCtrl();
	
	static 
	{
		System.loadLibrary("VoiceCtrlJni");
	}
	
}
