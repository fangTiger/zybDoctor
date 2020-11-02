package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class AddRecipeDialog extends Dialog {

    String cnName, enName, sickId;
    List<MedicineInfo> list;

    EditText etName;
    View pbWait, btnSubmit, btnCancel;

    public AddRecipeDialog(Context context, List<MedicineInfo> list, String cnName, String enName, String sickId) {
        super(context, R.style.AlertDialog);
        this.list = list;
        this.cnName = cnName;
        this.enName = enName;
        this.sickId = sickId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_recipe_add);
        etName = findViewById(R.id.et_dialog_recipe_add_name);
        pbWait = findViewById(R.id.progressBar);
        btnSubmit = findViewById(R.id.btn_dialog_recipe_add_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText())) {
                    ToastUtils.show(getContext(), "请输入个人处方名称！");
                    return;
                }
                pbWait.setVisibility(View.VISIBLE);
                httpAddRecipeSubmit(etName.getText().toString().trim());
            }
        });
        btnCancel = findViewById(R.id.btn_dialog_recipe_add_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void httpAddRecipeSubmit(String name) {
        String url = ServerAPI.getRecipeAddUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("sickName", cnName);
        if (!TextUtils.isEmpty(sickId))
            jsonObject.put("sickId", sickId);
        if (!TextUtils.isEmpty(enName))
            jsonObject.put("western", enName);
        jsonObject.put("medList", getMedArray());
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(getContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                dismiss();
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

    private JSONArray getMedArray() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            MedicineInfo med = list.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("medicineId", med.getMedicineId());
            jsonObject.put("dosage", med.getUseNum());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
