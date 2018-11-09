package com.zhonghong.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MediaSourceManager {
	
	public static final String AUTHORITY = "com.zhonghong.media";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri VIDEO_URI = Uri.withAppendedPath(CONTENT_URI, "video");
	public static final Uri AUDIO_URI = Uri.withAppendedPath(CONTENT_URI, "audio");
	
	/**
	 * 判断是否有音频文件存在
	 * @param context
	 * @return 
	 */
	public static boolean hasMusicFile(Context context){
		try{
			Cursor c = context.getContentResolver().query(AUDIO_URI, new String[]{"path"}, null, null, null);
			if(c != null && c.getCount() > 0){
				Log.i("MediaSourceManager", "hasMusicFile count=" + c.getCount());
				return true;
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断是否有音频文件存在
	 * @param context
	 * @return 
	 */
	public static boolean hasVideoFile(Context context){
		try{
			Cursor c = context.getContentResolver().query(VIDEO_URI, new String[]{"path"}, null, null, null);
			if(c != null && c.getCount() > 0){
				Log.i("MediaSourceManager", "hasMusicFile count=" + c.getCount());
				return true;
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return false;
	}
}
