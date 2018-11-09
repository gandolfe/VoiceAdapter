package com.zhonghong.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.zhonghong.mcuservice.McuHardKeyInfo;
import android.zhonghong.mcuservice.McuHardKeyProxy;
import android.zhonghong.mcuservice.RegistManager;
import android.zhonghong.mcuservice.RegistManager.IMcuHardKeyChangedListener;
import android.zhonghong.mcuservice.SettingsProxy;
import android.zhonghong.mcuservice.SystemProxy;

import com.zhonghong.can.CanControlManager;
import com.zhonghong.can.CanHelper;
import com.zhonghong.focus.AudioFocusManager;
import com.zhonghong.focus.ControllrFactory;
import com.zhonghong.focus.PackageConstant;
import com.zhonghong.iflyinterface.MediaControlCallBack;
import com.zhonghong.iflyplatformadapter.BTMusicController;
import com.zhonghong.iflyplatformadapter.MusicController;
import com.zhonghong.iflyplatformadapter.RadioController;
import com.zhonghong.iflyplatformadapter.VideoController;
import com.zhonghong.receive.CarStateReceiver;
import com.zhonghong.utils.SlogUtil;
import com.zhonghong.views.VolumeWindow;

/**
 * 这个类主要集中处理方控按键消息，
 * 1.包括切源（服务监控了音频焦点的变化，可以直接获取当前音频焦点的应用方便切源）。
 * 2.音量的控制。
 * 3.处理按键上下曲控制媒体应用（也是通过当前焦点，将事件通过应用中的服务starrService（）
 * 这种方式，将命令传递给相关应用）。
 * 4.以及一些快捷打开应用的事件。
 * @author lan
 * @date 2018 上午9:58:25
 */
public class InputKeyManager {
	
	private static final int KEYCODE_SRC 			= 0x0D;
	private static final int KEYCODE_MUTE 			= 0x0F;
	private static final int KEYCODE_PREVIOUS 		= 0x13;  //19
	private static final int KEYCODE_NEXT 			= 0x14;  //20
	private static final int KEYCODE_LONG_PREVIOUS  = 0x4B;  // 长按上一曲
	private static final int KEYCODE_LONG_NEXT 	    = 0x4A;  // 长按下一曲
	private static final int KEYCODE_VOL_UP 		= 0x17;  //23
	private static final int KEYCODE_VOL_DOWN 		= 0x18;  //24
	private static final int KEYCODE_SET            = 0x34;  //52设置
	private static final int KEYCODE_AM_FM          = 0x40;  //64切换AM和FM
	private static final int KEYCODE_PHONE_ACCEPT 	= 0x5D;  //
	private static final int KEYCODE_PHONE_HANGUP 	= 0x5C;
	private static final int KEYCODE_TELHONE        = 0x62;  //打开蓝牙电话界面
	private static final int KEYCODE_NAVIGATION     = 0x7E;  //打开地图
	private static final int KEYCODE_BACK           = 0x98; //返回键
	private static final int KEYCODE_HOME 			= 0xAC;
	private static final int KEYCODE_DISP           = 0xAE;  //待机
	private static final int KEYCODE_VOICE 			= 0xB2;  //一键语音
	private static final int KEYCODE_RECORD 		= 0xB3;  //长按录音
	
	private SystemProxy systemProxy = null;
	private static McuHardKeyProxy hardKeyProxy;
	private static InputKeyManager inputKeyManager;
	private Context mContext;
	private SlogUtil slogUtil;
	
	private boolean isCalling = false;
	private boolean isUpdate = false;
	private int callstate = CALLSTATE_DEFAULT;  //1来电 ，2接通，3 正常情况 
	private static final int CALLSTATE_DEFAULT = 3;
	private static final int CALLSTATE_COMING = 1;
	private static final int CALLSTATE_TALKING = 2;
	
	private boolean isCarlifeOnresume = false;
	
	private long lastInputKeyTime = 0;
	private String currentFocusPkg;
	private AudioManager audioManager;
	
