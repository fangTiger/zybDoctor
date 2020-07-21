package com.zuojianyou.zybdoctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dds.java.socket.IUserState;
import com.dds.java.socket.SocketManager;
import com.dds.nodejs.WebrtcUtil;
import com.zuojianyou.zybdoctor.constants.BroadcastAction;
import com.zuojianyou.zybdoctor.data.SpData;
import com.zuojianyou.zybdoctor.units.DocAuthStateUtils;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.ToastUtils;
import com.zuojianyou.zybdoctor.views.ScrollableViewPager;
import com.zuojianyou.zybdoctor.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    ScrollableViewPager vpFragment;
    List<Fragment> fragments;
    private ImageView ivPhoto;

    TreatBackReceiver treatBackReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        treatBackReceiver = new TreatBackReceiver();
        IntentFilter intentFilter = new IntentFilter(TreatActivity.ACTION_TREAT_BACK);
        registerReceiver(treatBackReceiver, intentFilter);
        registerMyReceiver();
        registerAuthReceiver();

        if (isPad(this)) {
            ivPhoto = findViewById(R.id.iv_act_main_doc_photo);
        }

        RadioButton rbHome = findViewById(R.id.rb_index_home);
        rbHome.setChecked(true);

        fragments = new ArrayList<>();
        fragments.add(new MainFragmentHome());
        fragments.add(new MainFragmentAsk());
        fragments.add(new MainFragmentDispensary());
        fragments.add(new MainFragmentReview());
