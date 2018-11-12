/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\E513\\VoiceAdapter\\src\\com\\zhonghong\\carfriends\\aidl\\IVoiceServer.aidl
 */
package com.zhonghong.carfriends.aidl;
public interface IVoiceServer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.zhonghong.carfriends.aidl.IVoiceServer
{
private static final java.lang.String DESCRIPTOR = "com.zhonghong.carfriends.aidl.IVoiceServer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.zhonghong.carfriends.aidl.IVoiceServer interface,
 * generating a proxy if needed.
 */
public static com.zhonghong.carfriends.aidl.IVoiceServer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.zhonghong.carfriends.aidl.IVoiceServer))) {
return ((com.zhonghong.carfriends.aidl.IVoiceServer)iin);
}
return new com.zhonghong.carfriends.aidl.IVoiceServer.Stub.Proxy(obj);
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
case TRANSACTION_setSpeechStatus:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setSpeechStatus(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setPlayStatus:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setPlayStatus(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startSpeechAwaken:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _arg0;
_arg0 = data.createStringArrayList();
this.startSpeechAwaken(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopSpeechAwaken:
{
data.enforceInterface(DESCRIPTOR);
this.stopSpeechAwaken();
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallBack:
{
data.enforceInterface(DESCRIPTOR);
com.zhonghong.carfriends.aidl.ICarFriendsCallBack _arg0;
_arg0 = com.zhonghong.carfriends.aidl.ICarFriendsCallBack.Stub.asInterface(data.readStrongBinder());
this.registerCallBack(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallBack:
{
data.enforceInterface(DESCRIPTOR);
com.zhonghong.carfriends.aidl.ICarFriendsCallBack _arg0;
_arg0 = com.zhonghong.carfriends.aidl.ICarFriendsCallBack.Stub.asInterface(data.readStrongBinder());
this.unregisterCallBack(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerFocusCallBack:
{
data.enforceInterface(DESCRIPTOR);
com.zhonghong.carfriends.aidl.IFocusChangeCallBack _arg0;
_arg0 = com.zhonghong.carfriends.aidl.IFocusChangeCallBack.Stub.asInterface(data.readStrongBinder());
this.registerFocusCallBack(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterFocusCallBack:
{
data.enforceInterface(DESCRIPTOR);
com.zhonghong.carfriends.aidl.IFocusChangeCallBack _arg0;
_arg0 = com.zhonghong.carfriends.aidl.IFocusChangeCallBack.Stub.asInterface(data.readStrongBinder());
this.unregisterFocusCallBack(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.zhonghong.carfriends.aidl.IVoiceServer
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
@Override public void setSpeechStatus(boolean isSpeech) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isSpeech)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setSpeechStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setPlayStatus(boolean isPlay) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isPlay)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setPlayStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startSpeechAwaken(java.util.List<java.lang.String> words) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStringList(words);
mRemote.transact(Stub.TRANSACTION_startSpeechAwaken, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopSpeechAwaken() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopSpeechAwaken, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallBack(com.zhonghong.carfriends.aidl.ICarFriendsCallBack callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallBack, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallBack(com.zhonghong.carfriends.aidl.ICarFriendsCallBack callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallBack, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerFocusCallBack(com.zhonghong.carfriends.aidl.IFocusChangeCallBack callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerFocusCallBack, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterFocusCallBack(com.zhonghong.carfriends.aidl.IFocusChangeCallBack callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterFocusCallBack, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setSpeechStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setPlayStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_startSpeechAwaken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_stopSpeechAwaken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_registerCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unregisterCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_registerFocusCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_unregisterFocusCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public void setSpeechStatus(boolean isSpeech) throws android.os.RemoteException;
public void setPlayStatus(boolean isPlay) throws android.os.RemoteException;
public void startSpeechAwaken(java.util.List<java.lang.String> words) throws android.os.RemoteException;
public void stopSpeechAwaken() throws android.os.RemoteException;
public void registerCallBack(com.zhonghong.carfriends.aidl.ICarFriendsCallBack callback) throws android.os.RemoteException;
public void unregisterCallBack(com.zhonghong.carfriends.aidl.ICarFriendsCallBack callback) throws android.os.RemoteException;
public void registerFocusCallBack(com.zhonghong.carfriends.aidl.IFocusChangeCallBack callback) throws android.os.RemoteException;
public void unregisterFocusCallBack(com.zhonghong.carfriends.aidl.IFocusChangeCallBack callback) throws android.os.RemoteException;
}
