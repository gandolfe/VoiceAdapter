package com.zhonghong.iflyplatformadapter;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhonghong.service.InputKeyManager;
import com.zhonghong.service.MediaSourceManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.kuwo.autosdk.api.KWAPI;

public class APPController{

	Context context = null;
	private KWAPI mKwapi = null;
	
	
	public APPController(Context context) {
		this.context = context;
	}
	
	private static final String TAG = "APPController";

	public String handle_APP(String jsonstr) {
		JSONObject resultJson = new JSONObject();
		JSONObject jsonobj =null;
		try {
			jsonobj = new JSONObject(jsonstr);
			String operation = jsonobj.optString("operation");
			String name = jsonobj.optString("name");
			Log.i(TAG, "name:" + name);
			if("LAUNCH".equals(operation) && open_APP(name)){
				resultJson.put("status", "success");
			}else if("EXIT".equals(operation)){
				close_APP(name);
				resultJson.put("status", "success");
			}else if("在线音乐".equals(name)||"酷我音乐".equals(name)){
				open_APP(name);
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
	
	private boolean open_APP(String name){
		Intent intent = new Intent(); 
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ComponentName cn = null;
		
		
		if(name.equals("导航") || "地图导航".equals(name) || "高德地图".equals(name) || "地图".equals(name)){
			cn = new ComponentName("com.autonavi.amapauto", "com.autonavi.auto.remote.fill.UsbFillActivity");
		}else if(name.equals("音乐") || "USB音乐".equals(name)|| "U盘音乐".equals(name)|| "本地音乐".equals(name)){
			
			if( InputKeyManager.getInstance(context).isUsbMount("/mnt/USB") && MediaSourceManager.hasMusicFile(context)){
				cn = new ComponentName("com.zhonghong.music", "com.zhonghong.music.main.MainActivity");
			}else{
				Log.i(TAG, "open music failed,don't find music file!");
				return false;
			}
			
		}else if(name.equals("视频")){
			
			if( InputKeyManager.getInstance(context).isUsbMount("/mnt/USB") && MediaSourceManager.hasVideoFile(context)){
				cn = new ComponentName("com.zhonghong.video", "com.zhonghong.video.main.MainActivity");
			}else{
				Log.i(TAG, "open video failed,don't find video file!");
				return false;
			}
			
		}else if(name.equals("收音机")){
			cn = new ComponentName("com.zhonghong.radio", "com.zhonghong.radio.MainActivity");
		}else if(name.equals("联系人")){
			cn = new ComponentName("com.zhonghong.bluetooth", "com.zhonghong.bluetooth.MainActivity");
		}else if(name.equals("蓝牙电话") || "电话".equals(name) || "蓝牙".equals(name)){
			cn = new ComponentName("com.zhonghong.bluetooth", "com.zhonghong.bluetooth.MainActivity");
		}else if(name.equals("设置")){
			cn = new ComponentName("com.zhonghong.settings", "com.zhonghong.settings.MainActivity");
		}else if("网络音乐".equals(name) || "在线音乐".equals(name) || "酷我音乐".equals(name)){
			if(mKwapi == null){
				mKwapi = KWAPI.createKWAPI(context, "auto");
			}
			mKwapi.startAPP(context, true);
			return true;
		}else {
			return false;
		}
		
		intent.setComponent(cn);
		context.startActivity(intent);
		Log.i(TAG, "openApp");
		return true;
	}
	
	public void close_APP(String name){
		
		if("音乐".equals(name)){
			new MusicController(context).pause();
		}else if("收音机".equals(name)){
			new RadioController(context).pause();
		}else if("网络音乐".equals(name) || "在线音乐".equals(name) || "酷我音乐".equals(name)){
			if(mKwapi == null){
				mKwapi = KWAPI.createKWAPI(context, "auto");
			}
			mKwapi.exitAPP(context);
		}
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);
		Log.i(TAG, "close_APP");
	}

}
