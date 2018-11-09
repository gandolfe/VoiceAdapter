package com.zhonghong.carfriends.aidl;

interface ICarFriendsCallBack{
	void startSpeech(boolean isStartSpeech);
	void onWakeupResult(int wordID);
}