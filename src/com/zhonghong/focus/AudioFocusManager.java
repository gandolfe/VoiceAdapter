package com.zhonghong.focus;

import java.util.Stack;

import com.zhonghong.iflyplatformadapter.MusicController;
import com.zhonghong.iflyplatformadapter.TTSController;
import com.zhonghong.media.database.SourcePreference;
import com.zhonghong.utils.SlogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.zhonghong.mcuservice.McuConstant;
import android.zhonghong.mcuservice.SystemProxy;


/**
 * 协助第三方应用切换音频焦点（MCU），由于在收音通道的情况下，媒体无法播放出声音
 * 需要在第三方请求焦点的时候，将MCU通道切换到媒体的通道。
 * @author lan
 * @date 2017-11-14 15:00
 * @author yangys
 * @date 2018-07-01
 */
public class AudioFocusManager {
	
	private static final String FOCUS_TABLE = "focus_table";
	private static final String SG_SIRI 	= "com.sogou.siri";
	private static final String TXZ_ADAPTER 	= "com.txznet.adapter.AdapterApplication";
	private static final String GAODE_NAVIGATION = "aid";
	private static final String GAODE_NAVIGATION02 = "abl";
	private SourcePreference sourcePreference;
	
	private static AudioFocusManager mAudioFocusManager;
	private IAudioFocus mAudioFocus;
	private SystemProxy systemProxy;
	private boolean isAsisstant = false;
	private SlogUtil slogUtil = new SlogUtil(getClass());
	private Context mcontext;
	private Handler serviceHandler;
	private int lastsrc = McuConstant.SYS_SOURCE_RADIO;  //用于ipk 、媒体中心
	private int currentsrc = McuConstant.SYS_SOURCE_RADIO; //用于ipk 、媒体中心
	private static final String TAG = "AudioFocusManager";
	
	public static AudioFocusManager getInstance(Context context){
		if(mAudioFocusManager == null){
			synchronized (AudioFocusManager.class) {
				mAudioFocusManager = new AudioFocusManager(context.getApplicationContext());
			}
		}
		return mAudioFocusManager;
	}
	
	public void setServiceHandler(Handler h){
		serviceHandler = h;
	}
	
	private interface IAudioFocus{
		void requestAudioFocus(String clientId);
		void abandonAudioFocus(String clientId);
		String getCurrentFocus();
		boolean isNavigation();
	}
	
	private class E531AudioFocusManager implements IAudioFocus{

		private Stack<String> focusStack;
		
		private boolean isNavigation = false;
		
		public E531AudioFocusManager() {
			focusStack = new Stack<>();
		}
		
