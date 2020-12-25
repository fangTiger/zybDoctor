package com.zuojianyou.zybdoctor.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.application.MyApplication;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录
 */
public class UserLoginActivity extends BaseActivity {

    Button btnConfirm;
    EditText etUser, etPwd, etMobile, etCode;
    View llAccount, llMobile;
    TextView btnChangeType;
    TextView btnCode;

    TimeThread timeThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.et_act_login_user);
        etPwd = findViewById(R.id.et_act_login_pwd);
        etMobile = findViewById(R.id.et_act_login_mobile);
        etCode = findViewById(R.id.et_act_login_code);

        llAccount = findViewById(R.id.ll_login_by_account);
        llMobile = findViewById(R.id.ll_login_by_mobile);
        llMobile.setVisibility(View.GONE);

        btnChangeType = findViewById(R.id.btn_login_type_change);
        btnChangeType.setText("验证码登录");
        btnChangeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llMobile.getVisibility() == View.VISIBLE) {
                    llMobile.setVisibility(View.GONE);
                    llAccount.setVisibility(View.VISIBLE);
                    btnChangeType.setText("验证码登录");
                } else {
                    llMobile.setVisibility(View.VISIBLE);
                    llAccount.setVisibility(View.GONE);
                    btnChangeType.setText("密码登录");
                }
            }
        });
        btnCode = findViewById(R.id.btn_act_login_code);
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etMobile.getText())) {
                    ToastUtils.show(getContext(), "请先输入手机号！");
                } else {
                    String mobile = etMobile.getText().toString();
                    httpGetCode(mobile);
                    btnCode.setEnabled(false);
                    timeThread = new TimeThread();
                    timeThread.start();
                }
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserRegisterActivity.class);
                startActivityForResult(intent, 400);
            }
        });

        btnConfirm = findViewById(R.id.btn_act_login_confirm);
        btnConfirm.setOnClickListener(confirmClicked);

        requestPermission();


        findViewById(R.id.btn_register_agreement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ServerAPI.BASE_DOMAIN+"/p/joined/serviceAgre.html";
                Intent intent=new Intent(getContext(),WebActivity.class);
                intent.putExtra("title","服务协议");
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_register_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ServerAPI.BASE_DOMAIN+"/p/joined/secret.html";
                Intent intent=new Intent(getContext(),WebActivity.class);
                intent.putExtra("title","隐私政策");
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 400 && resultCode == RESULT_OK) {
            if (llMobile.getVisibility() == View.VISIBLE) {
                btnChangeType.performClick();
            }
        }
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
            String user = SpData.getUser();
            String pwd = SpData.getPwd();
            if (user != null && pwd != null) {
                etUser.setText(user);
                etPwd.setText(pwd);
                btnConfirm.performClick();
            }
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
            String user = SpData.getUser();
            String pwd = SpData.getPwd();
            String doctorId = SpData.getMbrId();
            if (user != null && pwd != null && doctorId != null) {
                etUser.setText(user);
                etPwd.setText(pwd);
                btnConfirm.performClick();
            }
        }
    }

    Callback.Cancelable cancelable;

    View.OnClickListener confirmClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (llMobile.getVisibility() == View.VISIBLE) {
                loginByMobile();
            } else {
                loginByAccount();
            }
        }
    };

    private void loginByAccount() {
        if (TextUtils.isEmpty(etUser.getText()) || TextUtils.isEmpty(etPwd.getText())) {
            ToastUtils.show(getContext(), "用户名或密码不能为空！");
            return;
        }
        final String user = etUser.getText().toString().trim();
        final String pwd = etPwd.getText().toString().trim();

        if (!checkNetwork()) return;
        btnConfirm.setText("登录中...");
        btnConfirm.setEnabled(false);
        String url = ServerAPI.getLoginAccountUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("loginName", user);
        jsonObject.put("loginPwd", MD5.md5(pwd).toUpperCase());
        Log.d("UserLoginActivity", jsonObject.toJSONString());
        entity.setBodyContent(jsonObject.toJSONString());

        cancelable = x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("UserLoginActivity", "result=" + result);
                JSONObject json = JSONObject.parseObject(result);
                int code = json.getIntValue("code");
                if (code == 0) {
                    SpData.setUser(user);
                    SpData.setPwd(pwd);
                    JSONObject jsonData = json.getJSONObject("data");
                    SpData.setToken(jsonData.getString("token"));
                    SpData.setMbrId(jsonData.getString("doctorId"));
                    SpData.setPersonId(jsonData.getString("personid"));
                    SpData.setAuthFlag(jsonData.getString("authFlag"));
                    pushBindDevice();

                    httpGetMineInfo();
                } else {
                    String errMsg = json.getString("errMsg");
                    Toast.makeText(getContext(), errMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(UserLoginActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                btnConfirm.setText("登录");
                btnConfirm.setEnabled(true);
            }

            @Override
            public void onFinished() {
                btnConfirm.setText("登录");
                btnConfirm.setEnabled(true);
            }
        });
    }

    private void loginByMobile() {
        if (TextUtils.isEmpty(etMobile.getText()) || TextUtils.isEmpty(etCode.getText())) {
            ToastUtils.show(getContext(), "手机号或验证码不能为空！");
            return;
        }
        String mobile = etMobile.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        if (!checkNetwork()) return;
        btnConfirm.setText("登录中...");
        btnConfirm.setEnabled(false);
        String url = ServerAPI.getLoginMobileUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("mobile", mobile);
        jsonObject.put("loginSource", 1);
        Log.d("UserLoginActivity", jsonObject.toJSONString());
        entity.setBodyContent(jsonObject.toJSONString());

        cancelable = x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("UserLoginActivity", "result=" + result);
                JSONObject json = JSONObject.parseObject(result);
                int code = json.getIntValue("code");
                if (code == 0) {
                    JSONObject jsonData = json.getJSONObject("data");
                    SpData.setToken(jsonData.getString("token"));
                    SpData.setMbrId(jsonData.getString("doctorId"));
                    SpData.setPersonId(jsonData.getString("personid"));
                    SpData.setAuthFlag(jsonData.getString("authFlag"));
                    pushBindDevice();
                    httpGetMineInfo();
                } else {
                    String errMsg = json.getString("errMsg");
                    Toast.makeText(getContext(), errMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(UserLoginActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                btnConfirm.setText("登录");
                btnConfirm.setEnabled(true);
            }

            @Override
            public void onFinished() {
                btnConfirm.setText("登录");
                btnConfirm.setEnabled(true);
            }
        });
    }

    private void pushBindDevice() {
        String deviceToken = MyApplication.getInstance().getDeviceToken();
        if (!TextUtils.isEmpty(deviceToken)) {
            String url = ServerAPI.BASE_DOMAIN + "/appDoc/common/bindDevice/"+SpData.getPersonId()+"/1/" + deviceToken;
            RequestParams entity = new RequestParams(url);
            ServerAPI.addHeader(entity);
            x.http().get(entity, new HttpCallback(new MyCallBack() {
                @Override
                public void onSuccess(String data) {
                    Log.e("zyb", "bindDevice" + data);
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
    }

    public void httpGetMineInfo() {
        String url = ServerAPI.getDocInfoUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                SpData.setDoctorInfo(data);
                startActivity(new Intent(getContext(), MainActivity.class));
                finish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                startActivity(new Intent(getContext(), MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancelable != null && !cancelable.isCancelled()) {
            cancelable.cancel();
        }
        if (timeThread != null) timeThread.stop = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (cancelable != null && !cancelable.isCancelled()) {
            cancelable.cancel();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                btnCode.setText("获取验证码");
                btnCode.setEnabled(true);
            } else {
                btnCode.setText(msg.what + "s");
            }
        }
    };

    class TimeThread extends Thread {

        int time = 60;
        public boolean stop = false;

        @Override
        public void run() {
            super.run();
            while (time >= 0 && !stop) {
                handler.sendEmptyMessage(time);
                time--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void httpGetCode(String mobile) {
        String url = ServerAPI.getSmsCodeUrl();
        RequestParams entity = new RequestParams(url);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "1004");
        jsonObject.put("mobile", mobile);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.debugShow(getContext(), data);
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
}
