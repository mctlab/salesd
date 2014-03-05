package com.mctlab.ansight.common.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Map;

public class BroadcastConfig {

    public static final int REGISTER_MODE_CREATE = 1;
    public static final int REGISTER_MODE_RESUME = REGISTER_MODE_CREATE + 1;
    public static final int UNREGISTER_MODE_DESTROY = REGISTER_MODE_RESUME + 1;
    public static final int UNREGISTER_MODE_PAUSE = UNREGISTER_MODE_DESTROY + 1;

    // which lift cycle to register or unregister broadcast. OnCreate, OnResume, OnPause or onDestroy
    private int registerMode = REGISTER_MODE_CREATE;
    private int unregisterMode = UNREGISTER_MODE_DESTROY;

    private volatile boolean isRegistered = false;

    private IntentFilter filter;
    private BroadcastReceiver receiver;

    private Map<String, ConfigItem> configs;

    public BroadcastConfig() {
        configs = new HashMap<String, ConfigItem>();
    }

    public void register(LocalBroadcastManager broadcastManager) {
        if (isRegistered) {
            return;
        }
        isRegistered = true;
        filter = new IntentFilter();
        for (ConfigItem config : configs.values()) {
            filter.addAction(config.action);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for (ConfigItem config : configs.values()) {
                    if (config.match(intent)) {
                        config.performCallback(intent);
                    }
                }
            }
        };
        broadcastManager.registerReceiver(receiver, filter);
    }

    public void unregister(LocalBroadcastManager broadcastManager) {
        if (!isRegistered) {
            return;
        }
        isRegistered = false;
        broadcastManager.unregisterReceiver(receiver);
        configs.clear();
        receiver = null;
        filter = null;
    }

    public BroadcastConfig addConfig(String intentAction, BroadcastCallback callback) {
        configs.put(intentAction, new ConfigItem(intentAction, callback));
        return this;
    }

    public BroadcastConfig setMode(int registerMode, int unregisterMode) {
        this.registerMode = registerMode;
        this.unregisterMode = unregisterMode;
        return this;
    }

    public int getRegisterMode() {
        return registerMode;
    }

    public int getUnregisterMode() {
        return unregisterMode;
    }

    public boolean isEmpty() {
        return configs.isEmpty();
    }

    public static interface BroadcastCallback {
        public void onBroadcast(Intent intent);
    }

    private static class ConfigItem {
        private String action;
        private BroadcastCallback callback;

        public ConfigItem(String action, BroadcastCallback callback) {
            this.action = action;
            this.callback = callback;
        }

        public void performCallback(Intent intent) {
            callback.onBroadcast(intent);
        }

        @SuppressWarnings("unused")
        public boolean match(String intentAction) {
            return innerMatch(intentAction);
        }

        public boolean match(Intent intent) {
            return innerMatch(intent.getAction());
        }

        private boolean innerMatch(String intentAction) {
            if (callback instanceof DialogFragment) {
                DialogFragment fragment = (DialogFragment) callback;
                if (fragment.getDialog() == null) {
                    return false;
                }
            } else if (callback instanceof Fragment) {
                Fragment fragment = (Fragment) callback;
                if (fragment.getView() == null) {
                    return false;
                }
            }
            return intentAction.equals(action);
        }
    }
}
