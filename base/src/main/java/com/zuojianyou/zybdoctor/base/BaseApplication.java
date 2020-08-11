package com.zuojianyou.zybdoctor.base;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

/**
 * @author: weiwei
 * @date: 2020/8/7
 * @description:
 */
public class BaseApplication extends MultiDexApplication {
    /**
     * 系统上下文
     */
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
    }

    /**
     * 获取系统上下文
     */
    public static Context getAppContext() {
        return mAppContext;
    }
}
