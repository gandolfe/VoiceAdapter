package com.zhonghong.iflyinterface;
/**
 * ������صĿ��ƽӿ�
 * @author yys
 *
 */
public interface IMusicController extends MediaControlCallBack{
	/**
	 * �����onSearchPlayList�ص��ӿ�
	 * @param jsonstr
	 * @return
	 */
	String playSyncMusic(String jsonstr);
	
	/**
	 * �����onNLPResult�ص��ӿ�
	 * @param jsonstr
	 * @return
	 */
	String playMusic(String jsonstr);
	
	
}
