package com.baway.test.liuhaifeng20171024;

import android.app.Application;

/**
 * 刘海峰.13:34
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

}
