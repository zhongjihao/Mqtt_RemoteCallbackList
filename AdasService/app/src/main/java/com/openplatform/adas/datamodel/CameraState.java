package com.openplatform.adas.datamodel;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/7/4 10:21
 * Description :
 */
public class CameraState {
    private boolean isMount;
    private boolean hasFrame;

    public boolean isMount() {
        return isMount;
    }

    public void setMount(boolean mount) {
        isMount = mount;
    }

    public boolean isHasFrame() {
        return hasFrame;
    }

    public void setHasFrame(boolean hasFrame) {
        this.hasFrame = hasFrame;
    }
}
