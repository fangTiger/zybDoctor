package com.zuojianyou.zybdoctor.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.views.BaseView;

import java.io.Closeable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import io.reactivex.subscribers.ResourceSubscriber;

public abstract class CommonSubscriber<T> extends ResourceSubscriber<T> {
    private Reference<BaseView> mView;
    private String mErrorMsg;

    protected CommonSubscriber(BaseView view) {
        this.mView = new WeakReference<BaseView>(view);
    }

    protected CommonSubscriber(BaseView view, String errorMsg) {
        this.mView = new WeakReference<BaseView>(view);
        this.mErrorMsg = errorMsg;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onNext(T t) {
        if (mView != null && mView.get() != null) {
            onSuccess(t);
        }
    }

    public abstract void onSuccess(T t);

    @Override
    public void onError(Throwable e) {
        Log.e("=============", "======ApiException=======" + e.getMessage());
        Log.e("=============", "======ApiException=======" + e.getStackTrace());
        if (mView != null && mView.get() != null) {

            mView.get().hideLoading();
            if (!TextUtils.isEmpty(mErrorMsg)) {
                mView.get().showErrorMsg(mErrorMsg);
            } else if (e instanceof ApiException) {
                String code = ((ApiException)e).getCode();
                if ("9999".equals(code)) {
                    mView.get().showErrorMsg(((ApiException)e).getMsg(), code);
                } else {
                    mView.get().showErrorMsg(((ApiException)e).getMsg());
                }
            } else if (e instanceof TimeoutException || e instanceof SocketTimeoutException) {
                mView.get().showErrorMsg("数据加载超时", "9999");
                //            mView.showErrorMsg(((HttpException)e).message());
            } else if (e instanceof SocketException) {
                mView.get().showErrorMsg("网络异常", "9999");
            } else if (e instanceof JsonParseException) {
                mView.get().showErrorMsg("数据解析失败", "9999");
            } else {
                if (BuildConfig.DEBUG) {
                    mView.get().showErrorMsg("未知错误");
                } else {
                    mView.get().showErrorMsg("");
                }
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