	private Handler ServiceHandler;
	private boolean cfspeech = false; //车友圈录音状态
	//是否升级的广播（升级过程中不响应按键）
	private static final String START_UPDATE = "com.zhonghong.action.START_UPDATE";
	private static final String CANCEL_UPDATE = "com.zhonghong.action.CANCEL_UPDATE";
	
	//语音按键的广播，分长按和短按
	private static final String VOICE_KEY_ACTION = "com.zhonghong.action.HARD_KEY_VOICE";
	
	private static final String TAG = "InputKeyManager";
	public static InputKeyManager getInstance(Context context){
		synchronized (InputKeyManager.class) {
			if(hardKeyProxy == null){
				inputKeyManager = new InputKeyManager(context);
			}
			return inputKeyManager;
		}
	}
	
	private InputKeyManager(Context context) {
		this.mContext = context;
		slogUtil = new SlogUtil(getClass());
		systemProxy = new SystemProxy();
		initVolume();
		hardKeyProxy = new McuHardKeyProxy();
		hardKeyProxy.registKeyInfoChangedListener(changedListener);
		audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		VolumeWindow.getInstance(mContext).setOnSeekBarChangeListener(changels);
		registerBroadCast();
	}
	
	public void setServiceHandler(Handler hler){
		ServiceHandler = hler;
	}
	
	Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch(msg.what){
			case 0x1:
				volumeUp();
				break;
			case 0x2:
				volumeDown();
				break;
			}
		};
	};
	
	/**
	 * 车友圈的录音状态
	 * @param isSpeech
	 */
	public void setcarFriendsSpeechStatus(boolean isSpeech){
		cfspeech = isSpeech;
	}
	
	private RegistManager.IMcuHardKeyChangedListener changedListener = new IMcuHardKeyChangedListener() {
		@Override
		public void notify(McuHardKeyInfo arg1) {
			if(arg1 == null){
				slogUtil.i("McuHardKeyInfo is null");
				return;
			}
			try{
				slogUtil.i("key_code=" + arg1.getKeyCode());
				int code = arg1.getKeyCode();
				
				//当系统在倒车，休眠，或者关屏的状态，不响应按键事件
				if(CarStateReceiver.isAsternOn || CarStateReceiver.isAccOff || CarStateReceiver.isPowerOff){
					slogUtil.i("isAsternOn=" + CarStateReceiver.isAsternOn + ",isAccOff="+CarStateReceiver.isAccOff+",isPowerOff="+CarStateReceiver.isPowerOff);
					return;
				}
				
				//升级过程不响应按键
				if(isUpdate){
					Log.i(TAG, "is updating!");
					return;
				}
				
				//打电话的时候，可以退出AVM
				if(code == KEYCODE_BACK && isCalling){
					exitAVM();
				}
				
				//当在通话过程中，只有音量加减和挂断电话有作用
				if((code != KEYCODE_MUTE 
						&& code != KEYCODE_VOL_UP
						&& code != KEYCODE_VOL_DOWN 
						&& code != KEYCODE_PHONE_ACCEPT 
						&& code != KEYCODE_PHONE_HANGUP) && isCalling){
					slogUtil.i("isCalling");
					return;
				}
				
				
				if(audioManager != null){
					audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
				}
				
				if(arg1.getKeyCode() != KEYCODE_DISP){
					closeDisp();
				}
				
				switch (arg1.getKeyCode()) {
				
				case KEYCODE_NAVIGATION:
					if(often(500)){
						startNavigation();
					}
					break;
				case KEYCODE_SET:
					if(often(300)){
						startSettings();
					}
					break;
				case KEYCODE_TELHONE:
					if(often(300)){
						startTelPhone();
					}
					break;
				case KEYCODE_PREVIOUS:
					if(often(300)){
						
						MediaControlCallBack mediaControlCallBack1 = ControllrFactory.getInstance().getMediaControlller();
						if(mediaControlCallBack1!=null){
							mediaControlCallBack1.playPre();
						}
						 
					}
					break;
				case KEYCODE_NEXT:
					if(often(300)){
						
						MediaControlCallBack mediaControlCallBack2 = ControllrFactory.getInstance().getMediaControlller();
						if(mediaControlCallBack2!=null){
							mediaControlCallBack2.playNxt();
						}
						
					}
					break;
				case KEYCODE_LONG_PREVIOUS:
					//长按上一曲
					MediaControlCallBack mediaControlCallBack3 = ControllrFactory.getInstance().getMediaControlller();
					if(mediaControlCallBack3 instanceof RadioController){
						((RadioController) (mediaControlCallBack3)).longPlayPre();
					}
					break;
				case KEYCODE_LONG_NEXT:
					//长按下一曲
					MediaControlCallBack mediaControlCallBack4 = ControllrFactory.getInstance().getMediaControlller();
					if(mediaControlCallBack4 instanceof RadioController){
						((RadioController) (mediaControlCallBack4)).longPlayNxt();
					}
					break;
				case KEYCODE_HOME:
					goHome(mContext);
					break;
				case KEYCODE_BACK:
					try {
					     //虚拟返回按钮
					     Runtime.getRuntime().exec("input keyevent "+KeyEvent.KEYCODE_BACK);
					     Log.i(TAG, "KEYCODE_BACK ok");
					} catch (Exception e) {
					  Log.i(TAG, "KEYCODE_BACK error");
					}
					break;
				case KEYCODE_SRC:
					//获取当前系统焦点所在的应用
					if(often(500)){
						currentFocusPkg = AudioFocusManager.getInstance(mContext).getCurrentFocus();
						nextMedia();
					}
					break;
				case KEYCODE_MUTE:
					if(often(100)){
						mute();
					}
					break;
				case KEYCODE_VOL_UP:
					handler.sendEmptyMessage(0x1);
					break;
				case KEYCODE_VOL_DOWN:
					handler.sendEmptyMessage(0x2);
					break;
				case KEYCODE_VOICE:
					if(often(500)){
						if(isCarlifeOnresume){
							ControllrFactory.getInstance().getCarLifeController().openVoice();
						}else if(cfspeech){
							//停止车友圈录音
							ServiceHandler.sendEmptyMessage(0x11);
							cfspeech = false;
						}else{
							openVoiceAisstant();
						}
					}
					break;
				case KEYCODE_RECORD:
					if(often(500)){
						//开启车友圈录音
						ServiceHandler.sendEmptyMessage(0x10);
					}
					break;
				case KEYCODE_PHONE_ACCEPT:
					if(often(300)){
						if(callstate == CALLSTATE_DEFAULT){
							startTelPhone();
						}else if (callstate == CALLSTATE_COMING){
							ControllrFactory.getInstance().getCallController().accept();
						}
					}
					break;
				case KEYCODE_PHONE_HANGUP:
					if(often(300)){
						ControllrFactory.getInstance().getCallController().hangUp();
					}
					break;
				case KEYCODE_AM_FM:
					if(often(300)){
						change_AM_FM();
					}
					break;
				case KEYCODE_DISP:
					if(often(500)){
						setDisp();
					}
					break;
				}
				
				if(arg1.getKeyCode() == KEYCODE_BACK || arg1.getKeyCode() == KEYCODE_HOME 
						|| arg1.getKeyCode() == KEYCODE_SRC || arg1.getKeyCode() == KEYCODE_NAVIGATION
						|| arg1.getKeyCode() == KEYCODE_AM_FM || arg1.getKeyCode() == KEYCODE_DISP
						|| arg1.getKeyCode() == KEYCODE_SET || arg1.getKeyCode() == KEYCODE_TELHONE){
					
					
					exitAVM();
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	};

	/**
	 * 根据不同功能设置不同的频繁操作时间
	 * @param milli 时间间隔（毫秒）
	 * @return
	 */
	private boolean often(int milli){
		long curTime = SystemClock.elapsedRealtime() - lastInputKeyTime;
		if(curTime < milli){
			Log.i(TAG, "often false");
			return false;
		}
		lastInputKeyTime = SystemClock.elapsedRealtime();
		return true;
	}
	
	/**
	 * 打开地图导航
	 */
	private void startNavigation(){
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ComponentName cn = new ComponentName("com.autonavi.amapauto", "com.autonavi.auto.remote.fill.UsbFillActivity");
		intent.setComponent(cn);
		mContext.startActivity(intent);
		Log.i(TAG, "start Navigation!");
	}
	
	/**
	 * 打开蓝牙电话
	 */
	private void startTelPhone(){
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ComponentName cn = new ComponentName("com.zhonghong.bluetooth", "com.zhonghong.bluetooth.MainActivity");
		intent.setComponent(cn);
		mContext.startActivity(intent);
		Log.i(TAG, "start telphone!");
	}
	
	/**
	 * 打开设置应用
	 */
	private void startSettings(){
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ComponentName cn = new ComponentName("com.zhonghong.settings", "com.zhonghong.settings.MainActivity");
		intent.setComponent(cn);
		mContext.startActivity(intent);
		Log.i(TAG, "start settings");
	}
	
	public void phoneCallStateChanged(boolean isCalling){
		slogUtil.i("isCalling=" + isCalling);
		this.isCalling = isCalling;
	}
	
	public void setCallState(int state){
		callstate = state;
	}
	
	/**
	 * 发送休眠按键值给到设置服务
	 */
	private void setDisp(){
		Intent intent = new Intent(PackageConstant.SETTINGS);
		intent.putExtra("keycode", "disp");
		mContext.startService(intent);
		Log.i(TAG, "setDisp");
	}
	
	private void closeDisp(){
		Intent intent = new Intent(PackageConstant.SETTINGS);
		intent.putExtra("keycode", "closedisp");
		mContext.startService(intent);
		Log.i(TAG, "closeDisp");
	}
	
	/**
	 * 收音机AM、FM切换
	 */
	private void change_AM_FM(){
		Intent intent = new Intent(PackageConstant.RADIO_SERVER_ACTION);
		intent.putExtra("action", "am_fm");
		mContext.startService(intent);
		Log.i(TAG, "change_AM_FM");
	}
	
	
	/**
	 * 初始化音量大小
	 */
	private void initVolume(){
		//发送音量改变的广播
		slogUtil.println();
		mContext.sendBroadcast(new Intent(AudioManager.VOLUME_CHANGED_ACTION));
	}
	
	/**
	 * 系统静音
	 */
	private void mute(){
		slogUtil.i("isCalling=" + isCalling);
		if(!isCalling){
			AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			mAudioManager.setMasterMute(!mAudioManager.isMasterMute());
			slogUtil.i("isMute=" + mAudioManager.isMasterMute());
			mContext.sendBroadcast(new Intent(AudioManager.VOLUME_CHANGED_ACTION));
		}
	}
	
	/**
	 * 降低音量
	 */
	private void volumeDown(){
		AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		if(mAudioManager.isMasterMute()){
			 slogUtil.i("mastermute,set mute false!");
			 mute();
			 return;
		 }
		if(isCalling){
			int max01 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			int volume = mAudioManager.getStreamVolume(AudioManager. STREAM_VOICE_CALL) - 1;
			volume = volume < 0 ? 0 : volume;
			volume = volume > max01 ? max01 : volume;
			slogUtil.i("volume=" + volume);
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0);
			VolumeWindow.setMax(max01, mContext);
			VolumeWindow.show(volume, mContext);
		}
//		else if(AudioFocusManager.getInstance(mContext).isNavigation()){
//			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_FM);
//			Log.i(TAG, "AudioManager.STREAM_FM max:"+max);
//			int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_FM) - 1;
//			volume = volume < 0 ? 0 : volume;
//			volume = volume > max ? max : volume;
//			slogUtil.i("volume=" + volume);
//			mAudioManager.setStreamVolume(AudioManager.STREAM_FM, volume, 0);
//			VolumeWindow.show(volume, mContext);
//		}
		else{
			int max03 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
			int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) - 1;
			volume = volume < 0 ? 0 : volume;
			volume = volume > max03 ? max03 : volume;
			slogUtil.i("volume=" + volume);
			mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
			VolumeWindow.setMax(max03, mContext);
			VolumeWindow.show(volume, mContext);
		}
	}
	
	/**
	 * 增加音量
	 */
	private void volumeUp(){
		AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		 if(mAudioManager.isMasterMute()){
			 slogUtil.i("mastermute,set mute false!");
			 mute();
			 return;
		 }
		if(isCalling){
			int max01 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) + 1;
			volume = volume < 0 ? 0 : volume;
			volume = volume > max01 ? max01 : volume;
			slogUtil.i("bt call volume=" + volume);
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0);
			VolumeWindow.setMax(max01, mContext);
			VolumeWindow.show(volume, mContext);
		}
