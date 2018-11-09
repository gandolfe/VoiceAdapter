package com.zhonghong.iflyplatformadapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.util.Log;
import com.iflytek.platform.PlatformClientListener;
import com.iflytek.platform.type.PlatformCode;
import com.iflytek.platformservice.PlatformService;
import com.zhonghong.voicectrl.so.VoiceCtrlLib;

/***
 * 强烈建议客户，可以参考demo的接口实现。但是不要直接，基于此demo开发工程。
 * 
 * 主要参考TestPlatformAdapterClient类和 PlatformAdapterApp类的实现
 * 
 * 其他类为助理自身测试的类，没有太大的参考意义
 * 
 * @author li
 *
 */
public class PlatformAdapterClient implements PlatformClientListener {

	private static String tag = "PlatformAdapterClient";
	private Context mContext;
	private AudioManager audioManager = null;
	private VoiceCtrlLib voiceControl = null; //降噪模块

	public static final int SEARCH_MUSIC=0x20;
	public static final int SEARCH_RADIO=0x21;
	public static final int CALL_STATE_IDLE = 0;     //挂断电话
	public static final int CALL_STATE_OFFHOOK = 2;  //接听电话
	
	private CallController mCallController = null;
	private MusicController mMusicController = null;
	private APPController mAppController = null;
	private CMDController mCMDController = null;
	private RadioController mIRadioController = null;
	private AirController mAirController = null;
	
