package com.zuojianyou.zybdoctor.units;

import android.content.Context;
import android.widget.Toast;

import com.zuojianyou.zybdoctor.BuildConfig;

public class ToastUtils {

    public static void debugShow(Context context, String msg) {
        if (BuildConfig.DEBUG) show(context, msg);
    }

    public static void show(Context context, String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
