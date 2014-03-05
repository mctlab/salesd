package com.mctlab.ansight.common.broadcast;

import android.content.Intent;
import android.os.Bundle;

import com.mctlab.ansight.common.constant.AsArgumentConst;
import com.mctlab.ansight.common.constant.AsBroadcastConst;

public class BroadcastIntent implements AsBroadcastConst {

    protected Intent wrappedIntent;

    public BroadcastIntent(String action) {
        wrappedIntent = new Intent(action);
    }

    public BroadcastIntent(Intent intent) {
        wrappedIntent = intent;
    }

    public Intent getWrappedIntent() {
        return wrappedIntent;
    }

    public void putArguments(Bundle args) {
        wrappedIntent.putExtra(AsArgumentConst.ARGS, args);
    }

    public Bundle getArguments() {
        return wrappedIntent.getBundleExtra(AsArgumentConst.ARGS);
    }
}
