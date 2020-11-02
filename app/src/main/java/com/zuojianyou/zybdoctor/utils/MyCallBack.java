package com.zuojianyou.zybdoctor.utils;

import org.xutils.common.Callback;

public interface MyCallBack {

    void onSuccess(String data);

    void onError(Throwable ex, boolean isOnCallback);

    void onCancelled(Callback.CancelledException cex);

    void onFinished();
}
