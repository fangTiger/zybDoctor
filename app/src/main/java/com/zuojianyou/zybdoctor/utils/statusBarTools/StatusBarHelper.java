package com.zuojianyou.zybdoctor.utils.statusBarTools;

import android.content.Context;

/**
 * Created by jc.Ren on 2019/1/9
 * e-mail:renjiangchao1989@sina.com
 */
public class StatusBarHelper {

    public static double getWindowStatusBarHeight(Context context) {
        double statusBarHeight = Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }
}
