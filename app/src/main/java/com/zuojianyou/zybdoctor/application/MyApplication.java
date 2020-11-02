package com.zuojianyou.zybdoctor.application;

//import com.bjgjdsj.zyb.voip.core.VoipEvent;
//import com.dds.skywebrtc.SkyEngineKit;
import com.qiniu.droid.rtc.QNRTCEnv;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.base.BaseApplication;

import org.xutils.x;

public class MyApplication extends BaseApplication {

    private static MyApplication myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;

//        SkyEngineKit.init(new VoipEvent());

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        // 初始化友盟SDK
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        //七牛
        QNRTCEnv.init(getApplicationContext());
    }
}
