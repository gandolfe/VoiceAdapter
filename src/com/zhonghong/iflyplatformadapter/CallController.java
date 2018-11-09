package com.zhonghong.iflyplatformadapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.platform.type.PlatformCode;
import com.iflytek.platformservice.PlatformService;
import com.zhonghong.iflyinterface.ICallController;
import com.zhonghong.model.Contact;
import com.zhonghong.util.SettingsProviderStateHelper;
import com.zhonghong.util.SettingsProviderStateHelper.StateListener;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * 电话控制实现类
 * 
 * @author yys
 *
 */
public class CallController implements ICallController, StateListener {
	private static final String PHONE_SERVICE_ACTION = "com.zhonghong.action.BT_CALL";
	// 联系人的URI
	public static final String AUTHORITY = "content://com.car.phone_data";
	public static final String CONTACT = "/contacts";
	public static String NAME = "name";
	public static String NUMBER = "number";
	Context context;
	private SettingsProviderStateHelper mConnectStateHelper, mCallStateHelper;
	private static final String TAG = "CallController";

	// *************蓝牙连接状态定义********************
	/** 准备状态即未连接状态 */
	public static final int STATE_READY = 0X0;
	/** 连接中状态 */
	public static final int STATE_CONNECTING = 0X1;
	/** 已连接状态 */
	public static final int STATE_CONNECTED = 0X2;
	/** 断开连接状态 */
	public static final int STATE_DIS_CONNECTING = 0X3;

	// ************蓝牙通话状态定义***********************
	/** 通话中状态 */
	public static final int CALL_STATE_TALKING = 0;
	/** HOLD状态 */
	public static final int CALL_STATE_HOLD = 1;
	/** 去电状态 */
	public static final int CALL_STATE_OUTGOING = 2;
	/** 去电时对方响应状态 */
	public static final int CALL_STATE_ALERTING = 3;
	/** 来电状态 */
	public static final int CALL_STATE_INCOMING = 4;
	/** 等待状态 */
	public static final int CALL_STATE_WAITING = 5;
	/** 挂起 */
	public static final int CALL_STATE_HELD_BY_RESPONSE_AND_HOLD = 6;
	/** 挂机状态 */
	public static final int CALL_STATE_TERMINATED = 7;

	public CallController(Context ct) {
		// TODO Auto-generated constructor stub
		this.context = ct;

		mConnectStateHelper = new SettingsProviderStateHelper(context, ":bt_connect_state");
		mCallStateHelper = new SettingsProviderStateHelper(context, ":_bt_call_state");
		mCallStateHelper.setStateListener(this);
		mConnectStateHelper.setStateListener(this);
		mCallStateHelper.startObserver();
		mConnectStateHelper.startObserver();
	}

	@Override
	public String call(String number) {
		Log.i(TAG, "call num:"+number);
		 Intent intent = new Intent(PHONE_SERVICE_ACTION);
		 intent.putExtra("code", "call");
		 intent.putExtra("number", number);
		 context.startService(intent);
		return "";
	}

	@Override
	public String hangUp() {
		Log.i(TAG, "hangUp");
		Intent intent = new Intent(PHONE_SERVICE_ACTION);
		intent.putExtra("code", "hangup");
		context.startService(intent);
		return "";
	}

	@Override
	public String accept() {
		Log.i(TAG, "accept");
		Intent intent = new Intent(PHONE_SERVICE_ACTION);
		intent.putExtra("code", "accept");
		context.startService(intent);
		return "";
	}

	/**
	 * 拒接
	 */
	@Override
	public String reject() {
		return "";
	}

	@Override
	public void phoneCallClick() {
	
	}

