package com.openplatform.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.openplatform.adas.datamodel.BaseResponse;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/26 10:21
 * Description :
 */
public class GetDatTime extends BaseResponse implements Parcelable {
    private Data data;

    public GetDatTime(){

    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    protected GetDatTime(Parcel in) {
        setFlag(in.readInt() ==1 ?true:false);
        setCode(in.readInt());
        setMessage(in.readString());
        int size = in.dataAvail();
        Log.d("AdasService","GetDatTime------->size: "+size);
        if(size >0){
            data = in.readParcelable(Data.class.getClassLoader());
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
        if(data != null){
            Log.d("AdasService","GetDatTime-------writeToParcel");
            dest.writeParcelable(data,flags);
        }
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<GetDatTime> CREATOR = new Parcelable.Creator<GetDatTime>(){
        @Override
        public GetDatTime createFromParcel(Parcel source) {//从Parcel中读取数据，返回GetDatTime对象
            return new GetDatTime(source);
        }
        @Override
        public GetDatTime[] newArray(int size) {
            return new GetDatTime[size];
        }
    };

    public static class Data  implements Parcelable {
        private long nowTime;

        public long getNowTime() {
            return nowTime;
        }

        public void setNowTime(long nowTime) {
            this.nowTime = nowTime;
        }

        public Data(){

        }

        public Data(Parcel in) {
            setNowTime(in.readLong());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(getNowTime());
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
            return "{ nowTime= "+nowTime
                    +" } ";
        }

    }

    @Override
    public String toString() {
        return "GetDatTime[ flag= "+flag
                +", code= "+code
                +", message= "+message
                +", data="+(data!=null?data.toString():"null")+"  ]";
    }
}
