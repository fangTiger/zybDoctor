package com.zuojianyou.zybdoctor.utils;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.activity.AttestationActivity;
import com.zuojianyou.zybdoctor.base.data.SpData;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import androidx.annotation.NonNull;

public class DocAuthStateUtils {

    private OnAuth onAuth;

    public DocAuthStateUtils(@NonNull OnAuth onAuth) {
        this.onAuth = onAuth;
    }

    public void httpGetAuthed(View view) {
        String url = ServerAPI.getAuthStateUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                String flag = jsonObject.getString("authFlag");
                SpData.setAuthFlag(flag);
                if (flag.equals("9")) {
                    onAuth.onAuth();
                } else if(flag.equals("1")){
                    View viewPopup = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_common_alert, null);
                    PopupWindow popupWindow = new PopupWindow(viewPopup, -1, -1, true);
                    TextView tv = viewPopup.findViewById(R.id.tv_alert_msg);
                    tv.setText("您提交的医师认证正在审核中，我们会尽快为您办理，请等待。");
                    viewPopup.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                    viewPopup.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);
                }else{
                    View viewPopup = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_common_alert, null);
                    PopupWindow popupWindow = new PopupWindow(viewPopup, -1, -1, true);
                    TextView tv = viewPopup.findViewById(R.id.tv_alert_msg);
                    tv.setText("您还未认证，去认证？");
                    viewPopup.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                    viewPopup.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            Intent intent = new Intent(view.getContext(), AttestationActivity.class);
                            view.getContext().startActivity(intent);
                        }
                    });
                    popupWindow.showAtLocation(view.getRootView(), Gravity.CENTER, 0, 0);
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

    public interface OnAuth {
        void onAuth();
    }
}
