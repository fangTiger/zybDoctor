package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

import androidx.annotation.Nullable;

public class UserRegisterActivity extends BaseActivity {

    Button btnConfirm;
    EditText etMobile, etCode, etPwd, etConfirm;
    TextView btnCode;

    TimeThread timeThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        findViewById(R.id.ib_act_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etMobile = findViewById(R.id.et_act_login_mobile);
        etCode = findViewById(R.id.et_act_login_code);
        etPwd = findViewById(R.id.et_act_login_pwd);
        etConfirm = findViewById(R.id.et_act_login_pwd_confirm);

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

        btnConfirm = findViewById(R.id.btn_act_login_confirm);
        btnConfirm.setOnClickListener(confirmClicked);

        findViewById(R.id.btn_register_agreement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ServerAPI.BASE_DOMAIN+"/p/joined/secret.html";
                Intent intent=new Intent(getContext(),WebActivity.class);
                intent.putExtra("title","服务协议");
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_register_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ServerAPI.BASE_DOMAIN+"/p/joined/serviceAgre.html";
                Intent intent=new Intent(getContext(),WebActivity.class);
                intent.putExtra("title","隐私政策");
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancelable != null && !cancelable.isCancelled()) {
            cancelable.cancel();
        }
        if (timeThread != null) timeThread.stop = true;
    }

    Callback.Cancelable cancelable;

    View.OnClickListener confirmClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            httpRegister();
        }
    };

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
        jsonObject.put("type", "1003");
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

    private void httpRegister() {
        if (TextUtils.isEmpty(etMobile.getText()) || TextUtils.isEmpty(etCode.getText())) {
            ToastUtils.show(getContext(), "手机号或验证码不能为空！");
            return;
        }
        if (TextUtils.isEmpty(etPwd.getText())) {
            Toast.makeText(getContext(), "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etConfirm.getText())) {
            Toast.makeText(getContext(), "确认密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!etPwd.getText().toString().trim().equals(etConfirm.getText().toString().trim())) {
            Toast.makeText(getContext(), "密码与确认密码不一致！", Toast.LENGTH_SHORT).show();
            return;
        }
        String mobile = etMobile.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();

        if (!checkNetwork()) return;
        btnConfirm.setText("提交中...");
        btnConfirm.setEnabled(false);
        String url = ServerAPI.getUserRegisterUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("mobile", mobile);
        jsonObject.put("loginPwd", MD5.md5(pwd).toUpperCase());
        jsonObject.put("loginSource", 1);
        entity.setBodyContent(jsonObject.toJSONString());

        cancelable = x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject json = JSONObject.parseObject(result);
                int code = json.getIntValue("code");
                if (code == 0) {
                    ToastUtils.show(getContext(), "注册成功！");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errMsg = json.getString("errMsg");
                    ToastUtils.show(getContext(), errMsg);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(getContext(), ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                btnConfirm.setText("注册");
                btnConfirm.setEnabled(true);
            }

            @Override
            public void onFinished() {
                btnConfirm.setText("注册");
                btnConfirm.setEnabled(true);
            }
        });
    }
}
