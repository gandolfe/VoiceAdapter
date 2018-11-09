package com.zhonghong.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.zhonghong.mcuservice.McuConstant;
import android.zhonghong.mcuservice.SettingsProxy;
import android.zhonghong.mcuservice.SystemProxy;

import java.util.List;

import com.iflytek.adapter.custommvwservice.CustomMvwSession;
import com.iflytek.adapter.custommvwservice.ICustomMvwCallback;
import com.iflytek.platform.type.PlatformCode;
import com.iflytek.platformservice.PlatformService;
import com.zhonghong.car.service.CarServiceManager;
import com.zhonghong.car.service.CarSystemSetings;
import com.zhonghong.carfriends.aidl.ICarFriendsCallBack;
import com.zhonghong.carfriends.aidl.IFocusChangeCallBack;
import com.zhonghong.carfriends.aidl.IVoiceServer;
import com.zhonghong.component.activity.BaseService;
import com.zhonghong.focus.AudioFocusManager;
import com.zhonghong.focus.ControllrFactory;
import com.zhonghong.iflyinterface.SystemStateListener;
import com.zhonghong.receive.CarStateReceiver;
import com.zhonghong.utils.SlogUtil;

public class VoiceControlService extends BaseService{
	//导航状态消息
	private static final String SOUGOU_NAVIGATION_ACTION = "SOGOUNAVI_STANDARD_BROADCAST_SEND";
	// 请求获取蓝牙状态改变
	private static final String ACTION_REQ_BT_STATE 	= "com.zhonghong.bt.req_connect_state";
	// 蓝牙状�?�改变广�?
	//private static final String ACTION_BT_STATE_CHANGE 	= "com.zhonghong.bt.send_broadcast_state_change"; 
	// 蓝牙通讯录同步完成
	private static final String NOTIFY_BLUETOOTH_CONTACT_CHANGED = "com.zhonghong.bluetooth.NOTIFY_CONTACT_CHANGED";
	// 通话状态
	private static final String ACTION_BT_CALL_STATE = "com.zhonghong.bluetooth.BT_CALL_STATE_ACTION";
	//蓝牙通话广播
	private static final String BT_CALLING = "com.zhonghong.action.bt_call";
	// 蓝牙状态
	public static final String BLUETOOTH_CONNECT_STATE = "android.zhonghong.action.STATE_CHANGED";
	
	public static final String BT_CONNECT_STATE = "bt_connect";
	
	//高德地图开始语音广播
	private static final String GAODE_NAVIGATION_START_ACTION = "com.autonavi.xm.action.VOICE_PLAY_STARTED";
	//高德地图停止语音广播
	private static final String GAODE_NAVIGATION_STOP_ACTION = "com.autonavi.xm.action.VOICE_PLAY_STARTED";
	
	//栈顶改变广播
	private static final String CARLIFE_TOP_CHANGED = "com.zhonghong.change.activity";
	
	private static final String SERVICE_NAME = "zh_voiceservice";
	
	private SystemProxy mSystemProxy;
	private SettingsProxy settingsProxy;
	private CarServiceManager mCarServiceManager;
	private TopPackageManager packageManager;
	private SystemStateManager systemStateManager;
	private InputKeyManager mInputKeyManager;
	private MainHandler mainHandler;
	private SlogUtil slogUtil;
	private CarStateReceiver mCarStateReceiver;
	//private AutoTestManager autoTestManager;
	private SystemStateListener mSystemStateListener = null;
	private int callstate = 3;//通话状态，1来电，2接通，3default
	
	private RemoteCallbackList<ICarFriendsCallBack> callbacks = new RemoteCallbackList<ICarFriendsCallBack>();
	private RemoteCallbackList<IFocusChangeCallBack> focuscallbacks = new RemoteCallbackList<IFocusChangeCallBack>();
	
	private CustomMvwSession mCustomMvwSession; //自定义唤醒
	private List<String> awakewords; //唤醒词
	
	private static final String TAG = "VoiceControlService";
	@Override
	public IBinder onBind(Intent intent) {
		return carfriendsServer;
	}
		
