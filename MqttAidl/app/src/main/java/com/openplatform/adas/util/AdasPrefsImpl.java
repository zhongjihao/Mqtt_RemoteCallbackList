package com.openplatform.adas.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/12/17 10:21
 * Description :
 */
public abstract class AdasPrefsImpl extends AdasPrefs {
    private final Context mContext;

    public AdasPrefsImpl(final Context context) {
        mContext = context;
    }

    protected void validateKey(String key) {
    }

    @Override
    public int getInt(final String key, final int defaultValue) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }

    @Override
    public long getLong(final String key, final long defaultValue) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        return prefs.getLong(key, defaultValue);
    }

    @Override
    public float getFloat(final String key, final float defaultValue){
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        return prefs.getFloat(key, defaultValue);
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultValue);
    }

    @Override
    public String getString(final String key, final String defaultValue) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    @Override
    public byte[] getBytes(String key) {
        final String byteValue = getString(key, null);
        return byteValue == null ? null : Base64.decode(byteValue, Base64.DEFAULT);
    }

    @Override
    public void putInt(final String key, final int value) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    @Override
    public void putLong(final String key, final long value) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    @Override
    public void putFloat(final String key, final float value){
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    @Override
    public void putBoolean(final String key, final boolean value) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    @Override
    public void putString(final String key, final String value) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void putBytes(String key, byte[] value) {
        final String encodedBytes = Base64.encodeToString(value, Base64.DEFAULT);
        putString(key, encodedBytes);
    }

    @Override
    public void remove(final String key) {
        validateKey(key);
        final SharedPreferences prefs = mContext.getSharedPreferences(
                getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