	public PlatformAdapterClient(Context context) {
		// super(context);
		this.mContext = context;
		audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		
		voiceControl = new VoiceCtrlLib();
		voiceControl.initVoiceCtrl();
		
		mCallController = new CallController(context.getApplicationContext());
		mMusicController = new MusicController(context.getApplicationContext());
		mCMDController = new CMDController(context.getApplicationContext());
		mIRadioController = new RadioController(context.getApplicationContext());
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SEARCH_MUSIC:
				String paramsStr=(String) msg.obj;
				String jsonResult=mMusicController.playSyncMusic(paramsStr);
				if (PlatformService.platformCallback == null) {
					Log.e(tag, "PlatformService.platformCallback == null");
					return;
				}
				try {
					PlatformService.platformCallback.onSearchPlayListResult(SEARCH_MUSIC, jsonResult);
				} catch (RemoteException e) {
					Log.e(tag,
							"platformCallback get music error:" + e.getMessage());
				}
				
				break;
			case SEARCH_RADIO:
				break;
			case CALL_STATE_IDLE:
				mCallController.hangUp();
				break;
			case CALL_STATE_OFFHOOK:
				mCallController.accept();
				break;
			}
		}
	};


	public String onDoAction(String actionJson) {
		Log.i(tag, "onDoAction string:" + actionJson);
		/* 伪代码，实际需要客户实现 */
		JSONObject resultJson = new JSONObject();
		if (actionJson == null) {
			try {
				resultJson.put("status", "fail");
				resultJson.put("message", "抱歉，没有可处理的操作");
			} catch (JSONException e) {

			}
			return resultJson.toString();
		} else {
			try {
				JSONObject action = new JSONObject(actionJson);

				if ("call".equals(action.getString("action"))) {
					String tempnum = action.getString("param1");
					mCallController.call(tempnum);
					resultJson.put("status", "success");
					return resultJson.toString();
				} else if ("sendsms".equals(action.getString("action"))) {
					sendSMS(action.getString("param1"),
							action.getString("param2"));
					resultJson.put("status", "success");
					return resultJson.toString();
				} else if ("startspeechrecord".equals(action
						.getString("action"))) {
					Log.i(tag, "Action_StartSpeechRecord ");
					voiceControl.setVoiceCtrlMode(VoiceCtrlLib.FUNC_MODE_NOISECLEAN);
					resultJson.put("status", "success");
					return resultJson.toString();
				} else if ("stopspeechrecord"
						.equals(action.getString("action"))) {
					Log.i(tag, "Action_StopSpeechRecord ");
					resultJson.put("status", "success");
					return resultJson.toString();
				} else if ("startwakerecord".equals(action.getString("action"))) {
					Log.i(tag, "Action_StartWakeRecord ");
					voiceControl.setVoiceCtrlMode(VoiceCtrlLib.FUNC_MODE_WAKEUP);
					resultJson.put("status", "success");
					return resultJson.toString();
				} else if ("stopwakerecord".equals(action.getString("action"))) {
					Log.i(tag, "Action_StopWakeRecord ");
					resultJson.put("status", "success");
					return resultJson.toString();
				}
			} catch (JSONException e) {
				Log.e(tag, "Fail to do action:" + e.getMessage());
			}

		}
		try {
			resultJson.put("status", "fail");
			resultJson.put("message", "抱歉，无法处理此操作");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultJson.toString();
	}


	public String onNLPResult(String arg0) {
		Log.i(tag, "onNLPResult:"+arg0);
		JSONObject resultJson = new JSONObject();
		JSONObject jsonobj =null;
		String focus =null;
		try {
			jsonobj = new JSONObject(arg0);
			focus = jsonobj.optString("focus");
			
			if(focus.length()<1){
				resultJson.put("status", "fail");
				return resultJson.toString();
			}
			
			if(focus.equals("music")){
				
				mMusicController.playMusic(arg0);
				
				
			}else if(focus.equals("radio")){
				
				return mIRadioController.startRadio(arg0);
			
			}else if(focus.equals("cmd")){
				
				return mCMDController.doCMD(arg0);
			
			}else if(focus.equals("app")){
				if(mAppController ==null){
					mAppController = new APPController(mContext);
				}
				return mAppController.handle_APP(arg0);
			}else if(focus.equals("airControl")){
				if(mAirController == null){
					mAirController = new AirController(mContext);
				}
				return mAirController.doCMD(arg0);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			resultJson.put("message", "success");
		} catch (JSONException e1) {
			Log.d(tag, "json error");
		}
		return resultJson.toString();

	}
	
	@Override
	public boolean onSearchPlayList(String arg0) {
		try {
			Log.i(tag, "onSearchPlayList:"+arg0);
			JSONObject action = new JSONObject(arg0);
			String focus=action.optString("focus");
			if("music".equals(focus)){
				// 不要进行耗时操作，有需要的话请使用handler。
				Message message = new Message();
				message.what = SEARCH_MUSIC;
				message.obj = arg0;
				handler.sendMessage(message);
				return true;
			}
			else if("radio".equals(focus)){
				// 不要进行耗时操作，有需要的话请使用handler。
				Message message = new Message();
				message.what = SEARCH_RADIO;
				message.obj = arg0;
				handler.sendMessage(message);
				return true;
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public String onGetContacts(String arg0) {
		
		Log.i(tag, "onGetContacts arg0:" + arg0);
		if(mCallController!=null){
			return mCallController.getContacts(arg0);
		}
		
		return null;
	}
	
	@Override
	public int onGetState(int arg0) {
		
		Log.i(tag, "onGetState :" + arg0);
		if (arg0 == PlatformCode.STATE_BLUETOOTH_PHONE) {
			// 返回蓝牙电话状态
			return mCallController.isConencted() ? PlatformCode.STATE_OK : PlatformCode.STATE_NO;
		} else if (arg0 == PlatformCode.STATE_SENDSMS) {
			// 返回短信功能是否可用
			return PlatformCode.STATE_NO;
		} else {
			// 不存在此种状态请求
			return PlatformCode.FAILED;
		}
	}
	
	public void onAbandonAudioFocus() {
		Log.i(tag, "onAbandonAudioFocus");
		// 这里使用的 android AudioFocus的音频协调机制
		audioManager.abandonAudioFocus(afChangeListener);
		
		/** 失去音频焦点 通知导航 */
		sendAuidoCtrol(false);
	}
	
	public int onRequestAudioFocus(int streamType, int nDuration) {
		Log.i(tag, "onRequestAudioFocus");
		/* 伪代码，实际需要客户实现 */
		// 这里使用的 android AudioFocus的音频协调机制
		int audioFocusResult = audioManager.requestAudioFocus(afChangeListener,
				streamType, nDuration);
		if (audioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			/** 获得音频焦点 通知导航 */
			sendAuidoCtrol(true);
		}
		return audioFocusResult;
	}
	
	/**
	 * 客户主动回调的方法
	 */
	public void AudioFocusChange(int focusChange) {
		if (PlatformService.platformCallback == null) {
			Log.e(tag, "PlatformService.platformCallback == null");
			return;
		}
		try {
			PlatformService.platformCallback.audioFocusChange(focusChange);
		} catch (RemoteException e) {
			Log.e(tag,
					"platformCallback audioFocusChange error:" + e.getMessage());
		}
	}
	
	public String onGetLocation() {
		// 获取当前位置 这是只是模拟了一个位置 。实际的位置 需要客户实现
		// 语音助理 的：今天的天气、到上海的航班、附近的美食、附近的酒店，是依赖这个位置信息的
		// 117.143269,31.834399
		String location = "{'name':'科大讯飞信息科技股份有限公司','address':'黄山路616','city':'合肥市','longitude':'117.143269','latitude':'31.834399'}";
		return location;
	}

	public void onServiceUnbind() {
		// 助理因为异常，导致和平台适配器服务断开，这里可以做重置处理
		Log.e(tag, " onServiceUnbind ");
	}

	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			AudioFocusChange(focusChange);
		}
	};


	/**
	 * 客户主动回调的方法
	 */
	public void uploadCustomData(int type, String[] data) {
		if (PlatformService.platformCallback == null) {
			Log.e(tag, "PlatformService.platformCallback == null");
			return;
		}
		try {
			PlatformService.platformCallback.uploadCustomData(type, data);
		} catch (RemoteException e) {
			Log.e(tag,
					"platformCallback uploadCustomData error:" + e.getMessage());
		}
	}

	/** 导航音频协调方案 **/
	private void sendAuidoCtrol(boolean isStart) {
		Intent intent = new Intent();
		if (isStart) {
			intent.setAction("com.iflytek.startoperation");
		} else {
			intent.setAction("com.iflytek.endoperation");
		}
		mContext.sendBroadcast(intent);
	}

	private void sendSMS(String phoneNum, String message) {
		// 初始化发短信SmsManager类
		SmsManager smsManager = SmsManager.getDefault();
		// 如果短信内容长度超过200则分为若干条发
		if (message.length() > 200) {
			ArrayList<String> msgs = smsManager.divideMessage(message);
			for (String msg : msgs) {
				smsManager.sendTextMessage(phoneNum, null, msg, null, null);
			}
		} else {
			smsManager.sendTextMessage(phoneNum, null, message, null, null);
		}
	}

	public int changePhoneState(int state) {
		// 客户开发时应注意此处应为语音助理通知平台接听或者挂断电话，需要执行相应的电话操作
		Log.i(tag, "change phone state:" + state);
		
		Message msg = new Message();
		msg.what = state;
		handler.sendMessage(msg);
		
		return PlatformCode.SUCCESS;
	}

	public String onGetCarNumbersInfo() {
		// 获取车辆信息，暂时只是一个測試數據 。
		// 语音助理 的：违章查询业务依赖此信息
		// 包含三個信息：carNumber车牌号，carCode车架号，carDriveNo发动机号
		String carInfo = "{'carNumber':'粤YM5610','carCode':'116238','carDriveNo':'123446'}";
		return carInfo;
	}

}
