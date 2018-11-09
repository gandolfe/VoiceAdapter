package com.zhonghong.iflyplatformadapter;

import com.iflytek.adapter.ttsservice.ITtsClientListener;
import com.iflytek.adapter.ttsservice.TtsServiceAgent;
import com.iflytek.platformservice.PlatformService;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.zhonghong.mcuservice.McuConstant;
import android.zhonghong.mcuservice.SystemProxy;

public class TTSController implements ITtsClientListener{

	TtsServiceAgent agent = null;
	private SystemProxy systemProxy;
	private static final String TAG = "TTSController";
	
	private static TTSController mTTSController;
	
	private String jsonstr ;
	
	public static TTSController getInstance(Context context){
		if(mTTSController == null){
			mTTSController = new TTSController(context);
		}
		return mTTSController;
	}
	
	Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
		}
	};
	
	
	private TTSController(Context context) {
		agent = TtsServiceAgent.getInstence();
		agent.initService(this, context.getApplicationContext(),AudioManager.STREAM_MUSIC);
		systemProxy = new SystemProxy();
		Log.i(TAG, "startTTS init!");
	}
	
	
	public void startTTS(final String words , String jsonstr){
		this.jsonstr = jsonstr;
		Log.i(TAG, "startTTS words:"+words);
		systemProxy.setMcuState(systemProxy.entryState(McuConstant.SYS_STATE_TTS));
		Log.i(TAG, "change source");
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int code = 0;
				try {
					code = agent.startSpeak(words);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i(TAG, "resultcode:"+code);
			}
		}, 1500);
	}
	
	public void release(){
		agent.releaseService();
	}
	

	@Override
	public void onPlayBegin() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onPlayBegin");
	}

	@Override
	public void onPlayCompleted() {
		Log.i(TAG, "onPlayCompleted");
		systemProxy.setMcuState(systemProxy.exitState(McuConstant.SYS_STATE_TTS));
		try {
			PlatformService.platformCallback.phoneCallStateChange(jsonstr);
			Log.i(TAG, "onPlayCompleted json:"+jsonstr);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayInterrupted() {
		systemProxy.setMcuState(systemProxy.exitState(McuConstant.SYS_STATE_TTS));
	}

	@Override
	public void onProgressReturn(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTtsInited(boolean arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onTtsInited!");
	}

}