	@Override
	public void onCreate() {
		super.onCreate();
		slogUtil = new SlogUtil(this.getClass());
		slogUtil.println();
		//初始化音量大�?
		mSystemProxy = new SystemProxy();
		settingsProxy = new SettingsProxy();
		systemStateManager = new SystemStateManager(getApplicationContext(), mSystemProxy);
		mSystemProxy.registSystemInfoChangedListener(systemStateManager);
		initVolume();
		
		mSystemStateListener = new ImplSystemStateListener(this);
		
		//开机起来发送一个特殊的源给MCU，防止不出按键音
		mSystemProxy.setMcuSource(McuConstant.SYS_SOURCE_OTHER_APP);
		
		//初始化监控顶层应用的改变
		mainHandler = new MainHandler(getMainLooper());
		packageManager = new TopPackageManager(getApplicationContext());
		mCarServiceManager = CarServiceManager.getCarServiceManager();
		mCarServiceManager.registPackageChangedListener(getApplicationContext(), packageManager);
		
		// 初始化蓝牙连接状态监�?
		BluetoothManager.getBluetoothManager(getApplicationContext());
		
		// 初始化语音控制类的生�?
		ControllrFactory.getInstance().initFactory(getApplicationContext());
		// 注册蓝牙通话状、导航等广播接收
		reigsterStateChanged();
		// 服务启动完成，请求蓝牙状�?
		requestBtState();
		mCarStateReceiver = new CarStateReceiver();
		mCarStateReceiver.setCarStateChangeListener(mSystemStateListener);
		registerReceiver(mCarStateReceiver);
		mInputKeyManager = InputKeyManager.getInstance(getApplicationContext());
		mInputKeyManager.setServiceHandler(ServiceHandler);
		AudioFocusManager.getInstance(getApplicationContext()).setServiceHandler(ServiceHandler);
		ServiceManager.addService(SERVICE_NAME, carfriendsServer);
	}
	
	/**
	 * 初始化系统音量以及蓝牙�?�话的大小， 通过获取MCU的音量设置到系统�?
	 */
	private void initVolume(){
		try{
			int systemVolume = 16;
			if(getBootVolumeSwitch()){
				systemVolume = settingsProxy.getDefVol();
			}else{
				systemVolume = settingsProxy.getVol();
			}
			int btVolume = settingsProxy.getBtVol();
			slogUtil.i("systemVolume getDefVol =" + systemVolume + ",btVolume=" + btVolume);
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, systemVolume, 0);
			audioManager.setStreamVolume(AudioManager.STREAM_BLUETOOTH_SCO, btVolume, 0);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
	
	
	private void requestBtState(){
		Intent intent = new Intent();
		intent.setAction(ACTION_REQ_BT_STATE);
		sendBroadcast(intent);
	}
	
	private void reigsterStateChanged(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NOTIFY_BLUETOOTH_CONTACT_CHANGED);
		intentFilter.addAction(SOUGOU_NAVIGATION_ACTION);
		intentFilter.addAction(ACTION_BT_CALL_STATE);
		intentFilter.addAction(BT_CALLING);
		intentFilter.addAction(BLUETOOTH_CONNECT_STATE);
		intentFilter.addAction(GAODE_NAVIGATION_START_ACTION);
		intentFilter.addAction(GAODE_NAVIGATION_STOP_ACTION);
		intentFilter.addAction(CARLIFE_TOP_CHANGED);
		registerReceiver(broadcastReceiver, intentFilter);
		
	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,intent.getAction());
			if(BT_CALLING.equals(intent.getAction())){
				boolean isCalling = intent.getBooleanExtra("is_calling", false);
				mInputKeyManager.phoneCallStateChanged(isCalling); 
				//settings.Service has do it self;
				CarSystemSetings.Setings.putBoolean(getContentResolver(),"call_state",isCalling);
				slogUtil.i("isCalling=" + isCalling);
			}else if(BLUETOOTH_CONNECT_STATE.equals(intent.getAction())){
				boolean btConnected = intent.getBooleanExtra("bt_connect", false);
				mSystemStateListener.btStateChanged(btConnected);
				slogUtil.i("btConnected=" + btConnected);
			}else if(CARLIFE_TOP_CHANGED.equals(intent.getAction())){
				if(intent.getStringExtra("status").equals("onResume")){
					if(intent.getStringExtra("packageName").equals("com.zhonghong.carlife")){
						Log.d(TAG, "entry carlife page");
						mInputKeyManager.isCarlifeResume(true);
						try {
							PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_SPEECHOFF);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						Log.d(TAG, "exit carlife page");
						mInputKeyManager.isCarlifeResume(false);
						//如果是通话状态，收到这个回调之后，不需要执行语音的操作
						if(callstate == 2){
							try {
								PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_SPEECHOFF);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return;
						}
						
						try {
							PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_SPEECHON);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}else{
				Message.obtain(mainHandler, 0, intent).sendToTarget();
			}
		}
	};
	
