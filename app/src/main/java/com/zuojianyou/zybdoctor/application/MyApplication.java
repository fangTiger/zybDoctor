package com.zuojianyou.zybdoctor.application;

//import com.bjgjdsj.zyb.voip.core.VoipEvent;
//import com.dds.skywebrtc.SkyEngineKit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.droid.rtc.QNRTCEnv;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.app.Constants;
import com.zuojianyou.zybdoctor.app.ObserverModeListener;
import com.zuojianyou.zybdoctor.base.BaseApplication;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.rtc.RTCEngineKit;
import com.zuojianyou.zybdoctor.rtc.RoomActivity;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyApplication extends BaseApplication {

    private static MyApplication myApp;
    private String deviceToken;

    private SparseArray<List<ObserverModeListener>> observerListenerS = new SparseArray();

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;

//        SkyEngineKit.init(new VoipEvent());

        // 初始化友盟SDK
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "acbb60179cc4c476beb57f18e2b889dc");
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setNotificaitonOnForeground(true);
        mPushAgent.setMessageHandler(new UmengMessageHandler(){
            @Override
            public void handleMessage(Context context, UMessage uMessage) {
                super.handleMessage(context, uMessage);
                Log.e("zyb", "uMessage display_type:" + uMessage.display_type + "-text:" + uMessage.text + "--extra:" + uMessage.extra);
                Map<String, String> extraMap = uMessage.extra;
                if (extraMap != null) {
                    String pushObj = extraMap.get("pushObj");
                    if (!TextUtils.isEmpty(pushObj)) {
                        JSONObject jsonObject = JSONObject.parseObject(pushObj);
                        if ("custom".equals(uMessage.display_type)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", jsonObject.getString("accFlag"));
                            bundle.putString("user_id", jsonObject.getString("userId"));
                            MyApplication.getInstance().notifyObserver(Constants.VIDEO_CALL_MESSAGE_ACTION,bundle);
                        } else {
                            String type = jsonObject.getString("type");
                            String roomId = jsonObject.getString("roomId");
                            String callName = jsonObject.getString("callName");
                            String callImg = jsonObject.getString("callImg");
                            String callId = jsonObject.getString("callId");
                            if (!TextUtils.isEmpty(roomId)) {
                                getQnrtcToken(roomId, callName, callImg, callId, type);
                            }
                        }

                    }
                }
            }
        });

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                MyApplication.this.deviceToken = deviceToken;
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.e("zyb", "注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("zyb", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });

        /**
         * 初始化厂商通道
         */
        //小米通道
        MiPushRegistar.register(this, "2882303761518600426", "5521860074426");
        //华为通道，注意华为通道的初始化参数在minifest中配置
        HuaWeiRegister.register(this);
        //魅族通道
        MeizuRegister.register(this, "135559", "55efa5ae35de44f5973a0ba3d03e39ab");
        //OPPO通道
        OppoRegister.register(this, "4a6afe6a02c54d3e9fd84ac5703b1e2a", "fb651d5ab1cc46e1acd18088754c1d03");
        //VIVO 通道，注意VIVO通道的初始化参数在minifest中配置
        VivoRegister.register(this);

        //七牛
        QNRTCEnv.init(getApplicationContext());

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    public static MyApplication getInstance(){
        return myApp;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    //接听获取token
    private void getQnrtcToken(String roomId,String callName, String callImg, String callId, String type){
        Log.e("zyb", "getQnrtcToken roomId:" + roomId);
        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getQnrtcToken/"+roomId+"/"+ SpData.getPersonId();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {

                if (!TextUtils.isEmpty(data)) {
                    RTCEngineKit.getInstance(getApplicationContext()).shouldStartRing();
                    Intent intent = new Intent(getAppContext(), RoomActivity.class);
                    intent.putExtra("room_token", data);
                    intent.putExtra(RoomActivity.EXTRA_INVITER_USER_ID, callId);
                    intent.putExtra(RoomActivity.EXTRA_INVITER_USER_NAME, callName);
                    intent.putExtra(RoomActivity.EXTRA_INVITER_USER_IMG, callImg);
                    intent.putExtra(RoomActivity.EXTRA_INVITER_TYPE, type);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("zyb", "getQnrtcRoomId ex:" + ex.getMessage());
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    /**
     * 注册监听，不需要的时候要取消监听，可在ondestory()中取消
     */
    public void registerObserver(int action, ObserverModeListener listener) {
        if (listener != null) {
            List<ObserverModeListener> list = observerListenerS.get(action);
            if (list == null) {
                list = new ArrayList<>();
                observerListenerS.put(action, list);
            }

            list.add(listener);
        }
    }

    public void unRegisterObserver(int action, ObserverModeListener listener) {
        synchronized (MyApplication.this) {
            List<ObserverModeListener> list = observerListenerS.get(action);
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    public void notifyObserver(int action, Bundle bundle) {
        synchronized (MyApplication.this) {
            List<ObserverModeListener> list = observerListenerS.get(action);
            if (list != null) {
                Iterator<ObserverModeListener> iterable = list.iterator();
                while (iterable.hasNext()) {
                    iterable.next().toUpate(bundle);
                }
            }
        }
    }
}
