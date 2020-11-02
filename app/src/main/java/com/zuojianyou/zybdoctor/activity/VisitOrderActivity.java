package com.zuojianyou.zybdoctor.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.application.MyApplication;
import com.zuojianyou.zybdoctor.app.Constants;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.views.ImageGlideDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class VisitOrderActivity extends BaseActivity {

    boolean loop = true;
    boolean isPay = false;
    String diaId;
    TextView tvName, tvSex, tvAge, tvAdd, tvSickName, tvZhusu,
            tvGrs, tvJzs, tvJws, tvXbs, tvCostUpper, tvCostLower, tvHospital,
            tvDoc, tvTime, tvId;

    View btnClose, btnPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_order);

        btnClose = findViewById(R.id.ib_act_visit_order_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnPrint = findViewById(R.id.btn_act_recipe_order_print);

        tvName = findViewById(R.id.tv_act_visit_order_name);
        tvSex = findViewById(R.id.tv_act_visit_order_sex);
        tvAge = findViewById(R.id.tv_act_visit_order_age);
        tvAdd = findViewById(R.id.tv_act_visit_order_add);
        tvSickName = findViewById(R.id.tv_act_visit_order_sick_name);
        tvZhusu = findViewById(R.id.tv_act_visit_order_zhusu);
        tvGrs = findViewById(R.id.tv_act_visit_order_gerenshi);
        tvJzs = findViewById(R.id.tv_act_visit_order_jiazushi);
        tvJws = findViewById(R.id.tv_act_visit_order_jiwangshi);
        tvXbs = findViewById(R.id.tv_act_visit_order_xianbingshi);
        tvCostUpper = findViewById(R.id.tv_act_visit_order_cost_uppercase);
        tvCostLower = findViewById(R.id.tv_act_visit_order_cost_lowercase);
        tvHospital = findViewById(R.id.tv_act_visit_order_hospital);
        tvDoc = findViewById(R.id.tv_act_visit_order_doc);
        tvTime = findViewById(R.id.tv_act_visit_order_time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tvTime.setText(tvTime.getText() + sdf.format(new Date()));

        diaId = getIntent().getStringExtra("diaId");
        tvId = findViewById(R.id.tv_act_visit_order_id);
        tvId.setText(diaId);
        httpGetOrder(diaId);
        httpGetPayState(diaId);
    }

    //获取就诊单信息
    private void httpGetOrder(String diaId) {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getVisitOrderUrl(diaId);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject json = JSONObject.parseObject(result, Feature.OrderedField);
                int code = json.getIntValue("code");
                if (code == 0) {
                    onDataResult(json.getString("data"));
                } else if (code == 127) {
                    String errMsg = json.getString("errMsg");
                    Toast.makeText(MyApplication.getAppContext(), errMsg, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errMsg = json.getString("errMsg");
                    Toast.makeText(MyApplication.getAppContext(), errMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenLoadView();
            }
        });
    }

    public void onDataResult(String data) {
        JSONObject json = JSONObject.parseObject(data);
        //就诊人信息
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
        //问诊信息
        JSONObject inq = json.getJSONObject("inqObj");
        String diagnoseId = inq.getString("diagnoseId");
        String complaint = inq.getString("complaint");
        tvZhusu.setText("主诉：" + complaint);
        String sickName = inq.getString("sickName");
        tvSickName.setText(sickName);
        String sumFee = inq.getString("sumFee");
        tvCostLower.setText("￥" + sumFee);
        String sumFeeCh = inq.getString("sumFeeCh");
        tvCostUpper.setText(sumFeeCh);
        String hospitalName = inq.getString("hospitalName");
        tvHospital.setText(hospitalName);
        String docName = inq.getString("docName");
        tvDoc.setText("医师：" + docName);
        //就诊人历史
        JSONObject mbrHis = json.getJSONObject("mbrHisObj");
        //现病史
        String present = mbrHis.getString("present");
        tvXbs.setText("现病史：" + present);
        //既往史
        JSONObject opration = mbrHis.getJSONObject("opration");
        StringBuilder sbOpration = new StringBuilder("既往史：");
        if (opration != null && opration.size() > 0) {
            JSONArray oprationArr = new JSONArray();
            oprationArr.addAll(opration.getJSONArray("allergy"));
            oprationArr.addAll(opration.getJSONArray("food"));
            oprationArr.addAll(opration.getJSONArray("other"));
            for (int i = 0; i < oprationArr.size(); i++) {
                sbOpration.append(oprationArr.getString(i));
                sbOpration.append(";");
            }
        }
        tvJws.setText(sbOpration.toString());
        //家族史
        JSONArray heredityArr = mbrHis.getJSONArray("heredity");
        StringBuilder sbHeredity = new StringBuilder("家族史：");
        if (heredityArr != null) {
            for (int i = 0; i < heredityArr.size(); i++) {
                sbHeredity.append(heredityArr.getString(i));
                sbHeredity.append(";");
            }
        }
        tvJzs.setText(sbHeredity.toString());
        //个人史
        JSONObject personalObj = mbrHis.getJSONObject("personal");
        StringBuilder sbPersonal = new StringBuilder("个人史：");
        if (personalObj != null) {
            if (personalObj.getJSONObject("marry") != null
                    && personalObj.getJSONObject("marry").getString("keyName") != null) {
                sbPersonal.append(personalObj.getJSONObject("marry").getString("keyName"));
                sbPersonal.append(";");
            }
            if (personalObj.getJSONObject("child") != null
                    && personalObj.getJSONObject("child").getString("keyName") != null) {
                sbPersonal.append(personalObj.getJSONObject("child").getString("keyName"));
                sbPersonal.append(";");
            }
            if (personalObj.getJSONArray("allergy") != null &&
                    personalObj.getJSONArray("allergy").size() > 0) {
                sbPersonal.append("药物过敏:");
                for (int i = 0; i < personalObj.getJSONArray("allergy").size(); i++) {
                    sbPersonal.append(personalObj.getJSONArray("allergy").getString(i));
                    sbPersonal.append(";");
                }
            }
            if (personalObj.getJSONArray("food") != null &&
                    personalObj.getJSONArray("food").size() > 0) {
                sbPersonal.append("食物/接触物过敏:");
                for (int i = 0; i < personalObj.getJSONArray("food").size(); i++) {
                    sbPersonal.append(personalObj.getJSONArray("food").getString(i));
                    sbPersonal.append(";");
                }
            }
            if (personalObj.getJSONArray("habit") != null &&
                    personalObj.getJSONArray("habit").size() > 0) {
                sbPersonal.append("传染病史:");
                for (int i = 0; i < personalObj.getJSONArray("habit").size(); i++) {
                    sbPersonal.append(personalObj.getJSONArray("habit").getString(i));
                    sbPersonal.append(";");
                }
            }
        }
        tvGrs.setText(sbPersonal.toString());

        httpGetPayCode(diaId);
    }

    //获取支付二维码
    private void httpGetPayCode(String diaId) {
        String urlWx = ServerAPI.getPayCodeWxUrl(diaId);
        RequestParams entityWx = new RequestParams(urlWx);
        ServerAPI.addHeader(entityWx);
        x.http().get(entityWx, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                String imagePath = JSONObject.parseObject(data).getString("img_path");
                ImageView imageView = findViewById(R.id.iv_act_visit_order_pay_code_wx);
                Glide.with(getContext()).load(imagePath).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageGlideDialog(getContext(), imagePath, "").show();
                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        getWXPay();
                        return true;
                    }
                });
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

        String urlZfb = ServerAPI.getPayCodeZfbUrl(diaId);
        RequestParams entityZfb = new RequestParams(urlZfb);
        ServerAPI.addHeader(entityZfb);
        x.http().get(entityZfb, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                String imagePath = JSONObject.parseObject(data).getString("img_path");
                ImageView imageView = findViewById(R.id.iv_act_visit_order_pay_code_zfb);
                Glide.with(getContext()).load(imagePath).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageGlideDialog(getContext(), imagePath, "").show();
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        getAlipayPay();
                        return true;
                    }
                });
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


    private void getWXPay(){
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.registerApp(Constants.APP_ID);
        String url = ServerAPI.BASE_DOMAIN + ServerAPI.WX_DOC_APP_PAY + "/2/" + diaId;
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {

            @Override
            public void onSuccess(String data) {
                JSONObject datas = JSONObject.parseObject(data);
                PayReq req = new PayReq();
                req.appId = Constants.APP_ID;
                req.sign = datas.getString("sign");
                req.timeStamp = datas.getString("timestamp");
                req.nonceStr = datas.getString("noncestr");
                req.partnerId = datas.getString("partnerid");
                req.prepayId = datas.getString("prepayid");
                req.packageValue = datas.getString("package");
                req.extData = "app data"; // optional
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                api.sendReq(req);
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

    private void getAlipayPay(){

        String url = ServerAPI.BASE_DOMAIN + ServerAPI.ALI_DOC_APP_PAY + "/1/" + diaId;
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {

            @Override
            public void onSuccess(String data) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(VisitOrderActivity.this);
                        Map<String, String> result = alipay.payV2(data, true);

                        String resultStatus = result.get("resultStatus");
                        // 判断resultStatus 为9000则代表支付成功
                        if (BuildConfig.DEBUG) {
                            Log.e("zyb","resultStatus:" + resultStatus);
                        }
                    }
                }).start();
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



    //轮询支付结果
    Callback.Cancelable cancelable;

    private void httpGetPayState(String diaId) {
        String url = ServerAPI.getPayStateUrl(diaId);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        cancelable = x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                if (jsonObject.getIntValue("payState") == 1) {
                    isPay = true;
                    String payType = jsonObject.getString("payType");
                    ImageView ivPayType = findViewById(R.id.iv_act_visit_order_pay_ic);
                    TextView tvPayType = findViewById(R.id.tv_act_visit_order_pay_type);
                    if (payType.equals("1") || payType.equals("3")) {
                        ivPayType.setImageResource(R.mipmap.ic_pay_wx);
                        tvPayType.setText("微信支付");
                    } else if (payType.equals("2") || payType.equals("4")) {
                        ivPayType.setImageResource(R.mipmap.ic_pay_zfb);
                        tvPayType.setText("支付宝支付");
                    }
                    String payTime = jsonObject.getString("payTime");
                    TextView tvPayTime = findViewById(R.id.tv_act_visit_order_pay_time);
                    StringBuilder sb = new StringBuilder();
                    sb.append(payTime.substring(0, 4));
                    sb.append("-");
                    sb.append(payTime.substring(4, 6));
                    sb.append("-");
                    sb.append(payTime.substring(6, 8));
                    sb.append(" ");
                    sb.append(payTime.substring(8, 10));
                    sb.append(":");
                    sb.append(payTime.substring(10));
                    tvPayTime.setText(tvPayTime.getText() + sb.toString());
                    findViewById(R.id.ll_act_visit_order_code).setVisibility(View.INVISIBLE);
                    findViewById(R.id.rl_act_visit_order_pay_tip).setVisibility(View.VISIBLE);
                } else if (loop) {
                    loopRequest();
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

    @Override
    public void onBackPressed() {
        if (isPay) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cancelable != null) {
            cancelable.cancel();
            loop = false;
        }
    }

    private void loopRequest() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                httpGetPayState(diaId);
            }
        }, 1000);
    }
}
