package com.zhonghong.receive;

import android.content.Context;
import android.content.Intent;

import com.zhonghong.component.listener.CarStateChangeListener;
import com.zhonghong.component.receiver.BaseBroadcastReceiver;
import com.zhonghong.utils.SlogUtil;

public class CarStateReceiver extends BaseBroadcastReceiver{
	
	/**
	 * 车辆状态改变
	 */
	private static final String CAT_STATE = "com.zhonghong.mainctrl.send_broadcast_sys";
	
	private CarStateChangeListener mCarStateChangeListener;
	private SlogUtil slogUtil;
	
	/** 系统是否已经休眠 */
	public static boolean isAccOff = false;
	/** 是否在倒车 */
	public static boolean isAsternOn = false;
	/** 是否已经关屏*/
	public static boolean isPowerOff = false;
	
	public CarStateReceiver() {
		addIntentFilter(CAT_STATE);
		slogUtil = new SlogUtil(getClass());
	}
	
	public void setCarStateChangeListener(CarStateChangeListener carStateChangeListener){
		this.mCarStateChangeListener = carStateChangeListener;
	}
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if(CAT_STATE.equals(arg1.getAction())){
			if(arg1.hasExtra("syscode")){
				String state = arg1.getStringExtra("syscode");
				slogUtil.i("state=" + state);
				if(state.equals("ACC_ON")){
					isAccOff = false;
					if(mCarStateChangeListener != null) mCarStateChangeListener.onAccOn();
				}else if(state.equals("ACC_OFF")){
					isAccOff = true;
					if(mCarStateChangeListener != null) mCarStateChangeListener.onAccOff();
				}else if(state.equals("ASTERN_ON")){
					isAsternOn = true;
					if(mCarStateChangeListener != null) mCarStateChangeListener.onAsternOn();
				}else if(state.equals("ASTERN_OFF")){
					isAsternOn = false;
					if(mCarStateChangeListener != null) mCarStateChangeListener.onAsternOff();
				}
			}
			if(arg1.hasExtra("syscode2")){
				String state2 = arg1.getStringExtra("syscode2");
				slogUtil.i("state2=" + state2);
				if(state2.equals("POWER_ON")){
					isPowerOff = false;
					if(mCarStateChangeListener != null) mCarStateChangeListener.onPowerOn();
				}else if(state2.equals("POWER_OFF")){
					isPowerOff = true;
					if(mCarStateChangeListener != null) mCarStateChangeListener.onPowerOff();
				}
			}
		}
	}
}
