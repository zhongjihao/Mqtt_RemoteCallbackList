package com.openplatform.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/19 10:21
 * Description :
 */
public class SelfCheck implements Parcelable {
    private String deviceCode;
    private String productType;
    private int detectType;
    private String ICCID;
    private String simNo;
    private String netType;
    private String netSignal;
    private double latitude;
    private double longitude;
    private String commandId;
    private Map<String,Map<String,String>> signal;
    private Map<String,Map<String,String>> storage;
    private Map<String,Map<String,String>> camera;
    private Map<String,Map<String,String>> software;
    private Map<String,Map<String,String>> runstatus;
    private Map<String,Map<String,String>> dataServer;
    private Map<String,Map<String,String>> sensor;
    private Map<String,Map<String,String>> voltage;

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

    public int getDetectType() {
        return detectType;
    }

    public void setDetectType(int detectType) {
        this.detectType = detectType;
    }

    public String getICCID() {
        return ICCID;
    }

    public void setICCID(String ICCID) {
        this.ICCID = ICCID;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getNetSignal() {
        return netSignal;
    }

    public void setNetSignal(String netSignal) {
        this.netSignal = netSignal;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public Map<String, Map<String, String>> getSignal() {
        return signal;
    }

    public void setSignal(Map<String, Map<String, String>> signal) {
        this.signal = signal;
    }

    public Map<String, Map<String, String>> getStorage() {
        return storage;
    }

    public void setStorage(Map<String, Map<String, String>> storage) {
        this.storage = storage;
    }

    public Map<String, Map<String, String>> getCamera() {
        return camera;
    }

    public void setCamera(Map<String, Map<String, String>> camera) {
        this.camera = camera;
    }

    public Map<String, Map<String, String>> getSoftware() {
        return software;
    }

    public void setSoftware(Map<String, Map<String, String>> software) {
        this.software = software;
    }

    public Map<String, Map<String, String>> getRunstatus() {
        return runstatus;
    }

    public void setRunstatus(Map<String, Map<String, String>> runstatus) {
        this.runstatus = runstatus;
    }

    public Map<String, Map<String, String>> getDataServer() {
        return dataServer;
    }

    public void setDataServer(Map<String, Map<String, String>> dataServer) {
        this.dataServer = dataServer;
    }

    public Map<String, Map<String, String>> getSensor() {
        return sensor;
    }

    public void setSensor(Map<String, Map<String, String>> sensor) {
        this.sensor = sensor;
    }

    public Map<String, Map<String, String>> getVoltage() {
        return voltage;
    }

    public void setVoltage(Map<String, Map<String, String>> voltage) {
        this.voltage = voltage;
    }

    public SelfCheck() {

    }

    protected SelfCheck(Parcel in) {
        this.deviceCode = in.readString();
        this.productType = in.readString();
        this.detectType = in.readInt();
        this.ICCID = in.readString();
        this.simNo = in.readString();
        this.netType = in.readString();
        this.netSignal = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.commandId = in.readString();
        this.signal = in.readHashMap(HashMap.class.getClassLoader());
        this.storage = in.readHashMap(HashMap.class.getClassLoader());
        this.camera = in.readHashMap(HashMap.class.getClassLoader());
        this.software = in.readHashMap(HashMap.class.getClassLoader());
        this.runstatus = in.readHashMap(HashMap.class.getClassLoader());
        this.dataServer = in.readHashMap(HashMap.class.getClassLoader());
        this.sensor = in.readHashMap(HashMap.class.getClassLoader());
        this.voltage = in.readHashMap(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceCode);
        dest.writeString(this.productType);
        dest.writeInt(this.detectType);
        dest.writeString(this.ICCID);
        dest.writeString(this.simNo);
        dest.writeString(this.netType);
        dest.writeString(this.netSignal);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.commandId);
        dest.writeMap(this.signal);
        dest.writeMap(this.storage);
        dest.writeMap(this.camera);
        dest.writeMap(this.software);
        dest.writeMap(this.runstatus);
        dest.writeMap(this.dataServer);
        dest.writeMap(this.sensor);
        dest.writeMap(this.voltage);
    }

    //添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<SelfCheck> CREATOR = new Parcelable.Creator<SelfCheck>(){
        @Override
        public SelfCheck createFromParcel(Parcel source) {//从Parcel中读取数据，返回SelfCheck对象
            return new SelfCheck(source);
        }
        @Override
        public SelfCheck[] newArray(int size) {
            return new SelfCheck[size];
        }
    };

