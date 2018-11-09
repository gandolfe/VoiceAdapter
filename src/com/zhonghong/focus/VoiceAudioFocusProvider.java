package com.zhonghong.focus;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.zhonghong.utils.SlogUtil;

public class VoiceAudioFocusProvider extends ContentProvider{
	
	private SlogUtil slogUtil = new SlogUtil(getClass());
	private AudioFocusManager mFocusManager;
	
	private static final String TAG = "VoiceAudioFocusProvider";
	@Override
	public boolean onCreate() {
		slogUtil.println();
		mFocusManager = AudioFocusManager.getInstance(getContext().getApplicationContext());
		return false;
	}
	
	@Override
	public Bundle call(String method, String arg, Bundle extras) {
		Log.i(TAG, "method=" + method + ", packageName=" + arg);
		String packageName = getPackageName(arg);
		if("notifyFocusChange".equals(method)){
			if(mFocusManager == null){
				mFocusManager = AudioFocusManager.getInstance(getContext().getApplicationContext());
			}
			try{
				mFocusManager.notifyFocusChanged(packageName);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else if("abandonAudioFocus".equals(method)){
			if(mFocusManager == null){
				mFocusManager = AudioFocusManager.getInstance(getContext().getApplicationContext());
			}
			try{
				mFocusManager.abandonAudioFocus(packageName);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else if("getTopFocusPkg".equals(method)){
			String pkg = mFocusManager.getCurrentFocus();
			return Bundle.forPair("pkg", pkg);
		}else if("getBeforeRestartingFocusPkg".equals(method)){
			String pkg = mFocusManager.getBeforeRestartingFocusPkg();
			return Bundle.forPair("pkg", pkg);
		}
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", true);
		return bundle;
	}
	
	private String getPackageName(String pkg){
		try{
			String[] pkgs = pkg.split("@");
			if(pkgs.length <= 1){
				return "";
			}
			
			if(pkgs[1].length() <= 8){
				return "";
			}
			String first = pkgs[1];
			String packageName = first.substring(8, first.indexOf("$"));
			return packageName;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "";
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3, String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
}
