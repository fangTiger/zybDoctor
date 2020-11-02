package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.DispatchListInfo;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MedicineAddActivity extends BaseActivity {

    String repiceId, centerId;
    int type;

    RecyclerView rvDispatch;
    DispatchAdapter dispatchAdapter;

    EditText etMedName, etMedWeight;
    TextView tvMedUnit;
    Button btnMedAdd, btnConfirm, btnCancel;

    RecyclerView rvMedList;
    List<MedicineInfo> patentList;
    PatentAdapter patentAdapter;

    MedicineInfo selectPatent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_add);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.title_act_med_add);

        repiceId = getIntent().getStringExtra("repiceId");
        centerId = getIntent().getStringExtra("centerId");
        type = getIntent().getIntExtra("type", 0);

        findViewById(R.id.ib_reg_btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        patentList = new ArrayList<>();

        if (type == 0) {
            ToastUtils.debugShow(getContext(), "参数错误！");
        } else if (type == 1) {
            findViewById(R.id.ll_recipe_detail_dispatch).setVisibility(View.GONE);
        } else if (type == 2) {
            initDispatchLayout();
        }

        etMedName = findViewById(R.id.et_recipe_detail_med_name);
        etMedWeight = findViewById(R.id.et_recipe_detail_med_weight);
        tvMedUnit = findViewById(R.id.tv_recipe_detail_med_unit);
        btnMedAdd = findViewById(R.id.btn_recipe_detail_med_add);
        btnMedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPatent != null) {
                    if (TextUtils.isEmpty(etMedWeight.getText())) {
                        Toast.makeText(getContext(), "请输入用量！", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isExit = false;
                        for (int i = 0; i < patentList.size(); i++) {
                            MedicineInfo info = patentList.get(i);
                            if (info.getMedicineId().equals(selectPatent.getMedicineId())) {
                                int useNum = Integer.parseInt(info.getUseNum());
                                int addNum = Integer.parseInt(etMedWeight.getText().toString());
                                int sumNum = useNum + addNum;
                                info.setUseNum(String.valueOf(sumNum));
                                isExit = true;
                                break;
                            }
                        }
                        if (!isExit) {
                            selectPatent.setUseNum(etMedWeight.getText().toString());
                            patentList.add(selectPatent);
                        }
                        patentAdapter.notifyDataSetChanged();
                        selectPatent = null;
                        etMedName.setText(null);
                        etMedWeight.setText(null);
                        tvMedUnit.setText(null);
                        etMedName.requestFocus();
                    }
                }
            }
        });
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (patentList == null || patentList.size() == 0) {
                    ToastUtils.show(getContext(), "请选择药品！");
                    return;
                }
                Intent data = new Intent();
                data.putExtra("medicines", JSONObject.toJSONString(patentList));
                setResult(RESULT_OK, data);
                finish();
            }
        });
        btnCancel = findViewById(R.id.btn_cancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        etMedName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || (selectPatent != null && s.toString().equals(selectPatent.getGdName()))) {
                    hiddenMedicinePopup();
                } else {
                    showMedicinePopup();
                    if (centerId == null) {
                        getNetInputHelper(s.toString());
                    } else {
                        getNetInputHelper(s.toString(), centerId);
                    }
                }

            }
        });

        rvMedList = findViewById(R.id.rv_recipe_detail_med);
        rvMedList.setNestedScrollingEnabled(false);
        rvMedList.setHasFixedSize(true);
        FlexboxLayoutManager flm = new FlexboxLayoutManager(getContext());
        flm.setFlexDirection(FlexDirection.ROW);
        flm.setFlexWrap(FlexWrap.WRAP);
        rvMedList.setLayoutManager(flm);
        patentList = new ArrayList<>();
        patentAdapter = new PatentAdapter();
        rvMedList.setAdapter(patentAdapter);
    }

    private void initDispatchLayout() {
        rvDispatch = findViewById(R.id.rv_recipe_detail_dispatch);
        rvDispatch.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        dispatchAdapter = new DispatchAdapter();
        rvDispatch.setAdapter(dispatchAdapter);
        httpGetDispatchList();
    }

    //获取配送中心
    private void httpGetDispatchList() {
        String url = ServerAPI.getDocDispatchListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<DispatchListInfo> rateList = JSONObject.parseArray(data, DispatchListInfo.class);
                if (rateList != null && rateList.size() > 0) {
                    if (centerId == null) {
                        rateList.get(0).setChecked(true);
                        centerId = rateList.get(0).getCenterId();
                    } else {
                        for (int i = 0; i < rateList.size(); i++) {
                            if (centerId.equals(rateList.get(i).getCenterId())) {
                                rateList.get(i).setChecked(true);
                                break;
                            }
                        }
                    }
                }
                dispatchAdapter.setData(rateList);
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

    class DispatchAdapter extends RecyclerView.Adapter<DispatchHolder> {

        List<DispatchListInfo> areaList;

        public void setData(List<DispatchListInfo> areaList) {
            this.areaList = areaList;
            notifyDataSetChanged();
        }

        public List<DispatchListInfo> getAreaList() {
            return areaList;
        }


        @NonNull
        @Override
        public DispatchHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            RadioButton radioButton = new RadioButton(getContext());
            return new DispatchHolder(radioButton);
        }

        @Override
        public void onBindViewHolder(@NonNull DispatchHolder dispatchHolder, int i) {
            DispatchListInfo info = areaList.get(i);
            dispatchHolder.rbDispatch.setChecked(info.isChecked());
            dispatchHolder.rbDispatch.setText(info.getCenterName());
            dispatchHolder.rbDispatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!info.isChecked()) {
                        if (patentList != null && patentList.size() > 0) {
                            View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
                            PopupWindow popupWindow = new PopupWindow(view, -1, -1);
                            TextView tv = view.findViewById(R.id.tv_alert_msg);
                            tv.setText("更换配送中心将清空已选草药。");
                            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                    patentAdapter.notifyDataSetChanged();
                                }
                            });
                            view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                    patentList.clear();
                                    patentAdapter.notifyDataSetChanged();
                                    for (int i = 0; i < areaList.size(); i++) {
                                        if (areaList.get(i) == info) {
                                            areaList.get(i).setChecked(true);
                                        } else {
                                            areaList.get(i).setChecked(false);
                                        }
                                    }
                                    notifyDataSetChanged();
                                    centerId = info.getCenterId();
                                }
                            });
                            popupWindow.showAtLocation(etMedName, Gravity.CENTER, 0, 0);
                        } else {
                            for (int i = 0; i < areaList.size(); i++) {
                                if (areaList.get(i) == info) {
                                    areaList.get(i).setChecked(true);
                                } else {
                                    areaList.get(i).setChecked(false);
                                }
                            }
                            notifyDataSetChanged();
                            centerId = info.getCenterId();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return areaList == null ? 0 : areaList.size();
        }
    }

    class DispatchHolder extends RecyclerView.ViewHolder {

        RadioButton rbDispatch;

        public DispatchHolder(@NonNull View itemView) {
            super(itemView);
            rbDispatch = (RadioButton) itemView;
        }
    }

    //----------------------------------------------------------

    Callback.Cancelable cancelable;
    PopupWindow popupMedicine;
    View vPopupMedicine;

    private void showMedicinePopup() {
        if (popupMedicine != null && popupMedicine.isShowing()) {
            ProgressBar pb = vPopupMedicine.findViewById(R.id.pb_ebm_dialog_sick);
            pb.setVisibility(View.VISIBLE);
        } else {
            vPopupMedicine = getLayoutInflater().inflate(R.layout.popup_recipe_detail_med, null);
            popupMedicine = new PopupWindow(vPopupMedicine, -2, -2);
            popupMedicine.setOutsideTouchable(true);
            popupMedicine.setBackgroundDrawable(new ColorDrawable());
            popupMedicine.showAsDropDown(etMedName);
        }
    }

    private void hiddenMedicinePopup() {
        if (popupMedicine != null && popupMedicine.isShowing()) {
            popupMedicine.dismiss();
            popupMedicine = null;
        }
    }

    private void getNetInputHelper(String key) {
        if (!checkNetwork()) return;
        String url = ServerAPI.getPatentUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.addParameter("keyWd", key);
        entity.addParameter("medType", "1");
        entity.addParameter("manager", "1");
        if (repiceId != null)
            entity.addParameter("repiceId", repiceId);
        cancelable = x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                onMedHelperReturn(data);
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

    private void getNetInputHelper(String key, String centerId) {
        if (!checkNetwork()) return;
        String url = ServerAPI.getMedicineUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyWd", key);
        jsonObject.put("centerId", centerId);
        entity.setBodyContent(jsonObject.toJSONString());
        cancelable = x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                onMedHelperReturn(data);
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

    private void onMedHelperReturn(String data) {
        List<MedicineInfo> list = JSONObject.parseArray(data, MedicineInfo.class);
        ProgressBar pb = vPopupMedicine.findViewById(R.id.pb_ebm_dialog_sick);
        pb.setVisibility(View.GONE);
        ListView lvDialog = vPopupMedicine.findViewById(R.id.lv_ebm_dialog_list_sick);
        lvDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogListAdapter dialogListAdapter = (DialogListAdapter) parent.getAdapter();
                selectPatent = (MedicineInfo) dialogListAdapter.getItem(position);
                etMedName.setText(selectPatent.getGdName());
                tvMedUnit.setText(selectPatent.getMedUnit());
                etMedWeight.requestFocus();
                hiddenMedicinePopup();
            }
        });
        if (lvDialog.getAdapter() == null) {
            DialogListAdapter dialogListAdapter = new DialogListAdapter();
            dialogListAdapter.setData(list);
            lvDialog.setAdapter(dialogListAdapter);
        } else {
            DialogListAdapter dialogListAdapter = (DialogListAdapter) lvDialog.getAdapter();
            dialogListAdapter.setData(list);
        }

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
            list.get(position).setManager("1");
            return convertView;
        }
    }

    //---------------------------------------------
    class PatentAdapter extends RecyclerView.Adapter<PatentHolder> {

        @NonNull
        @Override
        public PatentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_recipe_add_med_list, viewGroup, false);
            return new PatentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PatentHolder patentHolder, int i) {
            MedicineInfo info = patentList.get(i);
            patentHolder.tvMed.setText(info.getGdName() + info.getUseNum() + "g");
            patentHolder.ivDel.setTag(i);
            patentHolder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    patentList.remove(position);
                    patentAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return patentList == null ? 0 : patentList.size();
        }
    }

    class PatentHolder extends RecyclerView.ViewHolder {

        TextView tvMed;
        ImageView ivDel;

        public PatentHolder(@NonNull View itemView) {
            super(itemView);
            tvMed = itemView.findViewById(R.id.tv_med);
            ivDel = itemView.findViewById(R.id.iv_del);
        }
    }
}
