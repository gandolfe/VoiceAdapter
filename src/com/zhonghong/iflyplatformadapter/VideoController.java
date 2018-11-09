package com.zhonghong.iflyplatformadapter;

import com.zhonghong.iflyinterface.IVideoController;
import com.zhonghong.service.InputKeyManager;
import com.zhonghong.service.MediaSourceManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class VideoController implements IVideoController{
	
	private static final String TAG = "VideoController";

	Context context = null;
	
	private static final String VIDEO_ACTION = "com.zhonghong.video.service.VideoService";
	
	public VideoController(Context context) {
		this.context = context;
	}

	@Override
	public void play() {
		Intent intent = new Intent(VIDEO_ACTION);
		intent.putExtra("code", "play");
		context.startService(intent);
		Log.i(TAG, "play");
	}

	@Override
	public void continuePlay() {
		Intent intent = new Intent(VIDEO_ACTION);
		intent.putExtra("code", "play");
		context.startService(intent);
		Log.i(TAG, "play");
	}

	@Override
	public void pause() {
		Intent intent = new Intent(VIDEO_ACTION);
		intent.putExtra("code", "pause");
		context.startService(intent);
		Log.i(TAG, "pause");
	}

	@Override
	public void playPre() {
		Intent intent = new Intent(VIDEO_ACTION);
		intent.putExtra("code", "pre");
		context.startService(intent);
		Log.i(TAG, "pre");
	}

	@Override
	public void playNxt() {
		Intent intent = new Intent(VIDEO_ACTION);
		intent.putExtra("code", "next");
		context.startService(intent);
		Log.i(TAG, "next");
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
		if( InputKeyManager.getInstance(context).isUsbMount("/mnt/USB") && MediaSourceManager.hasVideoFile(context)){
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			ComponentName cn = new ComponentName("com.zhonghong.video", "com.zhonghong.video.main.MainActivity");
			intent.setComponent(cn);
			context.startActivity(intent);
			Log.i(TAG, "openApp");
		}else{
			Log.i(TAG, "no file openApp failed");
		}
		
		
	}

}
