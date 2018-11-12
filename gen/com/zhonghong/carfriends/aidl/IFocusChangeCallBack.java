/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\E513\\VoiceAdapter\\src\\com\\zhonghong\\carfriends\\aidl\\IFocusChangeCallBack.aidl
 */
package com.zhonghong.carfriends.aidl;
public interface IFocusChangeCallBack extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.zhonghong.carfriends.aidl.IFocusChangeCallBack
{
private static final java.lang.String DESCRIPTOR = "com.zhonghong.carfriends.aidl.IFocusChangeCallBack";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.zhonghong.carfriends.aidl.IFocusChangeCallBack interface,
 * generating a proxy if needed.
 */
public static com.zhonghong.carfriends.aidl.IFocusChangeCallBack asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.zhonghong.carfriends.aidl.IFocusChangeCallBack))) {
return ((com.zhonghong.carfriends.aidl.IFocusChangeCallBack)iin);
}
return new com.zhonghong.carfriends.aidl.IFocusChangeCallBack.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_focusChange:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.focusChange(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.zhonghong.carfriends.aidl.IFocusChangeCallBack
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void focusChange(int fromfocus, int tofocus) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(fromfocus);
_data.writeInt(tofocus);
mRemote.transact(Stub.TRANSACTION_focusChange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_focusChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void focusChange(int fromfocus, int tofocus) throws android.os.RemoteException;
}
