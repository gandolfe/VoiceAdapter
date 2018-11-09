package com.zhonghong.iflyplatformadapter;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhonghong.focus.PackageConstant;
import com.zhonghong.iflyinterface.IRadioController;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RadioController implements IRadioController{

	private static final String TAG = "RadioController";
	
	Context context = null;
	public RadioController(Context context) {
		this.context = context;
	}

	@Override
	public void play() {
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "play");
		context.startService(intent);
		Log.i(TAG, "play");
	}

	@Override
	public void continuePlay() {
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "play");
		context.startService(intent);
		Log.i(TAG, "play");
	}

	@Override
	public void pause() {
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "pause");
		context.startService(intent);
		Log.i(TAG, "pause");
		
	}

	@Override
	public void playPre() {
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "pre"); //上一个
		context.startService(intent);
		Log.i(TAG, "pre");
		
	}

	@Override
	public void playNxt() {
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "next"); //下一个
		context.startService(intent);
		Log.i(TAG, "next");
		
	}
	
	public void longPlayPre(){
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "pre_long"); 
		context.startService(intent);
		Log.i(TAG, "pre_long");
	}
	
	public void longPlayNxt(){
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "next_long"); 
		context.startService(intent);
		Log.i(TAG, "next_long");
	}

	@Override
	public void setPlayRandomModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlaySingleModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayOrderModel() {
		// TODO Auto-generated method stub
		
	}

	@SuppressLint("NewApi")
	@Override
	public String startRadio(String jsonstr) {
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		JSONObject resultJson = new JSONObject();
		JSONObject jsonobj =null;
		try {
			jsonobj = new JSONObject(jsonstr);
			String waveband = jsonobj.optString("waveband");
			String code = jsonobj.optString("code");
			String rawText = jsonobj.optString("rawText");
			
			if(waveband.isEmpty() && code.isEmpty() && rawText.isEmpty()){
				Log.i(TAG, "fail");
				resultJson.put("status", "fail");
				resultJson.put("message", "抱歉，不能为您处理！");
				return resultJson.toString();
			}
			
			if(waveband.isEmpty() && code.isEmpty() && !rawText.isEmpty()){
				if(rawText.contains("打开收音") || rawText.contains("播放收音")){
					
				}else{
					Log.i(TAG, "fail ,don't case 打开收音 、播放收音");
					resultJson.put("status", "fail");
					resultJson.put("message", "抱歉，不能为您处理！");
					return resultJson.toString();
				}
			}
			
			if("am".equals(waveband)){
				intent.putExtra("action", "am");
			}else if("fm".equals(waveband)){
				intent.putExtra("action", "fm");
			}
			if(code.length()>0){
				intent.putExtra("code", code);
			}
			
			context.startService(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			resultJson.put("status", "success");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultJson.toString();
	}

	@Override
	public void openApp() {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ComponentName cn = new ComponentName("com.zhonghong.radio", "com.zhonghong.radio.MainActivity");
		intent.setComponent(cn);
		context.startActivity(intent);
		Log.i(TAG, "openApp");
	}

}
