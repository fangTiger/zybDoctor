package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author: weiwei
 * @date: 2020/9/7
 * @description:
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.back_btn)
    ImageButton backBtn;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.option_tv)
    TextView optionTv;
    @BindView(R.id.agreement_text)
    TextView agreementText;
    @BindView(R.id.privacy_text)
    TextView privacyText;
    @BindView(R.id.btn_frag_mine_exit)
    Button btnFragMineExit;

    private Unbinder mUnBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mUnBinder = ButterKnife.bind(this);

        titleTv.setText("设置");
    }

    @OnClick({R.id.back_btn,R.id.agreement_text,R.id.privacy_text,R.id.btn_frag_mine_exit})
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.back_btn:
                finish();

                break;
            case R.id.agreement_text:
                String url = ServerAPI.BASE_DOMAIN+"/p/joined/serviceAgre.html";
                Intent intent=new Intent(getContext(),WebActivity.class);
                intent.putExtra("title","服务协议");
                intent.putExtra("url",url);
                startActivity(intent);
                break;
            case R.id.privacy_text:
                Intent i=new Intent(getContext(),WebActivity.class);
                i.putExtra("title","隐私政策");
                i.putExtra("url",ServerAPI.BASE_DOMAIN+"/p/joined/secret.html");
                startActivity(i);
                break;
            case R.id.btn_frag_mine_exit:

                View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
                PopupWindow popupWindow = new PopupWindow(view, -1, -1);
                TextView tv = view.findViewById(R.id.tv_alert_msg);
                tv.setText("确定退出当前账号？");
                view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url = ServerAPI.BASE_DOMAIN + "/appDoc/common/bindDevice/"+SpData.getPersonId()+"/2/0";
                        RequestParams entity = new RequestParams(url);
                        ServerAPI.addHeader(entity);
                        x.http().get(entity, new HttpCallback(new MyCallBack() {
                            @Override
                            public void onSuccess(String data) {
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

                        popupWindow.dismiss();
                        SpData.clearUser();
                        SpData.clearPwd();
                        Intent intent = new Intent(getContext(), UserLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                popupWindow.showAtLocation(btnFragMineExit.getRootView(), Gravity.CENTER, 0, 0);

                break;
        }
    }


    @Override
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }
}