		@Override
		public void requestAudioFocus(String clientName) {
			if(clientName.equals(PackageConstant.VIDEOSUR_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}else if(clientName.equals(PackageConstant.BT_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_BTMUSIC;
			}else if(clientName.equals(PackageConstant.MUSIC_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_MP3;
				sourcePreference.saveData("pkg", clientName);
			}else if(clientName.equals(PackageConstant.RADIO_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_RADIO;
				sourcePreference.saveData("pkg", clientName);
			}else if(clientName.equals(PackageConstant.VIDEO_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_MEDIAPLAY;
				sourcePreference.saveData("pkg", clientName);
			}else if(clientName.equals(PackageConstant.MUSIC_FOCUS_MAINT)){
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}else if(clientName.equals(PackageConstant.INTERCONECETED_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}else if(clientName.equals(PackageConstant.CARLIFE_MUSIC_FOCUS)){
				Log.i(TAG, "carlife return!");
//				isCarlife = true;
				currentsrc = McuConstant.SYS_SOURCE_IPOD;
			}else if(clientName.equals(GAODE_NAVIGATION) || clientName.equals(GAODE_NAVIGATION02)){
				Log.i(TAG, "gaode navigation");
				systemProxy.notifyNaviVoiceToMcu(1);
				isNavigation = true;
				//导航不进入栈中，便于其他按键的操作，如seek+、-
				return;
			}else if(clientName.equals(PackageConstant.BT_TALKING_FOCUS)){
				//进入蓝牙通话
				systemProxy.setMcuState(systemProxy.entryState(McuConstant.SYS_STATE_BT_TALKING));
				slogUtil.i("entry call");
				focusStack.remove(clientName);
				focusStack.push(clientName);
				currentsrc = McuConstant.SYS_STATE_BT_TALKING;
				return;
			}else if(clientName.equals(PackageConstant.IFLYTEK)){
				//如果请求焦点的是语言助手，则不添加到栈中�?
				slogUtil.i("visstant request focus,do not handle");
				systemProxy.setMcuState(systemProxy.entryState(McuConstant.SYS_STATE_TTS));
				isAsisstant = true;
				currentsrc = McuConstant.SYS_STATE_TTS;
				closeDisp();
				return;
			}else if(clientName.equals(PackageConstant.CARLIFE_TTS_FOCUS)){
				return ;
			}else if(clientName.equals(PackageConstant.CARLIFE_VR_FOCUS)){
				return ;
			}else{
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}
			
			//移除栈中已经存在的焦点名�?
			focusStack.remove(clientName);
			//将新的焦点添加到栈中
			focusStack.push(clientName);
			
			systemProxy.setMcuSource(currentsrc);
			slogUtil.i("clientName=" + clientName + ",source=" + currentsrc);
		}

		@Override
		public void abandonAudioFocus(String packgeName) {
			
			if(GAODE_NAVIGATION.equals(packgeName)|| packgeName.equals(GAODE_NAVIGATION02)){
				Log.i(TAG, "exit gaode navigation!");
				systemProxy.notifyNaviVoiceToMcu(0);
				isNavigation = false;
				return;
			}
			
			if(packgeName.equals(PackageConstant.BT_TALKING_FOCUS)){
				//�?出蓝牙�?�话
				systemProxy.setMcuState(systemProxy.exitState(McuConstant.SYS_STATE_BT_TALKING));
				slogUtil.i("exit call");
			}
			
			if(packgeName.equals(PackageConstant.CARLIFE_MUSIC_FOCUS)){
				Log.i(TAG, "carlife return!");
//				isCarlife = false;
			}
			
			if(packgeName.equals(SG_SIRI) || packgeName.equals(TXZ_ADAPTER)|| packgeName.equals(PackageConstant.IFLYTEK)){
				//语音助手释放焦点�?
				slogUtil.i("visstant abandon focus,do not handle");
				systemProxy.setMcuState(systemProxy.exitState(McuConstant.SYS_STATE_TTS));
				isAsisstant = false;
			}
			focusStack.remove(packgeName);
			String topPkg = focusStack.isEmpty() ? "" : focusStack.peek();
			if(TextUtils.isEmpty(topPkg)){
				//栈顶没有任何媒体�?
				currentsrc = McuConstant.SYS_SOURCE_RADIO;
				return;
			}
			if(topPkg.equals(PackageConstant.VIDEOSUR_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}else if(topPkg.equals(PackageConstant.BT_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_BTMUSIC;
			}else if(topPkg.equals(PackageConstant.BT_TALKING_FOCUS)){
				//上一个焦点释放后，如果又进入到了通话的焦点，再切换到蓝牙通话状�?�，这种情况出现在�?�话过程中acc off再acc on的情�?
				systemProxy.setMcuState(systemProxy.entryState(McuConstant.SYS_STATE_BT_TALKING));
				currentsrc = McuConstant.SYS_STATE_BT_TALKING;
				slogUtil.i("entry call");
				return;
			}else if(topPkg.equals(PackageConstant.MUSIC_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_MP3;
				sourcePreference.saveData("pkg", topPkg);
			}else if(topPkg.equals(PackageConstant.RADIO_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_RADIO;
				sourcePreference.saveData("pkg", topPkg);
			}else if(topPkg.equals(PackageConstant.VIDEO_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_MEDIAPLAY;
				sourcePreference.saveData("pkg", topPkg);
			}else if(topPkg.equals(PackageConstant.MUSIC_FOCUS_MAINT)){
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}else if(topPkg.equals(PackageConstant.INTERCONECETED_FOCUS)){
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}else{
				currentsrc = McuConstant.SYS_SOURCE_OTHER_APP;
			}
			slogUtil.i("topPkg=" + topPkg + ",source=" + currentsrc);
			//如果语音助手界面正在使用焦点，则不处�?
			if(!isAsisstant){
				systemProxy.setMcuSource(currentsrc);
				sourcePreference.saveData("pkg", topPkg);
			}
		}

		@Override
		public String getCurrentFocus() {
			return focusStack.isEmpty() ? "" : focusStack.peek();
		}

		@Override
		public boolean isNavigation() {
			// TODO Auto-generated method stub
			return isNavigation;
		}
	}
	
	/**
	 * 采用单例模式
	 */
	private AudioFocusManager(Context context) {
		systemProxy = new SystemProxy();
		sourcePreference = new SourcePreference(context, FOCUS_TABLE);
		mAudioFocus = new E531AudioFocusManager();
		mcontext = context;
	}
	
	/**
	 * 改变音频焦点
	 * @param packgeName
	 */
	public void notifyFocusChanged(String clientName){
		Log.i(TAG, "clientName=" + clientName);
		if(TextUtils.isEmpty(clientName)){
			return;
		}
		lastsrc = currentsrc;
		mAudioFocus.requestAudioFocus(clientName);
		notifyFocusChange();
		sleep();
	}
	
	public void abandonAudioFocus(String clientName){
		slogUtil.i("clientName=" + clientName);
		if(TextUtils.isEmpty(clientName)){
			return;
		}
		lastsrc = currentsrc;
		mAudioFocus.abandonAudioFocus(clientName);
		notifyFocusChange();
		sleep();
	}
	
	//通知aidl回调接口
	private void notifyFocusChange(){
		Message msg = Message.obtain();
		msg.arg1 = lastsrc;
		msg.arg2 = currentsrc;
		msg.what = 0x20;
		if(serviceHandler==null){
			return;
		}
		serviceHandler.sendMessage(msg);
	}
	
	private void sleep(){
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取当前音频焦点
	 * @return
	 */
	public String getCurrentFocus(){
		return mAudioFocus.getCurrentFocus();
	}
	
	public boolean isNavigation(){
		return mAudioFocus.isNavigation();
	}
	/**
	 * 获取重启前系统最后得焦点
	 * @return
	 */
	public String getBeforeRestartingFocusPkg(){
		return sourcePreference.getStringData("pkg");
	}
	
	private void closeDisp(){
		Intent intent = new Intent(PackageConstant.SETTINGS);
		intent.putExtra("keycode", "closedisp");
		mcontext.startService(intent);
		Log.i(TAG, "closeDisp");
	}
}
