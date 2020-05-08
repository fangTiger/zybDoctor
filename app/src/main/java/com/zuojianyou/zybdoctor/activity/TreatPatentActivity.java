package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
 * 添加成品药
 */
public class TreatPatentActivity extends BaseActivity {

    MedicineInfo selectPatent;
    RadioGroup rgType;
    View viewPrice;
    EditText etName, etPrice, etNum;
    TextView tvUnit, tvPrice;

    View dialogLayout;
    View btnDialogClose;
    ListView lvDialog;
    DialogListAdapter dialogListAdapter;
    ProgressBar pbDialog;

    ListView lvPatent;
    List<MedicineInfo> patentList;
    PatentAdapter patentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treat_patent);
        TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText(R.string.title_act_patent_add);

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
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    Intent data = new Intent();
                    data.putExtra("result", JSONObject.toJSONString(patentList));
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        initSearchLayout();

        viewPrice = findViewById(R.id.ll_patent_price);
        rgType = findViewById(R.id.rg_patent_type);
        RadioButton radioButton = (RadioButton) rgType.getChildAt(0);
        radioButton.setChecked(true);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_patent_type1) {
                    viewPrice.setVisibility(View.GONE);
                } else {
                    viewPrice.setVisibility(View.VISIBLE);
                }
            }
        });
        etPrice = findViewById(R.id.et_patent_price);
        tvPrice = findViewById(R.id.tv_patent_price_offer);

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
                if (viewPrice.getVisibility() == View.VISIBLE && selectPatent == null) {
                    if (TextUtils.isEmpty(etName.getText())) {
                        Toast.makeText(getContext(), "请输入名称！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(etPrice.getText())) {
                        Toast.makeText(getContext(), "请输入单价！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(etNum.getText())) {
                        Toast.makeText(getContext(), "请输入用量！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectPatent=new MedicineInfo();
                    selectPatent.setMedicineId("");
                    selectPatent.setTyp("34");
                    selectPatent.setGdName(etName.getText().toString().trim());
                    selectPatent.setUseNum(etNum.getText().toString().trim());
                    selectPatent.setRetailPrice(etPrice.getText().toString().trim());
                    selectPatent.setMedUnit("项");
                    patentList.add(selectPatent);
                    patentAdapter.notifyDataSetChanged();
                    selectPatent = null;
                    etName.setText(null);
                    etNum.setText(null);
                    etPrice.setText(null);
                    tvPrice.setText(null);
                    tvUnit.setText("单位");
                    etName.requestFocus();
                } else if (selectPatent != null) {
                    if (TextUtils.isEmpty(etNum.getText())) {
                        Toast.makeText(getContext(), "请输入用量！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (viewPrice.getVisibility() == View.VISIBLE) {
                            if (!TextUtils.isEmpty(etName.getText()) && !etName.getText().toString().equals(selectPatent.getGdName())) {
                                selectPatent.setMedicineId("");
                                selectPatent.setGdName(etName.getText().toString());
                            }
                            if (!TextUtils.isEmpty(etPrice.getText()) && !etPrice.getText().toString().equals(selectPatent.getRetailPrice())) {
                                selectPatent.setRetailPrice(etPrice.getText().toString());
                            }
                            selectPatent.setUseNum(etNum.getText().toString());
                            patentList.add(selectPatent);
                        } else {
                            boolean isExit = false;
                            for (int i = 0; i < patentList.size(); i++) {
                                MedicineInfo info = patentList.get(i);
                                if (info.getMedicineId().equals(selectPatent.getMedicineId())) {
                                    int useNum = Integer.parseInt(info.getUseNum());
                                    int addNum = Integer.parseInt(etNum.getText().toString());
                                    int sumNum = useNum + addNum;
                                    info.setUseNum(String.valueOf(sumNum));
                                    isExit = true;
                                    break;
                                }
                            }
                            if (!isExit) {
                                selectPatent.setUseNum(etNum.getText().toString());
                                patentList.add(selectPatent);
                            }
                        }
                        patentAdapter.notifyDataSetChanged();
                        selectPatent = null;
                        etName.setText(null);
                        etNum.setText(null);
                        etPrice.setText(null);
                        tvPrice.setText(null);
                        tvUnit.setText("单位");
                        etName.requestFocus();
                    }
                }
            }
        });

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
                etPrice.setText(selectPatent.getRetailPrice());
                tvPrice.setText("指导售价：" + selectPatent.getRetailPrice() + "元");
                etNum.requestFocus();
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
        RadioButton radioButton = findViewById(rgType.getCheckedRadioButtonId());
        String medType = (String) radioButton.getTag();
        entity.addParameter("medType", medType);
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
            tvPrice.setText("单价：" + patentInfo.getRetailPrice() + "元");
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


}
