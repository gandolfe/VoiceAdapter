package com.zhonghong.iflyinterface;

public interface MediaControlCallBack {
	void openApp();
	void play();
	void continuePlay();
	void pause();
	void playPre();
	void playNxt();
	void setPlayRandomModel();
	void setPlaySingleModel();
	void setPlayOrderModel();
}
