package com.zhonghong.views;


import com.zhonghong.voiceadapter.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class VolumeWindow {
	private Dialog mDialog;
	Context context;
	private SeekBar volumeprogress = null;
	private TextView window_volume_value = null;
	private ImageView voiceImg = null;
	private RelativeLayout window_layout = null;
	private static VolumeWindow  instance = null;
	private OnSeekBarChangeListener ls = null;
	
	private static final String TAG = "VolumeWindow";
	public static VolumeWindow getInstance(Context context){
		if(instance == null){
			instance = new VolumeWindow(context);
		}
		return instance;
	}
	
	Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch(msg.what){
			case 1:
				//继续
				
				break;
			case 2:
				//消失
				dismiss();
				break;
			}
		};
	};
	
	private VolumeWindow(Context context) {
		this.context = context;
		
		mDialog = new Dialog(context, R.style.dialog);
		Window window = mDialog.getWindow();
		window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		params.y = 480;
		params.dimAmount = 0.8f;
		window.setAttributes(params);
		mDialog.setContentView(R.layout.window_volume_layout);
		mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		
		window_layout = (RelativeLayout) mDialog.findViewById(R.id.window_volume_bar_layout);
		volumeprogress = (SeekBar) mDialog.findViewById(R.id.window_volume_bar);
		window_volume_value = (TextView) mDialog.findViewById(R.id.window_volume_value);
		voiceImg = (ImageView) mDialog.findViewById(R.id.window_volume_icon);
		volumeprogress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				handler.sendEmptyMessageDelayed(0x2, 3000);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				handler.removeMessages(0x2);
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				if(arg2){ //true表示是滑动改变的数字，false为设置后改变的数字
					ls.datachange(arg1);
					if(arg1<10){
						window_volume_value.setText("0"+arg1);
					}else{
						window_volume_value.setText(arg1+"");
					}
					updateImg(arg1);
				}
			}
		});
		
	}
	
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener changels){
		ls = changels;
	}
	
	
	public static void show(int data,Context context){
		getInstance(context).setVolume(data).show();
		instance.handler.removeMessages(0x2);
		instance.handler.sendEmptyMessageDelayed(0x2, 3000);
	}
	
	public static void setMax(int max,Context context){
		getInstance(context).setMaxVolume(max);
	}
	
	public static void disMiss(){
		instance.dismiss();
	}
	
	
	
	private void setMaxVolume(int max){
		if(volumeprogress!=null){
			volumeprogress.setMax(max);
		}
	}
	
	private VolumeWindow setVolume(int data){
		if(volumeprogress!=null){
			volumeprogress.setProgress(data);
			
			if(data<10){
				window_volume_value.setText("0"+data);
			}else{
				window_volume_value.setText(data+"");
			}
			
			updateImg(data);
		}
		return instance;
	}
	
	private void show(){
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(true);
	}
	
	private void dismiss(){
		mDialog.dismiss();
	}
	
	private void updateImg(int progress){
		if(progress == 0){
			voiceImg.setImageResource(R.drawable.icon_window_volume_mute);
		}else{
			voiceImg.setImageResource(R.drawable.icon_window_volume);
		}
	}
	
	public interface OnSeekBarChangeListener{
		public void datachange(int progress);
	}

}
