package com.zhonghong.iflyinterface;

import com.zhonghong.component.listener.CarStateChangeListener;


public interface SystemStateListener extends CarStateChangeListener{
	void btStateChanged(boolean isConnected);
	/**
	 * 蓝牙通话状�?�改�?
	 * @param state 通话状�??
	 * @param name 姓名
	 * @param number 电话号码
	 */
	void talkStateChanged(int state,String name,String number);
	/**
	 * 硬件按钮事件
	 */
	void onHardwareClick(String keyCode);
	/**
	 * 应用正在结束
	 */
	void onFinishing();
	/**
	 * 蓝牙同步手机通讯录到车机完成
	 */
	void syncContactFinish();
}
