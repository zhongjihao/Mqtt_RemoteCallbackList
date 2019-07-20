package com.openplatform.adas.datamodel;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/5/29 10:21
 * Description :
 */
public class MqttCmdMsg {
    private String deviceId; //设备ID
    private String cmdSNO; //消息流水
    private String command; //指令
    private Object data;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCmdSNO() {
        return cmdSNO;
    }

    public void setCmdSNO(String cmdSNO) {
        this.cmdSNO = cmdSNO;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return "MqttCmdMsg[ deviceId= "+deviceId
                +", cmdSNO= "+cmdSNO
                +", command= "+command
                +", data= "+data
                +"  ]";
    }
}
