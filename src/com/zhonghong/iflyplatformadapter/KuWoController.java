package com.zhonghong.iflyplatformadapter;

import com.zhonghong.iflyinterface.IKuWoController;

import android.content.Context;
import android.util.Log;
import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.PlayMode;
import cn.kuwo.autosdk.api.PlayState;

public class KuWoController implements IKuWoController{

	Context context = null;
	KWAPI mKWAPI = null;
	
	private static final String TAG = "KuWoController";
	public KuWoController(Context context) {
		this.context = context;
		mKWAPI = KWAPI.createKWAPI(context, "auto");
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayState(context, PlayState.STATE_PLAY);
		Log.i(TAG, "play");
	}

	@Override
	public void continuePlay() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayState(context, PlayState.STATE_PLAY);
		Log.i(TAG, "play");
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayState(context, PlayState.STATE_PAUSE);
		Log.i(TAG, "pause");
	}

	@Override
	public void playPre() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayState(context, PlayState.STATE_PRE);
		Log.i(TAG, "pre");
	}

	@Override
	public void playNxt() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayState(context, PlayState.STATE_NEXT);
		Log.i(TAG, "next");
	}

	@Override
	public void setPlayRandomModel() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayMode(context, PlayMode.MODE_ALL_RANDOM);
		Log.i(TAG, "random");
	}

	@Override
	public void setPlaySingleModel() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayMode(context, PlayMode.MODE_SINGLE_CIRCLE);
		Log.i(TAG, "single");
	}

	@Override
	public void setPlayOrderModel() {
		// TODO Auto-generated method stub
		mKWAPI.setPlayMode(context, PlayMode.MODE_ALL_ORDER);
		Log.i(TAG, "order");
	}

	@Override
	public void openApp() {
		mKWAPI.startAPP(context, true);
		Log.i(TAG, "open");
	}

}
