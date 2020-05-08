package com.zuojianyou.zybdoctor.units.statusBarTools;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


/**
 * Utils for status bar
 * Created by qiu on 3/29/16.
 */
public class StatusBarCompat {

    //Get alpha color
    private static int calculateStatusBarColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * set statusBarColor
     *
     * @param statusColor color
     * @param alpha       0 - 255
     */
    public static void setStatusBarColor(Activity activity, int statusColor, int alpha) {
        setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha));
    }

    public static void setStatusBarColor(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColor(activity, statusColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColor(activity, statusColor);
        }
    }

    public static void setStatusBarColor(Activity activity, int statusColor, boolean isLightBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColor(activity, statusColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColor(activity, statusColor);
        }
        if (isLightBar) {
            lightStatusBar(activity);
        }
    }

    public static void setStatusBarColorForCollapsingToolbar(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColor(activity, statusColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColorForCollapsingToolbar(activity, statusColor);
        }
    }

    /**
     * change to full screen mode(Build.VERSION.SDK_INT >=19 take effect)
     *
     * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
     */
    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        translucentStatusBar(activity, 0, hideStatusBarBackground, true);
    }

    /**
     * @param activity
     * @param hideStatusBarBackground
     * @param isNeedAssistSoftKeyBoard
     */
    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground, boolean isNeedAssistSoftKeyBoard) {
        translucentStatusBar(activity, 0, hideStatusBarBackground, isNeedAssistSoftKeyBoard);
    }

    /**
     * change to full screen mode(Build.VERSION.SDK_INT >=19 take effect)
     *
     * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
     */
    public static void translucentStatusBar(Activity activity, int titleBarId, boolean hideStatusBarBackground) {
        translucentStatusBar(activity, titleBarId, hideStatusBarBackground, true);
    }

    /**
     * @param activity
     * @param hideStatusBarBackground
     * @param isNeedAssistSoftKeyBoard 是否需要解决输入框无法被软键盘顶上去的bug
     *                                 备注:有的地方解决此bug,会引起布局被挤压
     */
    public static void translucentStatusBar(Activity activity, int titleBarId, boolean hideStatusBarBackground, boolean isNeedAssistSoftKeyBoard) {
        //解决输入框无法被软键盘顶上去的bug
        if (isNeedAssistSoftKeyBoard) {
            SoftInputAssistUtil.assistActivity(activity);
        }

        //设置标题栏的topPadding为statusBar的高度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            if (mContentView.getChildAt(0) instanceof ViewGroup) {
                ViewGroup mContentChild = (ViewGroup) mContentView.getChildAt(0);
                if (mContentChild != null) {
                    if (titleBarId == 0) {//默认id
//                        titleBarId = R.id.root_layout;
                        return;
                    }
                    View childTitle = mContentChild.findViewById(titleBarId);
                    if (childTitle != null) {
                        childTitle.setPadding(0, getStatusBarHeight(activity), 0, 0);
                        mContentView.setTag(true);
                    }
                }
            }
        }

        translucentStatusBarIndeed(activity, hideStatusBarBackground);
    }

    /**
     * @param activity
     * @param hideStatusBarBackground
     */
    public static void translucentStatusBarIndeed(Activity activity, boolean hideStatusBarBackground) {
        translucentStatusBarIndeed(activity, hideStatusBarBackground, true);
    }

    public static void translucentStatusBarTextColor(Activity activity, boolean lightStatusBar) {
        translucentStatusBarIndeed(activity, true, lightStatusBar);
    }

    /**
     * @param activity
     * @param hideStatusBarBackground
     * @param lightStatusBar          是否设置状态栏文字、图标深色
     */
    public static void translucentStatusBarIndeed(Activity activity, boolean hideStatusBarBackground, boolean lightStatusBar) {
        translucentStatusBarIndeed(activity.getWindow(), hideStatusBarBackground, lightStatusBar);
    }

    /**
     * 设置statusBar透明、app内容区域
     *
     * @param window
     * @param hideStatusBarBackground
     * @param lightStatusBar          是否设置状态栏文字、图标深色
     */
    public static void translucentStatusBarIndeed(Window window, boolean hideStatusBarBackground, boolean lightStatusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.translucentStatusBar(window, hideStatusBarBackground);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.translucentStatusBar(window);
        }
        if (lightStatusBar) {
            lightStatusBar(window);
        }
    }

    public static void translucentStatusBar(Activity activity) {
        translucentStatusBar(activity, false);
    }

    /**
     * 状态栏文字、图标深色
     *
     * @param activity
     */
    public static void lightStatusBar(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    /**
     * 状态栏文字、图标深色
     *
     * @param window
     */
    public static void lightStatusBar(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    //Get status bar height
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }
}
