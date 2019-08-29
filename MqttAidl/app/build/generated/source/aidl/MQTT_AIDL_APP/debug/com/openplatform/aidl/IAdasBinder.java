/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/zhongjihao/workspace/sprd/AndroidMqttAidlDemo/MqttAidl/app/src/main/aidl/com/openplatform/aidl/IAdasBinder.aidl
 */
package com.openplatform.aidl;
/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/19 10:21
 * Description :
 */
public interface IAdasBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.openplatform.aidl.IAdasBinder
{
private static final java.lang.String DESCRIPTOR = "com.openplatform.aidl.IAdasBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.openplatform.aidl.IAdasBinder interface,
 * generating a proxy if needed.
 */
public static com.openplatform.aidl.IAdasBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.openplatform.aidl.IAdasBinder))) {
return ((com.openplatform.aidl.IAdasBinder)iin);
}
return new com.openplatform.aidl.IAdasBinder.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(descriptor);
com.openplatform.aidl.IAdasCallback _arg0;
_arg0 = com.openplatform.aidl.IAdasCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(descriptor);
com.openplatform.aidl.IAdasCallback _arg0;
_arg0 = com.openplatform.aidl.IAdasCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_OnLogin:
{
data.enforceInterface(descriptor);
com.openplatform.aidl.LoginRequest _arg0;
if ((0!=data.readInt())) {
_arg0 = com.openplatform.aidl.LoginRequest.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
this.OnLogin(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_OnSelfCheck:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
java.lang.String _arg4;
_arg4 = data.readString();
java.lang.String _arg5;
_arg5 = data.readString();
com.openplatform.aidl.SelfCheck _arg6;
if ((0!=data.readInt())) {
_arg6 = com.openplatform.aidl.SelfCheck.CREATOR.createFromParcel(data);
}
else {
_arg6 = null;
}
this.OnSelfCheck(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
reply.writeNoException();
return true;
}
case TRANSACTION_OnTakePicUpload:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
int _arg4;
_arg4 = data.readInt();
java.lang.String _arg5;
_arg5 = data.readString();
this.OnTakePicUpload(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
return true;
}
case TRANSACTION_OnCondTakePicUpload:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
int _arg4;
_arg4 = data.readInt();
java.lang.String _arg5;
_arg5 = data.readString();
java.lang.String _arg6;
_arg6 = data.readString();
this.OnCondTakePicUpload(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements com.openplatform.aidl.IAdasBinder
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
@Override public void registerCallback(com.openplatform.aidl.IAdasCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.openplatform.aidl.IAdasCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//终端登录

@Override public void OnLogin(com.openplatform.aidl.LoginRequest loginRequest, java.lang.String deviceVersion) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((loginRequest!=null)) {
_data.writeInt(1);
loginRequest.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(deviceVersion);
mRemote.transact(Stub.TRANSACTION_OnLogin, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//终端自检信息上报

@Override public void OnSelfCheck(java.lang.String token, int detectType, java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, com.openplatform.aidl.SelfCheck selfCheck) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
_data.writeInt(detectType);
_data.writeString(topic);
_data.writeString(deviceId);
_data.writeString(cmdSNO);
_data.writeString(command);
if ((selfCheck!=null)) {
_data.writeInt(1);
selfCheck.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_OnSelfCheck, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//拍照上传

@Override public void OnTakePicUpload(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, int channel, java.lang.String filePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(topic);
_data.writeString(deviceId);
_data.writeString(cmdSNO);
_data.writeString(command);
_data.writeInt(channel);
_data.writeString(filePath);
mRemote.transact(Stub.TRANSACTION_OnTakePicUpload, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//拍照上传

@Override public void OnCondTakePicUpload(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, int channel, java.lang.String batchNum, java.lang.String filePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(topic);
_data.writeString(deviceId);
_data.writeString(cmdSNO);
_data.writeString(command);
_data.writeInt(channel);
_data.writeString(batchNum);
_data.writeString(filePath);
mRemote.transact(Stub.TRANSACTION_OnCondTakePicUpload, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_OnLogin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_OnSelfCheck = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_OnTakePicUpload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_OnCondTakePicUpload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void registerCallback(com.openplatform.aidl.IAdasCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.openplatform.aidl.IAdasCallback cb) throws android.os.RemoteException;
//终端登录

public void OnLogin(com.openplatform.aidl.LoginRequest loginRequest, java.lang.String deviceVersion) throws android.os.RemoteException;
//终端自检信息上报

public void OnSelfCheck(java.lang.String token, int detectType, java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, com.openplatform.aidl.SelfCheck selfCheck) throws android.os.RemoteException;
//拍照上传

public void OnTakePicUpload(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, int channel, java.lang.String filePath) throws android.os.RemoteException;
//拍照上传

public void OnCondTakePicUpload(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, int channel, java.lang.String batchNum, java.lang.String filePath) throws android.os.RemoteException;
}
