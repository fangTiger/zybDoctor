package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class MyPwdEditActivity extends BaseActivity {

    Button btnConfirm;
    EditText etUser, etPwd, etConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pwd_edit);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etUser = findViewById(R.id.et_act_login_user);
        etPwd = findViewById(R.id.et_act_login_pwd);
        etConfirm = findViewById(R.id.et_act_login_pwd_confirm);

        btnConfirm = findViewById(R.id.btn_act_login_confirm);
        btnConfirm.setOnClickListener(confirmClicked);

    }

    Callback.Cancelable cancelable;

    View.OnClickListener confirmClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(etUser.getText())) {
                Toast.makeText(getContext(), "原密码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(etPwd.getText())) {
                Toast.makeText(getContext(), "新密码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(etConfirm.getText())) {
                Toast.makeText(getContext(), "确认密码不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!etPwd.getText().toString().trim().equals(etConfirm.getText().toString().trim())) {
                Toast.makeText(getContext(), "新密码与确认密码不一致！", Toast.LENGTH_SHORT).show();
                return;
            }
            final String user = etUser.getText().toString().trim();
            final String pwd = etPwd.getText().toString().trim();

            if (!checkNetwork()) return;
            btnConfirm.setText("正在提交...");
            btnConfirm.setEnabled(false);
            String url = ServerAPI.getPwdUpdUrl();
            RequestParams entity = new RequestParams(url);
            ServerAPI.addHeader(entity);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("oldPass", MD5.md5(user).toUpperCase());
            jsonObject.put("newPass", MD5.md5(pwd).toUpperCase());
            entity.setBodyContent(jsonObject.toJSONString());

            cancelable = x.http().post(entity, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    JSONObject json = JSONObject.parseObject(result);
                    int code = json.getIntValue("code");
                    if (code == 0) {
                        Toast.makeText(getContext(), "密码修改成功，请重新登录！", Toast.LENGTH_SHORT).show();
                        SpData.clearUser();
                        SpData.clearPwd();
                        Intent intent = new Intent(getContext(), UserLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        String errMsg = json.getString("errMsg");
                        Toast.makeText(getContext(), errMsg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    btnConfirm.setText("确定");
                    btnConfirm.setEnabled(true);
                }

                @Override
                public void onFinished() {
                    btnConfirm.setText("确定");
                    btnConfirm.setEnabled(true);
                }
            });
        }
    };

}
