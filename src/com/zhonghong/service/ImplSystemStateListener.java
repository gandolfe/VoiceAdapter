package com.zhonghong.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.platform.type.PlatformCode;
import com.iflytek.platformservice.PlatformService;
import com.zhonghong.iflyinterface.SystemStateListener;
import com.zhonghong.iflyplatformadapter.TTSController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class ImplSystemStateListener implements SystemStateListener{

	private static final String TAG =  "ImplSystemStateListener";
	Context context;
	public ImplSystemStateListener(Context context) {
		this.context = context;
		TTSController.getInstance(context);
	}

	@Override
	public void onAccOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPowerOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPowerOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAsternOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAsternOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void btStateChanged(boolean isConnected) {
		// TODO Auto-generated method stub
		
	}

	@SuppressLint("NewApi")
	@Override
	public void talkStateChanged(int state, String name, String number) {
		try {
			JSONObject obj = new JSONObject();
			if(state == 1){
				obj.put("state", 1);
				
				if(number != null && !number.isEmpty()){
					obj.put("number", number);
				}
				
				if(name != null && !name.isEmpty()){
					if(isVoice()){
						TTSController.getInstance(context).startTTS(name +"来电，接通或者挂断!",obj.toString());
					}
					
				}else if(number != null && !number.isEmpty()){
					
					if(isVoice()){
						TTSController.getInstance(context).startTTS(number +"来电，接通或者挂断!!",obj.toString());
					}
				}
//				
//				return;
				
			}else if(state == 2){
				obj.put("state", 2);
				PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_SPEECHOFF);
			}else{
				obj.put("state", 0);
				PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_SPEECHON);
			}
			Log.i(TAG, "talkStateChanged obj: "+obj.toString());
			PlatformService.platformCallback.phoneCallStateChange(obj.toString());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onHardwareClick(String keyCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishing() {
		// TODO Auto-generated method stub
		TTSController.getInstance(context).release();
	}

	@Override
	public void syncContactFinish() {
		// TODO Auto-generated method stub
		
	}
	
	private boolean isVoice(){
		int vmode =  Settings.System.getInt(context.getContentResolver(), "key_voice_phone_mode",0);
		Log.i(TAG, "vmode:"+vmode);
		if(vmode == 1){
			return true;
		}
		return false;
	}

}
