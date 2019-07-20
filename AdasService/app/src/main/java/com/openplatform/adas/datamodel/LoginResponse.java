package com.openplatform.adas.datamodel;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/17 10:21
 * Description :
 */
public class LoginResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "{ token= "+token+" }";
        }
    }

    @Override
    public String toString() {
        return "LoginResponse[ flag= "+flag
                +", code= "+code
                +", message= "+message
                +", data= "+data.toString()+"  ]";
    }
}
