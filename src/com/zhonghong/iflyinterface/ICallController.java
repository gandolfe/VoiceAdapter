package com.zhonghong.iflyinterface;

/**
 * �绰��ؿ�����ӿ�
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
