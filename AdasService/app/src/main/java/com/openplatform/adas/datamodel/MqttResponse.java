package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/5/29 10:21
 * Description :
 */
public class MqttResponse {
    private String deviceId; //设备ID
    private String cmdSNO; //消息流水
    private String command; //指令
    private Object response; //响应结果（指令执行状态）
    private int state; //状态(0>指令已下发，1>指令已接收，2>指令已执行)

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

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString(){
        return "MqttResponse[ deviceId= "+deviceId
                +", cmdSNO= "+cmdSNO
                +", command= "+command
                +", response= "+response
                +", state= "+state
                +"  ]";
    }


    public static class Response{
        private boolean flag;
        private String message;
        private Object data;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }


    }
}
