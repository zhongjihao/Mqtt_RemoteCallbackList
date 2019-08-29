package com.openplatform.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/26 10:21
 * Description :
 */
public class PutMsgRequest implements Parcelable {
    private String deviceCode;
    private String productType;
    private String msgType;
    private String msgCode;
    private String msgContent;

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

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public PutMsgRequest(String deviceCode, String productType, String msgType, String msgCode, String msgContent) {
        this.deviceCode = deviceCode;
        this.productType = productType;
        this.msgType = msgType;
        this.msgCode = msgCode;
        this.msgContent = msgContent;
    }

    protected PutMsgRequest(Parcel in) {
        this.deviceCode = in.readString();
        this.productType = in.readString();
        this.msgType = in.readString();
        this.msgCode = in.readString();
        this.msgContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceCode);
        dest.writeString(this.productType);
        dest.writeString(this.msgType);
        dest.writeString(this.msgCode);
        dest.writeString(this.msgContent);
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<PutMsgRequest> CREATOR = new Parcelable.Creator<PutMsgRequest>(){
        @Override
        public PutMsgRequest createFromParcel(Parcel source) {//从Parcel中读取数据，返回PutMsgRequest对象
            return new PutMsgRequest(source);
        }
        @Override
        public PutMsgRequest[] newArray(int size) {
            return new PutMsgRequest[size];
        }
    };
}
