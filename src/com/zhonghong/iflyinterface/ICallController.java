package com.zhonghong.iflyinterface;

/**
 * 电话相关控制类接口
 * @author yys
 *
 */
public interface ICallController {
	String call(String number);
	String hangUp();
	String accept();
	String reject();
	String getContacts(String json);
	void phoneCallClick();
	boolean isConencted();
}
