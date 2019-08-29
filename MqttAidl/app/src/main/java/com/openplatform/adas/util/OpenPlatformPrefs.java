package com.openplatform.adas.util;

import android.content.Context;


/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/29 10:21
 * Description :
 */
public class OpenPlatformPrefs extends AdasPrefsImpl {
    private static final String SHARED_PREFERENCES_NAME = "open_platform_adas";
    private static final int NO_SHARED_PREFERENCES_VERSION = -1;
    private static final String SHARED_PREFERENCES_KEY_PREFIX = "key_open_";

    public OpenPlatformPrefs(Context context) {
        super(context);
    }

    @Override
    public String getSharedPreferencesName() {
        return SHARED_PREFERENCES_NAME;
    }

    @Override
    protected void validateKey(String key) {
        super.validateKey(key);
        Assert.isTrue(key.startsWith(SHARED_PREFERENCES_KEY_PREFIX));
    }

    @Override
    public void onUpgrade(int oldVersion, int newVersion) {

    }
}
