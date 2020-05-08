package com.zuojianyou.zybdoctor.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public abstract class BaseListActivity extends BaseActivity {

    public final int PAGE_SIZE = 30;

    public abstract BaseListObject createBaseListObject();
    public abstract void onRequestOk(String data);

    protected BaseListObject listObject;
    protected ImageButton btnBack, btnMenu;
    protected TextView tvTitle, emptyView, tvLoadTip;
    protected View llLoad, pbLoad;
    protected SwipeRefreshLayout refreshLayout;
    protected NestedScrollView scrollView;
    protected RecyclerView recyclerView;

    private boolean noMore = false;

    private FrameLayout flHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        listObject = createBaseListObject();

        flHeader = findViewById(R.id.base_list_header);
        btnBack = findViewById(R.id.ib_act_base_list_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnMenu = findViewById(R.id.ib_act_base_list_menu);
        tvTitle = findViewById(R.id.tv_act_base_list_title);
        refreshLayout = findViewById(R.id.base_list_refresh_layout);
        refreshLayout.setOnRefreshListener(refreshListener);
        scrollView = findViewById(R.id.base_list_scroll_view);
        scrollView.setOnScrollChangeListener(scrollListener);
        recyclerView = findViewById(R.id.base_list_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(listObject.layoutManager);
        if (listObject.itemDecoration != null)
            recyclerView.addItemDecoration(listObject.itemDecoration);
        recyclerView.setAdapter(listObject.adapter);
        emptyView = findViewById(R.id.base_list_empty_view);

        tvLoadTip = findViewById(R.id.tv_act_base_list_load_tip);
        llLoad = findViewById(R.id.ll_act_base_list_load);
        pbLoad = findViewById(R.id.pb_act_base_list_load);

        httpGetList(false);
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            httpGetList(true);
        }
    };

    NestedScrollView.OnScrollChangeListener scrollListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                httpGetList(false);
            }
        }
    };

    protected void setHeader(View view) {
        flHeader.removeAllViews();
        flHeader.addView(view);
    }

    protected void httpGetList(boolean isRefresh) {
        if (noMore && !isRefresh) return;
        if (isRefresh) listObject.list.clear();
        loadBegin();
        RequestParams entity = new RequestParams(listObject.requestUrl);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject;
        if (listObject.requestBody != null) {
            jsonObject = (JSONObject) listObject.requestBody;
        } else {
            jsonObject = new JSONObject();
        }
        jsonObject.put("pageSize", PAGE_SIZE);
        jsonObject.put("pageNum", listObject.list.size() / PAGE_SIZE + 1);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                onRequestOk(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                List list = jsonObject.getJSONArray("list").toJavaList(listObject.clazz);
                if (list != null && list.size() > 0) {
                    listObject.list.addAll(list);
                    listObject.adapter.notifyDataSetChanged();
                }
                if (list == null || list.size() < PAGE_SIZE) {
                    loadFinish();
                } else {
                    loadEnd();
                }
                if (listObject.list == null || listObject.list.size() == 0) {
                    listObject.adapter.notifyDataSetChanged();
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
