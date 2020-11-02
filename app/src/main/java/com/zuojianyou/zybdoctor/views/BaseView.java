package com.zuojianyou.zybdoctor.views;

public interface BaseView {

    void showErrorMsg(String msg);

    void showErrorMsg(String msg, String errorCode);

    void showLoading();

    void hideLoading();

}
