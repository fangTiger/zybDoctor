package com.zuojianyou.zybdoctor.units;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.application.MyApplication;

import org.xutils.common.Callback;

public class HttpCallback implements Callback.CommonCallback<String> {

    MyCallBack myCallBack;

    public HttpCallback(MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
    }

    @Override
    public void onSuccess(String result) {
        if (BuildConfig.DEBUG)
            Log.d("xUtilsHttpLog", "result=" + result);
        JSONObject json = JSONObject.parseObject(result, Feature.OrderedField);
        int code = json.getIntValue("code");
        if (code == 0) {
            myCallBack.onSuccess(json.getString("data"));
        } else {
            String errMsg = json.getString("errMsg");
            Toast.makeText(MyApplication.getAppContext(), errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        Toast.makeText(MyApplication.getAppContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        myCallBack.onError(ex, isOnCallback);
    }

    @Override
    public void onCancelled(CancelledException cex) {
        myCallBack.onCancelled(cex);
    }

    @Override
    public void onFinished() {
        myCallBack.onFinished();
    }
}
