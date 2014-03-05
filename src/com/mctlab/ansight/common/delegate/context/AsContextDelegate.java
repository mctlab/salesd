package com.mctlab.ansight.common.delegate.context;

import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;

import com.mctlab.ansight.common.AsApplication;
import com.mctlab.ansight.common.activity.AsActivity;
import com.mctlab.ansight.common.broadcast.BroadcastConfig;
import com.mctlab.ansight.common.util.L;

public abstract class AsContextDelegate {

    public void onCreate(Bundle savedInstanceState) {
        if (!getBroadcastConfig().isEmpty()
                && getBroadcastConfig().getRegisterMode() == BroadcastConfig.REGISTER_MODE_CREATE) {
            getBroadcastConfig().register(LocalBroadcastManager.getInstance(getActivity()));
        }
    }

    public void onStart() {}

    public void onResume() {
        if (!getBroadcastConfig().isEmpty()
                && getBroadcastConfig().getRegisterMode() == BroadcastConfig.REGISTER_MODE_RESUME) {
            getBroadcastConfig().register(LocalBroadcastManager.getInstance(getActivity()));
        }
    }

    public void onPause() {
        if (!getBroadcastConfig().isEmpty()
                && getBroadcastConfig().getUnregisterMode() == BroadcastConfig.UNREGISTER_MODE_PAUSE) {
            getBroadcastConfig().unregister(LocalBroadcastManager.getInstance(getActivity()));
            mBroadcastConfig = null;
        }
    }

    public void onStop() {}

    public void onDestroy() {
        if (!getBroadcastConfig().isEmpty()
                && getBroadcastConfig().getUnregisterMode() == BroadcastConfig.UNREGISTER_MODE_DESTROY) {
            getBroadcastConfig().unregister(LocalBroadcastManager.getInstance(getActivity()));
            mBroadcastConfig = null;
        }
    }

    private BroadcastConfig mBroadcastConfig;

    private BroadcastConfig getBroadcastConfig() {
        if (mBroadcastConfig == null) {
            mBroadcastConfig = getDelegatingComponent().onCreateBroadcastConfig();
        }
        return mBroadcastConfig;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    /**
     * cancel all http request managed by context().
     */
    public void cancelRequests() {
        L.d(this, "cancelContextHttpRequests");
        getActivity().getRequestManager().cancelAll();
    }

    protected AsApplication getApp() {
        return AsApplication.getInstance();
    }

    public abstract AsActivity getActivity();

    /**
     * return the Activity or Fragment
     */
    protected abstract IDelegatable getDelegatingComponent();

    public abstract boolean isActivityDestroyed();

}
