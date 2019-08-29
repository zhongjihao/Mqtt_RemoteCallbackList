package com.openplatform.adas;

import android.content.Context;

import com.openplatform.adas.database.DBManager;
import com.openplatform.adas.database.DataModel;
import com.openplatform.adas.database.DataModelImpl;
import com.openplatform.adas.network.HttpEngine;
import com.openplatform.adas.network.IHttpEngine;
import com.openplatform.adas.util.AdasPrefs;
import com.openplatform.adas.util.Assert;
import com.openplatform.adas.util.OpenPlatformPrefs;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/16 10:21
 * Description :
 */
public class FactoryImpl  extends Factory {
    private Context mApplicationContext;
    private AdasServiceApplication mApplication;
    private IHttpEngine mHttpEngine;
    private AdasPrefs mOpenPlatformPrefs;
    private DataModel mDataModel;
    private DBManager mDbManager;


    private FactoryImpl() {

    }

    public static Factory register(final Context applicationContext,final AdasServiceApplication application) {
        // This only gets called once (from BugleApplication.onCreate), but its not called in tests.
        Assert.isTrue(!sRegistered);
        Assert.isNull(Factory.get());

        final FactoryImpl factory = new FactoryImpl();
        Factory.setInstance(factory);
        sRegistered = true;

        // At this point Factory is published. Services can now get initialized and depend on
        // Factory.get().

        factory.mApplicationContext = applicationContext;
        factory.mApplication = application;
        factory.mHttpEngine = new HttpEngine();
        factory.mOpenPlatformPrefs = new OpenPlatformPrefs(applicationContext);
        factory.mDataModel = new DataModelImpl(applicationContext);
        factory.mDbManager =  new DBManager(factory.mApplicationContext);

        factory.onRequiredInit();

        return factory;
    }

    @Override
    public void onRequiredInit(){
        if (sInitialized) {
            return;
        }
        sInitialized = true;

        mApplication.initializeSync(this);

        final Thread asyncInitialization = new Thread() {
            @Override
            public void run() {
                // Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                mApplication.initializeAsync(FactoryImpl.this);
            }
        };
        asyncInitialization.start();
    }

    @Override
    public  Context getApplicationContext(){
        return mApplicationContext;
    }

    @Override
    public IHttpEngine getHttpEngine(){
        return mHttpEngine;
    }

    @Override
    public AdasPrefs getApplicationPrefs(){
        return mOpenPlatformPrefs;
    }

    @Override
    public  DataModel getDataModel(){
        return mDataModel;
    }

    @Override
    public DBManager getDbManager(){
        return mDbManager;
    }

}
