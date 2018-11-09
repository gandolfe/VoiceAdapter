package com.zhonghong.iflyplatformadapter;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhonghong.focus.ControllrFactory;
import com.zhonghong.iflyinterface.ICMDController;
import com.zhonghong.iflyinterface.MediaControlCallBack;
import com.zhonghong.views.VolumeWindow;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

public class CMDController implements ICMDController{

	Context context;
	
	private static final String TAG = "CMDController";
	
	public CMDController(Context context) {
		this.context = context;
	}

	@Override
	public String doCMD(String jsonstr) {
		
		JSONObject resultJson = new JSONObject();
		JSONObject jsonobj =null;
		try {
			jsonobj = new JSONObject(jsonstr);
			String category = jsonobj.optString("category");
			String name = jsonobj.optString("name");
			Log.i(TAG, "name:" + name);
			if("播放模式".equals(category)){
				
				setCycleMode(name);
				resultJson.put("status", "success");
			}else if("音量控制".equals(category)){
				
				setVolume(name);
				resultJson.put("status", "success");
			}else if("曲目控制".equals(category)){
				
				setItem(name);
				resultJson.put("status", "success");
			}else{
				resultJson.put("status", "fail");
				resultJson.put("message", "抱歉，我不能理解您的意思，请换种说法");
			}
			return resultJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			resultJson.put("status", "fail");
			resultJson.put("message", "抱歉，不能支持此操作");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultJson.toString();
	}
	
	
	
	private void setItem(String name){
		
		MediaControlCallBack mc = null;
		if(isKWTopActivity()){
			//由于酷我音乐在语音出来的时候就失去焦点出栈了，所以根据焦点来控制不行，目前暂时判断是否在Activity的栈顶来操作
			mc = ControllrFactory.getInstance().getKuWoController();
		}else{
			mc = ControllrFactory.getInstance().getMediaControlller();
		}
		
		if("暂停".equals(name)){
			mc.pause();
		}else if("播放".equals(name)){
			mc.play();
		}else if("上一首".equals(name) || "上一曲".equals(name)){
			mc.playPre();
		}else if("下一首".equals(name) || "下一曲".equals(name)){
			mc.playNxt();
		}else if("重播".equals(name)){
			mc.playPre();
			mc.playNxt();
		}
	}
	
	
	private void setVolume(String name){
		
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int volume = 0;	
			
		if("音量+".equals(name)){
			volume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) + 1;
		}else if("音量-".equals(name)){
			volume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) - 1;
		}else if("静音".equals(name)){
			mAudioManager.setMasterMute(true);
			context.sendBroadcast(new Intent(AudioManager.VOLUME_CHANGED_ACTION));
			Log.i(TAG,"isMute=" + mAudioManager.isMasterMute());
			return;
		}else if("打开音量".equals(name)||"取消静音".equals(name)){
			mAudioManager.setMasterMute(false);
			context.sendBroadcast(new Intent(AudioManager.VOLUME_CHANGED_ACTION));
			volume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
			Log.i(TAG,"isMute=" + mAudioManager.isMasterMute());
		}
		volume = volume < 0 ? 0 : volume;
		volume = volume > 32 ? 32 : volume;
		Log.i(TAG,"system volume=" + volume);
		mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
	}
	
	
	private void setCycleMode(String name){
		MediaControlCallBack mc = null;
		if(isKWTopActivity()){
			//由于酷我音乐在语音出来的时候就失去焦点出栈了，所以根据焦点来控制不行，目前暂时判断是否在Activity的栈顶来操作
			mc = ControllrFactory.getInstance().getKuWoController();
		}else{
			mc = ControllrFactory.getInstance().getMediaControlller();
		}
		
		if("顺序循环".equals(name) || "顺序播放".equals(name)){
			mc.setPlayOrderModel();
		}else if("单曲循环".equals(name) || "单曲播放".equals(name)){
			mc.setPlaySingleModel();
		}else if("随机循环".equals(name) || "随机播放".equals(name)){
			mc.setPlayRandomModel();
		}
	}
	
	private boolean isKWTopActivity(){
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String topName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
		if(topName!=null && topName.contains("cn.kuwo.kwmusiccar")){
			return true;
		}
		return false;
	}
	

}
