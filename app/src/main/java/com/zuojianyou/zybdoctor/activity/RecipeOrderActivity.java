package com.zuojianyou.zybdoctor.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecipeOrderActivity extends BaseActivity {

    TextView tvName, tvSex, tvAge, tvAdd, tvSickName, tvYizhu,
            tvBoilType, tvNumber, tvDays, tvRevist, tvShuoming, tvYanfang, tvDaima,
            tvHospital, tvDoc, tvTime, tvId,
            tvRecName, tvRecPhone, tvRecAdd,
            tvShenfangyuan, tvTiaojiyuan, tvFuheyuan;

    RecyclerView rvMed;
    GridMedAdapter adapter;

    View btnClose, btnSign, btnPrint;

    String diaId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_order);
        diaId = getIntent().getStringExtra("diaId");

        btnClose = findViewById(R.id.ib_act_recipe_order_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSign = findViewById(R.id.btn_act_recipe_order_sign);
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpGetSign(diaId);
            }
        });
        btnPrint = findViewById(R.id.btn_act_recipe_order_print);

        tvName = findViewById(R.id.tv_act_recipe_order_name);
        tvSex = findViewById(R.id.tv_act_recipe_order_sex);
        tvAge = findViewById(R.id.tv_act_recipe_order_age);
        tvAdd = findViewById(R.id.tv_act_recipe_order_add);
        tvSickName = findViewById(R.id.tv_act_recipe_order_sick_name);
        tvHospital = findViewById(R.id.tv_act_recipe_order_hospital);
        tvDoc = findViewById(R.id.tv_act_recipe_order_doc);
        tvTime = findViewById(R.id.tv_act_recipe_order_time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tvTime.setText(tvTime.getText() + sdf.format(new Date()));
        tvYizhu = findViewById(R.id.tv_act_recipe_order_yizhu);
        tvBoilType = findViewById(R.id.tv_act_recipe_order_boil_type);
        tvNumber = findViewById(R.id.tv_act_recipe_order_yongyaoshuliang);
        tvDays = findViewById(R.id.tv_act_recipe_order_yongyaotianshu);
        tvRevist = findViewById(R.id.tv_act_recipe_order_fuzhenriqi);
        tvShuoming = findViewById(R.id.tv_act_recipe_order_fuyongfangfa);
        tvYanfang = findViewById(R.id.tv_act_recipe_order_yanfangming);
        tvDaima = findViewById(R.id.tv_act_recipe_order_fuwudaima);
        tvRecName = findViewById(R.id.tv_act_recipe_order_receiver_name);
        tvRecPhone = findViewById(R.id.tv_act_recipe_order_receiver_phone);
        tvRecAdd = findViewById(R.id.tv_act_recipe_order_receiver_add);
        tvShenfangyuan = findViewById(R.id.tv_act_recipe_order_shenfangyuan);
        tvTiaojiyuan = findViewById(R.id.tv_act_recipe_order_tiaojiyuan);
        tvFuheyuan = findViewById(R.id.tv_act_recipe_order_fuheyuan);

        rvMed = findViewById(R.id.rv_act_recipe_order_med);
        int spanCount = 2;
        if (isPad(getContext())) {
            spanCount = 3;
        }
        rvMed.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        rvMed.setNestedScrollingEnabled(false);
        rvMed.setHasFixedSize(true);
        adapter = new GridMedAdapter();
        rvMed.setAdapter(adapter);

        tvId = findViewById(R.id.tv_act_recipe_order_id);
        httpGetOrder(diaId);
    }

    private void httpGetOrder(String diaId) {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getRecipeOrderUrl(diaId);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject json = JSONObject.parseObject(data);

                JSONObject mbr = json.getJSONObject("mbrObj");
                String name = mbr.getString("name");
                tvName.setText("就诊人：" + name);
                String sex = mbr.getString("sex");
                tvSex.setText("性别：" + sex);
                String age = mbr.getString("age");
                tvAge.setText("年龄：" + age);
                String add = mbr.getString("birthProvince");
                if (TextUtils.isEmpty(add)) add = "外籍";
                tvAdd.setText("籍贯：" + add);

                JSONObject inq = json.getJSONObject("inqObj");
                String sickName = inq.getString("sickName");
                tvSickName.setText(sickName);
                String sumFee = inq.getString("sumFee");
                String sumFeeCh = inq.getString("sumFeeCh");
                String hospitalName = inq.getString("hospitalName");
                tvHospital.setText("诊所名称：" + hospitalName);
//                String docName = inq.getString("docName");
//                tvDoc.setText("医师：" + docName);
                String doctorAdvice = inq.getString("doctorAdvice");
                tvYizhu.setText(doctorAdvice);
                String revistDate = inq.getString("revistDate");
                tvRevist.setText("复诊日期：" + revistDate == null ? "" : revistDate);
                String reName = inq.getString("reName");
                tvRecName.setText("收货人：" + reName);
                String reMobile = inq.getString("reMobile");
                tvRecPhone.setText("电话：" + reMobile);
                String sendAddress = inq.getString("sendAddress");
                tvRecAdd.setText("收货地址：" + sendAddress);
                boolean isSign = inq.getBooleanValue("eleFlag");
                if (isSign) {
                    httpGetSign(diaId);
                }

                JSONObject pre = json.getJSONObject("preObj");
                if (pre != null && pre.size() > 0) {
                    JSONObject preChild = pre.getJSONObject("preObj");
                    String useNum = preChild.getString("useNum");
                    tvNumber.setText("用药数量：" + useNum + "副");
                    String dayNum = preChild.getString("dayNum");
                    tvDays.setText("用药天数：" + dayNum + "天");
                    String instruction = preChild.getString("instruction");
                    tvShuoming.setText(instruction);
                    String repiceName = preChild.getString("repiceName");
                    tvYanfang.setText("验方名称：" + repiceName);
                    String dealName = preChild.getString("dealName");
                    tvBoilType.setText(dealName);

                    JSONArray medList = pre.getJSONArray("medList");
                    if (medList != null && medList.size() > 0) {
                        for (int i = medList.size() - 1; i >= 0; i--) {
                            if (medList.getJSONObject(i).getString("typ").equals("1")) {
                                String strCode = medList.getJSONObject(i).getString("gdName");
                                tvDaima.setText("核心服务代码：" + strCode);
                                medList.remove(i);
                                break;
                            }
                        }
                    }
                    adapter.setArray(medList);
                    String id = preChild.getString("prescriptionId");
                    tvId.setText(id);
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
                hiddenLoadView();
            }
        }));
    }

    class GridMedAdapter extends RecyclerView.Adapter<MedHolder> {

        private JSONArray array;

        public void setArray(JSONArray array) {
            this.array = array;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView tv = new TextView(getContext());
            tv.setPadding(12, 12, 12, 12);
            return new MedHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull MedHolder medHolder, int i) {
            medHolder.setText(array.getJSONObject(i));
        }

        @Override
        public int getItemCount() {
            return array == null ? 0 : array.size();
        }
    }

    class MedHolder extends RecyclerView.ViewHolder {

        public MedHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setText(JSONObject jsonObject) {
            TextView tv = (TextView) itemView;
            String name = jsonObject.getString("gdName");
            String useNum = jsonObject.getString("useNum");
            String unit = jsonObject.getString("medUnit");
            tv.setText(name + useNum + unit);
        }
    }

    private void httpGetSign(String diaId) {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getDocSignUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("diagnoseId", diaId);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject json = JSONObject.parseObject(data);
                String signPath = json.getString("signPath");
                ImageView imageView = findViewById(R.id.iv_act_recipe_order_sign);
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + signPath).into(imageView);
                btnSign.setVisibility(View.GONE);
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
}
