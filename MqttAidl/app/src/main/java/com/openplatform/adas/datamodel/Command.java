package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/8/6 10:21
 * Description :
 */
public class Command {
    private long id;
    private String deviceId;
    private String command;
    private String cmdSNO;
    private int status;
    private int count;
    private String path;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCmdSNO() {
        return cmdSNO;
    }

    public void setCmdSNO(String cmdSNO) {
        this.cmdSNO = cmdSNO;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", command='" + command + '\'' +
                ", cmdSNO='" + cmdSNO + '\'' +
                ", status=" + status +
                ", count=" + count +
                ", path='" + path + '\'' +
                '}';
    }
}
