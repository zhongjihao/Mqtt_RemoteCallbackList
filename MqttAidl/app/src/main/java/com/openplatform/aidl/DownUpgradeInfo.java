package com.openplatform.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.openplatform.adas.datamodel.BaseResponse;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/25 10:21
 * Description :
 */
public class DownUpgradeInfo extends BaseResponse implements Parcelable {
    private Data[] data;

    public DownUpgradeInfo(){

    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    protected DownUpgradeInfo(Parcel in) {
        setFlag(in.readInt() ==1 ?true:false);
        setCode(in.readInt());
        setMessage(in.readString());
        int size = in.dataAvail();
        Log.d("AdasService","DownUpgradeInfo------->size: "+size);
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
            Log.d("AdasService","DownUpgradeInfo-------writeToParcel------>num: "+data.length);
            // dest.writeParcelableArray(data,flags);
            dest.writeTypedArray(data,flags);
        }
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<DownUpgradeInfo> CREATOR = new Parcelable.Creator<DownUpgradeInfo>(){
        @Override
        public DownUpgradeInfo createFromParcel(Parcel source) {//从Parcel中读取数据，返回DownUpgradeInfo对象
            return new DownUpgradeInfo(source);
        }
        @Override
        public DownUpgradeInfo[] newArray(int size) {
            return new DownUpgradeInfo[size];
        }
    };

    public static class Data  implements Parcelable {
        private String deviceCode;
        private String productType;
        private String apkType;
        private long deviceVersion;
        private String fileFullName;
        private long fileSize;
        private String fileMd5;

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public String getProductType() {
            return productType;
        }

        public void setProductType(String productType) {
            this.productType = productType;
        }

        public String getApkType() {
            return apkType;
        }

        public void setApkType(String apkType) {
            this.apkType = apkType;
        }

        public long getDeviceVersion() {
            return deviceVersion;
        }

        public void setDeviceVersion(long deviceversion) {
            this.deviceVersion = deviceversion;
        }

        public String getFileFullName() {
            return fileFullName;
        }

        public void setFileFullName(String filefullName) {
            this.fileFullName = filefullName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileMd5() {
            return fileMd5;
        }

        public void setFileMd5(String fileMd5) {
            this.fileMd5 = fileMd5;
        }

        public Data(){

        }

        public Data(Parcel in) {
            setDeviceCode(in.readString());
            setProductType(in.readString());
            setApkType(in.readString());
            setDeviceVersion(in.readLong());
            setFileFullName(in.readString());
            setFileSize(in.readLong());
            setFileMd5(in.readString());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(getDeviceCode());
            dest.writeString(getProductType());
            dest.writeString(getApkType());
            dest.writeLong(getDeviceVersion());
            dest.writeString(getFileFullName());
            dest.writeLong(getFileSize());
            dest.writeString(getFileMd5());
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
            return "{ deviceCode= "+deviceCode
                    +", productType= "+productType
                    +", apkType= "+apkType
                    +", deviceVersion= "+deviceVersion
                    +", fileFullName= "+fileFullName
                    +", fileSize= "+fileSize
                    +", fileMd5= "+fileMd5
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

        return "DownUpgradeInfo[ flag= "+flag
                +", code= "+code
                +", message= "+message
                +", data=["+dataStr+"]  ]";
    }
}
