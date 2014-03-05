package com.mctlab.ansight.common.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.mctlab.ansight.common.AsApplication;
import com.mctlab.ansight.common.AsRuntime;
import com.mctlab.ansight.common.annotation.Injector;
import com.mctlab.ansight.common.broadcast.BroadcastConfig;
import com.mctlab.ansight.common.broadcast.BroadcastConfig.BroadcastCallback;
import com.mctlab.ansight.common.constant.AsBroadcastConst;
import com.mctlab.ansight.common.delegate.context.AsActivityDelegate;
import com.mctlab.ansight.common.delegate.context.IDelegatable;
import com.mctlab.ansight.common.network.http.RequestManager;

public abstract class AsActivity extends Activity implements IDelegatable, BroadcastCallback {

    protected AsActivityDelegate<?> mContextDelegate;
    protected RequestManager requestManager = new RequestManager();

    private boolean paused = false;
    private boolean stopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        mContextDelegate = onCreateActivityDelegate();
        mContextDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopped = false;
        mContextDelegate.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsRuntime.getInstance().setCurrentActivity(this);
        mContextDelegate.onResume();
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContextDelegate.onPause();
        paused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopped = true;
        mContextDelegate.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContextDelegate.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mContextDelegate.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mContextDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStopped() {
        return stopped;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    protected AsApplication getApp() {
        return AsApplication.getInstance();
    }

    @Override
    public BroadcastConfig onCreateBroadcastConfig() {
        return new BroadcastConfig()
                .addConfig(AsBroadcastConst.KILL_ACTIVITY, this);
    }

    public AsActivityDelegate<?> getContextDelegate() {
        return mContextDelegate;
    }

    protected abstract AsActivityDelegate<?> onCreateActivityDelegate();

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        Injector.inject(this, this);
    }

    protected abstract int getLayoutId();

}
