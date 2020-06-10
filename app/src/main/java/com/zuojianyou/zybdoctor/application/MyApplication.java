package com.zuojianyou.zybdoctor.application;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.zuojianyou.zybdoctor.BuildConfig;

import org.xutils.x;

public class MyApplication extends MultiDexApplication {

    private static MyApplication myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        // 初始化友盟SDK
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public static Context getAppContext() {
        return myApp;
    }
}
