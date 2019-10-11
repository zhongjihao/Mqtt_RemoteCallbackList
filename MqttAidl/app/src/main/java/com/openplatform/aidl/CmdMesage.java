package com.openplatform.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/9/12 10:21
 * Description :
 */
public class CmdMesage implements Parcelable {
    private String cmd;
    private String value;

    public CmdMesage(){

    }

    public CmdMesage(String cmd, String value){
        this.cmd = cmd;
        this.value = value;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    protected CmdMesage(Parcel in) {
        setCmd(in.readString());
        setValue(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cmd);
        dest.writeString(value);
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Creator<CmdMesage> CREATOR = new Creator<CmdMesage>(){
        @Override
        public CmdMesage createFromParcel(Parcel source) {//从Parcel中读取数据，返回CmdMesage对象
            return new CmdMesage(source);
        }
        @Override
        public CmdMesage[] newArray(int size) {
            return new CmdMesage[size];
        }
    };


    @Override
    public String toString() {
        return "CmdMesage{" +
                "cmd='" + cmd + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
