package com.zhonghong.iflyplatformadapter;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhonghong.can.CanControlManager;
import com.zhonghong.can.CanHelper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AirController {

	private static final String TAG = "AirController";
	private JSONObject resultJson = null;
	private CanControlManager mCanControlManager = null;
	private int destWindValue; //命令要想调节到的风量值
	private int destTemprature;
	private static final int WINDOW_UP_MSG = 1;
	private static final int WINDOW_DOWN_MSG = 2;
	private static final int TEMPRATURE_UP_MSG = 3;
	private static final int TEMPRATURE_DOWN_MSG = 4;
	
	private boolean ischangewind = false;
	private boolean ischangetprt = false;
	Handler handler;
	
	public AirController(Context context) {
		Looper.prepare();
		mCanControlManager = CanControlManager.getInstance(context);
		initHandler(context);
	}
	
	private void initHandler(Context context){
		handler = new Handler(context.getMainLooper()){
			public void dispatchMessage(android.os.Message msg) {
				switch(msg.what){
				case WINDOW_UP_MSG:
					int current01 = mCanControlManager.getACInfo().mWindSpeed;
					if(current01<destWindValue){
						mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_BLOWER_UP, "1", "1");
						sendEmptyMessageDelayed(WINDOW_UP_MSG, 800);
					}else{
						ischangewind = false;
					}
					break;
				case WINDOW_DOWN_MSG:
					int current02 = mCanControlManager.getACInfo().mWindSpeed;
					if(current02>destWindValue){
						mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_BLOWER_UP, "1", "1");
						sendEmptyMessageDelayed(WINDOW_UP_MSG, 800);
					}else{
						ischangewind = false;
					}
					break;
				case TEMPRATURE_UP_MSG:
					int current03 = (int) mCanControlManager.getACInfo().mLeftTemperature;
					if(current03<destTemprature){
						mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_SET_TEMPERATURE_UP, "1", "1");
						sendEmptyMessageDelayed(TEMPRATURE_UP_MSG, 800);
					}else{
						ischangetprt = false;
					}
					break;
				case TEMPRATURE_DOWN_MSG:
					int current04 = (int) mCanControlManager.getACInfo().mLeftTemperature;
					if(current04>destTemprature){
						mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_SET_TEMPERATURE_DOWN, "1", "1");
						sendEmptyMessageDelayed(TEMPRATURE_DOWN_MSG, 800);
					}else{
						ischangetprt = false;
					}
					break;
				}
			};
		};
	}
	
	
	
	
	
	public String doCMD(String jsonstr){
		resultJson = new JSONObject();
		JSONObject jsonobj =null;
		try {
			jsonobj = new JSONObject(jsonstr);
			String operation = jsonobj.optString("operation");
			
			if("OPEN".equals(operation)){
				//打开空调
				openAirCondition();
				
			}else if("CLOSE".equals(operation)){
				//关闭操作
				if(jsonobj.has("mode")){
					closeMode(jsonobj.optString("mode"));
				}else{
					closeAirCondition();
				}
				
			}else if("SET".equals(operation)){
				
				if(jsonobj.has("temperature")){
					//温度相关设置
					handleTempereature(jsonobj.optString("temperature"));
				}else if(jsonobj.has("fan_speed")){
					//风量相关设置
					handleFanSpeed(jsonobj.optString("fan_speed"));
				}else if(jsonobj.has("airflow_direction")){
					//吹风模式设置
					handleAirflow(jsonobj.optString("airflow_direction"));
				}else if(jsonobj.has("mode")){
					//循环、除霜、自动
					handleMode(jsonobj.optString("mode"));
				}else{
					resultJson.put("status", "fail");
					resultJson.put("message", "抱歉，我不能理解您的意思，请换种说法");
				}
				
			}else{
				resultJson.put("status", "fail");
				resultJson.put("message", "抱歉，我不能理解您的意思，请换种说法");
			}
			return resultJson.toString();
		}catch(Exception e){
			
			try {
				resultJson.put("status", "fail");
				resultJson.put("message", "抱歉，操作异常");
			} catch (JSONException e1) {
				e1.printStackTrace();
				
			}
			return resultJson.toString();
		}
	}
		
			
	
	private void openAirCondition() throws JSONException {
		if(mCanControlManager.getACInfo().mAcPower== 0){
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_POWER, "1", "1");
			resultJson.put("status", "success");
		}else{
			resultJson.put("status", "fail");
			resultJson.put("message", "空调已经打开了！");
			Log.i(TAG, "openAirCondition() air is opened already!");
		}
		
	}

	private void closeAirCondition() throws JSONException {
		if(mCanControlManager.getACInfo().mAcPower== 1){
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_POWER, "1", "1");
			resultJson.put("status", "success");
		}else{
			resultJson.put("status", "fail");
			resultJson.put("message", "空调已经关闭了！");
			Log.i(TAG, "closeAirCondition() air is closed already!");
		}
	}

	//关闭除霜、自动
	private void closeMode(String optString) throws JSONException{
		if("除霜".equals(optString)){
			boolean mf = mCanControlManager.getACInfo().mFrontWindowHeating == 1;
			boolean mr = mCanControlManager.getACInfo().mRearWindowHeating == 1;
			if(!mf && !mr){
				resultJson.put("status", "fail");
				resultJson.put("message", "除霜已经关闭了！");
				Log.i(TAG, "closeMode() WindowHeating is closed already!");
				return;
			}
			if(mf){
				mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_FRONT_WINDOW_HEATING, "1", "1");
			}
			if(mr){
				mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_REAR_WINDOW_HEATING, "1", "1");
			}
		}else if("前除霜".equals(optString)){
			boolean mf = mCanControlManager.getACInfo().mFrontWindowHeating == 1;
			if(!mf){
				resultJson.put("status", "fail");
				resultJson.put("message", "前除霜已经关闭了！");
				Log.i(TAG, "closeMode() FrontWindowHeating is closed already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_FRONT_WINDOW_HEATING, "1", "1");
			
		}else if("后除霜".equals(optString)){
			boolean mr = mCanControlManager.getACInfo().mRearWindowHeating == 1;
			if(!mr){
				resultJson.put("status", "fail");
				resultJson.put("message", "后除霜已经关闭了！");
				Log.i(TAG, "closeMode() RearWindowHeating is closed already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_REAR_WINDOW_HEATING, "1", "1");
			
		}else if("自动".equals(optString)){
			if(mCanControlManager.getACInfo().mAuto != 1){
				resultJson.put("status", "fail");
				resultJson.put("message", "自动空调已经关闭了！");
				Log.i(TAG, "closeMode() auto is closed already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_AUTO, "1", "1");
			
		}
		resultJson.put("status", "success");
	}
	
	//打开内外循环模式、除霜、自动
	private void handleMode(String optString) throws JSONException {
		if("内循环".equals(optString)){
			if(mCanControlManager.getACInfo().mCycleMode == 1){
				resultJson.put("status", "fail");
				resultJson.put("message", "已经是内循环！");
				Log.i(TAG, "handleMode() mCycleMode is inner already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_CYCLE_MODE, "1", "1");
		}else if("外循环".equals(optString)){
			if(mCanControlManager.getACInfo().mCycleMode == 0){
				resultJson.put("status", "fail");
				resultJson.put("message", "已经是外循环！");
				Log.i(TAG, "handleMode() mCycleMode is outter already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_CYCLE_MODE, "1", "1");
		}else if("除霜".equals(optString)){
			boolean mf = mCanControlManager.getACInfo().mFrontWindowHeating == 1;
			boolean mr = mCanControlManager.getACInfo().mRearWindowHeating == 1;
			if(mf && mr){
				resultJson.put("status", "fail");
				resultJson.put("message", "除霜已经打开了！");
				Log.i(TAG, "handleMode() WindowHeating is opened already!");
				return;
			}
			if(!mf){
				mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_FRONT_WINDOW_HEATING, "1", "1");
			}
			if(!mr){
				mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_REAR_WINDOW_HEATING, "1", "1");
			}
		}else if("前除霜".equals(optString)){
			boolean mf = mCanControlManager.getACInfo().mFrontWindowHeating == 1;
			if(mf){
				resultJson.put("status", "fail");
				resultJson.put("message", "前除霜已经打开了！");
				Log.i(TAG, "closeMode() FrontWindowHeating is opened already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_FRONT_WINDOW_HEATING, "1", "1");
		}else if("后除霜".equals(optString)){
			boolean mr = mCanControlManager.getACInfo().mRearWindowHeating == 1;
			if(mr){
				resultJson.put("status", "fail");
				resultJson.put("message", "后除霜已经打开了！");
				Log.i(TAG, "closeMode() RearWindowHeating is opened already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_REAR_WINDOW_HEATING, "1", "1");
		}else if("自动".equals(optString)){
			if(mCanControlManager.getACInfo().mAuto == 1){
				resultJson.put("status", "fail");
				resultJson.put("message", "自动空调已经打开了！");
				Log.i(TAG, "closeMode() auto is opened already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_AUTO, "1", "1");
		}
		
		resultJson.put("status", "success");
	}

	//吹风模式
	private void handleAirflow(String optString) throws JSONException {
		
		if("吹脸吹脚".equals(optString)){
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_WINDMODE, "3", "3");
			
		}else if("吹脸".equals(optString)){
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_WINDMODE, "1", "1");
			
		}else if("吹脚".equals(optString)){
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_WINDMODE, "5", "5");
			
		}else if("吹脚除霜".equals(optString)){
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_WINDMODE, "7", "7");
			
		}
		
		resultJson.put("status", "success");
	}

	//风量操作
	private void handleFanSpeed(String optString) throws JSONException {
		if(ischangewind){
			resultJson.put("status", "fail");
			resultJson.put("message", "正在改变风量中！");
			return;
		}
		if("+".equals(optString)){
			if(mCanControlManager.getACInfo().mWindSpeed == 8){
				resultJson.put("status", "fail");
				resultJson.put("message", "风量已经最大了！");
				Log.i(TAG, "handleFanSpeed() WindSpeed max already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_BLOWER_UP, "1", "1");
		}else if("-".equals(optString)){
			if(mCanControlManager.getACInfo().mWindSpeed == 0){
				resultJson.put("status", "fail");
				resultJson.put("message", "风量已经最小了！");
				Log.i(TAG, "handleFanSpeed() WindSpeed mix already!");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_BLOWER_DOWN, "1", "1");
		}else if("最大".equals(optString)){
			destWindValue = 8;
			handler.sendEmptyMessage(WINDOW_UP_MSG);
			ischangewind = true;
		}else if("最小".equals(optString)){
			destWindValue = 1;
			handler.sendEmptyMessage(WINDOW_DOWN_MSG);
			ischangewind = true;
		}else if(isInteger(optString)){
			int value = Integer.parseInt(optString);
			if(value>8) value = 8;
			if(value<0) value = 0;
			destWindValue = value;
			int vindcurrent = mCanControlManager.getACInfo().mWindSpeed;
			if(vindcurrent<destWindValue){
				handler.sendEmptyMessage(WINDOW_UP_MSG);
			}else{
				handler.sendEmptyMessage(WINDOW_DOWN_MSG);
			}
			ischangewind = true;
		}else{
			resultJson.put("status", "fail");
			resultJson.put("message", "空调暂不支持此操作！");
			return;
		}
		resultJson.put("status", "success");
	}

	//温度操作
	private void handleTempereature(String tempereature) throws JSONException{
		if(ischangetprt){
			resultJson.put("status", "fail");
			resultJson.put("message", "正在改变温度中！");
			return;
		}
		if("-".equals(tempereature)){
			if(mCanControlManager.getACInfo().mLeftTemperature <=17){
				resultJson.put("status", "fail");
				resultJson.put("message", "已经是最低温度了！");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_SET_TEMPERATURE_DOWN, "1", "1");
		}else if("+".equals(tempereature)){
			if(mCanControlManager.getACInfo().mLeftTemperature >=32){
				resultJson.put("status", "fail");
				resultJson.put("message", "已经是最高温度了！");
				return;
			}
			mCanControlManager.execCmd(CanHelper.MODULE_TYPE_AC, CanHelper.AC_SET_TEMPERATURE_UP, "1", "1");
		}else if(tempereature.contains("+")){
			//增加多少度
			int current = (int) mCanControlManager.getACInfo().mLeftTemperature;
			if(current>=32){
				resultJson.put("status", "fail");
				resultJson.put("message", "已经是最高温度了！");
				return;
			}
			
			tempereature.replace("+", "");
			int addtemp = Integer.parseInt(tempereature);
			destTemprature = current + addtemp;
			if(destTemprature>32) destTemprature = 32;
			handler.sendEmptyMessage(TEMPRATURE_UP_MSG);
			ischangetprt = true;
		}else if(tempereature.contains("-")){
			//减少多少度
			int current = (int) mCanControlManager.getACInfo().mLeftTemperature;
			if(current<=17){
				resultJson.put("status", "fail");
				resultJson.put("message", "已经是最低温度了！");
				return;
			}
			
			tempereature.replace("-", "");
			int addtemp = Integer.parseInt(tempereature);
			destTemprature = current - addtemp;
			if(destTemprature<17) destTemprature = 17;
			handler.sendEmptyMessage(TEMPRATURE_DOWN_MSG);
			ischangetprt = true;
		}else if(isInteger(tempereature)){
			int current = (int) mCanControlManager.getACInfo().mLeftTemperature;
			int tempdata = Integer.parseInt(tempereature);
			if(tempdata<17 || tempdata>32){
				resultJson.put("status", "fail");
				resultJson.put("message", "温度超出范围！");
				return;
			}
			destTemprature = tempdata;
			if(current>destTemprature){
				handler.sendEmptyMessage(TEMPRATURE_DOWN_MSG);
			}else{
				handler.sendEmptyMessage(TEMPRATURE_UP_MSG);
			}
			ischangetprt = true;
			
		}else{
			resultJson.put("status", "fail");
			resultJson.put("message", "空调暂不支持此操作！");
			return;
		}
		
		resultJson.put("status", "success");
	}
	
	 private static boolean isInteger(String str) {  
	        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
	        return pattern.matcher(str).matches();  
	  }
	
	

}
