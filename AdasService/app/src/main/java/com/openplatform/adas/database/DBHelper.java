package com.openplatform.adas.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/11/7 10:21
 * Description : Allows access to the SQL database.  This is package private.
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";
	private static final String DBASE_NAME = "openplatform.db";
	
	private static final int DBASE_VERSION = 1;
	//表名
	public static final String COMMAND_TABLE = "tb_command";

	private static final Object sLock = new Object();
	private final Context mApplicationContext;
	private static DBHelper sHelperInstance;      // Protected by sLock.

	private final Object mDatabaseWrapperLock = new Object();
	private DatabaseWrapper mDatabaseWrapper;           // Protected by mDatabaseWrapperLock.
	private final DatabaseUpgradeHelper mUpgradeHelper = new DatabaseUpgradeHelper();

	private DBHelper(Context context, String name, CursorFactory factory,
                     int version) {
		super(context, name, factory, version);
		mApplicationContext = context;
	}

	private DBHelper(Context context, String name, CursorFactory factory,
                     int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		mApplicationContext = context;
	}
	
	public DBHelper(Context context) {
        super(context, DBASE_NAME, null, DBASE_VERSION);
		mApplicationContext = context;
    }

	/**
	 * Get a (singleton) instance of {@link DBHelper}, creating one if there isn't one yet.
	 * This is the only public method for getting a new instance of the class.
	 * @param context Should be the application context (or something that will live for the
	 * lifetime of the application).
	 * @return The current (or a new) DatabaseHelper instance.
	 */
	public static DBHelper getInstance(final Context context) {
		synchronized (sLock) {
			if (sHelperInstance == null) {
				sHelperInstance = new DBHelper(context);
			}
			return sHelperInstance;
		}
	}

	/**
	 * Get the (singleton) instance of @{link DatabaseWrapper}.
	 * <p>The database is always opened as a writeable database.
	 * @return The current (or a new) DatabaseWrapper instance.
	 */
	public DatabaseWrapper getDatabase() {
		synchronized (mDatabaseWrapperLock) {
			if (mDatabaseWrapper == null) {
				mDatabaseWrapper = new DatabaseWrapper(mApplicationContext, getWritableDatabase());
			}
			return mDatabaseWrapper;
		}
	}

    //数据库第一次被创建时onCreate会被调用  
    @Override
    public void onCreate(SQLiteDatabase db) {
		CmdTableColumns.createCmdTable(db);
    }  
  
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade, db.version:" + db.getVersion() + ", oldVersion:" + oldVersion + ", newVersion:" + newVersion);
		mUpgradeHelper.doOnUpgrade(db, oldVersion, newVersion);
    }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		mUpgradeHelper.onDowngrade(db, oldVersion, newVersion);
    }
    
    public static boolean isColumnExists(SQLiteDatabase db, String table, String column) {
        Cursor cursor = db.rawQuery("PRAGMA table_info("+ table +")", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if (column.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

}
