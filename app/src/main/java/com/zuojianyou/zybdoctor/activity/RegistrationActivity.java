package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.AreaInfo;
import com.zuojianyou.zybdoctor.beans.OldUserInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.views.LocationSelector;
import com.zuojianyou.zybdoctor.views.SexSelector;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 挂号
 */
public class RegistrationActivity extends BaseActivity {

    public static final int AI_FACE_CODE = 205;//人脸识别

    OldUserInfo mUserInfo;

    List<AreaInfo> provinceList;
    List<AreaInfo> cityList;
    LocationSelector locationSelector;
    SexSelector sexSelector;
    TextView tvLocation, tvSex, areaTv;
    EditText etName, etMobile, etCard, etAge, etAdd, etCost;
    ImageView ivPhoto;

    View patientLayout;
    View btnPatientClose;
    ListView lvPatient;
    PatientAdapter patientAdapter;
    ProgressBar pbPatient;

    private LocationSelector lSelector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        cityList = JSONObject.parseArray(getAreaString("city.json"), AreaInfo.class);
        provinceList = JSONObject.parseArray(getAreaString("province.json"), AreaInfo.class);

        findViewById(R.id.ib_reg_btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.ib_reg_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText())) {
                    Toast.makeText(RegistrationActivity.this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etMobile.getText())) {
                    Toast.makeText(RegistrationActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etAge.getText())) {
                    Toast.makeText(RegistrationActivity.this, "请输入年龄！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tvLocation.getText())) {
                    Toast.makeText(RegistrationActivity.this, "请选择籍贯！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tvSex.getText())) {
                    Toast.makeText(RegistrationActivity.this, "请选择性别！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etCard.getText())) {
                    etCard.setText("");
                }
                if (TextUtils.isEmpty(etAdd.getText())) {
                    etAdd.setText("");
                }
                if (TextUtils.isEmpty(etCost.getText())) {
                    etCost.setText("0");
                }
                if (mUserInfo == null) {
                    mUserInfo = new OldUserInfo();
                    mUserInfo.setFacePath("");
                }
                mUserInfo.setName(etName.getText().toString().trim());
                mUserInfo.setPhone(etMobile.getText().toString().trim());
                mUserInfo.setIdNumber(etCard.getText().toString().trim());
                mUserInfo.setSex(String.valueOf(tvSex.getTag()));
                mUserInfo.setAge(Integer.parseInt(etAge.getText().toString().trim()));
                if (tvLocation.getTag(R.id.tag_country_id) != null)
                    mUserInfo.setBirthCounty(String.valueOf(tvLocation.getTag(R.id.tag_country_id)));
                if (tvLocation.getTag(R.id.tag_province_id) != null)
                    mUserInfo.setBirthProvince(String.valueOf(tvLocation.getTag(R.id.tag_province_id)));
                if (tvLocation.getTag(R.id.tag_city_id) != null)
                    mUserInfo.setBirthCity(String.valueOf(tvLocation.getTag(R.id.tag_city_id)));

                if (areaTv.getTag(R.id.tag_province_id) != null)
                    mUserInfo.setProvince(String.valueOf(areaTv.getTag(R.id.tag_province_id)));
                if (areaTv.getTag(R.id.tag_city_id) != null)
                    mUserInfo.setCity(String.valueOf(areaTv.getTag(R.id.tag_city_id)));
                if (areaTv.getTag(R.id.tag_area_id) != null)
                    mUserInfo.setCountry(String.valueOf(areaTv.getTag(R.id.tag_area_id)));

                mUserInfo.setAddress(etAdd.getText().toString().trim());
                mUserInfo.setFee(Double.parseDouble(etCost.getText().toString().trim()));
                submitPatient();
            }
        });

        initPatientLayout();

        tvLocation = findViewById(R.id.tv_reg_location);
        locationSelector = new LocationSelector(tvLocation, LocationSelector.TYPE_NO_AREA);
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationSelector.showSelector();
            }
        });

        tvSex = findViewById(R.id.et_reg_sex);
        sexSelector = new SexSelector(tvSex, true);
        tvSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sexSelector.showSelector();
            }
        });

        areaTv = findViewById(R.id.area_tv);
        lSelector = new LocationSelector(areaTv, LocationSelector.TYPE_NO_COUNTRY);
        areaTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lSelector.showSelector();
            }
        });

        etName = findViewById(R.id.et_reg_name);
        etAdd = findViewById(R.id.et_reg_add_detail);
        etAge = findViewById(R.id.et_reg_age);
        etCard = findViewById(R.id.et_reg_card);
        etCost = findViewById(R.id.et_reg_cost);
        etMobile = findViewById(R.id.et_reg_mobile);
        ivPhoto = findViewById(R.id.iv_reg_photo);
        ivPhoto.setOnClickListener(onBtnDiscernClicked);


        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || (mUserInfo != null && s.toString().equals(mUserInfo.getName()))) {
                    patientLayout.setVisibility(View.GONE);
                } else {
                    patientLayout.setVisibility(View.VISIBLE);
                    pbPatient.setVisibility(View.VISIBLE);
                    getNetPatient(s.toString());
                }

            }
        });

        httpGetFee();
    }

    private void initPatientLayout() {
        patientLayout = findViewById(R.id.rl_reg_patient_dialog);
        btnPatientClose = findViewById(R.id.ib_reg_btn_patient_close);
        lvPatient = findViewById(R.id.lv_reg_list_patient);
        patientAdapter = new PatientAdapter();
        lvPatient.setAdapter(patientAdapter);
        lvPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUserInfo = (OldUserInfo) patientAdapter.getItem(position);
                patientLayout.setVisibility(View.GONE);
                onPatientResult(mUserInfo);
            }
        });
        pbPatient = findViewById(R.id.pb_reg_patient);

        btnPatientClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelable != null && !cancelable.isCancelled()) {
                    cancelable.cancel();
                }
                patientLayout.setVisibility(View.GONE);
            }
        });
    }

    private void onPatientResult(OldUserInfo mUserInfo) {
        etName.setText(mUserInfo.getName());
        etMobile.setText(mUserInfo.getPhone());
        etAge.setText(String.valueOf(mUserInfo.getAge()));
        etCard.setText(mUserInfo.getIdNumber());
        etAdd.setText(mUserInfo.getAddress());
        if (mUserInfo.getFacePath() != null && mUserInfo.getFacePath().length() > 0) {
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + mUserInfo.getFacePath()).into(ivPhoto);
        }
        tvSex.setTag(mUserInfo.getSex());
        switch (mUserInfo.getSex()) {
            case "1":
                tvSex.setText("男");
                break;
            case "2":
                tvSex.setText("女");
                break;
            case "3":
                tvSex.setText("保密");
                break;
        }


        StringBuffer sb = new StringBuffer();
        if (mUserInfo.getBirthCounty() != null) {
            if (mUserInfo.getBirthCounty().equals("156")) {
                sb.append("中国");
                int provinceId = Integer.parseInt(mUserInfo.getBirthProvince());
                for (int i = 0; i < provinceList.size(); i++) {
                    if (provinceList.get(i).getAreaId() == provinceId) {
                        sb.append(provinceList.get(i).getAreaName());
                        break;
                    }
                }

                int cityId = Integer.parseInt(mUserInfo.getBirthCity());
                for (int i = 0; i < cityList.size(); i++) {
                    if (cityList.get(i).getAreaId() == cityId) {
                        sb.append(cityList.get(i).getAreaName());
                        break;
                    }
                }
            } else {
                sb.append("外籍");
            }
        }
        tvLocation.setText(sb.toString());
        tvLocation.setTag(R.id.tag_country_id, mUserInfo.getBirthCounty());
        tvLocation.setTag(R.id.tag_province_id, mUserInfo.getBirthProvince());
        tvLocation.setTag(R.id.tag_city_id, mUserInfo.getBirthCity());
    }

    class PatientAdapter extends BaseAdapter {

        private List<OldUserInfo> userInfo;

        public void setData(List<OldUserInfo> userInfo) {
            this.userInfo = userInfo;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return userInfo == null ? 0 : userInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return userInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_reg_patient_list, parent, false);
            TextView name = convertView.findViewById(R.id.tv_patient_name);
            TextView sex = convertView.findViewById(R.id.tv_patient_sex);
            TextView age = convertView.findViewById(R.id.tv_patient_age);
            TextView mobile = convertView.findViewById(R.id.tv_patient_mobile);
            OldUserInfo user = userInfo.get(position);
            name.setText(user.getName());
            sex.setText(user.getSex().equals("1") ? "男" : "女");
            age.setText(user.getAge() + "岁");
            mobile.setText(user.getPhone());
            return convertView;
        }
    }

    Callback.Cancelable cancelable;

    private void getNetPatient(String key) {
        if (!checkNetwork()) return;
        String url = ServerAPI.getOldUserUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.addParameter("hospitalId", "");
        entity.addParameter("name", key);
        entity.addParameter("pageSize", "20");
        cancelable = x.http().get(entity, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.d("RegistrationActivity", "result=" + result);
                JSONObject json = JSONObject.parseObject(result);
                int code = json.getIntValue("code");
                if (code == 0) {
                    pbPatient.setVisibility(View.GONE);
                    List<OldUserInfo> userInfo = json.getJSONArray("data").toJavaList(OldUserInfo.class);
                    patientAdapter.setData(userInfo);
                } else {
                    String errMsg = json.getString("errMsg");
                    Toast.makeText(RegistrationActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(RegistrationActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancelable != null && !cancelable.isCancelled()) {
            cancelable.cancel();
        }
    }

    private void httpGetFee() {
        if (!checkNetwork()) return;
        String url = ServerAPI.getRegFeeUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                String fee = jsonObject.getString("fee");
                etCost.setText(fee);
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

    private void submitPatient() {
        if (!checkNetwork()) return;
        findViewById(R.id.ib_reg_btn_ok).setEnabled(false);
        String url = ServerAPI.getSubRegUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setBodyContent(JSONObject.toJSONString(mUserInfo));
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("RegistrationActivity", "result=" + result);
                JSONObject json = JSONObject.parseObject(result);
                int code = json.getIntValue("code");
                if (code == 0) {
                    JSONObject jsonData = json.getJSONObject("data");
                    String registrationId = jsonData.getString("registrationId");
                    String mbrId = jsonData.getString("mbrId");
                    String personid = jsonData.getString("personid");
                    Intent intent = new Intent(RegistrationActivity.this, TreatActivity.class);
                    intent.putExtra("mbrId", mbrId);
                    intent.putExtra("regId", registrationId);
                    intent.putExtra("fee", mUserInfo.getFee());
                    intent.putExtra("personid", personid);
                    startActivity(intent);
                    finish();
                } else {
                    String errMsg = json.getString("errMsg");
                    Toast.makeText(RegistrationActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                findViewById(R.id.ib_reg_btn_ok).setEnabled(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                findViewById(R.id.ib_reg_btn_ok).setEnabled(true);
            }
        });
    }

    private String getAreaString(String fileName) {
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = getResources().getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            String jsonLine;
            while ((jsonLine = reader.readLine()) != null) {
                builder.append(jsonLine);
            }
            reader.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private String mTempPhotoPath;

    private View.OnClickListener onBtnDiscernClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intentAI = new Intent(getContext(), AiFaceActivity.class);
            startActivityForResult(intentAI, AI_FACE_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == AI_FACE_CODE) {
            int code = data.getIntExtra("type", -1);
            if (code == 0) {
                Toast.makeText(getContext(), "识别成功！", Toast.LENGTH_SHORT).show();
                String userInfo = data.getStringExtra("data");
                mUserInfo = JSONObject.parseObject(userInfo, OldUserInfo.class);
                if (TextUtils.isEmpty(etCost.getText())) {
                    etCost.setText("0");
                }
                mUserInfo.setFee(Double.parseDouble(etCost.getText().toString()));
                submitPatient();
            } else {
                Toast.makeText(getContext(), "识别成功，未录入本系统，请录入信息。", Toast.LENGTH_SHORT).show();
                String url = data.getStringExtra("url");
                if (mUserInfo == null)
                    mUserInfo = new OldUserInfo();
                mUserInfo.setFacePath(url);
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + url).into(ivPhoto);
            }
        }
    }


}
