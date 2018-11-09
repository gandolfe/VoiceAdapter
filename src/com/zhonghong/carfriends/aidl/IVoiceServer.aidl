package com.zhonghong.carfriends.aidl;

import com.zhonghong.carfriends.aidl.ICarFriendsCallBack;
import com.zhonghong.carfriends.aidl.IFocusChangeCallBack;

interface IVoiceServer{
	void setSpeechStatus(boolean isSpeech);
	void setPlayStatus(boolean isPlay);
	void startSpeechAwaken(in List<String> words);
	void stopSpeechAwaken();
	void registerCallBack(ICarFriendsCallBack callback);
	void unregisterCallBack(ICarFriendsCallBack callback);
	void registerFocusCallBack(IFocusChangeCallBack callback);
	void unregisterFocusCallBack(IFocusChangeCallBack callback);
	
}