package com.zuojianyou.zybdoctor.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加个人药方
 */
public class RecipeAddActivity extends BaseActivity {

    String cnName, enName, sickId;

    MedicineInfo selectPatent;
    EditText etName, etNum;
    TextView tvUnit;

    View dialogLayout;
    View btnDialogClose;
    ListView lvDialog;
    DialogListAdapter dialogListAdapter;
    ProgressBar pbDialog;

    EditText etRecipeName;
    ListView lvPatent;
    List<MedicineInfo> patentList;
    PatentAdapter patentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treat_patent);
        cnName = getIntent().getStringExtra("cnName");
        enName = getIntent().getStringExtra("enName");
        sickId = getIntent().getStringExtra("sickId");
        TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText(R.string.title_act_recipe_add);

        findViewById(R.id.ib_reg_btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.ib_reg_btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (patentList.size() == 0) {
                    Toast.makeText(getContext(), "药品不能为空！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etRecipeName.getText())) {
                    Toast.makeText(getContext(), "请填写处方名称！", Toast.LENGTH_SHORT).show();
                } else {
                    httpSubmit(etRecipeName.getText().toString().trim());
                }
            }
        });

        initSearchLayout();

        etName = findViewById(R.id.et_patent_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || (selectPatent != null && s.toString().equals(selectPatent.getGdName()))) {
                    dialogLayout.setVisibility(View.GONE);
                } else {
                    dialogLayout.setVisibility(View.VISIBLE);
                    pbDialog.setVisibility(View.VISIBLE);
                    getNetInputHelper(s.toString());
                }

            }
        });
        etNum = findViewById(R.id.et_patent_num);
        tvUnit = findViewById(R.id.tv_patent_unit);

        findViewById(R.id.btn_patent_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPatent != null) {
                    if (TextUtils.isEmpty(etNum.getText())) {
                        Toast.makeText(getContext(), "请输入用量！", Toast.LENGTH_SHORT).show();
                    } else {
                        selectPatent.setUseNum(etNum.getText().toString());
                        patentList.add(selectPatent);
                        patentAdapter.notifyDataSetChanged();
                        selectPatent = null;
                        etName.setText(null);
                        etNum.setText(null);
                        tvUnit.setText("单位");
                    }
                }
            }
        });

        etRecipeName = findViewById(R.id.et_recipe_name);
        etRecipeName.setVisibility(View.VISIBLE);
        lvPatent = findViewById(R.id.lv_patent_list);
        patentList = new ArrayList<>();
        patentAdapter = new PatentAdapter();
        lvPatent.setAdapter(patentAdapter);
    }

    private void initSearchLayout() {
        dialogLayout = findViewById(R.id.rl_ebm_sick_dialog);
        pbDialog = findViewById(R.id.pb_ebm_dialog_sick);
        btnDialogClose = findViewById(R.id.ib_ebm_dialog_btn_close);
        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelable != null && !cancelable.isCancelled()) {
                    cancelable.cancel();
                }
                dialogLayout.setVisibility(View.GONE);
            }
        });
        lvDialog = findViewById(R.id.lv_ebm_dialog_list_sick);
        dialogListAdapter = new DialogListAdapter();
        lvDialog.setAdapter(dialogListAdapter);
        lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPatent = (MedicineInfo) dialogListAdapter.getItem(position);
                etName.setText(selectPatent.getGdName());
                tvUnit.setText(selectPatent.getMedUnit());
            }
        });
    }

    class DialogListAdapter extends BaseAdapter {

        private List<MedicineInfo> list;

        public void setData(List<MedicineInfo> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvItem = new TextView(getContext());
            tvItem.setPadding(20, 12, 20, 12);
            convertView = tvItem;
            tvItem.setText(list.get(position).getGdName());
            return convertView;
        }
    }

    Callback.Cancelable cancelable;

    private void getNetInputHelper(String key) {
        if (!checkNetwork()) return;
        String url = ServerAPI.getPatentUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.addParameter("keyWd", key);
        entity.addParameter("medType", "1");
        cancelable = x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                pbDialog.setVisibility(View.GONE);
                List<MedicineInfo> list = JSONObject.parseArray(data, MedicineInfo.class);
                dialogListAdapter.setData(list);
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
    protected void onDestroy() {
        super.onDestroy();
        if (cancelable != null && !cancelable.isCancelled()) {
            cancelable.cancel();
        }
    }

    class PatentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return patentList.size();
        }

        @Override
        public Object getItem(int position) {
            return patentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_treat_patent_list, parent, false);
            TextView tvName = view.findViewById(R.id.tv_patent_item_name);
            TextView tvPrice = view.findViewById(R.id.tv_patent_item_price);
            TextView tvNum = view.findViewById(R.id.tv_patent_item_num);
            final MedicineInfo patentInfo = patentList.get(position);
            tvName.setText(patentInfo.getGdName());
//            tvPrice.setText("单价：" + patentInfo.getXrPice() + "元");
            tvNum.setEnabled(false);
            tvNum.setBackgroundColor(0x00000000);
            tvNum.setText(patentInfo.getUseNum() + patentInfo.getMedUnit());
            view.findViewById(R.id.ib_patent_item_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    patentList.remove(position);
                    notifyDataSetChanged();
                }
            });
            convertView = view;
            return convertView;
        }
    }

    private void httpSubmit(String name) {
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
                setResult(RESULT_OK);
                finish();
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
        for (int i = 0; i < patentList.size(); i++) {
            MedicineInfo med = patentList.get(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("medicineId", med.getMedicineId());
            jsonObject.put("dosage", med.getUseNum());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
