package com.openplatform.adas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.SparseArray;

import com.openplatform.adas.util.Assert;

import java.util.Locale;
import java.util.Stack;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/11/2 10:21
 * Description :
 */
public class DatabaseWrapper {
    private static final String TAG = "DatabaseWrapper";

    private final SQLiteDatabase mDatabase;
    private final Context mContext;

    private static final int sTimingThreshold = 50;        // in milliseconds

    private final SparseArray<SQLiteStatement> mCompiledStatements;

    static class TransactionData {
        long time;
        boolean transactionSuccessful;
    }

    // track transaction on a per thread basis
    private static ThreadLocal<Stack<TransactionData>> sTransactionDepth =
            new ThreadLocal<Stack<TransactionData>>() {
                @Override
                public Stack<TransactionData> initialValue() {
                    return new Stack<TransactionData>();
                }
            };


    private static String[] sFormatStrings = new String[] {
            "took %d ms to %s",
            "   took %d ms to %s",
            "      took %d ms to %s",
    };

    DatabaseWrapper(final Context context, final SQLiteDatabase db) {
        mDatabase = db;
        mContext = context;
        mCompiledStatements = new SparseArray<SQLiteStatement>();
    }

    public SQLiteStatement getStatementInTransaction(final int index, final String statement) {
        // Use transaction to serialize access to statements
        Assert.isTrue(mDatabase.inTransaction());
        SQLiteStatement compiled = mCompiledStatements.get(index);
        if (compiled == null) {
            compiled = mDatabase.compileStatement(statement);
            Assert.isTrue(compiled.toString().contains(statement.trim()));
            mCompiledStatements.put(index, compiled);
        }
        return compiled;
    }

