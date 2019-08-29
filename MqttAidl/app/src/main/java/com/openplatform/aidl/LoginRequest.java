package com.openplatform.aidl;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/17 10:21
 * Description :
 */
public class LoginRequest implements Parcelable {
    private String simNo;
    private String terminalId;
    private String imei;
    private String simType;
    private String macAddress;
    private String serialNo;
    private String productType;

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSimType() {
        return simType;
    }

    public void setSimType(String simType) {
        this.simType = simType;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public LoginRequest(){

    }

    public LoginRequest(String simNo, String terminalId, String imei, String simType, String macAddress, String serialNo, String productType) {
        this.simNo = simNo;
        this.terminalId = terminalId;
        this.imei = imei;
        this.simType = simType;
        this.macAddress = macAddress;
        this.serialNo = serialNo;
        this.productType = productType;
    }

    protected LoginRequest(Parcel in) {
        this.simNo = in.readString();
        this.terminalId = in.readString();
        this.imei = in.readString();
        this.simType = in.readString();
        this.macAddress = in.readString();
        this.serialNo = in.readString();
        this.productType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.simNo);
        dest.writeString(this.terminalId);
        dest.writeString(this.imei);
        dest.writeString(this.simType);
        dest.writeString(this.macAddress);
        dest.writeString(this.serialNo);
        dest.writeString(this.productType);
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<LoginRequest> CREATOR = new Parcelable.Creator<LoginRequest>(){
        @Override
        public LoginRequest createFromParcel(Parcel source) {//从Parcel中读取数据，返回LoginRequest对象
            return new LoginRequest(source);
        }
        @Override
        public LoginRequest[] newArray(int size) {
            return new LoginRequest[size];
        }
    };

}
