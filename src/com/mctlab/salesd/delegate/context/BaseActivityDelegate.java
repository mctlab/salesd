package com.mctlab.salesd.delegate.context;

import com.mctlab.ansight.common.delegate.context.AsActivityDelegate;
import com.mctlab.salesd.activity.base.BaseActivity;

public class BaseActivityDelegate<T extends BaseActivity> extends AsActivityDelegate<T> {

    public BaseActivityDelegate(T activity) {
        super(activity);
    }

}
