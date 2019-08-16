package com.openplatform.adas.database;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/8/6 10:21
 * Description :
 */
public class CmdTableColumns implements BaseColumns {
	public static final String DEVICEID = "deviceId";
	public static final String COMMAND = "command";
	public static final String CMDSNO = "cmdSNO";
	public static final String STATUS = "status";
	public static final String COUNT = "count";
	public static final String PATH = "path";

	public static void createCmdTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBHelper.COMMAND_TABLE + " ("
				+ CmdTableColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ CmdTableColumns.DEVICEID + " TEXT, "
				+ CmdTableColumns.COMMAND + " TEXT, "
				+ CmdTableColumns.CMDSNO + " TEXT, "
				+ CmdTableColumns.STATUS + " INTEGER, "
				+ CmdTableColumns.COUNT + " INTEGER, "
				+ CmdTableColumns.PATH + " TEXT"
				+ " )");
	}
}
