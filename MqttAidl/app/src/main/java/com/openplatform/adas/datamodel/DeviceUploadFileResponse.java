package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/6/03 10:21
 * Description :
 */
public class DeviceUploadFileResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "{ url= "+url+" }";
        }
    }

    @Override
    public String toString() {
        return "DeviceUploadFileResponse[ flag= "+flag
                +", code= "+code
                +", message= "+message
                +", data= "+data.toString()+"  ]";
    }
}
