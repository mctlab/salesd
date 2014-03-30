package com.mctlab.salesd;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mctlab.ansight.common.AsAppConfig;

public class AppConfig extends AsAppConfig {

    private static final String PREF_NAME = "configuration";

    private static final String KEY_POSITIONS_VERSION_SUFFIX = "_positions_version";

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;

    public static void init(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        if (me == null) {
            synchronized (AsAppConfig.class) {
                if (me == null) {
                    me = new AppConfig();
                }
            }
        }
    }

    public static AppConfig getInstance() {
        return (AppConfig) AsAppConfig.getInstance();
    }

    public static void setCustomerPositionsVersion(String customer, int version) {
        if (!TextUtils.isEmpty(customer)) {
            mEditor.putInt(customer + KEY_POSITIONS_VERSION_SUFFIX, version).commit();
        }
    }

    public static int getCustomerPositionsVersion(String customer) {
        if (!TextUtils.isEmpty(customer)) {
            return mPreferences.getInt(customer + KEY_POSITIONS_VERSION_SUFFIX, -1);
        }
        return -1;
    }
}

