package com.openplatform.adas.database;

import android.content.Context;

import com.openplatform.adas.Factory;
import com.openplatform.adas.util.Assert.RunsOnMainThread;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/11/2 10:21
 * Description :
 */
public abstract class DataModel {

    public static DataModel get() {
        return Factory.get().getDataModel();
    }

    public abstract DatabaseWrapper getDatabase();

    @RunsOnMainThread
    public abstract void onApplicationCreated(Context context);

}
