package com.zuojianyou.zybdoctor.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {

    View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        rootView = findViewById(R.id.rl_act_welcome_root);
        requestPermission();
    }

    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO};
        List<String> perList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                perList.add(permissions[i]);
            }
        }
        if (perList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        } else {
            httpUpdate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 100) return;
        boolean flagDismiss = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == -1) {
                flagDismiss = true;
                break;
            }
        }
        if (flagDismiss) {
            finish();
        } else {
            httpUpdate();
        }
    }

    private void init() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(getContext(), UserLoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    public void httpUpdate() {
        if (!checkNetwork()) finish();
        String url = ServerAPI.getUpdateUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                String versionCode = jsonObject.getString("versionCode");
                String downUrl = jsonObject.getString("downUrl");
                String curVersion = getVersionName(getContext());
                if (curVersion.compareTo(versionCode) >= 0) {
                    init();
                } else {
                    downloadPackage(downUrl);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    File mFile;

    private void downloadPackage(String url) {
        showDownloadPopup();
        RequestParams entity = new RequestParams(url);
        String path = Environment.getExternalStorageDirectory() + File.separator + "zybDownload" + File.separator;
        String name = "zybDoctor.apk";
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        entity.setSaveFilePath(path + name);
        x.http().get(entity, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                mFile = result;
                hiddenDownloadPopup();
                applyInstallCheck(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(getContext(), "下载出错：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenDownloadPopup();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                pbDownload.setProgress((int) (current * 100 / total));
            }
        });
    }

    PopupWindow popupDownload;
    View vPopupDownload;
    ProgressBar pbDownload;

    private void showDownloadPopup() {
        vPopupDownload = getLayoutInflater().inflate(R.layout.popup_download_progress, null);
        pbDownload = vPopupDownload.findViewById(R.id.progressBar);
        popupDownload = new PopupWindow(vPopupDownload, -1, -1);
        popupDownload.setBackgroundDrawable(new ColorDrawable(0x33000000));
        popupDownload.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void hiddenDownloadPopup() {
        if (popupDownload != null && popupDownload.isShowing()) {
            popupDownload.dismiss();
            popupDownload = null;
        }
    }

    private void applyInstallCheck(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean flag = getPackageManager().canRequestPackageInstalls();
            if (flag) {
                installApk(file);
            } else {
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                startActivityForResult(intent, 100);
            }
        } else {
            installApk(file);
        }
    }

    private void installApk(File file) {
        if (file != null) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".android7.FileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                installApk(mFile);
            } else {
                applyInstallCheck(mFile);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (popupDownload != null && popupDownload.isShowing()) return;
        super.onBackPressed();
    }
}