    @Override
    public String toString() {
        Log.d("SelfCheck","signal:{ ");
        for (Map.Entry<String, Map<String,String>> entry : signal.entrySet()) {
            String key = entry.getKey();
            Map<String,String> valueMap = entry.getValue();
            Log.d("SelfCheck",key+"[ ");
            if(valueMap != null){
                for (Map.Entry<String, String> subentry : valueMap.entrySet()){
                    String subkey = subentry.getKey();
                    String subvalue = subentry.getValue();
                    Log.d("SelfCheck",subkey+"= "+subvalue+", ");
                }
            }
            Log.d("SelfCheck","] ");
        }
        Log.d("SelfCheck"," }");

        Log.d("SelfCheck","storage:{ ");
        for (Map.Entry<String, Map<String,String>> entry : storage.entrySet()) {
            String key = entry.getKey();
            Log.d("SelfCheck",key+"[ ");
            Map<String,String> valueMap = entry.getValue();
            if(valueMap != null){
                for (Map.Entry<String, String> subentry : valueMap.entrySet()){
                    String subkey = subentry.getKey();
                    String subvalue = subentry.getValue();
                    Log.d("SelfCheck",subkey+"= "+subvalue+", ");
                }
            }
            Log.d("SelfCheck","] ");
        }
        Log.d("SelfCheck"," }");

        Log.d("SelfCheck","camera:{ ");
        for (Map.Entry<String, Map<String,String>> entry : camera.entrySet()) {
            String key = entry.getKey();
            Log.d("SelfCheck",key+"[ ");
            Map<String,String> valueMap = entry.getValue();
            if(valueMap != null){
                for (Map.Entry<String, String> subentry : valueMap.entrySet()){
                    String subkey = subentry.getKey();
                    String subvalue = subentry.getValue();
                    Log.d("SelfCheck",subkey+"= "+subvalue+", ");
                }
            }
            Log.d("SelfCheck","] ");
        }
        Log.d("SelfCheck"," }");

        Log.d("SelfCheck","software:{ ");
        for (Map.Entry<String, Map<String,String>> entry : software.entrySet()) {
            String key = entry.getKey();
            Log.d("SelfCheck",key+"[ ");
            Map<String,String> valueMap = entry.getValue();
            if(valueMap != null){
                for (Map.Entry<String, String> subentry : valueMap.entrySet()){
                    String subkey = subentry.getKey();
                    String subvalue = subentry.getValue();
                    Log.d("SelfCheck",subkey+"= "+subvalue+", ");
                }
            }
            Log.d("SelfCheck","] ");
        }
        Log.d("SelfCheck"," }");

        Log.d("SelfCheck","runstatus:{ ");
        for (Map.Entry<String, Map<String,String>> entry : runstatus.entrySet()) {
            String key = entry.getKey();
            Log.d("SelfCheck",key+"[ ");
            Map<String,String> valueMap = entry.getValue();
            if(valueMap != null){
                for (Map.Entry<String, String> subentry : valueMap.entrySet()){
                    String subkey = subentry.getKey();
                    String subvalue = subentry.getValue();
                    Log.d("SelfCheck",subkey+"= "+subvalue+", ");
                }
            }
            Log.d("SelfCheck","] ");
        }
        Log.d("SelfCheck"," }");

        Log.d("SelfCheck","dataServer:{ ");
        for (Map.Entry<String, Map<String,String>> entry : dataServer.entrySet()) {
            String key = entry.getKey();
            Log.d("SelfCheck",key+"[ ");
            Map<String,String> valueMap = entry.getValue();
            if(valueMap != null){
                for (Map.Entry<String, String> subentry : valueMap.entrySet()){
                    String subkey = subentry.getKey();
                    String subvalue = subentry.getValue();
                    Log.d("SelfCheck",subkey+"= "+subvalue+", ");
                }
            }
            Log.d("SelfCheck","] ");
        }
        Log.d("SelfCheck"," }");

        return "SelfCheck [ deviceCode= " + deviceCode + " ,productType= " + productType
                + " ,detectType= " + detectType + " ,ICCID= " + ICCID + " ,simNo= " + simNo + " ,netType= "
                + netType + " ,netSignal= " + netSignal + " ,latitude= " + latitude + " ,longitude= " + longitude
                +", commandId= "+commandId
                +" ]";
    }
}
