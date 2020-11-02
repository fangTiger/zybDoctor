package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.treat.MbrInfo;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

/**
 * 病人信息
 */
public class PatientInfoActivity extends BaseActivity {

    String personid, mbrId, regId, diaId;
    double fee;
    boolean payState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        personid = getIntent().getStringExtra("personid");
        mbrId = getIntent().getStringExtra("mbrId");
        regId = getIntent().getStringExtra("regId");
        diaId = getIntent().getStringExtra("diaId");
        fee = getIntent().getDoubleExtra("fee", 0);
        payState = getIntent().getBooleanExtra("payState", false);

        TextView tvFee = findViewById(R.id.tv_patient_cost);
        String strFee = String.format("%.2f", fee);
        addText(tvFee, strFee + "元");

        findViewById(R.id.btn_act_patient_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientInfoActivity.this, TreatActivity.class);
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
}
