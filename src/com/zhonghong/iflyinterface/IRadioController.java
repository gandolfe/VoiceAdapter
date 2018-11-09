package com.zhonghong.iflyinterface;

/**
 * 收音机的控制接口
 * @author yys
 *
 */
public interface IRadioController extends MediaControlCallBack{

	String startRadio(String jsonstr);
}
