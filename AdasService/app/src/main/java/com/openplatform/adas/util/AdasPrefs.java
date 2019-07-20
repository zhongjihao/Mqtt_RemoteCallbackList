package com.openplatform.adas.util;

import com.openplatform.adas.Factory;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2018/12/17 10:21
 * Description :
 */
public abstract class AdasPrefs {
    public abstract String getSharedPreferencesName();

    public abstract void onUpgrade(final int oldVersion, final int newVersion);

    public static AdasPrefs getApplicationPrefs() {
        return Factory.get().getApplicationPrefs();
    }

    public abstract int getInt(final String key, final int defaultValue);
    public abstract long getLong(final String key, final long defaultValue);
    public abstract float getFloat(final String key, final float defaultValue);
    public abstract boolean getBoolean(final String key, final boolean defaultValue);
    public abstract String getString(final String key, final String defaultValue);
    public abstract byte[] getBytes(final String key);

    public abstract void putInt(final String key, final int value);
    public abstract void putLong(final String key, final long value);
    public abstract void putFloat(final String key, final float value);
    public abstract void putBoolean(final String key, final boolean value);
    public abstract void putString(final String key, final String value);
    public abstract void putBytes(final String key, final byte[] value);

    public abstract void remove(String key);
}
