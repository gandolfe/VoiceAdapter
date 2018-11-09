package com.zhonghong.iflyplatformadapter;

import java.io.IOException;

import com.zhonghong.iflyinterface.ICarlifeController;

import android.util.Log;
import android.view.KeyEvent;

public class CarlifeController implements ICarlifeController{

	private static final String TAG = "CarlifeController";
	public CarlifeController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openApp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void continuePlay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playPre() {
		// TODO Auto-generated method stub
			try {
				Runtime.getRuntime().exec("input keyevent "+KeyEvent.KEYCODE_MEDIA_PREVIOUS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i(TAG, "send KEYCODE_MEDIA_PREVIOUS!");
	}

	@Override
	public void playNxt() {
		// TODO Auto-generated method stub
		try {
			Runtime.getRuntime().exec("input keyevent "+KeyEvent.KEYCODE_MEDIA_NEXT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "send KEYCODE_MEDIA_NEXT!");
	}

	@Override
	public void setPlayRandomModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlaySingleModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlayOrderModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openVoice() {
		// TODO Auto-generated method stub
		try {
			Runtime.getRuntime().exec("input keyevent "+KeyEvent.KEYCODE_ASSIST);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "send KeyEvent.KEYCODE_ASSIST!");
	}

}