	/**
	 * 导航语音播放状态
	 * @param playState 播放状态 1：开始播报  2：播报完成
	 */
	private void naviTtsPlayState(int playState){
		Log.i(TAG,"naviTtsPlayState:"+playState);
		if(1 == playState){
			mSystemProxy.notifyNaviVoiceToMcu(1);
		}else if(2 == playState){
			mSystemProxy.notifyNaviVoiceToMcu(0);
		}
	}
	
	/**
	 * 获取开机音量开关
	 * 
	 * @return
	 */
	public boolean getBootVolumeSwitch() {
		int switchIntState = getSysFlag2();
		Log.i(TAG, "getBootVolumeSwitch():switchIntState = " + switchIntState);
		boolean bootVolSwitchState = (switchIntState & 0x04) == 4 ? true : false;
		Log.i(TAG, "getBootVolumeSwitch():bootVolSwitchState = " + bootVolSwitchState);
		return bootVolSwitchState;
	}
	
	public int getSysFlag2() {
		return settingsProxy.getSysFlag2();
	}
	
	private class MainHandler extends Handler{
		public MainHandler(Looper mainLooper) {
			super(mainLooper);
		}
		@Override
		public void handleMessage(Message msg){
			Intent intent = (Intent) msg.obj;
			if(NOTIFY_BLUETOOTH_CONTACT_CHANGED.equals(intent.getAction())){
				//蓝牙联系人同步完�?
				mSystemStateListener.syncContactFinish();
				return;
			}else if(ACTION_BT_CALL_STATE.equals(intent.getAction())){
				//蓝牙通话状�?�改�?
				
				int state = intent.getIntExtra("state", -1);
				callstate = state;
				String name = intent.getStringExtra("name");
				String number = intent.getStringExtra("number");
				Log.i(TAG, "call state :" + state);
				mSystemStateListener.talkStateChanged(state, name, number);
				mInputKeyManager.setCallState(state);
				return;
			}else if(GAODE_NAVIGATION_START_ACTION.equals(intent.getAction())){
				naviTtsPlayState(1);
				return;
			}else if(GAODE_NAVIGATION_STOP_ACTION.equals(intent.getAction())){
				naviTtsPlayState(2);
				return;
			}
			
			
		}
	}
	
