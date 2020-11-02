package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.MedicineInfo;
import com.zuojianyou.zybdoctor.beans.RecipeListItem;
import com.zuojianyou.zybdoctor.beans.RecipeTreeItem;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.TimeUtils;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MyRecipeActivity extends BaseActivity {

    public final int PAGE_SIZE = 20;

    public static final int CODE_ADD = 301;
    public static final int CODE_EDIT = 302;

    public static final int OPEN_TYPE_RESULT = 401;
    public static final int OPEN_TYPE_SCAN = 402;

    int openType;

    private boolean noMore = false;
    private TextView emptyView, tvLoadTip;
    private View llLoad, pbLoad;
    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView scrollView;

    EditText etSearchKey;
    RecyclerView rvMenu, rvContent;
    TreeAdapter treeAdapter;
    ContentAdapter contentAdapter;
    List<RecipeListItem> disList;

    RecipeTreeItem selectItem;

    private View llContent;
    private TextView tvEbmName;
    private View btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);
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
        }
        openType = getIntent().getIntExtra("openType", -1);
        findViewById(R.id.ib_act_base_list_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.ib_act_base_list_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyRecipeAddActivity.class);
                intent.putExtra("type", MyRecipeAddActivity.TYPE_ADD);
                startActivityForResult(intent, CODE_ADD);
            }
        });
        findViewById(R.id.btn_act_recipe_list_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpGetContent(selectItem.getDsickId(), true);
            }
        });
        etSearchKey = findViewById(R.id.et_act_recipe_list_key);
        rvMenu = findViewById(R.id.rv_act_my_recipe_menu);
        httpGetMenu();

        disList = new ArrayList<>();
        refreshLayout = findViewById(R.id.refresh_layout_frag_home);
        refreshLayout.setOnRefreshListener(refreshListener);
        scrollView = findViewById(R.id.nsv_frag_home_content);
        scrollView.setOnScrollChangeListener(scrollListener);

        rvContent = findViewById(R.id.rv_fragment_dis_list);
        rvContent.setNestedScrollingEnabled(false);
        rvContent.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(llm);
        contentAdapter = new ContentAdapter();
        rvContent.setAdapter(contentAdapter);

        emptyView = findViewById(R.id.frag_home_empty_view);
        tvLoadTip = findViewById(R.id.tv_frag_home_load_tip);
        llLoad = findViewById(R.id.ll_frag_home_load);
        pbLoad = findViewById(R.id.pb_frag_home_load);
    }

    private void httpGetMenu() {
        String url = ServerAPI.getMyRecipeTreeUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<RecipeTreeItem> list = JSONObject.parseArray(data, RecipeTreeItem.class);
                list.get(0).setSelect(true);
                selectItem = list.get(0);
                httpGetContent(selectItem.getDsickId(), true);
                calculateNodeLevel(list, 0);
                rvMenu.setLayoutManager(new LinearLayoutManager(getContext()));
                rvMenu.setNestedScrollingEnabled(false);
                rvMenu.setHasFixedSize(true);
                treeAdapter = new TreeAdapter(list);
                rvMenu.setAdapter(treeAdapter);
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

    private void httpAddMenu(String pId, String dId, String name) {
        String url = ServerAPI.getMyRecipeAddUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("psickId", pId);
        jsonObject.put("dsickName", name);
        if (dId != null) jsonObject.put("dsickId", dId);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                httpGetMenu();
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

    private void httpDelMenu(String id) {
        String url = ServerAPI.getMyRecipeDelUrl(id);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().request(HttpMethod.DELETE, entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                httpGetMenu();
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

    PopupWindow popupMenuManager;
    View.OnClickListener onMenuManagerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecipeTreeItem item = (RecipeTreeItem) v.getTag();
            switch (v.getId()) {
                case R.id.btn_cancel:
                    popupMenuManager.dismiss();
                    break;
                case R.id.btn_add_node:
                    popupMenuManager.dismiss();
                    item.setPsickId(item.getDsickId());
                    item.setDsickId(null);
                    item.setDsickName(null);
                    showPopupAddNode(v, item);
                    break;
                case R.id.btn_del_node:
                    popupMenuManager.dismiss();
                    showPopupDelNode(v, item);
                    break;
                case R.id.btn_edit_name:
                    popupMenuManager.dismiss();
                    showPopupAddNode(v, item);
                    break;
            }
        }
    };

    PopupWindow popupContentManager;
    View.OnClickListener onContentManagerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecipeListItem item = (RecipeListItem) v.getTag();
            switch (v.getId()) {
                case R.id.btn_cancel:
                    popupContentManager.dismiss();
                    break;
                case R.id.btn_recipe_delete:
                    popupContentManager.dismiss();
                    showPopupDelAlert(v, item);
                    break;
                case R.id.btn_recipe_edit:
                    popupContentManager.dismiss();
                    Intent intentEdit = new Intent(getContext(), MyRecipeAddActivity.class);
                    intentEdit.putExtra("type", MyRecipeAddActivity.TYPE_EDIT);
                    intentEdit.putExtra("recipeId", item.getRepiceId());
                    startActivityForResult(intentEdit, CODE_EDIT);
                    break;
                case R.id.btn_recipe_detail:
                    popupContentManager.dismiss();
                    Intent intent = new Intent(getContext(), MyRecipeAddActivity.class);
                    intent.putExtra("type", MyRecipeAddActivity.TYPE_DETAIL);
                    intent.putExtra("recipeId", item.getRepiceId());
                    startActivity(intent);
                    break;
            }
        }
    };

    private void showPopupDelAlert(View v, RecipeListItem item) {
        View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
        PopupWindow popupWindow = new PopupWindow(view, -1, -1);
        TextView tv = view.findViewById(R.id.tv_alert_msg);
        tv.setText("确定删除处方《" + item.getName() + "》？");
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
                httpDelRecipe(item.getRepiceId());
            }
        });
        popupWindow.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
    }

    PopupWindow popupAddNode;
    View addView;

    private void showPopupAddNode(View v, RecipeTreeItem item) {
        addView = getLayoutInflater().inflate(R.layout.popup_my_recipe_menu_add_node, null);
        addView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddNode.dismiss();
            }
        });
        addView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = addView.findViewById(R.id.et_node_name);
                if (TextUtils.isEmpty(et.getText())) {
                    return;
                } else {
                    String name = et.getText().toString().trim();
                    httpAddMenu(item.getPsickId(), item.getDsickId(), name);
                    popupAddNode.dismiss();
                }

            }
        });
        if (item.getDsickName() != null) {
            EditText et = addView.findViewById(R.id.et_node_name);
            et.setText(item.getDsickName());
        }
        popupAddNode = new PopupWindow(addView, -2, -2);
        popupAddNode.setFocusable(true);
        popupAddNode.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
    }

    PopupWindow popupDelNode;

    private void showPopupDelNode(View v, RecipeTreeItem item) {
        View delView = getLayoutInflater().inflate(R.layout.popup_my_recipe_menu_del_node, null);
        delView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDelNode.dismiss();
            }
        });
        delView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpDelMenu(item.getDsickId());
                popupDelNode.dismiss();
            }
        });
        TextView tvTip = delView.findViewById(R.id.tv_del_tip);
        tvTip.setText("确定删除“" + item.getDsickName() + "”？");
        popupDelNode = new PopupWindow(delView, -2, -2);
        popupDelNode.setFocusable(true);
        popupDelNode.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
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
            if (mList.get(i).isSelect()) {
                treeHolder.tvNodeName.getPaint().setFakeBoldText(true);
            } else {
                treeHolder.tvNodeName.getPaint().setFakeBoldText(false);
            }
            treeHolder.tvNodeName.setTag(mList.get(i));
            treeHolder.tvNodeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecipeTreeItem item = (RecipeTreeItem) v.getTag();
                    if (!item.getDsickId().equals(selectItem.getDsickId())) {
                        item.setSelect(true);
                        selectItem.setSelect(false);
                        selectItem = item;
                        treeAdapter.notifyDataSetChanged();
                        httpGetContent(selectItem.getDsickId(), true);
                        if (llContent != null) {
                            llContent.setVisibility(View.VISIBLE);
                            tvEbmName.setText(selectItem.getDsickName());
                        }
                    }
                }
            });
            treeHolder.tvNodeName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    RecipeTreeItem item = (RecipeTreeItem) v.getTag();
                    View view = getLayoutInflater().inflate(R.layout.popup_act_my_recipe_menu_manager, null);
                    view.findViewById(R.id.btn_edit_name).setTag(item);
                    view.findViewById(R.id.btn_add_node).setTag(item);
                    view.findViewById(R.id.btn_del_node).setTag(item);
                    view.findViewById(R.id.btn_cancel).setTag(item);
                    view.findViewById(R.id.btn_edit_name).setOnClickListener(onMenuManagerClick);
                    view.findViewById(R.id.btn_add_node).setOnClickListener(onMenuManagerClick);
                    view.findViewById(R.id.btn_del_node).setOnClickListener(onMenuManagerClick);
                    view.findViewById(R.id.btn_cancel).setOnClickListener(onMenuManagerClick);
                    popupMenuManager = new PopupWindow(view, -2, -2);
                    popupMenuManager.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
                    return true;
                }
            });
            if (mList.get(i).getChildren() != null && mList.get(i).getChildren().size() > 0) {
                if (mList.get(i).isSpread()) {
                    treeHolder.ivNodeIcon.setImageResource(R.mipmap.ic_list_close);
                    treeHolder.rvChildren.setLayoutManager(new LinearLayoutManager(getContext()));
                    treeHolder.rvChildren.setNestedScrollingEnabled(false);
                    treeHolder.rvChildren.setHasFixedSize(true);
                    TreeAdapter adapter = new TreeAdapter(mList.get(i).getChildren());
                    treeHolder.rvChildren.setAdapter(adapter);
                    treeHolder.rvChildren.setVisibility(View.VISIBLE);
                } else {
                    treeHolder.ivNodeIcon.setImageResource(R.mipmap.ic_list_expand);
                    treeHolder.rvChildren.setVisibility(View.GONE);
                }
                treeHolder.ivNodeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mList.get(i).setSpread(!mList.get(i).isSpread());
                        notifyDataSetChanged();
                    }
                });
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

    //---------------------------------------------------------------
    class ContentAdapter extends RecyclerView.Adapter<ContentHolder> {

        @NonNull
        @Override
        public ContentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_act_my_recipe_content, viewGroup, false);
            return new ContentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContentHolder contentHolder, int i) {
            RecipeListItem item = disList.get(i);
            contentHolder.tvName.setText(item.getName());
            contentHolder.tvSickCn.setText(item.getSickName());
            contentHolder.tvSickEn.setText(item.getWestern());
            contentHolder.tvTime.setText(TimeUtils.toNormMin(item.getCrtTime()));
            if(contentHolder.tvMed!=null){
                contentHolder.tvMed.setText(getMedString(item.getMedList()));
            }
            contentHolder.itemView.setTag(item);
            contentHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = getLayoutInflater().inflate(R.layout.popup_act_my_recipe_content_manager, null);
                    view.findViewById(R.id.btn_recipe_detail).setTag(item);
                    view.findViewById(R.id.btn_recipe_delete).setTag(item);
                    view.findViewById(R.id.btn_recipe_edit).setTag(item);
                    view.findViewById(R.id.btn_cancel).setTag(item);
                    view.findViewById(R.id.btn_recipe_detail).setOnClickListener(onContentManagerClick);
                    view.findViewById(R.id.btn_recipe_delete).setOnClickListener(onContentManagerClick);
                    view.findViewById(R.id.btn_recipe_edit).setOnClickListener(onContentManagerClick);
                    view.findViewById(R.id.btn_cancel).setOnClickListener(onContentManagerClick);
                    popupContentManager = new PopupWindow(view, -2, -2);
                    popupContentManager.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
                    return true;
                }
            });
            contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecipeListItem item = (RecipeListItem) v.getTag();
                    if (openType == OPEN_TYPE_RESULT) {
                        Intent data = new Intent();
                        data.putExtra("medicines", JSONObject.toJSONString(item));
                        setResult(RESULT_OK, data);
                        finish();
                    } else if (openType == OPEN_TYPE_SCAN) {
                        Intent intent = new Intent(getContext(), MyRecipeAddActivity.class);
                        intent.putExtra("type", MyRecipeAddActivity.TYPE_DETAIL);
                        intent.putExtra("recipeId", item.getRepiceId());
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return disList == null ? 0 : disList.size();
        }

        private String getMedString(List<MedicineInfo> list) {
            if (list == null || list.size() == 0) return "";
            StringBuilder sb = new StringBuilder();
            for (MedicineInfo med : list) {
                sb.append(med.getGdName());
                sb.append(med.getUseNum());
                sb.append(med.getMedUnit());
                sb.append(" ");
            }
            return sb.toString();
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvSickCn, tvSickEn, tvTime, tvMed;


        public ContentHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_recipe_name);
            tvSickCn = itemView.findViewById(R.id.tv_sick_cn_name);
            tvSickEn = itemView.findViewById(R.id.tv_sick_en_name);
            tvTime = itemView.findViewById(R.id.tv_recipe_create_time);
            tvMed=itemView.findViewById(R.id.tv_recipe_med);
        }
    }

    private void httpGetContent(String sickId, boolean isRefresh) {
        if (noMore && !isRefresh) return;
        if (isRefresh) {
            disList.clear();
            contentAdapter.notifyDataSetChanged();
        }
        loadBegin();
        String url = ServerAPI.getMyRecipeConUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize", PAGE_SIZE);
        jsonObject.put("pageNum", disList.size() / PAGE_SIZE + 1);
        jsonObject.put("dsickId", sickId);
        if (!TextUtils.isEmpty(etSearchKey.getText()))
            jsonObject.put("keyWd", etSearchKey.getText().toString().trim());
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                List<RecipeListItem> list = jsonObject.getJSONArray("list").toJavaList(RecipeListItem.class);
                if (list != null && list.size() > 0) {
                    disList.addAll(list);
                    contentAdapter.notifyDataSetChanged();
                }
                if (list == null || list.size() < PAGE_SIZE) {
                    loadFinish();
                } else {
                    loadEnd();
                }
                if (disList == null || disList.size() == 0) {
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
                refreshLayout.setRefreshing(false);
            }
        }));
    }

    private void httpDelRecipe(String id) {
        String url = ServerAPI.getRecipeDelUrl(id);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().request(HttpMethod.DELETE, entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                httpGetContent(selectItem.getDsickId(), true);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_ADD) {
            if (resultCode == RESULT_OK) {
                httpGetContent(selectItem.getDsickId(), true);
            }
        } else if (requestCode == CODE_EDIT) {
            if (resultCode == RESULT_OK) {
                httpGetContent(selectItem.getDsickId(), true);
            }
        }
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            httpGetContent(selectItem.getDsickId(), true);
        }
    };

    NestedScrollView.OnScrollChangeListener scrollListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                httpGetContent(selectItem.getDsickId(), false);
            }
        }
    };

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
}
