package com.zuojianyou.zybdoctor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.adapter.BaseAdapter;
import com.zuojianyou.zybdoctor.adapter.DepartmentDialogAdapter;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.beans.OfficeInfo;
import com.zuojianyou.zybdoctor.beans.treat.MbrInfo;
import com.zuojianyou.zybdoctor.rtc.RoomActivity;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 病人信息
 */
public class PatientInfoActivity extends BaseActivity {

    String personid, mbrId, regId, diaId, regType, accId;
    double fee;
    boolean payState;
    private String mCallType;
    private Button btnExpertAssistance;
    private Button btnExpertConsultation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        regType = getIntent().getStringExtra("regType");
        personid = getIntent().getStringExtra("personid");
        mbrId = getIntent().getStringExtra("mbrId");
        regId = getIntent().getStringExtra("regId");
        diaId = getIntent().getStringExtra("diaId");
        fee = getIntent().getDoubleExtra("fee", 0);
        payState = getIntent().getBooleanExtra("payState", false);
        accId = getIntent().getStringExtra("accId");

        TextView tvFee = findViewById(R.id.tv_patient_cost);
        String strFee = String.format("%.2f", fee);
        addText(tvFee, strFee + "元");

        findViewById(R.id.btn_act_patient_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("2".equals(regType) && !TextUtils.isEmpty(accId)) {
                    if (!TextUtils.isEmpty(diaId)){
                        Intent intentVisit = new Intent(getContext(), VisitOrderActivity.class);
                        intentVisit.putExtra("diaId", diaId);
                        startActivity(intentVisit);
                    } else {
                        ToastUtils.show(getContext(), "问诊未完成，不能查看");
                    }
                    return;
                }

                Intent intent = new Intent(PatientInfoActivity.this, TreatActivity.class);
                intent.putExtra(RoomActivity.EXTRA_CALL_TYPE, "1");
                intent.putExtra("personid", personid);
                intent.putExtra("mbrId", mbrId);
                intent.putExtra("regId", regId);
                intent.putExtra("fee", fee);
                intent.putExtra("payState", payState);
                if (diaId != null && diaId.length() > 0)
                    intent.putExtra("diaId", diaId);
                startActivity(intent);
                finish();
            }
        });

        MbrInfo mbrInfo = (MbrInfo) getIntent().getSerializableExtra("mbrInfo");
        showMbrInfo(mbrInfo);
        btnExpertAssistance = findViewById(R.id.btn_expert_assistance);
        btnExpertConsultation = findViewById(R.id.btn_expert_consultation);
        if (payState || "3".equals(regType) || (TextUtils.isEmpty(accId) && "2".equals(regType))) {
            btnExpertAssistance.setEnabled(false);
            btnExpertAssistance.setBackgroundResource(R.drawable.btn_red_pressed);
            btnExpertConsultation.setEnabled(false);
            btnExpertConsultation.setBackgroundResource(R.drawable.btn_red_pressed);
        } else {
            if (TextUtils.isEmpty(accId)) {
                btnExpertAssistance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallType = "2";
                        getOfficeList(1);
                    }
                });

                btnExpertConsultation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallType = "3";
                        getOfficeList(2);
                    }
                });
            } else {
                //呼叫协助
                if ("1".equals(regType)) {
                    btnExpertConsultation.setEnabled(false);
                    btnExpertConsultation.setBackgroundResource(R.drawable.btn_red_pressed);
                    btnExpertAssistance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallType = "2";

                        }
                    });
                }
                //呼叫专家
                else if ("2".equals(regType)) {
                    btnExpertAssistance.setEnabled(false);
                    btnExpertAssistance.setBackgroundResource(R.drawable.btn_red_pressed);
                    btnExpertConsultation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallType = "3";

                        }
                    });
                }
            }
        }
    }

    private void showMbrInfo(MbrInfo mbrInfo) {
        TextView tvName = findViewById(R.id.tv_patient_name);
        addText(tvName, mbrInfo.getName());
        TextView tvSex = findViewById(R.id.tv_patient_sex);
        String sex = mbrInfo.getSex().equals("1") ? "男" : "女";
        addText(tvSex, sex);
        TextView tvAge = findViewById(R.id.tv_patient_age);
        addText(tvAge, mbrInfo.getAge() + "岁");
        TextView tvMobile = findViewById(R.id.tv_patient_mobile);
        addText(tvMobile, mbrInfo.getPhone());
        TextView tvCard = findViewById(R.id.tv_patient_card);
        addText(tvCard, mbrInfo.getIdNumber());
        TextView tvAdd = findViewById(R.id.tv_patient_add);
        String add = getAdd(mbrInfo.getBirthCounty(), mbrInfo.getBirthProvince(), mbrInfo.getBirthCity());
        addText(tvAdd, add);
        TextView tvAddDetail = findViewById(R.id.tv_patient_add_detail);
        String addDetail = getAdd(mbrInfo.getProvince(), mbrInfo.getCity(), mbrInfo.getCountry(), mbrInfo.getAddress());
        addText(tvAddDetail, addDetail);
        TextView tvCost = findViewById(R.id.tv_patient_cost);
        addText(tvCost, "");
        ImageView ivPhoto = findViewById(R.id.iv_patient_photo);
        if(isPad(getContext())){
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + mbrInfo.getPersonimg())
                    .into(ivPhoto);
        }else{
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + mbrInfo.getPersonimg())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivPhoto);
        }
    }

    private void addText(TextView tv, String text) {
        tv.setText(tv.getText() + text);
    }

    private String getAdd(String s1, String s2, String s3) {
        StringBuilder sb = new StringBuilder();
        if (s1 != null && !s1.equals("中国"))
            if (s1 != null) sb.append(s1);
        if (s2 != null) sb.append(s2);
        if (s3 != null) sb.append(s3);
        return sb.toString();
    }

    private String getAdd(String s1, String s2, String s3, String s4) {
        StringBuilder sb = new StringBuilder();
        if (s1 != null) sb.append(s1);
        if (s2 != null) sb.append(s2);
        if (s3 != null) sb.append(s3);
        if (s4 != null) sb.append(s4);
        return sb.toString();
    }

    private void showDepartmentDialog(List<OfficeInfo> list, int type){
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.department_dialog, null);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DepartmentDialogAdapter adapter = new DepartmentDialogAdapter(getApplicationContext(),list);
        recyclerView.setAdapter(adapter);

        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        contentView.setLayoutParams(layoutParams);
        dialog.show();

        adapter.setOnItemViewClickListener(new BaseAdapter.OnItemViewClickListener() {
            @Override
            public void onItemViewClick(View view, int position) {
                dialog.dismiss();
                if (list.size() > position) {
                    getOffDocInfo(list.get(position).getOfficeId(), type);
                }
            }
        });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //随机获取科室医生信息
    private void getOffDocInfo(int officeId, int type){
        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getOffDocInfo/"+officeId+"/"+type+"/" + SpData.getPersonId();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                Log.e("zyb", "getOffDocInfo:" + data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String personId = jsonObject.getString("personid");
                String docName = jsonObject.getString("docName");
                String personimg = jsonObject.getString("personimg");
                Intent intent;
                if ("3".equals(mCallType)) {
                    intent = new Intent(getApplicationContext(), RoomActivity.class);
                    intent.putExtra(RoomActivity.EXTRA_CALL_TYPE,mCallType);
                    intent.putExtra(RoomActivity.EXTRA_REG_ID,regId);
                    intent.putExtra(RoomActivity.EXTRA_CALLING_USER_ID,personId);
                    intent.putExtra(RoomActivity.EXTRA_CALLING_USER_NAME,docName);
                    intent.putExtra(RoomActivity.EXTRA_CALLING_USER_IMG,personimg);
                } else {
                    intent = new Intent(PatientInfoActivity.this, TreatActivity.class);

                    intent.putExtra(RoomActivity.EXTRA_CALL_TYPE, mCallType);
                    intent.putExtra(RoomActivity.EXTRA_REG_ID,regId);
                    intent.putExtra(RoomActivity.EXTRA_CALLING_USER_ID,personId);
                    intent.putExtra(RoomActivity.EXTRA_CALLING_USER_NAME,docName);
                    intent.putExtra(RoomActivity.EXTRA_CALLING_USER_IMG,personimg);

                    intent.putExtra("personid", personid);
                    intent.putExtra("mbrId", mbrId);
                    intent.putExtra("regId", regId);
                    intent.putExtra("fee", fee);
                    intent.putExtra("payState", payState);
                    if (diaId != null && diaId.length() > 0)
                        intent.putExtra("diaId", diaId);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(getApplicationContext(), ex.getMessage());
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    //获取科室列表
    private void getOfficeList(int type){
        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getOfficeList/" + SpData.getPersonId() + "/"+ type;
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                List<OfficeInfo> list = jsonObject.getJSONArray("officeList").toJavaList(OfficeInfo.class);

                showDepartmentDialog(list, type);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(getApplicationContext(),ex.getMessage());
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
