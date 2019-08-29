package com.openplatform.adas.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.openplatform.adas.util.Assert;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/11/2 10:21
 * Description :数据库升级或降级
 */
public class DatabaseUpgradeHelper {
    private static final String TAG = "DatabaseUpgradeHelper";

    public void doOnUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Assert.isTrue(newVersion >= oldVersion);
        if (oldVersion == newVersion) {
            return;
        }
        Log.d(TAG, "Database upgrade started from version " + oldVersion + " to " + newVersion);





    }

    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.e(TAG, "Database downgrade requested for version " +
                oldVersion + " version " + newVersion + ", forcing db rebuild!");
    }
}
