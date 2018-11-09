package com.zhonghong.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;
import android.zhonghong.mcuservice.McuConstant;
import android.zhonghong.mcuservice.RegistManager.ISystemInfoChangedListener;
import android.zhonghong.mcuservice.SystemInfo;
import android.zhonghong.mcuservice.SystemProxy;

import com.zhonghong.utils.SlogUtil;

public class SystemStateManager implements ISystemInfoChangedListener{
	
	/** acc 状态 */
	public static boolean acc = false;
	/** power 状态 */
	public static boolean power = false;
	
	private Context context;	
	private SystemProxy systemProxy;
	private SlogUtil slogUtil;
	private AudioManager audioManager;
	
	public SystemStateManager(Context context,SystemProxy mSystemProxy){
		this.context = context;
		this.systemProxy = mSystemProxy;
		initSytstemState();
	}

	private void initSytstemState(){
		slogUtil = new SlogUtil(getClass());
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		acc = systemProxy.getSystemInfo().isAccOn();
		power = systemProxy.getSystemInfo().isPowerOnState();
	}
	
	@Override
	public void notify(SystemInfo arg0) {
		boolean localAcc = arg0.isAccOn();
		boolean localPower = arg0.isPowerOnState();
		slogUtil.i("localAcc:"+ localAcc + " ;acc:"+acc + " ;localPower: "+localPower + " ;power :"+power);
		if(localAcc != acc || localPower != power){
			acc = localAcc;
			power = localPower;
			if(acc &&  power){
				//AudioFocusManager.getInstance(context).abandonAudioFocus("com.zhonghong.voice");
				audioManager.abandonAudioFocus(audioFocusChangeListener);
			}else{
				audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
			}
		}
	}
	
	private OnAudioFocusChangeListener audioFocusChangeListener = new OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int arg0) {
			
		}
	};
}