    private static void printTiming(final long t1, final String msg) {
        final int transactionDepth = sTransactionDepth.get().size();
        final long t2 = System.currentTimeMillis();
        final long delta = t2 - t1;
        if (delta > sTimingThreshold) {
            Log.d(TAG, String.format(Locale.US,
                    sFormatStrings[Math.min(sFormatStrings.length - 1, transactionDepth)],
                    delta,
                    msg));
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void beginTransaction() {
        final long t1 = System.currentTimeMillis();

        // push the current time onto the transaction stack
        final TransactionData f = new TransactionData();
        f.time = t1;
        sTransactionDepth.get().push(f);

        mDatabase.beginTransaction();
    }

    public void setTransactionSuccessful() {
        final TransactionData f = sTransactionDepth.get().peek();
        f.transactionSuccessful = true;
        mDatabase.setTransactionSuccessful();
    }

    public void endTransaction() {
        long t1 = 0;
        long transactionStartTime = 0;
        final TransactionData f = sTransactionDepth.get().pop();
        if (f.transactionSuccessful == false) {
            Log.w(TAG, "endTransaction without setting successful");
            for (final StackTraceElement st : (new Exception()).getStackTrace()) {
                Log.w(TAG, "    " + st.toString());
            }
        }

        transactionStartTime = f.time;
        t1 = System.currentTimeMillis();

        try {
            mDatabase.endTransaction();
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to endTransaction", ex);
        }

        printTiming(t1, String.format(Locale.US,
                ">>> endTransaction (total for this transaction: %d)",
                (System.currentTimeMillis() - transactionStartTime)));

    }

    public void yieldTransaction() {
        long yieldStartTime = 0;
        yieldStartTime = System.currentTimeMillis();

        final boolean wasYielded = mDatabase.yieldIfContendedSafely();
        if (wasYielded) {
            printTiming(yieldStartTime, "yieldTransaction");
        }
    }

    public void insertWithOnConflict(final String searchTable, final String nullColumnHack,
                                     final ContentValues initialValues, final int conflictAlgorithm) {
        long t1 = 0;
        t1 = System.currentTimeMillis();

        try {
            mDatabase.insertWithOnConflict(searchTable, nullColumnHack, initialValues,
                    conflictAlgorithm);
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to insertWithOnConflict", ex);
        }

        printTiming(t1, String.format(Locale.US,
                "insertWithOnConflict with ", searchTable));

    }

    public Cursor query(final String searchTable, final String[] projection,
                        final String selection, final String[] selectionArgs, final String groupBy,
                        final String having, final String orderBy, final String limit) {
        long t1 = 0;
        t1 = System.currentTimeMillis();
        final Cursor cursor = mDatabase.query(searchTable, projection, selection, selectionArgs,
                groupBy, having, orderBy, limit);

        printTiming(
                t1,
                String.format(Locale.US, "query %s with %s ==> %d",
                        searchTable, selection, cursor.getCount()));

        return cursor;
    }

    public Cursor query(final String searchTable, final String[] columns,
                        final String selection, final String[] selectionArgs, final String groupBy,
                        final String having, final String orderBy) {
        return query(
                searchTable, columns, selection, selectionArgs,
                groupBy, having, orderBy, null);
    }

    public Cursor query(final SQLiteQueryBuilder qb,
                        final String[] projection, final String selection, final String[] queryArgs,
                        final String groupBy, final String having, final String sortOrder, final String limit) {
        long t1 = 0;
        t1 = System.currentTimeMillis();
        final Cursor cursor = qb.query(mDatabase, projection, selection, queryArgs, groupBy,
                having, sortOrder, limit);

        printTiming(
                t1,
                String.format(Locale.US, "query %s with %s ==> %d",
                        qb.getTables(), selection, cursor.getCount()));

        return cursor;
    }

    public long queryNumEntries(final String table, final String selection,
                                final String[] selectionArgs) {
        long t1 = 0;
        t1 = System.currentTimeMillis();
        final long retval =
                DatabaseUtils.queryNumEntries(mDatabase, table, selection, selectionArgs);

        printTiming(
                t1,
                String.format(Locale.US, "queryNumEntries %s with %s ==> %d", table,
                        selection, retval));

        return retval;
    }

    public Cursor rawQuery(final String sql, final String[] args) {
        long t1 = 0;
        t1 = System.currentTimeMillis();

        final Cursor cursor = mDatabase.rawQuery(sql, args);
        printTiming(
                t1,
                String.format(Locale.US, "rawQuery %s ==> %d", sql, cursor.getCount()));

        return cursor;
    }

    public int update(final String table, final ContentValues values,
                      final String selection, final String[] selectionArgs) {
        long t1 = 0;
        t1 = System.currentTimeMillis();
        int count = 0;
        try {
            count = mDatabase.update(table, values, selection, selectionArgs);
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to update", ex);

        }

        printTiming(t1, String.format(Locale.US, "update %s with %s ==> %d",
                table, selection, count));

        return count;
    }

    public int delete(final String table, final String whereClause, final String[] whereArgs) {
        long t1 = 0;
        t1 = System.currentTimeMillis();
        int count = 0;
        try {
            count = mDatabase.delete(table, whereClause, whereArgs);
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to delete", ex);

        }

        printTiming(t1,
                String.format(Locale.US, "delete from %s with %s ==> %d", table,
                        whereClause, count));

        return count;
    }

    public long insert(final String table, final String nullColumnHack,
                       final ContentValues values) {
        long t1 = 0;
        t1 = System.currentTimeMillis();
        long rowId = -1;
        try {
            rowId = mDatabase.insert(table, nullColumnHack, values);
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to insert", ex);

        }
        printTiming(t1, String.format(Locale.US, "insert to %s", table));

        return rowId;
    }

    public long replace(final String table, final String nullColumnHack,
                        final ContentValues values) {
        long t1 = 0;
        t1 = System.currentTimeMillis();
        long rowId = -1;
        try {
            rowId = mDatabase.replace(table, nullColumnHack, values);
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to replace", ex);

        }

        printTiming(t1, String.format(Locale.US, "replace to %s", table));

        return rowId;
    }

    public void execSQL(final String sql, final String[] bindArgs) {
        long t1 = 0;
        t1 = System.currentTimeMillis();

        try {
            mDatabase.execSQL(sql, bindArgs);
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to execSQL", ex);
        }

        printTiming(t1, String.format(Locale.US, "execSQL %s", sql));
    }

    public void execSQL(final String sql) {
        long t1 = 0;
        t1 = System.currentTimeMillis();

        try {
            mDatabase.execSQL(sql);
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to execSQL", ex);
        }

        printTiming(t1, String.format(Locale.US, "execSQL %s", sql));
    }

    public SQLiteStatement compileStatement(final String sql){
        final SQLiteStatement statement = mDatabase.compileStatement(sql);
        return statement;
    }

    public int execSQLUpdateDelete(SQLiteStatement statement) {
        long t1 = 0;
        t1 = System.currentTimeMillis();

        int rowsUpdated = 0;
        try {
            rowsUpdated = statement.executeUpdateDelete();
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to execSQLUpdateDelete", ex);
        }
        printTiming(t1, String.format(Locale.US, "execSQLUpdateDelete %s", statement.toString()));

        return rowsUpdated;
    }

    public long execSQLInsert(SQLiteStatement statement) {
        long t1 = 0;
        t1 = System.currentTimeMillis();

        long rowsInsert = 0;
        try {
            rowsInsert = statement.executeInsert();
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to execSQLInsert", ex);
        }
        printTiming(t1, String.format(Locale.US, "execSQLInsert %s", statement.toString()));

        return rowsInsert;
    }

    public int execSQLUpdateDelete(final String sql) {
        long t1 = 0;
        t1 = System.currentTimeMillis();

        final SQLiteStatement statement = mDatabase.compileStatement(sql);
        int rowsUpdated = 0;
        try {
            rowsUpdated = statement.executeUpdateDelete();
        } catch (SQLiteFullException ex) {
            Log.e(TAG, "Database full, unable to execSQLUpdateDelete", ex);
        }
        printTiming(t1, String.format(Locale.US, "execSQLUpdateDelete %s", sql));

        return rowsUpdated;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

}
