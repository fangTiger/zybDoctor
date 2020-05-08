package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.DispensaryListInfo;
import com.zuojianyou.zybdoctor.data.SpData;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.TimeUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment
 * 取药
 */
public class MainFragmentDispensary extends Fragment {


    public final int PAGE_SIZE = 20;

    private EditText etKeyword;

    private TextView emptyView, tvLoadTip;
    private View llLoad, pbLoad;
    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView scrollView;
    private List<DispensaryListInfo> disList;
    private RecyclerView rvAsk;
    private DispensaryAdapter adapter;

    private boolean noMore = false;
    private boolean isPad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disList = new ArrayList<>();
        MainActivity mainActivity = (MainActivity) getActivity();
        isPad = mainActivity.isPad(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_dispensary, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = view.findViewById(R.id.refresh_layout_frag_home);
        refreshLayout.setOnRefreshListener(refreshListener);
        scrollView = view.findViewById(R.id.nsv_frag_home_content);
        scrollView.setOnScrollChangeListener(scrollListener);

        rvAsk = view.findViewById(R.id.rv_fragment_dis_list);
        rvAsk.setNestedScrollingEnabled(false);
        rvAsk.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvAsk.setLayoutManager(llm);
        rvAsk.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                Paint mPaint = new Paint();
                mPaint.setARGB(255, 235, 235, 235);
                int childCount = parent.getChildCount();
                // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
                for (int i = 0; i < childCount; i++) {
                    // 获取每个Item的位置
                    final View child = parent.getChildAt(i);
                    // 设置矩形(分割线)的宽度为1px
                    final int mDivider = 1;
                    // 矩形左上顶点 = (ItemView的左边界,ItemView的下边界)
                    final int left = child.getLeft();
                    final int top = child.getBottom();
                    // 矩形右下顶点 = (ItemView的右边界,矩形的下边界)
                    final int right = child.getRight();
                    final int bottom = top + mDivider;
                    // 通过Canvas绘制矩形（分割线）
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }

        });
        adapter = new DispensaryAdapter();
        rvAsk.setAdapter(adapter);

        emptyView = view.findViewById(R.id.frag_home_empty_view);
        tvLoadTip = view.findViewById(R.id.tv_frag_home_load_tip);
        llLoad = view.findViewById(R.id.ll_frag_home_load);
        pbLoad = view.findViewById(R.id.pb_frag_home_load);

        etKeyword = view.findViewById(R.id.et_frag_dispensary_keyword);
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (cancelable != null && !cancelable.isCancelled()) {
                    cancelable.cancel();
                    cancelable = null;
                }
                httpTakeMedList(true);
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            httpTakeMedList(true);
        }
    };

    NestedScrollView.OnScrollChangeListener scrollListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                httpTakeMedList(false);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (SpData.getAuthFlag().equals("9")) {
            httpTakeMedList(true);
        }
    }

    class DispensaryAdapter extends RecyclerView.Adapter<DisHolder> {

        @NonNull
        @Override
        public DisHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_dispensary_list, viewGroup, false);
            return new DisHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DisHolder disHolder, int i) {
            DispensaryListInfo dis = disList.get(i);
            if (isPad) {
                disHolder.tvIndex.setText(String.valueOf(i));
                disHolder.tvSicker.setText(dis.getMbrName());
                disHolder.tvSickName.setText(dis.getSickName());
                disHolder.tvCost.setText(dis.getSumFee() + "元");
                disHolder.tvPayTime.setText(TimeUtils.toNormMin(dis.getPayTime()));
                disHolder.tvDoctor.setText(dis.getDocName());
            } else {
                disHolder.tvIndex.setText(String.valueOf(i));
                disHolder.tvSicker.setText("就诊人：" + dis.getMbrName());
                disHolder.tvSickName.setText("病症：" + dis.getSickName());
                disHolder.tvCost.setText("费用：" + dis.getSumFee() + "元");
                disHolder.tvPayTime.setText("缴费时间：" + TimeUtils.toNormMin(dis.getPayTime()));
                disHolder.tvDoctor.setText("医师：" + dis.getDocName());
            }

            disHolder.itemView.setTag(i);
            disHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    DispensaryListInfo disInfo = disList.get(position);
                    Intent intent = new Intent(getActivity(), DispensaryActivity.class);
                    intent.putExtra("diaId", disInfo.getDiagnoseId());
                    intent.putExtra("doctor", disInfo.getDocName());
                    intent.putExtra("sicker", disInfo.getMbrName());
                    intent.putExtra("payTime", disInfo.getPayTime());
                    getActivity().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return disList == null ? 0 : disList.size();
        }
    }

    class DisHolder extends RecyclerView.ViewHolder {

        TextView tvIndex, tvSicker, tvSickName, tvPayTime, tvCost, tvDoctor;

        public DisHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_dis_list_item_index);
            tvSicker = itemView.findViewById(R.id.tv_dis_list_item_sicker);
            tvSickName = itemView.findViewById(R.id.tv_dis_list_item_sick_name);
            tvPayTime = itemView.findViewById(R.id.tv_dis_list_item_pay_time);
            tvCost = itemView.findViewById(R.id.tv_dis_list_item_cost);
            tvDoctor = itemView.findViewById(R.id.tv_dis_list_item_doctor);
        }

    }

    Callback.Cancelable cancelable;

    private void httpTakeMedList(boolean isRefresh) {
        if (noMore && !isRefresh) return;
        if (isRefresh) {
            disList.clear();
            adapter.notifyDataSetChanged();
        }
        loadBegin();
        String url = ServerAPI.getTakeMedUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize", PAGE_SIZE);
        jsonObject.put("pageNum", disList.size() / PAGE_SIZE + 1);
        jsonObject.put("keyWd", "");
        if (!TextUtils.isEmpty(etKeyword.getText())) {
            jsonObject.put("keyWd", etKeyword.getText().toString().trim());
        }
        entity.setBodyContent(jsonObject.toJSONString());
        cancelable = x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                List<DispensaryListInfo> list = jsonObject.getJSONArray("list").toJavaList(DispensaryListInfo.class);
                if (list != null && list.size() > 0) {
                    disList.addAll(list);
                    adapter.notifyDataSetChanged();
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
