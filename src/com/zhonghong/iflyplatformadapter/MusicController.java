package com.zhonghong.iflyplatformadapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.platformservice.PlatformService;
import com.zhonghong.focus.AudioFocusManager;
import com.zhonghong.focus.PackageConstant;
import com.zhonghong.iflyinterface.IMusicController;
import com.zhonghong.service.InputKeyManager;
import com.zhonghong.service.MediaSourceManager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import cn.kuwo.autosdk.api.KWAPI;

public class MusicController implements IMusicController{

	private Context context = null;
	public static final int SEARCH_MUSIC=0;
	private static final String AUTHORITY = "content://com.zhonghong.media";
	private static final String AUDIO = "/audio";
	private static final String FILY_NMAE = "cn.kuwo.kwmusiccar";
	public static final String MUSIS_SERVICE_ACTION = "com.zhonghong.music.service.MusicService";
	public static final String MUSIS_ACTIVITY_ACTION = "com.zhonghong.media.MainActivity";
	public static String PATH = "path";//�ļ�·��
	public static String NAME = "name";//�ļ�����
	public static String TITLE = "title";//id3��Ϣ�еı���
	public static String ALBUM = "album";//id3��Ϣ�е�ר��
	public static String ARTIST = "artist";//id3��Ϣ�е��ݳ���
	final String[] musicArrayType = {"au","pcm","samr","flac","m4a","mp3","wav","wma"};
	private KWAPI mKwapi = null;
	private ActivityManager mActivityManager ;
	
	private static final String TAG = "MusicController";
	public MusicController(Context context) {
		this.context = context;
		mKwapi = KWAPI.createKWAPI(context, "auto");
	}

