package com.dds.java.voip;

import android.content.Context;

/**
 * Created by dds on 2019/8/5.
 * android_shuai@163.com
 */
public class Utils {

    public static String ACTION_VOIP_RECEIVER(Context context) {
        return context.getPackageName() + ".voip.Receiver";
    }

}