//		else if(AudioFocusManager.getInstance(mContext).isNavigation()){
//			int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_FM);
//			Log.i(TAG, "AudioManager.STREAM_FM max:"+max);
//			int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_FM) + 1;
//			volume = volume < 0 ? 0 : volume;
//			volume = volume > max ? max : volume;
//			slogUtil.i("volume=" + volume);
//			mAudioManager.setStreamVolume(AudioManager.STREAM_FM, volume, 0);
//			VolumeWindow.setMax(max, mContext);
//			VolumeWindow.show(volume, mContext);
//		}
		else{
			int max03 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
			int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) + 1;
			volume = volume < 0 ? 0 : volume;
			volume = volume > max03 ? max03 : volume;
			slogUtil.i("system volume=" + volume);
			mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
			VolumeWindow.setMax(max03, mContext);
			VolumeWindow.show(volume, mContext);
		}
	}
	
	public VolumeWindow.OnSeekBarChangeListener changels = new VolumeWindow.OnSeekBarChangeListener() {
		
		@Override
		public void datachange(int progress) {
			slogUtil.i("datachange=" + progress);
			AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			if(isCalling){
				mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, 0);
			}else{
				mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, progress, 0);
			}
		}
	};
	
	/**
	 * 打开车联网
	 */
	private void openCarNetwork(){
		slogUtil.println();
		
	}
	
	/**
	 * 打开语音助手
	 */
	private void openVoiceAisstant(){
		Log.i(TAG, "openVoiceAisstant");
		//语音会监听按键消息
		Intent intent = new Intent();
		ComponentName cn = new ComponentName("com.iflytek.cutefly.speechclient", "com.iflytek.autofly.SpeechClientService");
		intent.setComponent(cn);
		intent.putExtra("fromservice", "com.iflytek.voiceadapter");
		mContext.startService(intent);
	}
	
	/**
	 * 播放下一个媒体（切缘）
	 * 本方法采用递归的方式，如果当前媒体无法播放，递归修找下一个可以播放的媒体，直到找到可以播放的媒体为止。
	 */
	private void nextMedia(){
		if(isCalling){
			slogUtil.i("isCalling");
			return;
		}
		try {
			slogUtil.i("focusPkg=" + currentFocusPkg);
			if(TextUtils.isEmpty(currentFocusPkg)){
				//如果当前没有播放的应用，默认跳转到收音
				transferToRadio();
				return;
			}
			if(currentFocusPkg.equals(PackageConstant.RADIO_FOCUS)){
				if(transferToMusic()){
					return;
				}else{
					nextMedia();
					return;
				}
			}
			
			if(currentFocusPkg.equals(PackageConstant.MUSIC_FOCUS)){
				if(transferToBtMusic()){
					return;
				}else{
					nextMedia();
					return;
				}
			}
			
			if(currentFocusPkg.equals(PackageConstant.BT_FOCUS)){
				if(transferToVideo()){
					return;
				}else{
					nextMedia();
					return;
				}
			}
			
			if(currentFocusPkg.equals(PackageConstant.VIDEO_FOCUS)){
				if(transferToRadio()){
					return;
				}else{
					nextMedia();
					return;
				}
			}
			//如果在其它源，直接切换到收音
			transferToRadio();
		} catch (Exception e) {
			slogUtil.i("getCurrentAudioFocusPackage exception;ex=" + e.getMessage());
		}
	}
	
	/**
	 * 跳转到音乐
	 * @return 是否成功
	 */
	private boolean transferToMusic(){
		currentFocusPkg = PackageConstant.MUSIC_FOCUS;
		MusicController musicController = (MusicController) ControllrFactory.getInstance().getMusicController();
		if(musicController != null && isUsbMount("/mnt/USB") && MediaSourceManager.hasMusicFile(mContext)){
			slogUtil.println();
			musicController.openApp();
			return true;
		}
		slogUtil.i("not find music file");
		return false;
	}
	
	/**
	 * 跳转到视频
	 * @return 是否成功
	 */
	private boolean transferToVideo(){
		currentFocusPkg = PackageConstant.VIDEO_FOCUS;
		VideoController mVideoController = (VideoController) ControllrFactory.getInstance().getVideoController();
		if(mVideoController != null && isUsbMount("/mnt/USB") && MediaSourceManager.hasVideoFile(mContext)){
			slogUtil.println();
			mVideoController.openApp();
			return true;
		}
		slogUtil.i("not find video file");
		return false;
	}
	
	/**
	 * 跳转到收音
	 * @return 是否成功
	 */
	private boolean transferToRadio(){
		currentFocusPkg = PackageConstant.RADIO_FOCUS;
		RadioController radioController = (RadioController) ControllrFactory.getInstance().getRadioController();
		if(radioController != null){
			slogUtil.println();
			radioController.openApp();
			return true;
		}
		return false;
	}
	
	/**
	 * 跳转到蓝牙音乐
	 * @return 是否成功
	 */
	private boolean transferToBtMusic(){
		currentFocusPkg = PackageConstant.BT_FOCUS;
		BTMusicController mBTMusicController = (BTMusicController) ControllrFactory.getInstance().getBtMusicController();
		if(mBTMusicController != null && BluetoothManager.getBluetoothManager(mContext).isConnected()){
			slogUtil.println();
			mBTMusicController.openApp();
			return true;
		}
		slogUtil.i("bt not connected");
		return false;
	}
	
	/**
	 * 返回hone界面
	 * @param context
	 */
	private void goHome(Context context){
		Intent intent = new Intent();
		// 为Intent设置Action、Category属性
		intent.setAction(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);
	}
	
	/**
	 * U盘是否已经在设备上了
	 * @param path
	 * @return
	 */
	@SuppressLint("NewApi")
	public boolean isUsbMount(String extraPath) { 
        try { 
        	//判断是否有设备挂载上了
        	StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
    		StorageVolume[] volumes = storageManager.getVolumeList();
    		for(StorageVolume volume : volumes){
    			slogUtil.i("volume=" + volume.getPath() + ",state="+ volume.getState());
    			if(extraPath.equals(volume.getPath()) && Environment.MEDIA_MOUNTED.equals(volume.getState())){
    				return true;
    			}
    		}
        } catch (Exception e) { 
            e.printStackTrace();
        } 
        return false;
    }
	
	private void registerBroadCast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(START_UPDATE);
		filter.addAction(CANCEL_UPDATE);
		mContext.registerReceiver(receiver, filter);
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(START_UPDATE.equals(arg1.getAction())){
				Log.i(TAG, "update is starting!");
				audioManager.setMasterMute(true);
				mContext.sendBroadcast(new Intent(AudioManager.VOLUME_CHANGED_ACTION));
				systemProxy.sendResetCmdToMcu(0x7);
				isUpdate = true;
			}else if(CANCEL_UPDATE.equals(arg1.getAction())){
				Log.i(TAG, "update is cancel!");
				audioManager.setMasterMute(false);
				mContext.sendBroadcast(new Intent(AudioManager.VOLUME_CHANGED_ACTION));
				systemProxy.sendResetCmdToMcu(0x8);
				isUpdate = false;
			}
			
		}
		
	};
	
	private void exitAVM(){
		CanControlManager.getInstance(mContext.getApplicationContext()).execCmd(CanHelper.MODULE_TYPE_AVM, CanHelper.CMD_ACTIVE_COMMAND, "0", null);
	}
	
	public void isCarlifeResume(boolean isresume){
		isCarlifeOnresume = isresume;
	}
	
}