	/**
	 * 获取联系人
	 */
	@Override
	public String getContacts(String json) {

		Log.i(TAG, "getContacts():" + json);

		if (!isConencted()) {
			Log.i(TAG, "not connect return null!");
			return null;
		}

		ContentResolver cr = context.getContentResolver(); // 实例化ContentResolver
															// 对象;
		Uri phoneUri = Uri.parse(AUTHORITY);
		Uri phone_contact_uri = Uri.withAppendedPath(phoneUri, CONTACT);
		JSONObject action = null;
		Cursor cursor = null;
		try {
			action = new JSONObject(json);

			JSONObject result = new JSONObject();
			JSONArray numberarray = new JSONArray();

			String type = action.optString("type");
			String obj = action.optString("obj");
			String selectstr = null;
			String[] args = new String[1];
			if ("searchname".equals(type)) {
				// 通过号码查询联系人姓名，查询不到返回null
				selectstr = NUMBER + " = ?";
			} else if ("searchnumber".equals(type)) {
				// 通过联系人查询号码
				selectstr = NAME + " = ?";
			}
			args[0] = obj;

			cursor = cr.query(phone_contact_uri, null, selectstr, args, null);
			if (cursor == null) {
				Log.i(TAG, "cursor = null!");
				return null;
			}
			Log.i(TAG, "getAllContacts cursor.count = " + cursor.getCount());
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex(NAME));
				String numberstr = cursor.getString(cursor.getColumnIndex(NUMBER));
				Log.i(TAG, "getAllContacts name =" + name + ", number=" + numberstr);

				if (numberstr == null || numberstr.length() <= 0) {
					continue;
				}
				result.put("contactName", name);
				String[] numbers = numberstr.split(",");// 号码可能会有多个，用“，”分开
				for (String string : numbers) {
					JSONObject num = new JSONObject();
					num.put("number", string);
					numberarray.put(num);
				}
			}

			// test
			// Log.i(TAG, "test getall datas!");
			// cursor = cr.query(phone_contact_uri, null, null, null, null);
			// while (cursor.moveToNext()) {
			// String name = cursor.getString(cursor.getColumnIndex(NAME));
			// String numberstr =
			// cursor.getString(cursor.getColumnIndex(NUMBER));
			// Log.i(TAG, "test all name =" + name + ", number=" + numberstr);
			// }

			if (numberarray.length() <= 0) {
				return null;
			} else {
				result.put("contactNumbers", numberarray);
				Log.i(TAG, "result.toString:" + result.toString());
				return result.toString();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void onStateChanged(String str) {
		if (":bt_connect_state".equals(str)) {// 连接状态发生改变 见 蓝牙连接状态定义
			if (isConencted()) {
				uploadContacts();
			}
		} else if (":_bt_call_state".equals(str)) { // 通话状态发生改变
													// 值为json格式{"state":状态,"number":"号码"}
			// 例如 {"state":7,"number":""} {"state":3,"number":"10010"}
//			String callState = mCallStateHelper.getStringValue("");
//			Log.i(TAG, "callState = " + callState);
//
//			if (callState == null || callState.length() <= 0) {
//				return;
//			}
//
//			try {
//
//				JSONObject outobj = new JSONObject();
//
//				JSONObject jobj = new JSONObject(callState);
//				int state = (int) jobj.opt("state");
//				if (state == CALL_STATE_INCOMING) {
//					outobj.put("state", 1);
//					outobj.put("number", jobj.optString("number"));
//				} else if (state == CALL_STATE_TERMINATED) {
//					outobj.put("state", 0);
//				} else if (state == CALL_STATE_TALKING) {
//					outobj.put("state", 2);
//				}
//
//				PlatformService.platformCallback.phoneCallStateChange(outobj.toString());
//
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		}

	}

	/**
	 * get the connect status of Blutooth
	 * 
	 * @return
	 */
	public boolean isConencted() {
		int connectstate = mConnectStateHelper.getIntKeyValue(-1);
		Log.i(TAG, "connectstate:" + connectstate);
		if (connectstate == STATE_CONNECTED) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * put contacts infomation to iflyteck, so that can realized what we said
	 */
	private void uploadContacts() {
		ContentResolver cr = context.getContentResolver(); // 实例化ContentResolver
															// 对象;
		Uri phoneUri = Uri.parse(AUTHORITY);
		Uri phone_contact_uri = Uri.withAppendedPath(phoneUri, CONTACT);
		ArrayList<String> datas = new ArrayList<String>();
		Log.i(TAG, "test getall datas!");
		Cursor cursor = cr.query(phone_contact_uri, null, null, null, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(NAME));
			Log.i(TAG, "test all  name =" + name);
			datas.add(name);
		}

		if (datas.isEmpty()) {
			return;
		}

		int length = datas.size();
		String[] outdatas = new String[length];

		for (int i = 0; i < length; i++) {
			outdatas[i] = datas.get(i);
			Log.i(TAG, i+"upload:"+outdatas[i]);
		}

		try {
			PlatformService.platformCallback.uploadCustomData(PlatformCode.UPLOADTYPE_CONTACT, outdatas);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
