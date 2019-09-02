package com.openplatform.adas.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/8/30 10:21
 * Description :
 */
public class UpgradeInfoTableColumns implements BaseColumns {
    public static final String APKTYPE = "apkType";
    public static final String DEVICEVERSION = "deviceVersion";
    public static final String FILESIZE = "fileSize";
    public static final String FILEMD5 = "fileMd5";
    public static final String DOWNLOADURL = "downLoadUrl";
    public static final String COMMANDID = "commandId";

    public static void createUpgradeTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBHelper.UPGRADE_TABLE + " ("
                + UpgradeInfoTableColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UpgradeInfoTableColumns.APKTYPE + " TEXT, "
                + UpgradeInfoTableColumns.DEVICEVERSION + " INTEGER, "
                + UpgradeInfoTableColumns.FILESIZE + " INTEGER, "
                + UpgradeInfoTableColumns.FILEMD5 + " VARCHAR(256), "
                + UpgradeInfoTableColumns.DOWNLOADURL + " VARCHAR(256), "
                + UpgradeInfoTableColumns.COMMANDID + " INTEGER"
                + " )");
    }
}
