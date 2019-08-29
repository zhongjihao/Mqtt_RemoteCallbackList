/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/zhongjihao/workspace/sprd/AndroidMqttAidlDemo/MqttAidl/app/src/main/aidl/com/openplatform/aidl/IAdasCallback.aidl
 */
package com.openplatform.aidl;
/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/19 10:21
 * Description :
 */
public interface IAdasCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.openplatform.aidl.IAdasCallback
{
private static final java.lang.String DESCRIPTOR = "com.openplatform.aidl.IAdasCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.openplatform.aidl.IAdasCallback interface,
 * generating a proxy if needed.
 */
public static com.openplatform.aidl.IAdasCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.openplatform.aidl.IAdasCallback))) {
return ((com.openplatform.aidl.IAdasCallback)iin);
}
return new com.openplatform.aidl.IAdasCallback.Stub.Proxy(obj);
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
case TRANSACTION_loginCallback:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.loginCallback(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_terminalParamCallback:
{
data.enforceInterface(descriptor);
com.openplatform.aidl.TerminalParamDownloadResponse _arg0;
if ((0!=data.readInt())) {
_arg0 = com.openplatform.aidl.TerminalParamDownloadResponse.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.terminalParamCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_serverParamCallback:
{
data.enforceInterface(descriptor);
com.openplatform.aidl.ServerParamDownloadResponse _arg0;
if ((0!=data.readInt())) {
_arg0 = com.openplatform.aidl.ServerParamDownloadResponse.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.serverParamCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_mqttTakePic:
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
this.mqttTakePic(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_mqttDeviceDetect:
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
this.mqttDeviceDetect(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_mqttCondTakePic:
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
java.lang.String _arg4;
_arg4 = data.readString();
int _arg5;
_arg5 = data.readInt();
int _arg6;
_arg6 = data.readInt();
int _arg7;
_arg7 = data.readInt();
java.lang.String _arg8;
_arg8 = data.readString();
java.lang.String _arg9;
_arg9 = data.readString();
int _arg10;
_arg10 = data.readInt();
this.mqttCondTakePic(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements com.openplatform.aidl.IAdasCallback
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
//登录回调

@Override public void loginCallback(java.lang.String token, java.lang.String simNo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
_data.writeString(simNo);
mRemote.transact(Stub.TRANSACTION_loginCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//终端参数下载回调

@Override public void terminalParamCallback(com.openplatform.aidl.TerminalParamDownloadResponse response) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((response!=null)) {
_data.writeInt(1);
response.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_terminalParamCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//服务参数下载回调

@Override public void serverParamCallback(com.openplatform.aidl.ServerParamDownloadResponse response) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((response!=null)) {
_data.writeInt(1);
response.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_serverParamCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//拍照

@Override public void mqttTakePic(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, int cameraId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(topic);
_data.writeString(deviceId);
_data.writeString(cmdSNO);
_data.writeString(command);
_data.writeInt(cameraId);
mRemote.transact(Stub.TRANSACTION_mqttTakePic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//自检指令

@Override public void mqttDeviceDetect(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(topic);
_data.writeString(deviceId);
_data.writeString(cmdSNO);
_data.writeString(command);
mRemote.transact(Stub.TRANSACTION_mqttDeviceDetect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//拍照

@Override public void mqttCondTakePic(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, java.lang.String batchNum, int channelId, int interval, int count, java.lang.String distance, java.lang.String minSpeed, int angle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(topic);
_data.writeString(deviceId);
_data.writeString(cmdSNO);
_data.writeString(command);
_data.writeString(batchNum);
_data.writeInt(channelId);
_data.writeInt(interval);
_data.writeInt(count);
_data.writeString(distance);
_data.writeString(minSpeed);
_data.writeInt(angle);
mRemote.transact(Stub.TRANSACTION_mqttCondTakePic, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_loginCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_terminalParamCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_serverParamCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_mqttTakePic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_mqttDeviceDetect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_mqttCondTakePic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
//登录回调

public void loginCallback(java.lang.String token, java.lang.String simNo) throws android.os.RemoteException;
//终端参数下载回调

public void terminalParamCallback(com.openplatform.aidl.TerminalParamDownloadResponse response) throws android.os.RemoteException;
//服务参数下载回调

public void serverParamCallback(com.openplatform.aidl.ServerParamDownloadResponse response) throws android.os.RemoteException;
//拍照

public void mqttTakePic(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, int cameraId) throws android.os.RemoteException;
//自检指令

public void mqttDeviceDetect(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command) throws android.os.RemoteException;
//拍照

public void mqttCondTakePic(java.lang.String topic, java.lang.String deviceId, java.lang.String cmdSNO, java.lang.String command, java.lang.String batchNum, int channelId, int interval, int count, java.lang.String distance, java.lang.String minSpeed, int angle) throws android.os.RemoteException;
}
