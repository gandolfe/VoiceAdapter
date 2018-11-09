package com.zhonghong.iflyplatformadapter;

import com.zhonghong.iflyinterface.IBTMusicController;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BTMusicController implements IBTMusicController{
	private Context context = null;
	private static final String TAG = "BTMusicController";
	private static final String BT_ACTION = "com.zhonghong.action.btmusic";
	public BTMusicController(Context context) {
		this.context = context;
	}

	@Override
	public void play() {
		Intent intent = new Intent(BT_ACTION);
		intent.putExtra("code", "play");
		context.startService(intent);
		Log.i(TAG, "play");
	}

	@Override
	public void continuePlay() {
		Intent intent = new Intent(BT_ACTION);
		intent.putExtra("code", "play");
		context.startService(intent);
		Log.i(TAG, "continuePlay");
	}

	@Override
	public void pause() {
		Intent intent = new Intent(BT_ACTION);
		intent.putExtra("code", "pause");
		context.startService(intent);
		Log.i(TAG, "pause");
	}

	@Override
	public void playPre() {
		Intent intent = new Intent(BT_ACTION);
		intent.putExtra("code", "pre");
		context.startService(intent);
		Log.i(TAG, "playPre");
	}

	@Override
	public void playNxt() {
		Intent intent = new Intent(BT_ACTION);
		intent.putExtra("code", "next");
		context.startService(intent);
		Log.i(TAG, "playNxt");
	}

	@Override
	public void setPlayRandomModel() {
		
	}

	@Override
	public void setPlaySingleModel() {
		
	}

	@Override
	public void setPlayOrderModel() {
		
	}

	@Override
	public void open() {
		
	}

	@Override
	public void openApp() {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ComponentName cn = new ComponentName("com.zhonghong.btmusic", "com.zhonghong.btmusic.MainActivity");
		intent.setComponent(cn);
		context.startActivity(intent);
		Log.i(TAG, "openApp");
	}

}