	public Handler ServiceHandler = new Handler(){
		public void dispatchMessage(Message msg) {
			switch(msg.what){
			case 0x10:
				//开启车友圈录音
				Log.i(TAG, "ServiceHandler:开启车友圈录音");
					try {
						int length  = callbacks.beginBroadcast();
						for(int i=0;i<length;i++){
							callbacks.getBroadcastItem(i).startSpeech(true);
						}
						callbacks.finishBroadcast();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case 0x11:
				//关闭车友圈录音
				Log.i(TAG, "ServiceHandler:关闭车友圈录音");
					try {
						int length  = callbacks.beginBroadcast();
						for(int i=0;i<length;i++){
							callbacks.getBroadcastItem(i).startSpeech(false);
						}
						callbacks.finishBroadcast();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case 0x12:
				//自定义结果反馈
				try {
					int length  = callbacks.beginBroadcast();
					for(int i=0;i<length;i++){
						callbacks.getBroadcastItem(i).onWakeupResult(msg.arg1);
					}
					callbacks.finishBroadcast();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 0x13:
				//重新执行唤醒操作
				Log.i(TAG, "重新执行开启唤醒功能！");
				mCustomMvwSession.startBackgroundMvw(awakewords, true);
				break;
			case 0x20:
				//发送当前焦点
				Log.i(TAG, "ServiceHandler:发送当前焦点");
				try {
					int length  = focuscallbacks.beginBroadcast();
					for(int i=0;i<length;i++){
						focuscallbacks.getBroadcastItem(i).focusChange(msg.arg1, msg.arg2);
					}
					focuscallbacks.finishBroadcast();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		};
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		slogUtil.println();
		// 注销系统消息广播
		unregisterReceiver(broadcastReceiver);
		// 解除顶层activity改变回调
		mCarServiceManager.unRegistPackageChangedListener(this, packageManager);
		// 通知语音应用即将关闭
		mSystemStateListener.onFinishing();
		//方式自定义唤醒
		if(mCustomMvwSession!=null){
			mCustomMvwSession.release();
		}
		
		// 重新启动服务
		Intent intent = new Intent(this,VoiceControlService.class);
		startService(intent);
		
	}
	
	private IBinder carfriendsServer = new IVoiceServer.Stub() {
		
		@Override
		public void setSpeechStatus(boolean isSpeech) throws RemoteException {
			Log.i(TAG, "setSpeechStatus:"+isSpeech);
			mInputKeyManager.setcarFriendsSpeechStatus(isSpeech);
		}
		
		@Override
		public void setPlayStatus(boolean isPlay) throws RemoteException {
			Log.i(TAG, "setPlayStatus:"+isPlay);
		}
		
		@Override
		public void registerCallBack(ICarFriendsCallBack callback) throws RemoteException {
			Log.i(TAG, "registerCallBack");
			VoiceControlService.this.callbacks.register(callback);
		}
		
		@Override
		public void unregisterCallBack(ICarFriendsCallBack callback) throws RemoteException {
			VoiceControlService.this.callbacks.unregister(callback);
		}

		@Override
		public void registerFocusCallBack(IFocusChangeCallBack callback) throws RemoteException {
			Log.i(TAG, "registerFocusCallBack");
			VoiceControlService.this.focuscallbacks.register(callback);
		}

		@Override
		public void unregisterFocusCallBack(IFocusChangeCallBack callback) throws RemoteException {
			VoiceControlService.this.focuscallbacks.unregister(callback);
		}
		
		/**
		 * 开启语音唤醒
		 */
		@Override
		public void startSpeechAwaken(List<String> words) throws RemoteException {
			if(mCustomMvwSession == null){
				mCustomMvwSession = CustomMvwSession.getInstance(getApplicationContext(), customcallback);
			}
			
			if(words.isEmpty()){
				Log.i(TAG, "words is null!");
			}
			
			for(int i=0;i<words.size();i++){
				Log.i(TAG, "words["+i+"]:"+words.get(i));
			}
			awakewords = words;
			int resultcode = mCustomMvwSession.startBackgroundMvw(words, true);
			Log.i(TAG, "resultcode:"+resultcode);
			if(resultcode != 0){
				ServiceHandler.sendEmptyMessageDelayed(0x13, 1500);
			}
		}

		@Override
		public void stopSpeechAwaken() throws RemoteException {
			Log.i(TAG, "stopSpeechAwaken!");
			mCustomMvwSession.stopMvw();
		}

	};
	
	ICustomMvwCallback customcallback = new ICustomMvwCallback() {
		
		@Override
		public void onWakeupResult(int nMvwId, int nMvwScore) {
			Log.i(TAG, "nMvwId:"+nMvwId);
			Message msg = ServiceHandler.obtainMessage();
			msg.what = 0x12;
			msg.arg1 = nMvwId;
			ServiceHandler.sendMessage(msg);
		}
		
		@Override
		public void initMvwCallback(boolean state, int errId) {
			Log.i(TAG, "initMvwCallback :"+state + " ;errId:"+errId);
		}
		
		@Override
		public void initCallback(boolean state, int errId) {
			Log.i(TAG, "initCallback :"+state + " ;errId:"+errId);
			if(!state){
				mCustomMvwSession.initService();
			}
		}
	};
	
}
