package com.zuojianyou.zybdoctor.application;

import android.app.Application;
import android.content.Context;

import com.zuojianyou.zybdoctor.BuildConfig;

import org.xutils.x;

public class MyApplication extends Application {

    private static MyApplication myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    public static Context getAppContext() {
        return myApp;
    }
}
