package com.zhonghong.carfriends.impl;

import java.util.List;

import com.zhonghong.carfriends.aidl.ICarFriendsCallBack;
import com.zhonghong.carfriends.aidl.IFocusChangeCallBack;
import com.zhonghong.carfriends.aidl.IVoiceServer;
import com.zhonghong.serviceconnect.ServiceConnector;
import com.zhonghong.serviceconnect.interfaces.ServiceConnectCallback;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * 用于车友圈录音、播放状态设置 ，以及硬按键触发的录音指令、停止录音的监听；
 * 用于获取当前焦点，以及焦点变化监听
 * @author yys
 *
 */

public class VoiceControlManager implements ServiceConnectCallback{

	private static VoiceControlManager instance;
	private ServiceConnector mServiceConnector;
	
	private IVoiceServer mCarFriendsServer;
	private ISystemInfoListener ls;
	private IFocusInfoListener fl;
	private boolean isRegister_ls = false;
	private boolean isRegister_fl = false;
	private static final String SERVICE_NAME = "zh_voiceservice";
	
	private static final String TAG = "VoiceControlManager";
	
	private VoiceControlManager(Context context) {
		mServiceConnector = new ServiceConnector(context);
		mServiceConnector.getServiceByName(SERVICE_NAME, this);
	}
	
	public static VoiceControlManager getInstance(Context context){
		if(instance == null){
			synchronized (VoiceControlManager.class) {
				if(instance == null){
					instance = new VoiceControlManager(context);
				}
			}
		}
		return instance;
	}

	@Override
	public void onServiceConnected(IBinder arg0, boolean arg1) {
		mCarFriendsServer = IVoiceServer.Stub.asInterface(arg0);
		if(isRegister_fl){
			try {
				mCarFriendsServer.registerFocusCallBack(fcallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			isRegister_fl = false;
		}
		if(isRegister_ls){
			try {
				mCarFriendsServer.registerCallBack(callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		isRegister_ls = false;
	}

	@Override
	public void onServiceDied() {
		mCarFriendsServer = null;
	}

	@Override
	public void onServiceDisconnected() {
		mCarFriendsServer = null;
	}
	
	private ICarFriendsCallBack callback = new ICarFriendsCallBack.Stub() {
		
		@Override
		public void startSpeech(boolean isStartSpeech) throws RemoteException {
			Log.i(TAG, "startSpeech:" + isStartSpeech);
			if(ls!=null){
				ls.startSpeech(isStartSpeech);
			}
		}
		
		@Override
		public void onWakeupResult(int wordID) throws RemoteException {
			Log.i(TAG, "onWakeupResult:"+wordID);
			if(ls!=null){
				ls.onWakeupResult(wordID);
			}
		}
	}; 
	
	private IFocusChangeCallBack fcallback = new IFocusChangeCallBack.Stub() {
		
		@Override
		public void focusChange(int fromfocus, int tofocus) throws RemoteException {
			Log.i(TAG, "focusChange:" + fromfocus + ","+ tofocus);
			if(fl!=null){
				fl.focuschanged(fromfocus, tofocus);
			}
		}
	};
	
	/**
	 * 设置录音状态
	 * @param isspeech
	 */
	public void setSpeechStatus(boolean isspeech){
		Log.i(TAG, "setSpeechStatus :"+isspeech);
		if(mCarFriendsServer == null){
			Log.i(TAG, "setSpeechStatus mCarFriendsServer is null");
			return;
		}
		try {
			mCarFriendsServer.setSpeechStatus(isspeech);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置播放状态
	 * @param isplay
	 */
	public void setPlayStatus(boolean isplay){
		Log.i(TAG, "setPlayStatus :"+isplay);
		if(mCarFriendsServer == null){
			Log.i(TAG, "setPlayStatus mCarFriendsServer is null");
			return;
		}
		try {
			mCarFriendsServer.setPlayStatus(isplay);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 开始自定义唤醒语音
	 */
	public void startSpeechAwake(List<String> words){
		Log.i(TAG, "startSpeechAwake :"+words.size());
		if(mCarFriendsServer == null){
			Log.i(TAG, "startSpeechAwake mCarFriendsServer is null");
			return;
		}
		try {
			mCarFriendsServer.startSpeechAwaken(words);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止自定义唤醒语音
	 * 客户端在调用了startSpeechAwake（）开始自定义唤醒语音之后，不管是会话结束还是其他情况，都需要主动调用此方法停止
	 */
	public void stopSpeechAwake(){
		
		Log.i(TAG, "stopSpeechAwake");
		if(mCarFriendsServer == null){
			Log.i(TAG, "stopSpeechAwake mCarFriendsServer is null");
			return;
		}
		try {
			mCarFriendsServer.stopSpeechAwaken();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 设置系统操作的改变监听
	 * 
	 */
	public void registerISystemInfoListener(ISystemInfoListener l){
		ls = l;
		if(mCarFriendsServer ==null){
			isRegister_ls = true;
			return;
		}else{
			isRegister_ls = false;
		}
		try {
			mCarFriendsServer.registerCallBack(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void unregisterISystemInfoListener(ISystemInfoListener l){
		ls = null;
		if(mCarFriendsServer ==null){
			return;
		}
		try {
			mCarFriendsServer.unregisterCallBack(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 用于监听系统按键通知的开始录音、结束录音的监听接口
	 * 增加自定义唤醒后结果的反馈监听
	 * @author yys
	 *
	 */
	public static interface ISystemInfoListener{
		void startSpeech(boolean isStartSpeech);
		
		void onWakeupResult(int wordID);
	}
	
	/**
	 * 用于监听当前焦点变化的接口
	 * @author yys
	 *
	 */
	public static interface IFocusInfoListener{
		void focuschanged(int fromsrc ,int tosrc);
	}
	
	public void registerIFocusInfoListener(IFocusInfoListener fl){
		this.fl = fl;
		if(mCarFriendsServer ==null){
			isRegister_fl = true;
			return;
		}else{
			isRegister_fl = false;
		}
		try {
			mCarFriendsServer.registerFocusCallBack(fcallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void unregisterIFocusInfoListener(IFocusInfoListener fl){
		this.fl = null;
		if(mCarFriendsServer ==null){
			return;
		}
		try {
			mCarFriendsServer.unregisterFocusCallBack(fcallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	

}
