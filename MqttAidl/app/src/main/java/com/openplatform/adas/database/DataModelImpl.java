package com.openplatform.adas.database;

import android.content.Context;

import com.openplatform.adas.util.Assert;
import com.openplatform.adas.util.Assert.RunsOnMainThread;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/11/2 10:21
 * Description :
 */
public class DataModelImpl extends DataModel {
    private final Context mContext;
    private final DBHelper mDatabaseHelper;

    public DataModelImpl(final Context context) {
        super();
        mContext = context;
        mDatabaseHelper = DBHelper.getInstance(context);
    }

    @Override
    public DatabaseWrapper getDatabase() {
        return mDatabaseHelper.getDatabase();
    }

    @Override
    @RunsOnMainThread
    public  void onApplicationCreated(Context context){
        Assert.isMainThread();
    }
}
