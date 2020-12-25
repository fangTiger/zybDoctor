package com.zuojianyou.zybdoctor.app;

import android.os.Bundle;

/**
 *
 * @Todo 全局的观察者监听，注册在application中，不用的时候一定要解除注册
 */
public interface ObserverModeListener {
    void toUpate(Bundle bundle);
}