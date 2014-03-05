package com.mctlab.salesd.activity.base;

import android.content.Intent;

import com.mctlab.ansight.common.activity.AsActivity;
import com.mctlab.salesd.ThisApplication;
import com.mctlab.salesd.delegate.context.BaseActivityDelegate;

public abstract class BaseActivity extends AsActivity {

    @Override
    protected ThisApplication getApp() {
        return ThisApplication.getInstance();
    }

    @Override
    protected BaseActivityDelegate onCreateActivityDelegate() {
        return new BaseActivityDelegate<BaseActivity>(this);
    }

    @Override
    public void onBroadcast(Intent intent) {}

    protected BaseActivity getActivity() {
        return BaseActivity.this;
    }

}
