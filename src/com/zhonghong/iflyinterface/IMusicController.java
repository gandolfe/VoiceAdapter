package com.zhonghong.iflyinterface;
/**
 * 音乐相关的控制接口
 * @author yys
 *
 */
public interface IMusicController extends MediaControlCallBack{
	/**
	 * 针对于onSearchPlayList回调接口
	 * @param jsonstr
	 * @return
	 */
	String playSyncMusic(String jsonstr);
	
	/**
	 * 针对于onNLPResult回调接口
	 * @param jsonstr
	 * @return
	 */
	String playMusic(String jsonstr);
	
	
}
