package com.mctlab.salesd;

import android.content.ContentResolver;
import android.net.Uri;
import android.net.Uri.Builder;

import com.mctlab.ansight.common.AsApplication;
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
        loadCustomerPositions();
        // TODO: JsonMapper.registerDeserializer(Something.class, new Something.Deserializer());
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

    public void loadCustomerPositions() {
        ContentResolver resolver = getContentResolver();
        Builder builder = TasksProvider.POSITIONS_CONTENT_URI.buildUpon();
        Uri uri = builder.appendPath("positions.xml").build();
        resolver.insert(uri, null);
    }
}
