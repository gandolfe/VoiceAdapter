/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\E513\\VoiceAdapter\\src\\com\\zhonghong\\carfriends\\aidl\\ICarFriendsCallBack.aidl
 */
package com.zhonghong.carfriends.aidl;
public interface ICarFriendsCallBack extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.zhonghong.carfriends.aidl.ICarFriendsCallBack
{
private static final java.lang.String DESCRIPTOR = "com.zhonghong.carfriends.aidl.ICarFriendsCallBack";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.zhonghong.carfriends.aidl.ICarFriendsCallBack interface,
 * generating a proxy if needed.
 */
public static com.zhonghong.carfriends.aidl.ICarFriendsCallBack asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.zhonghong.carfriends.aidl.ICarFriendsCallBack))) {
return ((com.zhonghong.carfriends.aidl.ICarFriendsCallBack)iin);
}
return new com.zhonghong.carfriends.aidl.ICarFriendsCallBack.Stub.Proxy(obj);
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
case TRANSACTION_startSpeech:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.startSpeech(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onWakeupResult:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onWakeupResult(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.zhonghong.carfriends.aidl.ICarFriendsCallBack
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
@Override public void startSpeech(boolean isStartSpeech) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isStartSpeech)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_startSpeech, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onWakeupResult(int wordID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(wordID);
mRemote.transact(Stub.TRANSACTION_onWakeupResult, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startSpeech = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onWakeupResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void startSpeech(boolean isStartSpeech) throws android.os.RemoteException;
public void onWakeupResult(int wordID) throws android.os.RemoteException;
}
