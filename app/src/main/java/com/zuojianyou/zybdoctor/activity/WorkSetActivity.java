package com.zuojianyou.zybdoctor.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.ToastUtils;
import com.zuojianyou.zybdoctor.views.TextInput;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.NumberFormat;

import androidx.annotation.Nullable;

public class WorkSetActivity extends BaseActivity {

    TextView tvWorkState, tvOnlineFee, tvOfflineFee, tvMorningNum, tvAfternoonNum, tvNightNum, tvMorningTag, tvAfternoonTag, tvNightTag;
    String docId;
    ImageView ivMorningState, ivAfternoonState, ivNightState;
    View viewWorkContainer, viewMorning, viewAfternoon, viewNight;
    Switch swWorkState;

    boolean initFinish = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_set);
        findViewById(R.id.ib_act_base_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvWorkState = findViewById(R.id.tv_doc_work_state);
        ivMorningState = findViewById(R.id.iv_work_set_morning_state);
        ivAfternoonState = findViewById(R.id.iv_work_set_afternoon_state);
        ivNightState = findViewById(R.id.iv_work_set_night_state);
        viewWorkContainer = findViewById(R.id.ll_doc_work_container);
        viewMorning = findViewById(R.id.ll_morning_container);
        viewMorning.setOnClickListener(onClickListener);
        viewAfternoon = findViewById(R.id.ll_afternoon_container);
        viewAfternoon.setOnClickListener(onClickListener);
        viewNight = findViewById(R.id.ll_night_container);
        viewNight.setOnClickListener(onClickListener);
        swWorkState = findViewById(R.id.sw_doc_work_state);
        swWorkState.setOnClickListener(onClickListener);
        tvOnlineFee = findViewById(R.id.tv_work_set_online_fee);
        tvOnlineFee.addTextChangedListener(textWatcher);
        tvOfflineFee = findViewById(R.id.tv_work_set_offline_fee);
        tvOfflineFee.addTextChangedListener(textWatcher);
        tvMorningNum = findViewById(R.id.tv_work_set_morning_num);
        tvMorningNum.addTextChangedListener(textWatcher);
        tvAfternoonNum = findViewById(R.id.tv_work_set_afternoon_num);
        tvAfternoonNum.addTextChangedListener(textWatcher);
        tvNightNum = findViewById(R.id.tv_work_set_night_num);
        tvNightNum.addTextChangedListener(textWatcher);
        tvMorningTag = findViewById(R.id.tv_work_set_morning_tag);
        tvAfternoonTag = findViewById(R.id.tv_work_set_afternoon_tag);
        tvNightTag = findViewById(R.id.tv_work_set_night_tag);

        tvOnlineFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("线上问诊金", (TextView) v);
                textInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                textInput.setTip("单位（元）");
                textInput.show();
            }
        });
        tvOfflineFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("线下问诊金", (TextView) v);
                textInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                textInput.setTip("单位（元）");
                textInput.show();
            }
        });
        tvMorningNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("上午预约人数限制", (TextView) v);
                textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                textInput.setTip("单位（人）");
                textInput.show();
            }
        });
        tvAfternoonNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("下午预约人数限制", (TextView) v);
                textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                textInput.setTip("单位（人）");
                textInput.show();
            }
        });
        tvNightNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("晚上预约人数限制", (TextView) v);
                textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                textInput.setTip("单位（人）");
                textInput.show();
            }
        });

        httpQueryWorkSet();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ll_morning_container) {
                String state = (String) ivMorningState.getTag();
                if (state.equals("0")) {
                    ivMorningState.setTag("1");
                    ivMorningState.setImageResource(R.mipmap.common_icon_cb_draw_checked);
                    tvMorningTag.setEnabled(true);
                    tvMorningNum.setEnabled(true);
                } else {
                    ivMorningState.setTag("0");
                    ivMorningState.setImageResource(R.mipmap.common_icon_cb_draw_normal);
                    tvMorningTag.setEnabled(false);
                    tvMorningNum.setEnabled(false);
                }
            } else if (v.getId() == R.id.ll_afternoon_container) {
                String state = (String) ivAfternoonState.getTag();
                if (state.equals("0")) {
                    ivAfternoonState.setTag("1");
                    ivAfternoonState.setImageResource(R.mipmap.common_icon_cb_draw_checked);
                    tvAfternoonTag.setEnabled(true);
                    tvAfternoonNum.setEnabled(true);
                } else {
                    ivAfternoonState.setTag("0");
                    ivAfternoonState.setImageResource(R.mipmap.common_icon_cb_draw_normal);
                    tvAfternoonTag.setEnabled(false);
                    tvAfternoonNum.setEnabled(false);
                }
            } else if (v.getId() == R.id.ll_night_container) {
                String state = (String) ivNightState.getTag();
                if (state.equals("0")) {
                    ivNightState.setTag("1");
                    ivNightState.setImageResource(R.mipmap.common_icon_cb_draw_checked);
                    tvNightTag.setEnabled(true);
                    tvNightNum.setEnabled(true);
                } else {
                    ivNightState.setTag("0");
                    ivNightState.setImageResource(R.mipmap.common_icon_cb_draw_normal);
                    tvNightTag.setEnabled(false);
                    tvNightNum.setEnabled(false);
                }
            } else if (v.getId() == R.id.sw_doc_work_state) {
                String state = (String) tvWorkState.getTag();
                if (state.equals("0")) {
                    tvWorkState.setTag("1");
                    tvWorkState.setText("在线");
                    swWorkState.setChecked(true);
                    viewWorkContainer.setVisibility(View.VISIBLE);
                } else {
                    tvWorkState.setTag("0");
                    tvWorkState.setText("休息");
                    swWorkState.setChecked(false);
                    viewWorkContainer.setVisibility(View.GONE);
                }
            }
            httpSubmit();
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (initFinish)
                httpSubmit();
        }
    };

    private void httpQueryWorkSet() {
        showLoadView();
        String url = ServerAPI.getWorkSetUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                boolean state = jsonObject.getBooleanValue("onlineState");
                swWorkState.setChecked(state);
                if (state) {
                    tvWorkState.setText("在线");
                    tvWorkState.setTag("1");
                    viewWorkContainer.setVisibility(View.VISIBLE);
                } else {
                    tvWorkState.setText("休息");
                    tvWorkState.setTag("0");
                    viewWorkContainer.setVisibility(View.GONE);
                }
                double onlineFee = jsonObject.getDoubleValue("onlineFee");
                tvOnlineFee.setText(String.valueOf(onlineFee));
                double offlineFee = jsonObject.getDoubleValue("fee");
                tvOfflineFee.setText(String.valueOf(offlineFee));
                JSONArray appArr = jsonObject.getJSONArray("appiontArr");
                for (int i = 0; i < appArr.size(); i++) {
                    JSONObject json = appArr.getJSONObject(i);
                    if (docId == null)
                        docId = json.getString("doctorId");
                    int num = json.getIntValue("num");
                    if (json.getIntValue("timeTyp") == 1) {
                        if (json.getBooleanValue("isChoose")) {
                            ivMorningState.setTag("1");
                            ivMorningState.setImageResource(R.mipmap.common_icon_cb_draw_checked);
                            tvMorningTag.setEnabled(true);
                            tvMorningNum.setEnabled(true);
                        } else {
                            ivMorningState.setTag("0");
                            ivMorningState.setImageResource(R.mipmap.common_icon_cb_draw_normal);
                            tvMorningTag.setEnabled(false);
                            tvMorningNum.setEnabled(false);
                        }
                        tvMorningNum.setText(String.valueOf(num));
                    } else if (json.getIntValue("timeTyp") == 2) {
                        if (json.getBooleanValue("isChoose")) {
                            ivAfternoonState.setTag("1");
                            ivAfternoonState.setImageResource(R.mipmap.common_icon_cb_draw_checked);
                            tvAfternoonTag.setEnabled(true);
                            tvAfternoonNum.setEnabled(true);
                        } else {
                            ivAfternoonState.setTag("0");
                            ivAfternoonState.setImageResource(R.mipmap.common_icon_cb_draw_normal);
                            tvAfternoonTag.setEnabled(false);
                            tvAfternoonNum.setEnabled(false);
                        }
                        tvAfternoonNum.setText(String.valueOf(num));
                    } else if (json.getIntValue("timeTyp") == 3) {
                        if (json.getBooleanValue("isChoose")) {
                            ivNightState.setTag("1");
                            ivNightState.setImageResource(R.mipmap.common_icon_cb_draw_checked);
                            tvNightTag.setEnabled(true);
                            tvNightNum.setEnabled(true);
                        } else {
                            ivNightState.setTag("0");
                            ivNightState.setImageResource(R.mipmap.common_icon_cb_draw_normal);
                            tvNightTag.setEnabled(false);
                            tvNightNum.setEnabled(false);
                        }
                        tvNightNum.setText(String.valueOf(num));
                    }
                }
                initFinish = true;
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenLoadView();
            }
        }));
    }

    private void httpSubmit() {
        if (!checkInputData()) return;
        checkNetwork();
        showLoadView();
        String url = ServerAPI.getDocWorkSetUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setBodyContent(createRequestBody());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
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
                hiddenLoadView();
            }
        }));
    }

    private boolean checkInputData() {
        if (tvWorkState.getTag().equals("0")) return true;
        if (TextUtils.isEmpty(tvOnlineFee.getText())) {
            ToastUtils.show(getContext(), "请输入线上问诊金！");
            return false;
        }
        if (TextUtils.isEmpty(tvOfflineFee.getText())) {
            ToastUtils.show(getContext(), "请输入线下问诊金！");
            return false;
        }
        if (ivMorningState.getTag().equals("1") && TextUtils.isEmpty(tvMorningNum.getText())) {
            ToastUtils.show(getContext(), "请输入上午预约人数限制！");
            return false;
        }
        if (ivAfternoonState.getTag().equals("1") && TextUtils.isEmpty(tvAfternoonNum.getText())) {
            ToastUtils.show(getContext(), "请输入下午预约人数限制！");
            return false;
        }
        if (ivNightState.getTag().equals("1") && TextUtils.isEmpty(tvNightNum.getText())) {
            ToastUtils.show(getContext(), "请输入晚上预约人数限制！");
            return false;
        }
        return true;
    }

    private String createRequestBody() {
        JSONObject jsonObject = new JSONObject();
        String workState = (String) tvWorkState.getTag();
        jsonObject.put("onlineState", workState);
        if (workState.equals("1")) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            String strOnline = tvOnlineFee.getText().toString().trim();
            double dOnline = Double.valueOf(strOnline);
            jsonObject.put("onlineFee", nf.format(dOnline));
            String strOffline = tvOfflineFee.getText().toString().trim();
            double dOffline = Double.valueOf(strOffline);
            jsonObject.put("fee", nf.format(dOffline));
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(createNumLimit("1", (String) ivMorningState.getTag(), tvMorningNum.getText().toString()));
            jsonArray.add(createNumLimit("2", (String) ivAfternoonState.getTag(), tvAfternoonNum.getText().toString()));
            jsonArray.add(createNumLimit("3", (String) ivNightState.getTag(), tvNightNum.getText().toString()));
            jsonObject.put("appiontArr", jsonArray);
        }
        return jsonObject.toJSONString();
    }

    private JSONObject createNumLimit(String type, String able, String num) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doctorId", docId);
        jsonObject.put("isChoose", able);
        jsonObject.put("timeTyp", type);
        jsonObject.put("num", num);
        return jsonObject;
    }
}


