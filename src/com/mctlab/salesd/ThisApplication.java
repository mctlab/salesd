package com.mctlab.salesd;

import com.mctlab.ansight.common.AsApplication;

public class ThisApplication extends AsApplication {

    public static ThisApplication getInstance() {
        return (ThisApplication) AsApplication.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: JsonMapper.registerDeserializer(Something.class, new Something.Deserializer());
    }

    @Override
    public void initAppConfig() {
        AppConfig.init();
    }

    @Override
    public void initRuntime() {
        Runtime.init();
    }

}
