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
public class ServerParamDownloadResponse extends BaseResponse implements Parcelable {
    private Data[] data;

    public ServerParamDownloadResponse(){

    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    protected ServerParamDownloadResponse(Parcel in) {
        setFlag(in.readInt() ==1 ?true:false);
        setCode(in.readInt());
        setMessage(in.readString());
        int size = in.dataAvail();
        Log.d("AdasService","ServerParamDownloadResponse------->size: "+size);
        if(size >0){
            //data = (Data[]) in.readParcelableArray(Data.class.getClassLoader());
            data = in.createTypedArray(Data.CREATOR);
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
        if(data != null && data.length>0){
            Log.d("AdasService","ServerParamDownloadResponse-------writeToParcel------>num: "+data.length);
           // dest.writeParcelableArray(data,flags);
            dest.writeTypedArray(data,flags);
        }
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<ServerParamDownloadResponse> CREATOR = new Parcelable.Creator<ServerParamDownloadResponse>(){
        @Override
        public ServerParamDownloadResponse createFromParcel(Parcel source) {//从Parcel中读取数据，返回ServerParamDownloadResponse对象
            return new ServerParamDownloadResponse(source);
        }
        @Override
        public ServerParamDownloadResponse[] newArray(int size) {
            return new ServerParamDownloadResponse[size];
        }
    };

    public static class Data  implements Parcelable {
        private String ip;
        private String ipPort;
        private String ossUrl;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String spareIp;
        private String spareIpPort;
        private String spareOssUrl;
        private String spareAccessKeyId;
        private String spareAccessKeySecret;
        private String spareBucketName;
        private String type;
        private String upMode;
        private String mainServer;
        private String appName;
        private String pushAddress;
        private String rtmpUrl;

        public Data(){

        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getIpPort() {
            return ipPort;
        }

        public void setIpPort(String ipPort) {
            this.ipPort = ipPort;
        }

        public String getOssUrl() {
            return ossUrl;
        }

        public void setOssUrl(String ossUrl) {
            this.ossUrl = ossUrl;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getSpareIp() {
            return spareIp;
        }

        public void setSpareIp(String spareIp) {
            this.spareIp = spareIp;
        }

        public String getSpareIpPort() {
            return spareIpPort;
        }

        public void setSpareIpPort(String spareIpPort) {
            this.spareIpPort = spareIpPort;
        }

        public String getSpareOssUrl() {
            return spareOssUrl;
        }

        public void setSpareOssUrl(String spareOssUrl) {
            this.spareOssUrl = spareOssUrl;
        }

        public String getSpareAccessKeyId() {
            return spareAccessKeyId;
        }

        public void setSpareAccessKeyId(String spareAccessKeyId) {
            this.spareAccessKeyId = spareAccessKeyId;
        }

        public String getSpareAccessKeySecret() {
            return spareAccessKeySecret;
        }

        public void setSpareAccessKeySecret(String spareAccessKeySecret) {
            this.spareAccessKeySecret = spareAccessKeySecret;
        }

        public String getSpareBucketName() {
            return spareBucketName;
        }

        public void setSpareBucketName(String spareBucketName) {
            this.spareBucketName = spareBucketName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUpMode() {
            return upMode;
        }

        public void setUpMode(String upMode) {
            this.upMode = upMode;
        }

        public String getMainServer() {
            return mainServer;
        }

        public void setMainServer(String mainServer) {
            this.mainServer = mainServer;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getPushAddress() {
            return pushAddress;
        }

        public void setPushAddress(String pushAddress) {
            this.pushAddress = pushAddress;
        }

        public String getRtmpUrl() {
            return rtmpUrl;
        }

        public void setRtmpUrl(String rtmpUrl) {
            this.rtmpUrl = rtmpUrl;
        }


        public Data(Parcel in) {
            setIp(in.readString());
            setIpPort(in.readString());
            setOssUrl(in.readString());
            setAccessKeyId(in.readString());
            setAccessKeySecret(in.readString());
            setBucketName(in.readString());
            setSpareIp(in.readString());
            setSpareIpPort(in.readString());
            setSpareOssUrl(in.readString());
            setSpareAccessKeyId(in.readString());
            setSpareAccessKeySecret(in.readString());
            setSpareBucketName(in.readString());
            setType(in.readString());
            setUpMode(in.readString());
            setMainServer(in.readString());
            setAppName(in.readString());
            setPushAddress(in.readString());
            setRtmpUrl(in.readString());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(getIp());
            dest.writeString(getIpPort());
            dest.writeString(getOssUrl());
            dest.writeString(getAccessKeyId());
            dest.writeString(getAccessKeySecret());
            dest.writeString(getBucketName());
            dest.writeString(getSpareIp());
            dest.writeString(getSpareIpPort());
            dest.writeString(getSpareOssUrl());
            dest.writeString(getSpareAccessKeyId());
            dest.writeString(getSpareAccessKeySecret());
            dest.writeString(getSpareBucketName());
            dest.writeString(getType());
            dest.writeString(getUpMode());
            dest.writeString(getMainServer());
            dest.writeString(getAppName());
            dest.writeString(getPushAddress());
            dest.writeString(getRtmpUrl());
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {

            @Override
            public Data createFromParcel(Parcel source) {
                return new Data(source);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }

        };

        @Override
        public String toString() {
            return "{ ip= "+ip
                    +", ipPort= "+ipPort
                    +", ossUrl= "+ossUrl
                    +", accessKeyId= "+accessKeyId
                    +", accessKeySecret= "+accessKeySecret
                    +", bucketName= "+bucketName
                    +", spareIp= "+spareIp
                    +", spareIpPort= "+spareIpPort
                    +", spareOssUrl= "+spareOssUrl
                    +", spareAccessKeyId= "+spareAccessKeyId
                    +", spareAccessKeySecret= "+spareAccessKeySecret
                    +", spareBucketName= "+spareBucketName
                    +", type= "+type
                    +", upMode= "+upMode
                    +", mainServer= "+mainServer
                    +", appName= "+appName
                    +", pushAddress= "+pushAddress
                    +", rtmpUrl= "+rtmpUrl
                    +" } ";
        }
    }

    @Override
    public String toString() {
        String dataStr = null;
        if(data != null && data.length >0){
            for(int i=0;i<data.length;i++){
                dataStr += data[i].toString();
            }
        }

        return "ServerParamDownloadResponse[ flag= "+flag
                +", code= "+code
                +", message= "+message
                +", data=["+dataStr+"]  ]";
    }

}
