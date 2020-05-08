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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.DispatchListInfo;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.beans.OfficeInfo;
import com.zuojianyou.zybdoctor.beans.OfficeSickInfo;
import com.zuojianyou.zybdoctor.beans.RecipeTreeItem;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.ToastUtils;
import com.zuojianyou.zybdoctor.views.ScrollFlexBoxManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MyRecipeAddActivity extends BaseActivity {

    public static final int TYPE_ADD = 1;
    public static final int TYPE_EDIT = 2;
    public static final int TYPE_DETAIL = 3;
    public static final int TYPE_SAVE = 4;

    int type;
    String repiceId, sickId;
    int officeId, offsickId;

    EditText etRecipeName, etCnName, etEnName, etMedName, etMedWeight,
            etCnGist, etSickReason, etTreatWay, etEnGist, etDocAdvice, etMedUsage;
    TextView tvTypeName, tvDispatch, tvMedUnit;
    TextView tvOfficeName, tvOfficeSickName;
    Button btnMedAdd, btnConfirm, btnCancel;
    LinearLayout llDispatch, llMedName, llMedWeight, llBtn;

    RecyclerView rvMedList;
    List<MedicineInfo> patentList;
    PatentAdapter patentAdapter;

    MedicineInfo selectPatent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe_detail);

        etRecipeName = findViewById(R.id.et_recipe_detail_recipe_name);
        etCnName = findViewById(R.id.et_recipe_detail_cn_name);
        etEnName = findViewById(R.id.et_recipe_detail_en_name);
        etMedName = findViewById(R.id.et_recipe_detail_med_name);
        etMedWeight = findViewById(R.id.et_recipe_detail_med_weight);
        etCnGist = findViewById(R.id.et_recipe_detail_cn_gist);
        etSickReason = findViewById(R.id.et_recipe_detail_sick_reason);
        etTreatWay = findViewById(R.id.et_recipe_detail_treat_way);
        etEnGist = findViewById(R.id.et_recipe_detail_en_gist);
        etDocAdvice = findViewById(R.id.et_recipe_detail_doc_advice);
        etMedUsage = findViewById(R.id.et_recipe_detail_med_usage);
        tvMedUnit = findViewById(R.id.tv_recipe_detail_med_unit);
        tvTypeName = findViewById(R.id.tv_recipe_detail_type_name);
        tvDispatch = findViewById(R.id.tv_recipe_detail_dispatch);
        btnMedAdd = findViewById(R.id.btn_recipe_detail_med_add);
        tvOfficeName = findViewById(R.id.tv_recipe_detail_office_name);
        tvOfficeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupOffice();
            }
        });
        tvOfficeSickName = findViewById(R.id.tv_recipe_detail_office_sick_name);
        tvOfficeSickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (officeId == 0) {
                    ToastUtils.show(getContext(), "请先选择所属科室！");
                } else {
                    httpOfficeSickList(officeId);
                }
            }
        });
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (patentList.size() == 0) {
                    Toast.makeText(getContext(), "药品不能为空！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etRecipeName.getText())) {
                    Toast.makeText(getContext(), "请填写处方名称！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etCnName.getText())) {
                    Toast.makeText(getContext(), "请填写中医诊断名称！", Toast.LENGTH_SHORT).show();
                } else if (tvTypeName.getTag() == null) {
                    Toast.makeText(getContext(), "请选择所属病症！", Toast.LENGTH_SHORT).show();
                } else if (officeId == 0) {
                    Toast.makeText(getContext(), "请选择所属科室！", Toast.LENGTH_SHORT).show();
                } else {
                    httpSubmit();
                }
            }
        });
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llDispatch = findViewById(R.id.ll_recipe_detail_dispatch);
        llMedName = findViewById(R.id.ll_recipe_detail_med_name);
        llMedWeight = findViewById(R.id.ll_recipe_detail_med_weight);
        llBtn = findViewById(R.id.ll_recipe_detail_btn);

        rvMedList = findViewById(R.id.rv_recipe_detail_med);
        rvMedList.setNestedScrollingEnabled(false);
        rvMedList.setHasFixedSize(true);
        FlexboxLayoutManager flm = new ScrollFlexBoxManager(getContext());
        flm.setFlexDirection(FlexDirection.ROW);
        flm.setFlexWrap(FlexWrap.WRAP);
        rvMedList.setLayoutManager(flm);
        patentList = new ArrayList<>();
        patentAdapter = new PatentAdapter();
        rvMedList.setAdapter(patentAdapter);

        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        type = getIntent().getIntExtra("type", -1);
        TextView tvTitle = findViewById(R.id.tv_title);
        switch (type) {
            case TYPE_SAVE:
                tvTitle.setText("保存个人处方");
                String data = getIntent().getStringExtra("data");
                initSaveType(data);
                break;
            case TYPE_ADD:
                tvTitle.setText("新建个人处方");
                break;
            case TYPE_EDIT:
                tvTitle.setText("编辑个人处方");
                repiceId = getIntent().getStringExtra("recipeId");
                httpGetRecipeDetail(repiceId);
                break;
            case TYPE_DETAIL:
                tvTitle.setText("个人处方详情");
                repiceId = getIntent().getStringExtra("recipeId");
                httpGetRecipeDetail(repiceId);
                break;
        }

        tvTypeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypePopup();
            }
        });

        tvDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDispatchPopup();
            }
        });

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
                    if (tvDispatch.getTag() == null) {
                        getNetInputHelper(s.toString());
                    } else {
                        String centerId = (String) tvDispatch.getTag();
                        getNetInputHelper(s.toString(), centerId);
                    }
                }

            }
        });

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
                        tvMedUnit.setText("单位");
                        etMedName.requestFocus();
                    }
                }
            }
        });

        httpOfficeList();
    }

    private void initSaveType(String data) {
        JSONObject result = JSONObject.parseObject(data);
        etCnName.setText(result.getString("cnName"));
        etEnName.setText(result.getString("westName"));
        etTreatWay.setText(result.getString("therapies"));
        etSickReason.setText(result.getString("pathogeny"));
        etMedUsage.setText(result.getString("tisane"));
        etDocAdvice.setText(result.getString("doctorAdvice"));
        patentList = result.getJSONArray("medList").toJavaList(MedicineInfo.class);
        patentAdapter.notifyDataSetChanged();

        llDispatch.setVisibility(View.GONE);
        llMedName.setVisibility(View.GONE);
        llMedWeight.setVisibility(View.GONE);

        String sickId = result.getString("sickId");
        if (sickId != null && sickId.contains("-")) {
            officeId = Integer.parseInt(sickId.substring(0, sickId.indexOf('-')));
            offsickId = Integer.parseInt(sickId.substring(sickId.indexOf('-') + 1));

            if (officeId != 0)
                httpGetOfficeName(officeId);
            if (offsickId != 0 && offsickId != 0)
                httpGetSickName(officeId, offsickId);
        }
    }

    //----------------------所属病症---------------------
    PopupWindow popupType;
    View vPopupType;

    private void showTypePopup() {
        if (popupType != null && popupType.isShowing()) return;
        vPopupType = getLayoutInflater().inflate(R.layout.popup_recipe_detail_type, null);
        vPopupType.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenTypePopup();
            }
        });
        popupType = new PopupWindow(vPopupType, -2, -2);
        popupType.setOutsideTouchable(true);
        popupType.setBackgroundDrawable(new ColorDrawable());
        popupType.showAtLocation(tvTypeName.getRootView(), Gravity.CENTER, 0, 0);
        httpGetMenu();
    }

    private void hiddenTypePopup() {
        if (popupType != null && popupType.isShowing()) {
            popupType.dismiss();
            popupType = null;
        }
    }

    private void httpGetMenu() {
        String url = ServerAPI.getMyRecipeTreeUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<RecipeTreeItem> list = JSONObject.parseArray(data, RecipeTreeItem.class);
                calculateNodeLevel(list, 0);
                RecyclerView rvMenu = vPopupType.findViewById(R.id.rv_act_my_recipe_menu);
                rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
                rvMenu.setNestedScrollingEnabled(false);
                rvMenu.setHasFixedSize(true);
                TreeAdapter adapter = new TreeAdapter(list);
                rvMenu.setAdapter(adapter);
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

    private void calculateNodeLevel(List<RecipeTreeItem> list, int level) {
        if (list == null || list.size() == 0) return;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setLevel(level);
            calculateNodeLevel(list.get(i).getChildren(), level + 1);
            if (list.get(i).getChildren() == null) {
                list.get(i).setLeaf(true);
            }
        }
    }

    class TreeAdapter extends RecyclerView.Adapter<TreeHolder> {

        List<RecipeTreeItem> mList;

        public TreeAdapter(List<RecipeTreeItem> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public TreeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_act_my_recipe_tree, viewGroup, false);
            return new TreeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TreeHolder treeHolder, int i) {
            treeHolder.tvNodeName.setText(mList.get(i).getDsickName());
            treeHolder.tvNodeName.setTag(mList.get(i));
            treeHolder.tvNodeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecipeTreeItem item = (RecipeTreeItem) v.getTag();
                    tvTypeName.setText(item.getDsickName());
                    tvTypeName.setTag(item.getDsickId());
                    hiddenTypePopup();
                }
            });
            if (mList.get(i).getChildren() != null && mList.get(i).getChildren().size() > 0) {
                treeHolder.rvChildren.setLayoutManager(new LinearLayoutManager(getContext()));
                treeHolder.rvChildren.setNestedScrollingEnabled(false);
                treeHolder.rvChildren.setHasFixedSize(true);
                TreeAdapter adapter = new TreeAdapter(mList.get(i).getChildren());
                treeHolder.rvChildren.setAdapter(adapter);

                if (mList.get(i).isSpread()) {
                    treeHolder.ivNodeIcon.setImageResource(R.mipmap.ic_list_close);
                } else {
                    treeHolder.ivNodeIcon.setImageResource(R.mipmap.ic_list_expand);
                }
            } else {
                treeHolder.ivNodeIcon.setVisibility(View.INVISIBLE);
            }
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) treeHolder.ivNodeIcon.getLayoutParams();
            lp.setMargins(50 * mList.get(i).getLevel(), 0, 0, 0);
            treeHolder.ivNodeIcon.setLayoutParams(lp);
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    class TreeHolder extends RecyclerView.ViewHolder {

        ImageView ivNodeIcon;
        TextView tvNodeName;
        RecyclerView rvChildren;

        public TreeHolder(@NonNull View itemView) {
            super(itemView);
            ivNodeIcon = itemView.findViewById(R.id.iv_tree_item_icon);
            tvNodeName = itemView.findViewById(R.id.tv_tree_item_node);
            rvChildren = itemView.findViewById(R.id.rv_tree_item_list);
        }
    }

    //----------------------配送中心--------------------------
    PopupWindow popupDispatch;
    View vPopupDispatch;

    private void showDispatchPopup() {
        if (popupDispatch != null && popupDispatch.isShowing()) return;
        vPopupDispatch = getLayoutInflater().inflate(R.layout.popup_recipe_detail_dispatch, null);
        popupDispatch = new PopupWindow(vPopupDispatch, -2, -2);
        popupDispatch.setOutsideTouchable(true);
        popupDispatch.setBackgroundDrawable(new ColorDrawable());
        popupDispatch.showAsDropDown(tvDispatch);
        httpGetDispatch();
    }

    private void hiddenDispatchPopup() {
        if (popupDispatch != null && popupDispatch.isShowing()) {
            popupDispatch.dismiss();
            popupDispatch = null;
        }
    }

    private void httpGetDispatch() {
        String url = ServerAPI.getDispatchListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<DispatchListInfo> rateList = JSONObject.parseArray(data, DispatchListInfo.class);
                RecyclerView rvMenu = vPopupDispatch.findViewById(R.id.rv_act_my_recipe_menu);
                rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
                rvMenu.setNestedScrollingEnabled(false);
                rvMenu.setHasFixedSize(true);
                DispatchAdapter adapter = new DispatchAdapter(rateList);
                rvMenu.setAdapter(adapter);
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

    private void httpGetDispatchName(String id) {
        String url = ServerAPI.getDispatchListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<DispatchListInfo> rateList = JSONObject.parseArray(data, DispatchListInfo.class);
                for (int i = 0; i < rateList.size(); i++) {
                    if (rateList.get(i).getCenterId().equals(id)) {
                        tvDispatch.setText(rateList.get(i).getCenterName());
                        break;
                    }
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

    class DispatchAdapter extends RecyclerView.Adapter<DispatchHolder> {

        List<DispatchListInfo> mList;

        public DispatchAdapter(List<DispatchListInfo> mList) {
            this.mList = mList;
        }

        @NonNull
        @Override
        public DispatchHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView tvItem = new TextView(getContext());
            tvItem.setPadding(8, 8, 8, 8);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
            tvItem.setLayoutParams(lp);
            return new DispatchHolder(tvItem);
        }

        @Override
        public void onBindViewHolder(@NonNull DispatchHolder dispatchHolder, int i) {
            DispatchListInfo info = mList.get(i);
            dispatchHolder.tvItem.setText(info.getCenterName());
            dispatchHolder.tvItem.setTag(info);
            dispatchHolder.tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DispatchListInfo info = (DispatchListInfo) v.getTag();
                    if (tvDispatch.getTag() != null) {
                        String id = (String) tvDispatch.getTag();
                        if (id.equals(info.getCenterId())) {
                            hiddenDispatchPopup();
                            return;
                        }
                    }
                    if (patentList.size() > 0) {
                        View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
                        PopupWindow popupWindow = new PopupWindow(view, -1, -1);
                        TextView tv = view.findViewById(R.id.tv_alert_msg);
                        tv.setText("更换配送中心将清空已选草药。");
                        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                                patentList.clear();
                                patentAdapter.notifyDataSetChanged();
                                tvDispatch.setText(info.getCenterName());
                                tvDispatch.setTag(info.getCenterId());
                                hiddenDispatchPopup();
                            }
                        });
                        popupWindow.showAtLocation(tvDispatch, Gravity.CENTER, 0, 0);
                    } else {
                        tvDispatch.setText(info.getCenterName());
                        tvDispatch.setTag(info.getCenterId());
                        hiddenDispatchPopup();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    class DispatchHolder extends RecyclerView.ViewHolder {

        TextView tvItem;

        public DispatchHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = (TextView) itemView;
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
            if (type == TYPE_DETAIL || type == TYPE_SAVE) {
                patentHolder.ivDel.setVisibility(View.GONE);
            }
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

    //----------------------------------------------------------
    private void httpSubmit() {
        String url = ServerAPI.getRecipeAddUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", etRecipeName.getText().toString());
        jsonObject.put("sickName", etCnName.getText().toString());
        jsonObject.put("dsickId", tvTypeName.getTag());
        jsonObject.put("medList", getMedArray());
        jsonObject.put("officeId", officeId);
        jsonObject.put("offsickId", offsickId);
        if (!TextUtils.isEmpty(sickId))
            jsonObject.put("sickId", sickId);
        if (!TextUtils.isEmpty(repiceId))
            jsonObject.put("repiceId", repiceId);
        if (!TextUtils.isEmpty(etEnName.getText()))
            jsonObject.put("western", etEnName.getText().toString());
        if (!TextUtils.isEmpty(etCnGist.getText()))
            jsonObject.put("symptom", etCnGist.getText().toString());
        if (!TextUtils.isEmpty(etEnGist.getText()))
            jsonObject.put("westernDesc", etEnGist.getText().toString());
        if (!TextUtils.isEmpty(etSickReason.getText()))
            jsonObject.put("pathogeny", etSickReason.getText().toString());
        if (!TextUtils.isEmpty(etTreatWay.getText()))
            jsonObject.put("therapies", etTreatWay.getText().toString());
        if (!TextUtils.isEmpty(etDocAdvice.getText()))
            jsonObject.put("doctorAdvice", etDocAdvice.getText().toString());
        if (!TextUtils.isEmpty(etMedUsage.getText()))
            jsonObject.put("tisane", etMedUsage.getText().toString());

        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                String tip;
                if (type == TYPE_EDIT) {
                    tip = "修改成功！";
                } else {
                    tip = "添加成功！";
                }
                Toast.makeText(getContext(), tip, Toast.LENGTH_SHORT).show();
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

    //------------------------------------------------------------
    private void httpGetRecipeDetail(String id) {
        String url = ServerAPI.getRecipeDetailUrl(id);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject result = JSONObject.parseObject(data);
                etRecipeName.setText(result.getString("name"));
                etCnName.setText(result.getString("sickName"));
                etEnName.setText(result.getString("western"));
                etEnGist.setText(result.getString("westernDesc"));
                etCnGist.setText(result.getString("symptom"));
                etDocAdvice.setText(result.getString("doctorAdvice"));
                tvTypeName.setText(result.getString("dsickName"));
                tvTypeName.setTag(result.getString("dsickId"));
                etSickReason.setText(result.getString("pathogeny"));
                etTreatWay.setText(result.getString("therapies"));
                etMedUsage.setText(result.getString("tisane"));
                tvDispatch.setTag(result.getString("centerId"));
                tvOfficeName.setText(result.getString("officeName"));
                tvOfficeSickName.setText(result.getString("offsickName"));
                officeId = result.getIntValue("officeId");
                offsickId = result.getIntValue("offsickId");
                httpGetDispatchName(result.getString("centerId"));
                patentList = result.getJSONArray("medList").toJavaList(MedicineInfo.class);
                patentAdapter.notifyDataSetChanged();
                if (type == TYPE_DETAIL) {
                    etRecipeName.setEnabled(false);
                    etCnName.setEnabled(false);
                    etEnName.setEnabled(false);
                    etMedName.setEnabled(false);
                    etMedWeight.setEnabled(false);
                    etCnGist.setEnabled(false);
                    etSickReason.setEnabled(false);
                    etTreatWay.setEnabled(false);
                    etEnGist.setEnabled(false);
                    etDocAdvice.setEnabled(false);
                    etMedUsage.setEnabled(false);
                    tvTypeName.setEnabled(false);
                    tvDispatch.setEnabled(false);
                    tvOfficeName.setEnabled(false);
                    tvOfficeSickName.setEnabled(false);

//                    llDispatch.setVisibility(View.GONE);
                    llMedName.setVisibility(View.GONE);
                    llMedWeight.setVisibility(View.GONE);
                    llBtn.setVisibility(View.GONE);
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


    //-----------------返回键先释放PopupWindow-------------------
    @Override
    public void onBackPressed() {
        if (popupDispatch != null && popupDispatch.isShowing()) {
            hiddenDispatchPopup();
        } else if (popupType != null && popupType.isShowing()) {
            hiddenTypePopup();
        } else {
            super.onBackPressed();
        }
    }

    List<OfficeInfo> officeInfos;

    private void httpOfficeList() {
        String url = ServerAPI.getOfficeListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                officeInfos = JSONObject.parseArray(data, OfficeInfo.class);
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

    List<OfficeSickInfo> sickInfos;

    private void httpOfficeSickList(int officeId) {
        String url = ServerAPI.getOfficeSickListUrl();
        RequestParams entity = new RequestParams(url);
        entity.addParameter("officeId", officeId);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                sickInfos = JSONObject.parseArray(data, OfficeSickInfo.class);
                showPopupOfficeSick();
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

    PopupWindow popupOffice;

    private void showPopupOffice() {
        if (popupOffice == null) {
            View view = getLayoutInflater().inflate(R.layout.popup_my_recipe_office, null);
            view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupOffice.dismiss();
                }
            });
            TextView tvTitle = view.findViewById(R.id.tv_title);
            tvTitle.setText("选择所属科室");
            ListView listView = view.findViewById(R.id.lv_popup_office);
            OfficeAdapter officeAdapter = new OfficeAdapter();
            listView.setAdapter(officeAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    OfficeInfo officeInfo = officeInfos.get(position);
                    if (officeId != officeInfo.getOfficeId()) {
                        tvOfficeName.setText(officeInfo.getOfficeName());
                        officeId = officeInfo.getOfficeId();
                        tvOfficeSickName.setText(null);
                        offsickId = 0;
                    }
                    popupOffice.dismiss();
                }
            });
            popupOffice = new PopupWindow(view, -1, -1, true);
        }
        popupOffice.showAtLocation(tvOfficeName.getRootView(), Gravity.CENTER, 0, 0);
    }

    class OfficeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return officeInfos == null ? 0 : officeInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getContext());
            tv.setPadding(32, 48, 32, 48);
            tv.setBackgroundColor(0xffffffff);
            tv.setText(officeInfos.get(position).getOfficeName());
            ListView.LayoutParams layoutParams = new AbsListView.LayoutParams(-1, -2);
            tv.setLayoutParams(layoutParams);
            convertView = tv;
            return convertView;
        }
    }

    PopupWindow popupOfficeSick;

    private void showPopupOfficeSick() {

        View view = getLayoutInflater().inflate(R.layout.popup_my_recipe_office, null);
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupOfficeSick.dismiss();
            }
        });
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("选择所属病种");
        ListView listView = view.findViewById(R.id.lv_popup_office);
        OfficeSickAdapter officeAdapter = new OfficeSickAdapter();
        listView.setAdapter(officeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OfficeSickInfo sickInfo = sickInfos.get(position);
                tvOfficeSickName.setText(sickInfo.getOffsickName());
                offsickId = sickInfo.getOffsickId();
                popupOfficeSick.dismiss();
                popupOfficeSick = null;
            }
        });
        popupOfficeSick = new PopupWindow(view, -1, -1, true);
        popupOfficeSick.showAtLocation(tvOfficeSickName.getRootView(), Gravity.CENTER, 0, 0);
    }

    class OfficeSickAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sickInfos == null ? 0 : sickInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getContext());
            tv.setPadding(32, 48, 32, 48);
            tv.setBackgroundColor(0xffffffff);
            tv.setText(sickInfos.get(position).getOffsickName());
            ListView.LayoutParams layoutParams = new AbsListView.LayoutParams(-1, -2);
            tv.setLayoutParams(layoutParams);
            convertView = tv;
            return convertView;
        }
    }

    //----------------------------------------------
    private void httpGetOfficeName(int parentId) {
        String url = ServerAPI.getOfficeListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<OfficeInfo> list = JSONObject.parseArray(data, OfficeInfo.class);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getOfficeId() == parentId) {
                        tvOfficeName.setText(list.get(i).getOfficeName());
                        break;
                    }
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


    private void httpGetSickName(int parentId, int childId) {
        String url = ServerAPI.getOfficeSickListUrl();
        RequestParams entity = new RequestParams(url);
        entity.addParameter("officeId", parentId);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<OfficeSickInfo> list = JSONObject.parseArray(data, OfficeSickInfo.class);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getOffsickId() == childId) {
                        tvOfficeSickName.setText(list.get(i).getOffsickName());
                        break;
                    }
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
}
