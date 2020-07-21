package com.zuojianyou.zybdoctor.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zuojianyou.zybdoctor.units.statusBarTools.StatusBarCompat;
import com.zuojianyou.zybdoctor.views.LoadingDialog;

public class BaseActivity extends AppCompatActivity {

    protected boolean isPad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPad = isPad(this);
        if (isPad) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            StatusBarCompat.setStatusBarColor(this, 0xffffffff, true);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    protected Context getContext() {
        return this;
    }

    protected boolean checkNetwork() {
        boolean flag = false;
        ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            Toast.makeText(this, "网络连接不可用，请检查网络!", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }


    LoadingDialog loadingDialog;

    protected void showLoadView() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    protected void hiddenLoadView() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
