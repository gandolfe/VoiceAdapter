package com.zhonghong.service;

import android.content.Context;
import android.provider.Settings.System;

public class BluetoothManager {

	private static BluetoothManager mBluetoothManager;
	private Context context;
	// *************蓝牙连接状态定义********************
	/** 准备状态即未连接状态 */
	public static final int STATE_READY = 0X0;
	/** 连接中状态 */
	public static final int STATE_CONNECTING = 0X1;
	/** 已连接状态 */
	public static final int STATE_CONNECTED = 0X2;
	/** 断开连接状态 */
	public static final int STATE_DIS_CONNECTING = 0X3;

	public static BluetoothManager getBluetoothManager(Context context) {
		synchronized (BluetoothManager.class) {
			if (mBluetoothManager == null) {
				mBluetoothManager = new BluetoothManager(context);
			}
			return mBluetoothManager;
		}
	}

	private BluetoothManager(Context context) {
		this.context = context;

	}

	public boolean isConnected() {

		int state = System.getInt(context.getContentResolver(), ":bt_connect_state", 0);
		if (state == STATE_CONNECTED) {
			return true;
		}
		return false;
	}
}
