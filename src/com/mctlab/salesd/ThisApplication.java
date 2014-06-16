package com.mctlab.salesd;

import android.content.ContentResolver;
import android.net.Uri;
import android.net.Uri.Builder;

import com.mctlab.ansight.common.AsApplication;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.salesd.data.SyncData;
import com.mctlab.salesd.data.SyncData.Deserializer;
import com.mctlab.salesd.provider.TasksProvider;

public class ThisApplication extends AsApplication {

    public static ThisApplication getInstance() {
        return (ThisApplication) AsApplication.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAppConfig();
        loadConfigCategories();
        JsonMapper.registerDeserializer(SyncData.class, new Deserializer());
    }

    @Override
    public void initAppConfig() {
        AppConfig.init(getApplicationContext());
    }

    @Override
    public void initRuntime() {
        Runtime.init();
    }

    public void loadConfigCategories() {
        ContentResolver resolver = getContentResolver();
        Builder builder = TasksProvider.CONFIG_CATEGORIES_CONTENT_URI.buildUpon();
        Uri uri = builder.appendPath("categories.xml").build();
        resolver.insert(uri, null);
    }
}
