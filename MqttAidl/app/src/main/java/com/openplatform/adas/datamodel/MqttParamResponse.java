package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/9/12 10:21
 * Description :
 */
public class MqttParamResponse {
    private String deviceId; //设备ID
    private String cmdSNO; //消息流水
    private String command; //指令
    private Response response; //响应结果（指令执行状态）
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

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
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
        return "MqttParamResponse[ deviceId= "+deviceId
                +", cmdSNO= "+cmdSNO
                +", command= "+command
                +", response= "+response
                +", state= "+state
                +"  ]";
    }

    public static class Response{
        private boolean flag;
        private String message;
        private Data data;

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

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        @Override
        public String toString(){
            return "{ flag= "+flag
                    +", message= "+message
                    +", data= "+data.toString()
                    +"  }";
        }
    }

    public static class Data{
        private String[] results;

        public String[] getResults() {
            return results;
        }

        public void setResults(String[] results) {
            this.results = results;
        }

        @Override
        public String toString() {
            String ret = "";
            if(results!=null && results.length>0){
                for(int i=0;i<results.length;i++){
                    ret += results[i]+" ,";
                }
            }
            return "{ results= "+ret+" }";
        }

    }
}