	@Override
	public String playSyncMusic(String jsonstr) {
		
		JSONObject action = null;
		try {
			action = new JSONObject(jsonstr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String songStr = action.optString("song");
		String artistStr = action.optString("artist");
		String category = action.optString("category"); //风格类型
		String source = action.optString("source");  //网络或者本地
		String albumstr = action.optString("album");  //专辑
		
		String selectstr =null;
		String[] args = null;
		JSONArray songs = new JSONArray();
		
		if(songStr!=null && songStr.length()>0){
			selectstr = NAME+" like ?";
			args = new String[1];
			args[0] = "%"+songStr+"%";
		}else if(artistStr!=null && artistStr.length()>0){
			selectstr = ARTIST+" like ?";
			args = new String[1];
			args[0] ="%"+artistStr+"%";
		}
		
		ContentResolver cr = context.getContentResolver(); //ʵ����ContentResolver ����;
		Uri phoneUri = Uri.parse(AUTHORITY);
		Uri phone_contact_uri = Uri.withAppendedPath(phoneUri, AUDIO);
		Cursor cursor = cr.query(phone_contact_uri, null,  
				selectstr, args, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if( cursor != null ){
			Log.v(TAG, "getAllAudios cursor.count = "+cursor.getCount());
			while (cursor.moveToNext()){
				String name = cursor.getString(cursor.getColumnIndex(NAME));
				String path = cursor.getString(cursor.getColumnIndex(PATH));
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				String album = cursor.getString(cursor.getColumnIndex(ALBUM));
				String artist = cursor.getString(cursor.getColumnIndex(ARTIST));
				Log.v(TAG, "getAllAudios name ="+name+", path="+path +" ,title ="+title
						+" ,album ="+album +" ,artist ="+artist);
				
				if(!allowType(path)){
					continue;
				}
				JSONObject jsonobj = new JSONObject();
				try {
					jsonobj.put("song", name);
					jsonobj.put("artist", artist);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				songs.put(jsonobj);
			}
		}
		
		if(songs.length()<1){
			//再次进行模糊查询
			selectAgain(songs,songStr,artistStr);
		}
		
		JSONObject result = new JSONObject();
		try {
			result.put("focus", "music");
			if(songs.length()>=1){
				//search songs success
				result.put("status", "success");
				result.put("result", songs);
				Log.i(TAG, "result json:"+result.toString());
				PlatformService.platformCallback.onSearchPlayListResult(SEARCH_MUSIC, result.toString());
			}else{
				//failed to search songs from local,go to the network music
				 mKwapi.playClientMusics(context, songStr, artistStr, albumstr);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	@Override
	public String playMusic(String jsonstr) {
		
		JSONObject jsonobj =null;
		JSONObject resultobj = new JSONObject();
		try {
			jsonobj = new JSONObject(jsonstr);
			String action = jsonobj.optString("PLAY");
			String song = jsonobj.optString("song");
			String artist = jsonobj.optString("artist");
			
			String selectstr =null;
			String[] args = null;
			
			if(artist.length()>0){
				selectstr = NAME+" = ? and "+ARTIST+" = ?";
				args = new String[2];
				args[0] = song;
				args[1] = artist;
			}else{
				selectstr = NAME+" = ?";
				args = new String[1];
				args[0] = song;
			}
			
			Uri phoneUri = Uri.parse(AUTHORITY);
			Uri phone_contact_uri = Uri.withAppendedPath(phoneUri, AUDIO);
			ContentResolver resolver = context.getContentResolver();
			
			Cursor cursor = resolver.query(phone_contact_uri, null,  
					selectstr, args, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
			String path = null;
			while(cursor.moveToNext()){
				path = cursor.getString(cursor.getColumnIndex(PATH));
			}
			
			if(path == null){
				resultobj.put("status", "fail");
				resultobj.put("message", "播放歌曲失败");
				return resultobj.toString();
			}
			
			//如果本来就是网络app播放，那就继续让网络app播放
			if(AudioFocusManager.getInstance(context).getCurrentFocus().equals(PackageConstant.KW_FOCUS)
					|| FILY_NMAE.equals(getTopName())){
				
				mKwapi.playLocalMusic(context, path);
			}else {
				Intent intent = new Intent(MUSIS_SERVICE_ACTION);
				intent.putExtra("path", path);
				context.startService(intent);
			}
			resultobj.put("status", "success");
			return resultobj.toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
	}
	
	@Override
	public void play() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "play");
		context.startService(intent);
		Log.i(TAG, "play");
	}

	@Override
	public void continuePlay() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "play");
		context.startService(intent);
		Log.i(TAG, "play");
	}

	@Override
	public void pause() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "pause");
		context.startService(intent);
		Log.i(TAG, "pause");
	}

	@Override
	public void playPre() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "pre");
		context.startService(intent);
		Log.i(TAG, "pre");
	}

	@Override
	public void playNxt() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "next");
		context.startService(intent);
		Log.i(TAG, "next");
	}

	@Override
	public void setPlayRandomModel() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "random");
		context.startService(intent);
		Log.i(TAG, "random");
	}

	@Override
	public void setPlaySingleModel() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "single");
		context.startService(intent);
		Log.i(TAG, "single");
	}

	@Override
	public void setPlayOrderModel() {
		Intent intent = new Intent(MUSIS_SERVICE_ACTION);
		intent.putExtra("code", "order");
		context.startService(intent);
		Log.i(TAG, "order");
		
	}
	
	private boolean allowType(String path){
		String type = path.substring(path.lastIndexOf(".")+1);
		String ltype = type.toLowerCase();
		for(int i =0;i<musicArrayType.length;i++){
			if(musicArrayType[i].equals(ltype)){
				return true;
			}
		}
		return false;
	}
	
	private String getTopName(){
		if(mActivityManager == null){
			mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		}
		String tempPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity
				.getPackageName();
		Log.i(TAG, "topActivityName:" + tempPackageName);
		return tempPackageName;
	}
	
	/**
	 * 更加模糊的查询
	 * @param songs
	 * @param title
	 * @param artist
	 */
	private void selectAgain(JSONArray songs,String title,String artist){
		
		String selectstr =null;
		String[] args = null;
		
		if(title!=null&& title.length()>0){
			selectstr = NAME+" like ?";
			args = new String[1];
			if(title.length()>2){
				args[0] = "%"+title.substring(0, 2)+"%";
				Log.i(TAG, "search again title:"+title.substring(0, 2));
			}else{
				args[0] = "%"+title.substring(0, 1)+"%";
				Log.i(TAG, "search again title:"+title.substring(0, 1));
			}
			
			
		}else if(artist!=null&& artist.length()>0){
			selectstr = ARTIST+" like ?";
			args = new String[1];
			if(artist.length()>=3){
				args[0] ="%"+artist.substring(0, 2)+"%";
				Log.i(TAG, "search again artist:"+artist.substring(0, 2));
			}else{
				args[0] ="%"+artist.substring(0, 1)+"%";
				Log.i(TAG, "search again artist:"+artist.substring(0, 1));
			}
		}
		
		Uri phoneUri = Uri.parse(AUTHORITY);
		Uri phone_contact_uri = Uri.withAppendedPath(phoneUri, AUDIO);
		ContentResolver resolver = context.getContentResolver();
		
		Cursor cursor = resolver.query(phone_contact_uri, null,  
				selectstr, args, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
		
		while(cursor.moveToNext()){
			
			String name = cursor.getString(cursor.getColumnIndex(NAME));
			String path = cursor.getString(cursor.getColumnIndex(PATH));
			String titledata = cursor.getString(cursor.getColumnIndex(TITLE));
			String album = cursor.getString(cursor.getColumnIndex(ALBUM));
			String artistdata = cursor.getString(cursor.getColumnIndex(ARTIST));
			
			if(!allowType(path)){
				continue;
			}
			
			Log.i(TAG, "again name:"+name+" ;again titledata:"+titledata);
			JSONObject jsonobj = new JSONObject();
			try {
				jsonobj.put("song", name);
				jsonobj.put("artist", titledata);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			songs.put(jsonobj);
		}
	}

	@Override
	public void openApp() {
		
		if( InputKeyManager.getInstance(context).isUsbMount("/mnt/USB") && MediaSourceManager.hasMusicFile(context)){
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			ComponentName cn = new ComponentName("com.zhonghong.music", "com.zhonghong.music.main.MainActivity");
			intent.setComponent(cn);
			context.startActivity(intent);
			Log.i(TAG, "openApp");
		}else{
			Log.i(TAG, "no file openApp failed");
		}
		
		
		
	}

}
