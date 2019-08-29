package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/6/03 10:21
 * Description :
 */
public class PutUpFileInfo {
    private String deviceCode;
    private String connectorType;
    private Data[] fileList;

    public PutUpFileInfo(){

    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public Data[] getFileList() {
        return fileList;
    }

    public void setFileList(Data[] fileList) {
        this.fileList = fileList;
    }

    public static class Data{
        private String commandId;
        private String fileUrl;
        private String fileName;
        private int channelNo;
        private String fileType;

        public String getCommandId() {
            return commandId;
        }

        public void setCommandId(String commandId) {
            this.commandId = commandId;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getChannelNo() {
            return channelNo;
        }

        public void setChannelNo(int channelNo) {
            this.channelNo = channelNo;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public Data(){

        }

        @Override
        public String toString() {
            return "{ commandId= "+commandId
                    +", fileUrl= "+fileUrl
                    +", fileName= "+fileName
                    +", channelNo= "+channelNo
                    +", fileType= "+fileType
                    +" } ";
        }
    }

    @Override
    public String toString() {
        String dataStr = null;
        if(fileList != null && fileList.length >0){
            for(int i=0;i<fileList.length;i++){
                dataStr += fileList[i].toString();
            }
        }

        return "PutUpFileInfo[ deviceCode= "+deviceCode
                +", connectorType= "+connectorType+", fileList=["+dataStr+"]  ]";
    }
}
