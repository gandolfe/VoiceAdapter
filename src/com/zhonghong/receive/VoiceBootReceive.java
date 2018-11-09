package com.zhonghong.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhonghong.service.VoiceControlService;
import com.zhonghong.utils.SlogUtil;

public class VoiceBootReceive extends BroadcastReceiver{
	private SlogUtil slogUtil = new SlogUtil(this.getClass());
	@Override
	public void onReceive(Context context, Intent intent) {
		slogUtil.println();
		Intent intent2 = new Intent(context,VoiceControlService.class);
		context.startService(intent2);
	}
}
