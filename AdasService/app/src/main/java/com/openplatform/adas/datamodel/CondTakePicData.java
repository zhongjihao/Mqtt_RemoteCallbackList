package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/8/27 10:21
 * Description :
 */
public class CondTakePicData {
    private String batchNum;
    private String channelId;
    private int interval;
    private int count;
    private String distance;
    private String minSpeed;
    private int angle;

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(String minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "CondTakePicData{" +
                "batchNum='" + batchNum + '\'' +
                ", channelId='" + channelId + '\'' +
                ", interval=" + interval +
                ", count=" + count +
                ", distance='" + distance + '\'' +
                ", minSpeed='" + minSpeed + '\'' +
                ", angle=" + angle +
                '}';
    }
}
