package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/17 10:21
 * Description :
 */
public class BaseResponse extends GsonObject {
    protected boolean flag;
    protected int code;
    protected String message;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return "BaseResponse[ flag= "+flag
                +", code= "+code
                +", message= "+message
                +"  ]";

    }
}
