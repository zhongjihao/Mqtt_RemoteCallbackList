package com.openplatform.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.openplatform.adas.datamodel.BaseResponse;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/23 10:21
 * Description :
 */
public class TerminalParamDownloadResponse extends BaseResponse implements Parcelable {
    private Data data;

    public TerminalParamDownloadResponse(){

    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    protected TerminalParamDownloadResponse(Parcel in) {
        setFlag(in.readInt() ==1 ?true:false);
        setCode(in.readInt());
        setMessage(in.readString());
        int size = in.dataAvail();
        Log.d("AdasService","TerminalParamDownloadResponse----size: "+size);
        if(size>0){
            setData((Data)in.readParcelable(Data.class.getClassLoader()));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(flag?1:0);
        dest.writeInt(code);
        dest.writeString(message);
        Log.d("AdasService","TerminalParamDownloadResponse--------writeToParcel---->data: "+data);
        if(data != null){
            dest.writeParcelable(data,flags);
        }
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<TerminalParamDownloadResponse> CREATOR = new Parcelable.Creator<TerminalParamDownloadResponse>(){
        @Override
        public TerminalParamDownloadResponse createFromParcel(Parcel source) {//从Parcel中读取数据，返回TerminalParamDownloadResponse对象
            return new TerminalParamDownloadResponse(source);
        }
        @Override
        public TerminalParamDownloadResponse[] newArray(int size) {
            return new TerminalParamDownloadResponse[size];
        }
    };

    public static class Data implements Parcelable{
        private String eye_switch;
        private String eye_speed;
        private String eye_photoUpload;
        private String eye_videoUpload;
        private String eye_voiceSwitch;
        private String yawn_switch;
        private String yawn_speed;
        private String yawn_photoUpload;
        private String yawn_videoUpload;
        private String yawn_voiceSwitch;
        private String phone_switch;
        private String phone_photoUpload;
        private String phone_videoUpload;
        private String phone_voiceSwitch;
        private String phone_volume;
        private String phone_volume_type;
        private String smoking_switch;
        private String smoking_speed;
        private String smoking_photoUpload;
        private String smoking_videoUpload;
        private String smoking_voiceSwitch;
        private String smoking_volume;
        private String smoking_volume_type;
        private String abnormal_switch;
        private String abnormal_speed;
        private String abnormal_photoUpload;
        private String abnormal_videoUpload;
        private String abnormal_voiceSwitch;
        private String nodriver_switch;
        private String nodriver_speed;
        private String nodriver_photoUpload;
        private String nodriver_videoUpload;
        private String nodriver_voiceSwitch;
        private String alwaysPlayNoDriver;
        private String dbwsNodriverTime;
        private String overtime_switch;
        private String overtime_photoUpload;
        private String overtime_videoUpload;
        private String overtime_voiceSwitch;
        private String max_time;
        private String hotspot;
        private String volume;
        private String uploadStorage_time;
        private String voiceSwicth;
        private String autoUploadAlarmMedia;
        private String uploadSensorData;
        private String isCameraPoweroff;
        private String fcw_switch;
        private String fcw_speed;
        private String fcw_level;
        private String fcw_photoUpload;
        private String fcw_videoUpload;
        private String ufcw_switch;
        private String ldw_switch;
        private String ldw_level;
        private String ldw_speed;
        private String ldw_photoUpload;
        private String ldw_videoUpload;
        private String pcw_switch;
        private String pcw_speed;
        private String pcw_photoUpload;
        private String pcw_videoUpload;
        private String overspeed_switch;
        private String overspeed_photoUpload;
        private String overspeed_videoUpload;
        private String max_speed="";
        private String fcw_cameraHeight;
        private String fcw_carWidth;
        private String fcw_cameraHLevel;
        private String fcw_cameraForwardDis;
        private String fcw_cameraCoef;
        private String pointX;
        private String pointY;
        private String type;
        private String interval_time;
        private String available_time;
        private String content;
        private String deviceAdjustInfo;
        private String upDeviceAdjustInfo;
        private String adjustCmd;
        private String adjustMedia;
        private String deviceLogError;
        private String netTime;
        private String saveFaceModelList;
        private String getFaceModel;
        private String getDriverInfoList;
        private String bindDriverToVehicle;

        public String getEye_switch() {
            return eye_switch;
        }

        public void setEye_switch(String eye_switch) {
            this.eye_switch = eye_switch;
        }

        public String getEye_speed() {
            return eye_speed;
        }

        public void setEye_speed(String eye_speed) {
            this.eye_speed = eye_speed;
        }

        public String getEye_photoUpload() {
            return eye_photoUpload;
        }

        public void setEye_photoUpload(String eye_photoUpload) {
            this.eye_photoUpload = eye_photoUpload;
        }

        public String getEye_videoUpload() {
            return eye_videoUpload;
        }

        public void setEye_videoUpload(String eye_videoUpload) {
            this.eye_videoUpload = eye_videoUpload;
        }

        public String getEye_voiceSwitch() {
            return eye_voiceSwitch;
        }

        public void setEye_voiceSwitch(String eye_voiceSwitch) {
            this.eye_voiceSwitch = eye_voiceSwitch;
        }

        public String getYawn_switch() {
            return yawn_switch;
        }

        public void setYawn_switch(String yawn_switch) {
            this.yawn_switch = yawn_switch;
        }

        public String getYawn_speed() {
            return yawn_speed;
        }

        public void setYawn_speed(String yawn_speed) {
            this.yawn_speed = yawn_speed;
        }

        public String getYawn_photoUpload() {
            return yawn_photoUpload;
        }

        public void setYawn_photoUpload(String yawn_photoUpload) {
            this.yawn_photoUpload = yawn_photoUpload;
        }

        public String getYawn_videoUpload() {
            return yawn_videoUpload;
        }

        public void setYawn_videoUpload(String yawn_videoUpload) {
            this.yawn_videoUpload = yawn_videoUpload;
        }

        public String getYawn_voiceSwitch() {
            return yawn_voiceSwitch;
        }

        public void setYawn_voiceSwitch(String yawn_voiceSwitch) {
            this.yawn_voiceSwitch = yawn_voiceSwitch;
        }

        public String getPhone_switch() {
            return phone_switch;
        }

        public void setPhone_switch(String phone_switch) {
            this.phone_switch = phone_switch;
        }

        public String getPhone_photoUpload() {
            return phone_photoUpload;
        }

        public void setPhone_photoUpload(String phone_photoUpload) {
            this.phone_photoUpload = phone_photoUpload;
        }

        public String getPhone_videoUpload() {
            return phone_videoUpload;
        }

        public void setPhone_videoUpload(String phone_videoUpload) {
            this.phone_videoUpload = phone_videoUpload;
        }

        public String getPhone_voiceSwitch() {
            return phone_voiceSwitch;
        }

        public void setPhone_voiceSwitch(String phone_voiceSwitch) {
            this.phone_voiceSwitch = phone_voiceSwitch;
        }

        public String getPhone_volume() {
            return phone_volume;
        }

        public void setPhone_volume(String phone_volume) {
            this.phone_volume = phone_volume;
        }

        public String getPhone_volume_type() {
            return phone_volume_type;
        }

        public void setPhone_volume_type(String phone_volume_type) {
            this.phone_volume_type = phone_volume_type;
        }

        public String getSmoking_switch() {
            return smoking_switch;
        }

        public void setSmoking_switch(String smoking_switch) {
            this.smoking_switch = smoking_switch;
        }

        public String getSmoking_speed() {
            return smoking_speed;
        }

        public void setSmoking_speed(String smoking_speed) {
            this.smoking_speed = smoking_speed;
        }

        public String getSmoking_photoUpload() {
            return smoking_photoUpload;
        }

        public void setSmoking_photoUpload(String smoking_photoUpload) {
            this.smoking_photoUpload = smoking_photoUpload;
        }

        public String getSmoking_videoUpload() {
            return smoking_videoUpload;
        }

        public void setSmoking_videoUpload(String smoking_videoUpload) {
            this.smoking_videoUpload = smoking_videoUpload;
        }

        public String getSmoking_voiceSwitch() {
            return smoking_voiceSwitch;
        }

        public void setSmoking_voiceSwitch(String smoking_voiceSwitch) {
            this.smoking_voiceSwitch = smoking_voiceSwitch;
        }

        public String getSmoking_volume() {
            return smoking_volume;
        }

        public void setSmoking_volume(String smoking_volume) {
            this.smoking_volume = smoking_volume;
        }

        public String getSmoking_volume_type() {
            return smoking_volume_type;
        }

        public void setSmoking_volume_type(String smoking_volume_type) {
            this.smoking_volume_type = smoking_volume_type;
        }

        public String getAbnormal_switch() {
            return abnormal_switch;
        }

        public void setAbnormal_switch(String abnormal_switch) {
            this.abnormal_switch = abnormal_switch;
        }

        public String getAbnormal_speed() {
            return abnormal_speed;
        }

        public void setAbnormal_speed(String abnormal_speed) {
            this.abnormal_speed = abnormal_speed;
        }

        public String getAbnormal_photoUpload() {
            return abnormal_photoUpload;
        }

        public void setAbnormal_photoUpload(String abnormal_photoUpload) {
            this.abnormal_photoUpload = abnormal_photoUpload;
        }

        public String getAbnormal_videoUpload() {
            return abnormal_videoUpload;
        }

        public void setAbnormal_videoUpload(String abnormal_videoUpload) {
            this.abnormal_videoUpload = abnormal_videoUpload;
        }

        public String getAbnormal_voiceSwitch() {
            return abnormal_voiceSwitch;
        }

        public void setAbnormal_voiceSwitch(String abnormal_voiceSwitch) {
            this.abnormal_voiceSwitch = abnormal_voiceSwitch;
        }

        public String getNodriver_switch() {
            return nodriver_switch;
        }

        public void setNodriver_switch(String nodriver_switch) {
            this.nodriver_switch = nodriver_switch;
        }

        public String getNodriver_speed() {
            return nodriver_speed;
        }

        public void setNodriver_speed(String nodriver_speed) {
            this.nodriver_speed = nodriver_speed;
        }

        public String getNodriver_photoUpload() {
            return nodriver_photoUpload;
        }

        public void setNodriver_photoUpload(String nodriver_photoUpload) {
            this.nodriver_photoUpload = nodriver_photoUpload;
        }

        public String getNodriver_videoUpload() {
            return nodriver_videoUpload;
        }

        public void setNodriver_videoUpload(String nodriver_videoUpload) {
            this.nodriver_videoUpload = nodriver_videoUpload;
        }

        public String getNodriver_voiceSwitch() {
            return nodriver_voiceSwitch;
        }

        public void setNodriver_voiceSwitch(String nodriver_voiceSwitch) {
            this.nodriver_voiceSwitch = nodriver_voiceSwitch;
        }

        public String getAlwaysPlayNoDriver() {
            return alwaysPlayNoDriver;
        }

        public void setAlwaysPlayNoDriver(String alwaysPlayNoDriver) {
            this.alwaysPlayNoDriver = alwaysPlayNoDriver;
        }

        public String getDbwsNodriverTime() {
            return dbwsNodriverTime;
        }

        public void setDbwsNodriverTime(String dbwsNodriverTime) {
            this.dbwsNodriverTime = dbwsNodriverTime;
        }

        public String getOvertime_switch() {
            return overtime_switch;
        }

        public void setOvertime_switch(String overtime_switch) {
            this.overtime_switch = overtime_switch;
        }

        public String getOvertime_photoUpload() {
            return overtime_photoUpload;
        }

        public void setOvertime_photoUpload(String overtime_photoUpload) {
            this.overtime_photoUpload = overtime_photoUpload;
        }

        public String getOvertime_videoUpload() {
            return overtime_videoUpload;
        }

        public void setOvertime_videoUpload(String overtime_videoUpload) {
            this.overtime_videoUpload = overtime_videoUpload;
        }

        public String getOvertime_voiceSwitch() {
            return overtime_voiceSwitch;
        }

        public void setOvertime_voiceSwitch(String overtime_voiceSwitch) {
            this.overtime_voiceSwitch = overtime_voiceSwitch;
        }

        public String getMax_time() {
            return max_time;
        }

        public void setMax_time(String max_time) {
            this.max_time = max_time;
        }

        public String getHotspot() {
            return hotspot;
        }

        public void setHotspot(String hotspot) {
            this.hotspot = hotspot;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getUploadStorage_time() {
            return uploadStorage_time;
        }

        public void setUploadStorage_time(String uploadStorage_time) {
            this.uploadStorage_time = uploadStorage_time;
        }

        public String getVoiceSwicth() {
            return voiceSwicth;
        }

        public void setVoiceSwicth(String voiceSwicth) {
            this.voiceSwicth = voiceSwicth;
        }

        public String getAutoUploadAlarmMedia() {
            return autoUploadAlarmMedia;
        }

        public void setAutoUploadAlarmMedia(String autoUploadAlarmMedia) {
            this.autoUploadAlarmMedia = autoUploadAlarmMedia;
        }

        public String getUploadSensorData() {
            return uploadSensorData;
        }

        public void setUploadSensorData(String uploadSensorData) {
            this.uploadSensorData = uploadSensorData;
        }

        public String getIsCameraPoweroff() {
            return isCameraPoweroff;
        }

        public void setIsCameraPoweroff(String isCameraPoweroff) {
            this.isCameraPoweroff = isCameraPoweroff;
        }

        public String getFcw_switch() {
            return fcw_switch;
        }

        public void setFcw_switch(String fcw_switch) {
            this.fcw_switch = fcw_switch;
        }

        public String getFcw_speed() {
            return fcw_speed;
        }

        public void setFcw_speed(String fcw_speed) {
            this.fcw_speed = fcw_speed;
        }

        public String getFcw_level() {
            return fcw_level;
        }

        public void setFcw_level(String fcw_level) {
            this.fcw_level = fcw_level;
        }

        public String getFcw_photoUpload() {
            return fcw_photoUpload;
        }

        public void setFcw_photoUpload(String fcw_photoUpload) {
            this.fcw_photoUpload = fcw_photoUpload;
        }

        public String getFcw_videoUpload() {
            return fcw_videoUpload;
        }

        public void setFcw_videoUpload(String fcw_videoUpload) {
            this.fcw_videoUpload = fcw_videoUpload;
        }

        public String getUfcw_switch() {
            return ufcw_switch;
        }

        public void setUfcw_switch(String ufcw_switch) {
            this.ufcw_switch = ufcw_switch;
        }

        public String getLdw_switch() {
            return ldw_switch;
        }

        public void setLdw_switch(String ldw_switch) {
            this.ldw_switch = ldw_switch;
        }

        public String getLdw_level() {
            return ldw_level;
        }

        public void setLdw_level(String ldw_level) {
            this.ldw_level = ldw_level;
        }

        public String getLdw_speed() {
            return ldw_speed;
        }

        public void setLdw_speed(String ldw_speed) {
            this.ldw_speed = ldw_speed;
        }

        public String getLdw_photoUpload() {
            return ldw_photoUpload;
        }

        public void setLdw_photoUpload(String ldw_photoUpload) {
            this.ldw_photoUpload = ldw_photoUpload;
        }

        public String getLdw_videoUpload() {
            return ldw_videoUpload;
        }

        public void setLdw_videoUpload(String ldw_videoUpload) {
            this.ldw_videoUpload = ldw_videoUpload;
        }

        public String getPcw_switch() {
            return pcw_switch;
        }

        public void setPcw_switch(String pcw_switch) {
            this.pcw_switch = pcw_switch;
        }

        public String getPcw_speed() {
            return pcw_speed;
        }

        public void setPcw_speed(String pcw_speed) {
            this.pcw_speed = pcw_speed;
        }

        public String getPcw_photoUpload() {
            return pcw_photoUpload;
        }

        public void setPcw_photoUpload(String pcw_photoUpload) {
            this.pcw_photoUpload = pcw_photoUpload;
        }

        public String getPcw_videoUpload() {
            return pcw_videoUpload;
        }

        public void setPcw_videoUpload(String pcw_videoUpload) {
            this.pcw_videoUpload = pcw_videoUpload;
        }

        public String getOverspeed_switch() {
            return overspeed_switch;
        }

        public void setOverspeed_switch(String overspeed_switch) {
            this.overspeed_switch = overspeed_switch;
        }

        public String getOverspeed_photoUpload() {
            return overspeed_photoUpload;
        }

        public void setOverspeed_photoUpload(String overspeed_photoUpload) {
            this.overspeed_photoUpload = overspeed_photoUpload;
        }

        public String getOverspeed_videoUpload() {
            return overspeed_videoUpload;
        }

        public void setOverspeed_videoUpload(String overspeed_videoUpload) {
            this.overspeed_videoUpload = overspeed_videoUpload;
        }

        public String getMax_speed() {
            return max_speed;
        }

        public void setMax_speed(String max_speed) {
            this.max_speed = max_speed;
        }

        public String getFcw_cameraHeight() {
            return fcw_cameraHeight;
        }

        public void setFcw_cameraHeight(String fcw_cameraHeight) {
            this.fcw_cameraHeight = fcw_cameraHeight;
        }

        public String getFcw_carWidth() {
            return fcw_carWidth;
        }

        public void setFcw_carWidth(String fcw_carWidth) {
            this.fcw_carWidth = fcw_carWidth;
        }

        public String getFcw_cameraHLevel() {
            return fcw_cameraHLevel;
        }

        public void setFcw_cameraHLevel(String fcw_cameraHLevel) {
            this.fcw_cameraHLevel = fcw_cameraHLevel;
        }

        public String getFcw_cameraForwardDis() {
            return fcw_cameraForwardDis;
        }

        public void setFcw_cameraForwardDis(String fcw_cameraForwardDis) {
            this.fcw_cameraForwardDis = fcw_cameraForwardDis;
        }

        public String getFcw_cameraCoef() {
            return fcw_cameraCoef;
        }

        public void setFcw_cameraCoef(String fcw_cameraCoef) {
            this.fcw_cameraCoef = fcw_cameraCoef;
        }

        public String getPointX() {
            return pointX;
        }

        public void setPointX(String pointX) {
            this.pointX = pointX;
        }

        public String getPointY() {
            return pointY;
        }

        public void setPointY(String pointY) {
            this.pointY = pointY;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInterval_time() {
            return interval_time;
        }

        public void setInterval_time(String interval_time) {
            this.interval_time = interval_time;
        }

        public String getAvailable_time() {
            return available_time;
        }

        public void setAvailable_time(String available_time) {
            this.available_time = available_time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDeviceAdjustInfo() {
            return deviceAdjustInfo;
        }

        public void setDeviceAdjustInfo(String deviceAdjustInfo) {
            this.deviceAdjustInfo = deviceAdjustInfo;
        }

        public String getUpDeviceAdjustInfo() {
            return upDeviceAdjustInfo;
        }

        public void setUpDeviceAdjustInfo(String upDeviceAdjustInfo) {
            this.upDeviceAdjustInfo = upDeviceAdjustInfo;
        }

        public String getAdjustCmd() {
            return adjustCmd;
        }

        public void setAdjustCmd(String adjustCmd) {
            this.adjustCmd = adjustCmd;
        }

        public String getAdjustMedia() {
            return adjustMedia;
        }

        public void setAdjustMedia(String adjustMedia) {
            this.adjustMedia = adjustMedia;
        }

        public String getDeviceLogError() {
            return deviceLogError;
        }

        public void setDeviceLogError(String deviceLogError) {
            this.deviceLogError = deviceLogError;
        }

        public String getNetTime() {
            return netTime;
        }

        public void setNetTime(String netTime) {
            this.netTime = netTime;
        }

        public String getSaveFaceModelList() {
            return saveFaceModelList;
        }

        public void setSaveFaceModelList(String saveFaceModelList) {
            this.saveFaceModelList = saveFaceModelList;
        }

        public String getGetFaceModel() {
            return getFaceModel;
        }

        public void setGetFaceModel(String getFaceModel) {
            this.getFaceModel = getFaceModel;
        }

        public String getGetDriverInfoList() {
            return getDriverInfoList;
        }

        public void setGetDriverInfoList(String getDriverInfoList) {
            this.getDriverInfoList = getDriverInfoList;
        }

        public String getBindDriverToVehicle() {
            return bindDriverToVehicle;
        }

        public void setBindDriverToVehicle(String bindDriverToVehicle) {
            this.bindDriverToVehicle = bindDriverToVehicle;
        }

        public Data(){

        }

        public Data(Parcel in) {
            setEye_switch(in.readString());
            setEye_speed(in.readString());
            setEye_photoUpload(in.readString());
            setEye_videoUpload(in.readString());
            setEye_voiceSwitch(in.readString());
            setYawn_switch(in.readString());
            setYawn_speed(in.readString());
            setYawn_photoUpload(in.readString());
            setYawn_videoUpload(in.readString());
            setYawn_voiceSwitch(in.readString());
            setPhone_switch(in.readString());
            setPhone_photoUpload(in.readString());
            setPhone_videoUpload(in.readString());
            setPhone_voiceSwitch(in.readString());
            setPhone_volume(in.readString());
            setPhone_volume_type(in.readString());
            setSmoking_switch(in.readString());
            setSmoking_speed(in.readString());
            setSmoking_photoUpload(in.readString());
            setSmoking_videoUpload(in.readString());
            setSmoking_voiceSwitch(in.readString());
            setSmoking_volume(in.readString());
            setSmoking_volume_type(in.readString());
            setAbnormal_switch(in.readString());
            setAbnormal_speed(in.readString());
            setAbnormal_photoUpload(in.readString());
            setAbnormal_videoUpload(in.readString());
            setAbnormal_voiceSwitch(in.readString());
            setNodriver_switch(in.readString());
            setNodriver_speed(in.readString());
            setNodriver_photoUpload(in.readString());
            setNodriver_videoUpload(in.readString());
            setNodriver_voiceSwitch(in.readString());
            setAlwaysPlayNoDriver(in.readString());
            setDbwsNodriverTime(in.readString());
            setOvertime_switch(in.readString());
            setOvertime_photoUpload(in.readString());
            setOvertime_videoUpload(in.readString());
            setOvertime_voiceSwitch(in.readString());
            setMax_time(in.readString());
            setHotspot(in.readString());
            setVolume(in.readString());
            setUploadStorage_time(in.readString());
            setVoiceSwicth(in.readString());
            setAutoUploadAlarmMedia(in.readString());
            setUploadSensorData(in.readString());
            setIsCameraPoweroff(in.readString());
            setFcw_switch(in.readString());
            setFcw_speed(in.readString());
            setFcw_level(in.readString());
            setFcw_photoUpload(in.readString());
            setFcw_videoUpload(in.readString());
            setUfcw_switch(in.readString());
            setLdw_switch(in.readString());
            setLdw_level(in.readString());
            setLdw_speed(in.readString());
            setLdw_photoUpload(in.readString());
            setLdw_videoUpload(in.readString());
            setPcw_switch(in.readString());
            setPcw_speed(in.readString());
            setPcw_photoUpload(in.readString());
            setPcw_videoUpload(in.readString());
            setOverspeed_switch(in.readString());
            setOverspeed_photoUpload(in.readString());
            setOverspeed_videoUpload(in.readString());
            setMax_speed(in.readString());
            setFcw_cameraHeight(in.readString());
            setFcw_carWidth(in.readString());
            setFcw_cameraHLevel(in.readString());
            setFcw_cameraForwardDis(in.readString());
            setFcw_cameraCoef(in.readString());
            setPointX(in.readString());
            setPointY(in.readString());
            setType(in.readString());
            setInterval_time(in.readString());
            setAvailable_time(in.readString());
            setContent(in.readString());
            setDeviceAdjustInfo(in.readString());
            setUpDeviceAdjustInfo(in.readString());
            setAdjustCmd(in.readString());
            setAdjustMedia(in.readString());
            setDeviceLogError(in.readString());
            setNetTime(in.readString());
            setSaveFaceModelList(in.readString());
            setGetFaceModel(in.readString());
            setGetDriverInfoList(in.readString());
            setBindDriverToVehicle(in.readString());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(getEye_switch());
            dest.writeString(getEye_speed());
            dest.writeString(getEye_photoUpload());
            dest.writeString(getEye_videoUpload());
            dest.writeString(getEye_voiceSwitch());
            dest.writeString(getYawn_switch());
            dest.writeString(getYawn_speed());
            dest.writeString(getYawn_photoUpload());
            dest.writeString(getYawn_videoUpload());
            dest.writeString(getYawn_voiceSwitch());
            dest.writeString(getPhone_switch());
            dest.writeString(getPhone_photoUpload());
            dest.writeString(getPhone_videoUpload());
            dest.writeString(getPhone_voiceSwitch());
            dest.writeString(getPhone_volume());
            dest.writeString(getPhone_volume_type());
            dest.writeString(getSmoking_switch());
            dest.writeString(getSmoking_speed());
            dest.writeString(getSmoking_photoUpload());
            dest.writeString(getSmoking_videoUpload());
            dest.writeString(getSmoking_voiceSwitch());
            dest.writeString(getSmoking_volume());
            dest.writeString(getSmoking_volume_type());
            dest.writeString(getAbnormal_switch());
            dest.writeString(getAbnormal_speed());
            dest.writeString(getAbnormal_photoUpload());
            dest.writeString(getAbnormal_videoUpload());
            dest.writeString(getAbnormal_voiceSwitch());
            dest.writeString(getNodriver_switch());
            dest.writeString(getNodriver_speed());
            dest.writeString(getNodriver_photoUpload());
            dest.writeString(getNodriver_videoUpload());
            dest.writeString(getNodriver_voiceSwitch());
            dest.writeString(getAlwaysPlayNoDriver());
            dest.writeString(getDbwsNodriverTime());
            dest.writeString(getOvertime_switch());
            dest.writeString(getOvertime_photoUpload());
            dest.writeString(getOvertime_videoUpload());
            dest.writeString(getOvertime_voiceSwitch());
            dest.writeString(getMax_time());
            dest.writeString(getHotspot());
            dest.writeString(getVolume());
            dest.writeString(getUploadStorage_time());
            dest.writeString(getVoiceSwicth());
            dest.writeString(getAutoUploadAlarmMedia());
            dest.writeString(getUploadSensorData());
            dest.writeString(getIsCameraPoweroff());
            dest.writeString(getFcw_switch());
            dest.writeString(getFcw_speed());
            dest.writeString(getFcw_level());
            dest.writeString(getFcw_photoUpload());
            dest.writeString(getFcw_videoUpload());
            dest.writeString(getUfcw_switch());
            dest.writeString(getLdw_switch());
            dest.writeString(getLdw_level());
            dest.writeString(getLdw_speed());
            dest.writeString(getLdw_photoUpload());
            dest.writeString(getLdw_videoUpload());
            dest.writeString(getPcw_switch());
            dest.writeString(getPcw_speed());
            dest.writeString(getPcw_photoUpload());
            dest.writeString(getPcw_videoUpload());
            dest.writeString(getOverspeed_switch());
            dest.writeString(getOverspeed_photoUpload());
            dest.writeString(getOverspeed_videoUpload());
            dest.writeString(getMax_speed());
            dest.writeString(getFcw_cameraHeight());
            dest.writeString(getFcw_carWidth());
            dest.writeString(getFcw_cameraHLevel());
            dest.writeString(getFcw_cameraForwardDis());
            dest.writeString(getFcw_cameraCoef());
            dest.writeString(getPointX());
            dest.writeString(getPointY());
            dest.writeString(getType());
            dest.writeString(getInterval_time());
            dest.writeString(getAvailable_time());
            dest.writeString(getContent());
            dest.writeString(getDeviceAdjustInfo());
            dest.writeString(getUpDeviceAdjustInfo());
            dest.writeString(getAdjustCmd());
            dest.writeString(getAdjustMedia());
            dest.writeString(getDeviceLogError());
            dest.writeString(getNetTime());
            dest.writeString(getSaveFaceModelList());
            dest.writeString(getGetFaceModel());
            dest.writeString(getGetDriverInfoList());
            dest.writeString(getBindDriverToVehicle());
        }

        //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
        public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>(){
            @Override
            public Data createFromParcel(Parcel source) {//从Parcel中读取数据，返回Data对象
                return new Data(source);
            }
            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        @Override
        public String toString() {
            return "{ eye_switch= "+eye_switch
                    +", eye_speed= "+eye_speed
                    +", eye_photoUpload= "+eye_photoUpload
                    +", eye_videoUpload= "+eye_videoUpload
                    +", eye_voiceSwitch= "+eye_voiceSwitch
                    +", yawn_switch= "+yawn_switch
                    +", yawn_speed= "+yawn_speed
                    +", yawn_photoUpload= "+yawn_photoUpload
                    +", yawn_videoUpload= "+yawn_videoUpload
                    +", yawn_voiceSwitch= "+yawn_voiceSwitch
                    +", phone_switch= "+phone_switch
                    +", phone_photoUpload= "+phone_photoUpload
                    +", phone_videoUpload= "+phone_videoUpload
                    +", phone_voiceSwitch= "+phone_voiceSwitch
                    +", phone_volume= "+phone_volume
                    +", phone_volume_type= "+phone_volume_type
                    +", smoking_switch= "+smoking_switch
                    +", smoking_speed= "+smoking_speed
                    +", smoking_photoUpload= "+smoking_photoUpload
                    +", smoking_videoUpload= "+smoking_videoUpload
                    +", smoking_voiceSwitch= "+smoking_voiceSwitch
                    +", smoking_volume= "+smoking_volume
                    +", smoking_volume_type= "+smoking_volume_type
                    +", abnormal_switch= "+abnormal_switch
                    +", abnormal_speed= "+abnormal_speed
                    +", abnormal_photoUpload= "+abnormal_photoUpload
                    +", abnormal_videoUpload= "+abnormal_videoUpload
                    +", abnormal_voiceSwitch= "+abnormal_voiceSwitch
                    +", nodriver_switch= "+nodriver_switch
                    +", nodriver_speed= "+nodriver_speed
                    +", nodriver_photoUpload= "+nodriver_photoUpload
                    +", nodriver_videoUpload= "+nodriver_videoUpload
                    +", nodriver_voiceSwitch= "+nodriver_voiceSwitch
                    +", alwaysPlayNoDriver= "+alwaysPlayNoDriver
                    +", dbwsNodriverTime= "+dbwsNodriverTime
                    +", overtime_switch= "+overtime_switch
                    +", overtime_photoUpload= "+overtime_photoUpload
                    +", overtime_videoUpload= "+overtime_videoUpload
                    +", overtime_voiceSwitch= "+overtime_voiceSwitch
                    +", max_time= "+max_time
                    +", hotspot= "+hotspot
                    +", volume= "+volume
                    +", uploadStorage_time= "+uploadStorage_time
                    +", voiceSwicth= "+voiceSwicth
                    +", autoUploadAlarmMedia= "+autoUploadAlarmMedia
                    +", uploadSensorData= "+uploadSensorData
                    +", isCameraPoweroff= "+isCameraPoweroff
                    +", fcw_switch= "+fcw_switch
                    +", fcw_speed= "+fcw_speed
                    +", fcw_level= "+fcw_level
                    +", fcw_photoUpload= "+fcw_photoUpload
                    +", fcw_videoUpload= "+fcw_videoUpload
                    +", ufcw_switch= "+ufcw_switch
                    +", ldw_switch= "+ldw_switch
                    +", ldw_level= "+ldw_level
                    +", ldw_speed= "+ldw_speed
                    +", ldw_photoUpload= "+ldw_photoUpload
                    +", ldw_videoUpload= "+ldw_videoUpload
                    +", pcw_switch= "+pcw_switch
                    +", pcw_speed= "+pcw_speed
                    +", pcw_photoUpload= "+pcw_photoUpload
                    +", pcw_videoUpload= "+pcw_videoUpload
                    +", overspeed_switch= "+overspeed_switch
                    +", overspeed_photoUpload= "+overspeed_photoUpload
                    +", overspeed_videoUpload= "+overspeed_videoUpload
                    +", max_speed= "+max_speed
                    +", fcw_cameraHeight= "+fcw_cameraHeight
                    +", fcw_carWidth= "+fcw_carWidth
                    +", fcw_cameraHLevel= "+fcw_cameraHLevel
                    +", fcw_cameraForwardDis= "+fcw_cameraForwardDis
                    +", fcw_cameraCoef= "+fcw_cameraCoef
                    +", pointX= "+pointX
                    +", pointY= "+pointY
                    +", type= "+type
                    +", interval_time= "+interval_time
                    +", available_time= "+available_time
                    +", content= "+content
                    +", deviceAdjustInfo= "+deviceAdjustInfo
                    +", upDeviceAdjustInfo= "+upDeviceAdjustInfo
                    +", adjustCmd= "+adjustCmd
                    +", adjustMedia= "+adjustMedia
                    +", deviceLogError= "+deviceLogError
                    +", netTime= "+netTime
                    +", saveFaceModelList= "+saveFaceModelList
                    +", getFaceModel= "+getFaceModel
                    +", getDriverInfoList= "+getDriverInfoList
                    +", bindDriverToVehicle= "+bindDriverToVehicle
                    +" }";
        }
    }

    @Override
    public String toString() {
        return "TerminalParamDownloadResponse[ flag= "+flag
                +", code= "+code
                +", message= "+message
                +", data= "+(data!=null?data.toString():"null")+"  ]";
    }

}
