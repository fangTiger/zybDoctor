package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.EbmMenuInfo;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.beans.SickInfo;
import com.zuojianyou.zybdoctor.beans.SickSearchInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 中医循证库
 */
public class TreatEbmActivity extends BaseActivity {

    public final int PAGE_SIZE = 20;

    RadioGroup menuTypeGroup;
    RecyclerView rvMenu, rvContent;
    List<EbmMenuInfo> menuInfos;
    MenuAdapter menuAdapter;
    List<SickInfo> sickInfos;
    ContentAdapter contentAdapter;

    String sickId = "";
    String searchId = null;
    String keyword;
    EditText etKeyword;
    TextView tvSickSymptom, tvCnName, tvWestName;

    View dialogLayout;
    View btnDialogClose;
    ListView lvDialogSick;
    SickAdapter sickAdapter;
    ProgressBar pbDialog;

    private TextView emptyView, tvLoadTip;
    private View llLoad, pbLoad;
    private NestedScrollView scrollView;
    private boolean noMore = false;

    private View llContent;
    private TextView tvEbmName;
    private View btnBack, btnConfirm;
    private SickInfo mSickInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treat_ebm);
        if (!isPad(getContext())) {
            llContent = findViewById(R.id.ll_emb_content);
            tvEbmName = findViewById(R.id.tv_ebm_sick_name);
            btnBack = findViewById(R.id.ib_ebm_btn_back);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llContent.setVisibility(View.GONE);
                }
            });
            btnConfirm = findViewById(R.id.ib_ebm_btn_confirm);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSickInfo == null) return;
                    Intent data = new Intent();
                    data.putExtra("sickInfo", JSONObject.toJSONString(mSickInfo));
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }
        sickInfos = new ArrayList<>();
        findViewById(R.id.ib_ebm_btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.btn_act_ebm_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpGetContent(sickId, true);
            }
        });

        initSearchLayout();

        etKeyword = findViewById(R.id.et_act_ebm_keyword);
        tvSickSymptom = findViewById(R.id.tv_act_ebm_sick_symptom);
        tvCnName = findViewById(R.id.tv_treat_sick_cn_name);
        tvWestName = findViewById(R.id.tv_treat_sick_west_name);

        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) || (keyword != null && s.toString().equals(keyword))) {
                    dialogLayout.setVisibility(View.GONE);
                } else {
                    searchId = null;
                    dialogLayout.setVisibility(View.VISIBLE);
                    pbDialog.setVisibility(View.VISIBLE);
                    getNetSick(s.toString());
                }

            }
        });

        menuTypeGroup = findViewById(R.id.rg_ebm_menu_type);
        menuTypeGroup.check(R.id.rb_ebm_menu_cn);
        menuTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                menuAdapter.notifyDataSetChanged();
            }
        });

        rvMenu = findViewById(R.id.rv_act_ebm_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapter = new MenuAdapter();
        rvMenu.setAdapter(menuAdapter);

        rvContent = findViewById(R.id.rv_act_ebm_content);
        rvContent.setNestedScrollingEnabled(false);
        rvContent.setHasFixedSize(true);
        rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        contentAdapter = new ContentAdapter();
        rvContent.setAdapter(contentAdapter);
        rvContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 1;
            }
        });

        scrollView = findViewById(R.id.nsv_frag_home_content);
        scrollView.setOnScrollChangeListener(scrollListener);
        emptyView = findViewById(R.id.frag_home_empty_view);
        tvLoadTip = findViewById(R.id.tv_frag_home_load_tip);
        llLoad = findViewById(R.id.ll_frag_home_load);
        pbLoad = findViewById(R.id.pb_frag_home_load);

        httpGetMenu();
        httpGetContent(sickId, true);
    }

    NestedScrollView.OnScrollChangeListener scrollListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                httpGetContent(sickId, false);
            }
        }
    };

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
        lvDialogSick = findViewById(R.id.lv_ebm_dialog_list_sick);
        sickAdapter = new SickAdapter();
        lvDialogSick.setAdapter(sickAdapter);
        lvDialogSick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SickSearchInfo sick = (SickSearchInfo) sickAdapter.getItem(position);
                keyword = sick.getName();
                searchId = sick.getSickId();
                etKeyword.setText(keyword);
            }
        });
    }

    class SickAdapter extends BaseAdapter {

        private List<SickSearchInfo> list;

        public void setData(List<SickSearchInfo> list) {
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
            tvItem.setText(list.get(position).getName());
            return convertView;
        }
    }

    Callback.Cancelable cancelable;

    private void getNetSick(String key) {
        if (!checkNetwork()) return;
        String url = ServerAPI.getEbmSickUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.addParameter("keyWd", key);
        cancelable = x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                pbDialog.setVisibility(View.GONE);
                List<SickSearchInfo> sickInfo = JSONObject.parseArray(data, SickSearchInfo.class);
                sickAdapter.setData(sickInfo);
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

    private void httpGetMenu() {
        String url = ServerAPI.getEbmMenuUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<EbmMenuInfo> menuData = JSONObject.parseArray(data, EbmMenuInfo.class);
                menuInfos = sortList(menuData);
                menuAdapter.notifyDataSetChanged();
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

    class MenuAdapter extends RecyclerView.Adapter<MenuHolder> {
        @NonNull
        @Override
        public MenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_ebm_menu_list, viewGroup, false);
            return new MenuHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuHolder menuHolder, int i) {
            menuHolder.setState(i);
        }

        @Override
        public int getItemCount() {
            return menuInfos == null ? 0 : menuInfos.size();
        }
    }

    private EbmMenuInfo checkedInfo = null;

    class MenuHolder extends RecyclerView.ViewHolder {

        LinearLayout llItem;
        ImageView ivState;
        TextView tvName;

        public MenuHolder(@NonNull View itemView) {
            super(itemView);
            llItem = (LinearLayout) itemView;
            ivState = itemView.findViewById(R.id.iv_ebm_menu_item_state);
            tvName = itemView.findViewById(R.id.tv_ebm_menu_item_name);
        }

        public void setState(int position) {
            final EbmMenuInfo menuInfo = menuInfos.get(position);
            if (getItemState(menuInfo.getParentId())) {
                setVisibility(true);
            } else {
                setVisibility(false);
                return;
            }
            if (menuInfo.isChecked()) {
                tvName.setTextColor(0xffaf8d5d);
            } else {
                tvName.setTextColor(0xff333333);
            }
            if (menuInfo.isLeaf()) {
                ivState.setImageResource(R.mipmap.icon_eject2);
            } else if (menuInfo.isExpanded()) {
                ivState.setImageResource(R.mipmap.ic_list_close);
            } else {
                ivState.setImageResource(R.mipmap.ic_list_expand);
            }
            llItem.setPadding(16 * menuInfo.getNodeLevel(), 12, 0, 12);
            if (menuTypeGroup.getCheckedRadioButtonId() == R.id.rb_ebm_menu_cn) {
                tvName.setText(menuInfo.getName());
            } else {
                tvName.setText(menuInfo.getWestern());
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!menuInfo.isLeaf()) {
                        menuInfo.setExpanded(!menuInfo.isExpanded());
                    }
                    sickId = menuInfo.getSickId();
                    if (checkedInfo != null) {
                        checkedInfo.setChecked(false);
                    }
                    menuInfo.setChecked(true);
                    checkedInfo = menuInfo;
                    menuAdapter.notifyDataSetChanged();
                    httpGetContent(sickId, true);
                    if (llContent != null) {
                        llContent.setVisibility(View.VISIBLE);
                        tvEbmName.setText(checkedInfo.getName());
                    }
                }
            });
        }

        public void setVisibility(boolean isVisible) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;// 这里注意使用自己布局的根布局类型
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;// 这里注意使用自己布局的根布局类型
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        public boolean getItemState(String parentId) {
            boolean flag = true;
            do {
                EbmMenuInfo parent = getParent(parentId);
                if (parent == null) {
                    break;
                } else if (parent.getParentId().equals("0")) {
                    break;
                } else if (!parent.isExpanded()) {
                    flag = false;
                    break;
                } else {
                    parentId = parent.getParentId();
                }
            } while (flag);
            return flag;
        }

        public EbmMenuInfo getParent(String parentId) {
            EbmMenuInfo menu = null;
            for (EbmMenuInfo menuInfo : menuInfos) {
                if (menuInfo.getSickId().equals(parentId)) {
                    menu = menuInfo;
                    break;
                }
            }
            return menu;
        }
    }

    private List<EbmMenuInfo> sortList(List<EbmMenuInfo> menuData) {
        List<EbmMenuInfo> sortList = new LinkedList<>();

        //获取头结点
        for (int i = menuData.size() - 1; i >= 0; i--) {
            EbmMenuInfo menuInfo = menuData.get(i);
            if (menuInfo.getParentId().equals("0")) {
                menuInfo.setNodeLevel(0);
                sortList.add(menuInfo);
                menuData.remove(menuInfo);
                break;
            }
        }

        //排序
        while (menuData.size() > 0) {
            boolean flag = false;
            for (int j = menuData.size() - 1; j >= 0; j--) {
                EbmMenuInfo menuInfo = menuData.get(j);
                for (int i = 0; i < sortList.size(); i++) {
                    if (sortList.get(i).getSickId().equals(menuInfo.getParentId())) {
                        if (sortList.get(i).isLeaf()) sortList.get(i).setLeaf(false);
                        menuInfo.setNodeLevel(sortList.get(i).getNodeLevel() + 1);
                        sortList.add(i + 1, menuInfo);
                        menuData.remove(menuInfo);
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }
            if (!flag) {
                return null;
            }
        }

        //默认展开第一项
        for (int i = 0; i < sortList.size(); i++) {
            EbmMenuInfo menuInfo = sortList.get(i);
            if (menuInfo.isLeaf()) break;
            if (i + 1 < sortList.size()) {
                EbmMenuInfo nextInfo = sortList.get(i + 1);
                if (nextInfo.getParentId().equals(menuInfo.getSickId())) {
                    menuInfo.setExpanded(true);
                } else {
                    break;
                }
            }
        }
        return sortList;
    }

    private void httpGetContent(String mId, boolean isRefresh) {
        if (noMore && !isRefresh) return;
        if (isRefresh) {
            sickInfos.clear();
            contentAdapter.notifyDataSetChanged();
        }
        loadBegin();
        String url = ServerAPI.getEbmListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        String keyword = etKeyword.getText() == null ? "" : etKeyword.getText().toString().trim();
        if (searchId != null) {
            entity.addBodyParameter("sickId", searchId);
        } else {
            entity.addBodyParameter("keyWd", keyword);
        }
        entity.addBodyParameter("parentId", "");
        entity.addBodyParameter("pageSize", String.valueOf(PAGE_SIZE));
        entity.addBodyParameter("pageNum", String.valueOf(sickInfos.size() / PAGE_SIZE + 1));
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonData = JSONObject.parseObject(data);
                JSONObject jsonSick = jsonData.getJSONObject("list").getJSONObject("sickObj");
                if (jsonSick.getString("westernDesc") != null) {
                    tvSickSymptom.setText(Html.fromHtml(getString(R.string.tag_act_ebm_sick_symptom) + jsonSick.getString("westernDesc")));
                } else {
                    tvSickSymptom.setText(getString(R.string.tag_act_ebm_sick_symptom));
                }
                if (jsonSick.getString("name") != null) {
                    tvCnName.setText(getString(R.string.tag_act_ebm_sick_cn_name) + jsonSick.getString("name"));
                } else {
                    tvCnName.setText(getString(R.string.tag_act_ebm_sick_cn_name));
                }
                if (jsonSick.getString("western") != null) {
                    tvWestName.setText(getString(R.string.tag_act_ebm_sick_west_name) + jsonSick.getString("western"));
                } else {
                    tvWestName.setText(getString(R.string.tag_act_ebm_sick_west_name));
                }

                List list = jsonData.getJSONObject("list").getJSONArray("sickList").toJavaList(SickInfo.class);
                if (list != null && list.size() > 0) {
                    sickInfos.addAll(list);
                    contentAdapter.notifyDataSetChanged();
                }
                if (list == null || list.size() < PAGE_SIZE) {
                    loadFinish();
                } else {
                    loadEnd();
                }
                if (sickInfos == null || sickInfos.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    loadHidden();
                } else {
                    emptyView.setVisibility(View.GONE);
                    loadShow();
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

    class ContentAdapter extends RecyclerView.Adapter<ContentHolder> {
        @NonNull
        @Override
        public ContentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_ebm_content_list, viewGroup, false);
            return new ContentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContentHolder contentHolder, int i) {
            contentHolder.setState(i);
        }

        @Override
        public int getItemCount() {
            return sickInfos == null ? 0 : sickInfos.size();
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvSymptom, tvRepice, tvTisane, tvAdvice;
        TextView tvTitle;
        ImageView ivState;
        View llInfo;

        public ContentHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ebm_content_item_name);
            tvSymptom = itemView.findViewById(R.id.tv_ebm_content_item_symptom);
            tvRepice = itemView.findViewById(R.id.tv_ebm_content_item_repice);
            tvTisane = itemView.findViewById(R.id.tv_ebm_content_item_tisane);
            tvAdvice = itemView.findViewById(R.id.tv_ebm_content_item_advice);

            tvTitle = itemView.findViewById(R.id.tv_ebm_content_item_title);
            ivState = itemView.findViewById(R.id.iv_ebm_content_item_state);
            llInfo = itemView.findViewById(R.id.ll_ebm_content_item_info);
        }

        public void setState(int position) {
            final SickInfo sickInfo = sickInfos.get(position);
            if (tvTitle != null) {
                tvTitle.setText(sickInfo.getName());
                if (sickInfo == mSickInfo) {
                    tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_red));
                } else {
                    tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.color_ask_title_black));
                }
                if (sickInfo.isExpand()) {
                    ivState.setImageResource(R.mipmap.ic_ebm_item_close);
                    llInfo.setVisibility(View.VISIBLE);
                } else {
                    ivState.setImageResource(R.mipmap.ic_ebm_item_open);
                    llInfo.setVisibility(View.GONE);
                }
                ivState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sickInfo != mSickInfo) {
                            if (mSickInfo != null) mSickInfo.setExpand(false);
                            mSickInfo = sickInfo;
                        }
                        sickInfo.setExpand(!sickInfo.isExpand());
                        contentAdapter.notifyDataSetChanged();
                    }
                });
            }
            tvName.setText(sickInfo.getName());
            tvSymptom.setText(Html.fromHtml(sickInfo.getSymptom()));
            tvRepice.setText(Html.fromHtml(getMedStr(sickInfo.getRepiceName(), sickInfo.getMedList())));
            tvTisane.setText(Html.fromHtml(sickInfo.getTisane()));
            tvAdvice.setText(Html.fromHtml(sickInfo.getDoctorAdvice()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvTitle == null) {
                        Intent data = new Intent();
                        data.putExtra("sickInfo", JSONObject.toJSONString(sickInfo));
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        if (sickInfo != mSickInfo) {
                            mSickInfo = sickInfo;
                            contentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    private void loadBegin() {
        pbLoad.setVisibility(View.VISIBLE);
        tvLoadTip.setText(R.string.tip_load_view_loading);
    }

    private void loadEnd() {
        noMore = false;
        pbLoad.setVisibility(View.INVISIBLE);
        tvLoadTip.setText(R.string.tip_load_view_complete);
    }

    private void loadFinish() {
        noMore = true;
        pbLoad.setVisibility(View.INVISIBLE);
        tvLoadTip.setText(R.string.tip_load_view_anymore);
    }

    private void loadHidden() {
        llLoad.setVisibility(View.GONE);
    }

    private void loadShow() {
        llLoad.setVisibility(View.VISIBLE);
    }

    private String getMedStr(String name, List<MedicineInfo> meds) {
        String redName = "<font color='#FF0000'>" + name + ":</font>";
        if (meds == null || meds.size() == 0) {
            return redName;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(redName);
        sb.append("<br/>");
        for (MedicineInfo med : meds) {
            sb.append(med.getGdName());
            sb.append(med.getUseNum());
            sb.append(med.getMedUnit());
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
