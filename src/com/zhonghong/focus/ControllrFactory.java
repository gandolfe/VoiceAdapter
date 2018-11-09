package com.zhonghong.focus;

import android.content.Context;
import android.text.TextUtils;

import com.zhonghong.iflyinterface.IBTMusicController;
import com.zhonghong.iflyinterface.ICallController;
import com.zhonghong.iflyinterface.ICarlifeController;
import com.zhonghong.iflyinterface.IKuWoController;
import com.zhonghong.iflyinterface.IMusicController;
import com.zhonghong.iflyinterface.IRadioController;
import com.zhonghong.iflyinterface.IVideoController;
import com.zhonghong.iflyinterface.MediaControlCallBack;
import com.zhonghong.iflyplatformadapter.APPController;
import com.zhonghong.iflyplatformadapter.BTMusicController;
import com.zhonghong.iflyplatformadapter.CallController;
import com.zhonghong.iflyplatformadapter.CarlifeController;
import com.zhonghong.iflyplatformadapter.KuWoController;
import com.zhonghong.iflyplatformadapter.MusicController;
import com.zhonghong.iflyplatformadapter.RadioController;
import com.zhonghong.iflyplatformadapter.VideoController;
import com.zhonghong.utils.SlogUtil;

public class ControllrFactory {
	
	/**科大讯飞 */
	public static final String IFLY = "ifly";
	
	public static String ASSISTANT_TYPE = IFLY;
	
	private Context mContext;
	private SlogUtil slogUtil;
	private ICallController mCallControlCallback;   //电话
	private IMusicController mMusicControlCallback; //音乐（本地）
	private IBTMusicController mBTController; //蓝牙音乐
	private IRadioController mRadioControlCallback; //收音
	private APPController mSystemControlCallback;  //app类打开或者关闭
	private IKuWoController kuWoMediaController;  //酷我音乐
	private IVideoController videoMediaController;  //视频
	private ICarlifeController mCarlifeController;
	
	private static final class SingleInstance{
		private static final ControllrFactory controllrFactory = new ControllrFactory();
	}
	
	public static ControllrFactory getInstance(){
		return SingleInstance.controllrFactory;
	}
	
	public void initFactory(Context context){
		this.mContext = context.getApplicationContext();
		slogUtil = new SlogUtil(getClass());
		createE513Contorller();
		return;
	}
	
	private void createE513Contorller(){
		mCallControlCallback = new CallController(mContext);
		mMusicControlCallback = new MusicController(mContext);
		mBTController = new BTMusicController(mContext);
		mRadioControlCallback = new RadioController(mContext);
		mSystemControlCallback = new APPController(mContext);
		kuWoMediaController = new KuWoController(mContext);
		videoMediaController = new VideoController(mContext);
		mCarlifeController = new CarlifeController();
	}
	
	
	public ICallController getCallController(){
		return mCallControlCallback;
	}
	
	public IMusicController getMusicController(){
		return mMusicControlCallback;
	}
	
	public IRadioController getRadioController(){
		return mRadioControlCallback;
	}
	
	public APPController getSystemController(){
		return mSystemControlCallback;
	}
	
	public IVideoController getVideoController(){
		return videoMediaController;
	}
	
	public IBTMusicController getBtMusicController(){
		return mBTController;
	}
	
	public IKuWoController getKuWoController(){
		return kuWoMediaController;
	}
	
	public ICarlifeController getCarLifeController(){
		return mCarlifeController;
	}
	
	/**
	 * 判断是否是第三方应用焦点
	 * @return
	 */
	public boolean isAlien(){
		String currentFocus =  AudioFocusManager.getInstance(mContext).getCurrentFocus();
		slogUtil.i("currentFocus=" + currentFocus);
		if(!TextUtils.isEmpty(currentFocus)){
			if(currentFocus.contains(PackageConstant.RADIO_FOCUS)){
				return false;
			}else if(currentFocus.contains(PackageConstant.BT_FOCUS)){
				return false;
			}else if(currentFocus.contains(PackageConstant.MUSIC_FOCUS)){
				return false;
			}else if(currentFocus.contains(PackageConstant.KAOLA_FOCUS)){
				return true;
			}else if(currentFocus.contains(PackageConstant.XIMALAYA_RADIO)){
				return true;
			}else if(currentFocus.contains(PackageConstant.KW_FOCUS)){
				return true;
			}
		}
		return true;
	}
	
	/**
	 * 获取语音控制
	 * @return
	 */
	public MediaControlCallBack getMediaControlller(){
		String currentFocus =  AudioFocusManager.getInstance(mContext).getCurrentFocus();
		slogUtil.i("currentFocus=" + currentFocus);
		if(!TextUtils.isEmpty(currentFocus)){
			if(currentFocus.contains(PackageConstant.RADIO_FOCUS)){
				return mRadioControlCallback;
			}else if(currentFocus.contains(PackageConstant.BT_FOCUS)){
				return mBTController;
			}else if(currentFocus.contains(PackageConstant.MUSIC_FOCUS)){
				return mMusicControlCallback;
			}else if(currentFocus.contains(PackageConstant.KW_FOCUS)){
				return kuWoMediaController;
			}else if(currentFocus.contains(PackageConstant.VIDEO_FOCUS)){
				return videoMediaController;
			}else if(currentFocus.contains(PackageConstant.CARLIFE_MUSIC_FOCUS)){
				return mCarlifeController;
			}
		}
		return null;
	}
	
//	public boolean isCarlifeGetFocus(){
//		
//		return AudioFocusManager.getInstance(mContext).isCarlifeFocus();
//	}
}
