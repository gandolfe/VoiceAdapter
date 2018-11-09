package com.zh.external;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

/**
 * 获取VIN码、UUID
 * @author yys
 *
 */

public class ExternalManager {

	private static final String TAG = "ExternalManager";
	
	private static ExternalManager mExternalManager;
	
	public static ExternalManager getInstance() {
		if (mExternalManager == null) {
			synchronized (ExternalManager.class) {
				if (mExternalManager == null) {
					mExternalManager = new ExternalManager();
				}
			}
		}
		return mExternalManager;
	}
	
	private ExternalManager() {
	}
	
	/**
	 * 获取VIN码
	 * @return
	 */
	public String getVIN() {
		String vin = "";
		String getVin = SystemProperties.get("sys.car.vin");
		Log.i(TAG, "vin:" + getVin);
		if (!TextUtils.isEmpty(getVin)) {
			vin = getVin;
		}
		return vin;
	}
	
	/**
	 * 获取UUID
	 * @return
	 */
	@SuppressLint("NewApi")
	public String getUUID() {
		String uuid = "";
		String getUUID = Build.SERIAL;
		Log.i(TAG, "uuid:" + getUUID);
		if (!TextUtils.isEmpty(getUUID)) {
			uuid = getUUID;
		}
		return uuid;
	}
}
