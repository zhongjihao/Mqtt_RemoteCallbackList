package com.openplatform.adas;

import android.content.Context;

import com.openplatform.adas.network.IHttpEngine;
import com.openplatform.adas.util.AdasPrefs;
import com.openplatform.adas.util.Assert;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/16 10:21
 * Description :
 */
public abstract class Factory {
    private static volatile Factory sInstance;
    protected static boolean sRegistered = false;
    protected static boolean sInitialized = false;

    public static Factory get() {
        return sInstance;
    }

    protected static void setInstance(final Factory factory) {
        // Not allowed to call this after real application initialization is complete
        Assert.isTrue(!sRegistered);
        Assert.isTrue(!sInitialized);

        sInstance = factory;
    }

    public abstract Context getApplicationContext();
    public abstract void onRequiredInit();
    public abstract IHttpEngine getHttpEngine();
    public abstract AdasPrefs getApplicationPrefs();
}
