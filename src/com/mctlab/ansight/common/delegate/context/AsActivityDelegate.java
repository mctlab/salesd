package com.mctlab.ansight.common.delegate.context;

import com.mctlab.ansight.common.activity.AsActivity;
import com.mctlab.ansight.common.annotation.TaskMovedOnBack;

public class AsActivityDelegate<T extends AsActivity> extends AsContextDelegate {

    private T activity;
    private volatile boolean isDestroyed = false;

    public AsActivityDelegate(T activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelRequests();
        isDestroyed = true;
    }

    /**
     * @return true if the back event has been consumed
     */
    public boolean onBackPressed() {
        if (activity.getClass().isAnnotationPresent(TaskMovedOnBack.class)) {
            activity.moveTaskToBack(true);
            return true;
        }
        return false;
    }

    @Override
    public AsActivity getActivity() {
        return activity;
    }

    @Override
    protected IDelegatable getDelegatingComponent() {
        return activity;
    }

    @Override
    public boolean isActivityDestroyed() {
        return isDestroyed;
    }

}