//        fragments.add(new MainFragmentBook());
        fragments.add(new MainFragmentMine());
        MainFragmentAdapter fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager(), fragments);

        vpFragment = findViewById(R.id.vp_main_act_fragment);
        vpFragment.setOffscreenPageLimit(fragments.size());
        vpFragment.setAdapter(fragmentAdapter);
        vpFragment.setScrollable(false);

        RadioGroup radioGroup = findViewById(R.id.rg_index_main);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_index_home:
                        setViewPagerPage(0);
                        break;
                    case R.id.rb_index_ask:
                        if (SpData.getAuthFlag().equals("9") || SpData.getAuthFlag().equals("8")) {
                            setViewPagerPage(1);
                        } else {
                            DocAuthStateUtils utils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                                @Override
                                public void onAuth() {
                                    setViewPagerPage(1);
                                }
                            });
                            utils.httpGetAuthed(vpFragment);
                        }
                        break;
                    case R.id.rb_index_disp:
                        if (SpData.getAuthFlag().equals("9") || SpData.getAuthFlag().equals("8")) {
                            setViewPagerPage(2);
                        } else {
                            DocAuthStateUtils utils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                                @Override
                                public void onAuth() {
                                    setViewPagerPage(2);
                                }
                            });
                            utils.httpGetAuthed(vpFragment);
                        }
                        break;
                    case R.id.rb_index_review:
                        if (SpData.getAuthFlag().equals("9") || SpData.getAuthFlag().equals("8")) {
                            setViewPagerPage(3);
                        } else {
                            DocAuthStateUtils utils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                                @Override
                                public void onAuth() {
                                    setViewPagerPage(3);
                                }
                            });
                            utils.httpGetAuthed(vpFragment);
                        }
                        break;
                    case R.id.rb_index_mine:
                        setViewPagerPage(4);
                        break;
                }
            }

        });

        SocketManager.getInstance(this).addUserStateCallback(iUserState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userState = SocketManager.getInstance(this).getUserState();
        if (userState != 1 && SpData.getMbrId() != null) {
            String ws = "ws://" + WebrtcUtil.HOST + ":5000/ws";
            String id = SpData.getPersonId();
            SocketManager.getInstance(this).connect(
                    ws, id, 0);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    IUserState iUserState = new IUserState() {
        @Override
        public void userLogin() {
            handler.post(MainActivity.this::webrtcLogin);
        }

        @Override
        public void userLogout() {
            handler.post(MainActivity.this::webrtcOffline);
        }
    };

    public void webrtcLogin() {
//        ToastUtils.show(getContext(), "视频系统登录成功！");
        Log.d("webrtcLogin", "webrtcLogin" + System.currentTimeMillis());
    }

    public void webrtcOffline() {
//        ToastUtils.show(getContext(), "视频系统登录成功！");
//        String ws = "ws://" + WebrtcUtil.HOST + ":5000/ws";
//        String id = SpData.getMbrId();
//        SocketManager.getInstance(this).connect(ws, id, 0);
    }

    public void setDocPhoto(String path) {
        if (ivPhoto != null) {
            Glide.with(getContext()).load(path).apply(RequestOptions.circleCropTransform()).into(ivPhoto);
        }
    }

    private void setViewPagerPage(int page) {
        if (vpFragment.getCurrentItem() != page) vpFragment.setCurrentItem(page, false);
    }

    class MainFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> lists;

        public MainFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public MainFragmentAdapter(FragmentManager fm, List<Fragment> lists) {
            super(fm);
            this.lists = lists;
        }

        @Override
        public Fragment getItem(int i) {
            return lists.get(i);
        }

        @Override
        public int getCount() {
            return lists == null ? 0 : lists.size();
        }
    }

    PopupWindow popupWindow;

    @Override
    public void onBackPressed() {
        if (popupDownload != null && popupDownload.isShowing()) return;
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
        popupWindow = new PopupWindow(view, -1, -1);
        TextView tv = view.findViewById(R.id.tv_alert_msg);
        tv.setText("确定退出？");
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finish();
            }
        });
        popupWindow.showAtLocation(vpFragment.getRootView(), Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(treatBackReceiver);
        unRegisterMyReceiver();
        unRegisterAuthReceiver();
        SocketManager.getInstance(this).unConnect();
    }

    class TreatBackReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (vpFragment.getCurrentItem() != 1) {
                RadioButton rb = findViewById(R.id.rb_index_ask);
                rb.performClick();
            }
        }
    }

    public void httpUpdate(boolean silence) {
        String url = ServerAPI.getUpdateUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                String versionCode = jsonObject.getString("versionCode");
                String downUrl = jsonObject.getString("downUrl");
                boolean isMust = jsonObject.getJSONObject("forceUpdObj").getBooleanValue("keyValue");
                String curVersion = getVersionName(getContext());
                if (curVersion.compareTo(versionCode) >= 0) {
                    if (!silence)
                        Toast.makeText(getContext(), "已是最新版本！", Toast.LENGTH_SHORT).show();
                } else {
                    if (isMust) {
                        downloadPackage(downUrl);
                    } else {
                        showUpdateTip(downUrl);
                    }
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
        popupDownload.showAtLocation(vpFragment.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void hiddenDownloadPopup() {
        if (popupDownload != null && popupDownload.isShowing()) {
            popupDownload.dismiss();
            popupDownload = null;
        }
    }

    private void showUpdateTip(String url) {
        View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
        PopupWindow popupWindow = new PopupWindow(view, -1, -1);
        TextView tv = view.findViewById(R.id.tv_alert_msg);
        tv.setText("发现新的版本，是否更新？");
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                downloadPackage(url);
            }
        });
        popupWindow.showAtLocation(vpFragment.getRootView(), Gravity.CENTER, 0, 0);
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

    MyBroadcastReceiver myBroadcastReceiver;

    private void registerMyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastAction.ACTION_ARTICLE_CREATED);
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void unRegisterMyReceiver() {
        unregisterReceiver(myBroadcastReceiver);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainFragmentHome fragmentHome = (MainFragmentHome) fragments.get(0);
            fragmentHome.httpGetList(true);
        }
    }

    AuthBroadcastReceiver authBroadcastReceiver;

    private void registerAuthReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastAction.ACTION_AUTH_SUBMIT);
        authBroadcastReceiver = new AuthBroadcastReceiver();
        registerReceiver(authBroadcastReceiver, intentFilter);
    }

    private void unRegisterAuthReceiver() {
        unregisterReceiver(authBroadcastReceiver);
    }

    class AuthBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainFragmentMine fragmentHome = (MainFragmentMine) fragments.get(4);
            fragmentHome.httpGetMineInfo();
        }
    }
}
