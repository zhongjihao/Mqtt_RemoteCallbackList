package com.openplatform.adas.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.openplatform.adas.datamodel.Command;
import com.openplatform.adas.util.Assert;
import com.openplatform.adas.util.Assert.DoesNotRunOnMainThread;

import java.util.ArrayList;
import java.util.List;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/8/5 10:21
 * Description :
 */
public class DBManager{
	private static final String TAG = "DBManager";

	private Context mCxt;
	
	public DBManager(Context context) {
		mCxt = context;
	}

	@DoesNotRunOnMainThread
	public long insertCommand(String deviceId,String command,String cmdSNO,int status){
		Assert.isNotMainThread();
		final DatabaseWrapper db = DataModel.get().getDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(CmdTableColumns.DEVICEID, deviceId);
		contentValues.put(CmdTableColumns.COMMAND, command);
		contentValues.put(CmdTableColumns.CMDSNO, cmdSNO);
		contentValues.put(CmdTableColumns.STATUS, status);
		contentValues.put(CmdTableColumns.COUNT, 0);
		contentValues.put(CmdTableColumns.PATH, "");
		return db.insert(DBHelper.COMMAND_TABLE, null, contentValues);
	}

	@DoesNotRunOnMainThread
	public int updateCmdState(long id,int status){
		Assert.isNotMainThread();
		ContentValues contentValues = new ContentValues();
		contentValues.put(CmdTableColumns.STATUS, status);
		int rowNum = -1;
		final DatabaseWrapper db = DataModel.get().getDatabase();
		rowNum = db.update(DBHelper.COMMAND_TABLE,contentValues,CmdTableColumns._ID+"=?", new String[]{String.valueOf(id)});
		return rowNum;
	}

	@DoesNotRunOnMainThread
	public int updateCmdCount(long id,int count){
		Assert.isNotMainThread();
		ContentValues contentValues = new ContentValues();
		contentValues.put(CmdTableColumns.COUNT, count);
		int rowNum = -1;
		final DatabaseWrapper db = DataModel.get().getDatabase();
		rowNum = db.update(DBHelper.COMMAND_TABLE,contentValues,CmdTableColumns._ID+"=?", new String[]{String.valueOf(id)});
		return rowNum;
	}

	@DoesNotRunOnMainThread
	public int updateUpgradeFile(long id,int status,String path){
		Assert.isNotMainThread();
		ContentValues contentValues = new ContentValues();
		contentValues.put(CmdTableColumns.STATUS, status);
		contentValues.put(CmdTableColumns.PATH, path);
		int rowNum = -1;
		final DatabaseWrapper db = DataModel.get().getDatabase();
		rowNum = db.update(DBHelper.COMMAND_TABLE,contentValues,CmdTableColumns._ID+"=?", new String[]{String.valueOf(id)});
		return rowNum;
	}

	@DoesNotRunOnMainThread
	public void deleteCommand(long id){
		Assert.isNotMainThread();
		final DatabaseWrapper db = DataModel.get().getDatabase();
		db.delete(DBHelper.COMMAND_TABLE, CmdTableColumns._ID+"=?", new String[]{String.valueOf(id)});
	}

	@DoesNotRunOnMainThread
	public Command queryCommand(long id){
		Assert.isNotMainThread();
		String selection = String.format("%s=?", CmdTableColumns._ID);
		String[] selectionArgs = new String[]{ String.valueOf(id)};
		Cursor cursor = null;
		Command item  = null;
		final DatabaseWrapper db = DataModel.get().getDatabase();
		try {
			cursor = db.query(DBHelper.COMMAND_TABLE, null, selection, selectionArgs, null, null, null);
			if (cursor.moveToNext()) {
				item  = new Command();
				item.setId(cursor.getLong(cursor.getColumnIndex(CmdTableColumns._ID)));
				item.setDeviceId(cursor.getString(cursor.getColumnIndex(CmdTableColumns.DEVICEID)));
				item.setCommand(cursor.getString(cursor.getColumnIndex(CmdTableColumns.COMMAND)));
				item.setCmdSNO(cursor.getString(cursor.getColumnIndex(CmdTableColumns.CMDSNO)));
				item.setStatus(cursor.getInt(cursor.getColumnIndex(CmdTableColumns.STATUS)));
				item.setCount(cursor.getInt(cursor.getColumnIndex(CmdTableColumns.COUNT)));
				item.setPath(cursor.getString(cursor.getColumnIndex(CmdTableColumns.PATH)));
			}
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			if(cursor != null)
				cursor.close();
		}

		return item;
	}

	@DoesNotRunOnMainThread
	public List<Command> queryCommand(){
		Assert.isNotMainThread();
		Cursor cursor = null;
		List<Command> list = new ArrayList<>();
		final DatabaseWrapper db = DataModel.get().getDatabase();
		try {
			cursor = db.query(DBHelper.COMMAND_TABLE, null, null, null, null, null, null);
			if (cursor.moveToNext()) {
				Command item  = new Command();
				item.setId(cursor.getLong(cursor.getColumnIndex(CmdTableColumns._ID)));
				item.setDeviceId(cursor.getString(cursor.getColumnIndex(CmdTableColumns.DEVICEID)));
				item.setCommand(cursor.getString(cursor.getColumnIndex(CmdTableColumns.COMMAND)));
				item.setCmdSNO(cursor.getString(cursor.getColumnIndex(CmdTableColumns.CMDSNO)));
				item.setStatus(cursor.getInt(cursor.getColumnIndex(CmdTableColumns.STATUS)));
				item.setCount(cursor.getInt(cursor.getColumnIndex(CmdTableColumns.COUNT)));
				item.setPath(cursor.getString(cursor.getColumnIndex(CmdTableColumns.PATH)));
				list.add(item);
			}
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			if(cursor != null)
				cursor.close();
		}

		return list;
	}
}
