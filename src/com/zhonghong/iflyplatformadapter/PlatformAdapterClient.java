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
 * ǿ�ҽ���ͻ������Բο�demo�Ľӿ�ʵ�֡����ǲ�Ҫֱ�ӣ����ڴ�demo�������̡�
 * 
 * ��Ҫ�ο�TestPlatformAdapterClient��� PlatformAdapterApp���ʵ��
 * 
 * ������Ϊ����������Ե��࣬û��̫��Ĳο�����
 * 
 * @author li
 *
 */
public class PlatformAdapterClient implements PlatformClientListener {

	private static String tag = "PlatformAdapterClient";
	private Context mContext;
	private AudioManager audioManager = null;
	private VoiceCtrlLib voiceControl = null; //����ģ��

	public static final int SEARCH_MUSIC=0x20;
	public static final int SEARCH_RADIO=0x21;
	public static final int CALL_STATE_IDLE = 0;     //�Ҷϵ绰
	public static final int CALL_STATE_OFFHOOK = 2;  //�����绰
	
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
		/* α���룬ʵ����Ҫ�ͻ�ʵ�� */
		JSONObject resultJson = new JSONObject();
		if (actionJson == null) {
			try {
				resultJson.put("status", "fail");
				resultJson.put("message", "��Ǹ��û�пɴ���Ĳ���");
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
			resultJson.put("message", "��Ǹ���޷�����˲���");
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
				// ��Ҫ���к�ʱ����������Ҫ�Ļ���ʹ��handler��
				Message message = new Message();
				message.what = SEARCH_MUSIC;
				message.obj = arg0;
				handler.sendMessage(message);
				return true;
			}
			else if("radio".equals(focus)){
				// ��Ҫ���к�ʱ����������Ҫ�Ļ���ʹ��handler��
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
			// ���������绰״̬
			return mCallController.isConencted() ? PlatformCode.STATE_OK : PlatformCode.STATE_NO;
		} else if (arg0 == PlatformCode.STATE_SENDSMS) {
			// ���ض��Ź����Ƿ����
			return PlatformCode.STATE_NO;
		} else {
			// �����ڴ���״̬����
			return PlatformCode.FAILED;
		}
	}
	
	public void onAbandonAudioFocus() {
		Log.i(tag, "onAbandonAudioFocus");
		// ����ʹ�õ� android AudioFocus����ƵЭ������
		audioManager.abandonAudioFocus(afChangeListener);
		
		/** ʧȥ��Ƶ���� ֪ͨ���� */
		sendAuidoCtrol(false);
	}
	
	public int onRequestAudioFocus(int streamType, int nDuration) {
		Log.i(tag, "onRequestAudioFocus");
		/* α���룬ʵ����Ҫ�ͻ�ʵ�� */
		// ����ʹ�õ� android AudioFocus����ƵЭ������
		int audioFocusResult = audioManager.requestAudioFocus(afChangeListener,
				streamType, nDuration);
		if (audioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			/** �����Ƶ���� ֪ͨ���� */
			sendAuidoCtrol(true);
		}
		return audioFocusResult;
	}
	
	/**
	 * �ͻ������ص��ķ���
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
		// ��ȡ��ǰλ�� ����ֻ��ģ����һ��λ�� ��ʵ�ʵ�λ�� ��Ҫ�ͻ�ʵ��
		// �������� �ģ���������������Ϻ��ĺ��ࡢ��������ʳ�������ľƵ꣬���������λ����Ϣ��
		// 117.143269,31.834399
		String location = "{'name':'�ƴ�Ѷ����Ϣ�Ƽ��ɷ����޹�˾','address':'��ɽ·616','city':'�Ϸ���','longitude':'117.143269','latitude':'31.834399'}";
		return location;
	}

	public void onServiceUnbind() {
		// ������Ϊ�쳣�����º�ƽ̨����������Ͽ���������������ô���
		Log.e(tag, " onServiceUnbind ");
	}

	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			AudioFocusChange(focusChange);
		}
	};


	/**
	 * �ͻ������ص��ķ���
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

	/** ������ƵЭ������ **/
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
		// ��ʼ��������SmsManager��
		SmsManager smsManager = SmsManager.getDefault();
		// ����������ݳ��ȳ���200���Ϊ��������
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
		// �ͻ�����ʱӦע��˴�ӦΪ��������֪ͨƽ̨�������߹Ҷϵ绰����Ҫִ����Ӧ�ĵ绰����
		Log.i(tag, "change phone state:" + state);
		
		Message msg = new Message();
		msg.what = state;
		handler.sendMessage(msg);
		
		return PlatformCode.SUCCESS;
	}

	public String onGetCarNumbersInfo() {
		// ��ȡ������Ϣ����ʱֻ��һ���yԇ���� ��
		// �������� �ģ�Υ�²�ѯҵ����������Ϣ
		// ����������Ϣ��carNumber���ƺţ�carCode���ܺţ�carDriveNo��������
		String carInfo = "{'carNumber':'��YM5610','carCode':'116238','carDriveNo':'123446'}";
		return carInfo;
	}

}
