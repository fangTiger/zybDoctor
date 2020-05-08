package com.zuojianyou.zybdoctor.activity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.EarnListInfo;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class MyEarnDetailActivity extends BaseActivity {

    TextView tvTotal, tvNum, tvWeight;

    TextView tvEmptyView;
    RecyclerView recyclerView;
    MyAdapter adapter;

    boolean isPad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_earn_detail);

        isPad = isPad(getContext());

        findViewById(R.id.ib_act_base_list_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvEmptyView = findViewById(R.id.tv_empty_view);
        recyclerView = findViewById(R.id.rv_earn_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        tvTotal = findViewById(R.id.tv_earn_header_total);
        tvNum = findViewById(R.id.tv_earn_header_num);
        tvWeight = findViewById(R.id.tv_earn_header_weight);

        String date = getIntent().getStringExtra("date");
        String docId = getIntent().getStringExtra("docId");
        httpGetList(date, date, docId);
    }

    RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);
            Paint mPaint = new Paint();
            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_common_divider));
            mPaint.setStrokeWidth(2);
            int childCount = parent.getChildCount();
            // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
            for (int i = 0; i < childCount; i++) {
                // 获取每个Item的位置
                final View child = parent.getChildAt(i);
                if (i == 0) {
                    c.drawLine(child.getLeft(), child.getTop(), child.getRight(), child.getTop(), mPaint);
                }
                if (i == childCount - 1) {
                    c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), mPaint);
                }
                if (i > 0) {
                    EarnListInfo curEarn = adapter.getmList().get(i);
                    EarnListInfo preEarn = adapter.getmList().get(i - 1);
                    if (preEarn.getDay().equals(curEarn.getDay())) {
                        c.drawLine(child.getLeft() + 200, child.getTop(), child.getRight(), child.getTop(), mPaint);
                    } else {
                        c.drawLine(child.getLeft(), child.getTop(), child.getRight(), child.getTop(), mPaint);
                    }
                }
            }
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = 2;
        }
    };

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        private List<EarnListInfo> mList;

        public void setmList(List<EarnListInfo> mList) {
            this.mList = mList;
            notifyDataSetChanged();
        }

        public List<EarnListInfo> getmList() {
            return mList;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_act_my_earn_list_item, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            EarnListInfo earn = mList.get(i);
            myHolder.tvMonth.setText(earn.getPayYMonth().substring(0, 4) + "年" + earn.getPayYMonth().substring(4) + "月");
            myHolder.tvDay.setText(earn.getPayDay());
            myHolder.tvWeek.setText(earn.getDayWeek());
            if (isPad)
                myHolder.tvTotal.setText(earn.getSettleFee());
            else
                myHolder.tvTotal.setText("+" + earn.getSettleFee());

            SpannableString ss = new SpannableString("就诊人：" + earn.getMbrName());
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            ss.setSpan(span, 4, ss.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            myHolder.tvCon1.setText(ss);
            myHolder.tvCon2.setText("药剂副数：" + earn.getUseNum());
            myHolder.tvCon3.setText("问诊费：" + earn.getDiagnoseFee());
            myHolder.tvCon4.setText("煎药费：" + earn.getDealFee());
            myHolder.tvCon5.setText("运费：" + earn.getExpensFee());
            myHolder.tvCon6.setText("基础药费：" + earn.getMedicineFeeBasic());
            myHolder.tvCon7.setText("系数药费：" + earn.getExtraFee());
            myHolder.tvCon8.setText("成品药费：" + earn.getBoxMedicineFee());
            myHolder.tvCon9.setText("技术服务：" + earn.getTecFee());
            myHolder.tvCon10.setText("收费合计：" + earn.getSumFee());

            if(earn.isCenterFlag()){
                myHolder.ivCenterFlag.setVisibility(View.VISIBLE);
            }else{
                myHolder.ivCenterFlag.setVisibility(View.INVISIBLE);
            }
            if (i > 0&& isPad) {
                EarnListInfo preEarn = mList.get(i - 1);
                if (preEarn.getPayYMonth().equals(earn.getPayYMonth())) {
                    myHolder.groupTitle.setVisibility(View.GONE);
                } else {
                    myHolder.groupTitle.setVisibility(View.VISIBLE);
                }
                if (preEarn.getPayDay().equals(earn.getPayDay())) {
                    myHolder.tvDay.setVisibility(View.INVISIBLE);
                    myHolder.tvWeek.setVisibility(View.INVISIBLE);
                } else {
                    myHolder.tvDay.setVisibility(View.VISIBLE);
                    myHolder.tvWeek.setVisibility(View.VISIBLE);
                }
            } else {
                myHolder.groupTitle.setVisibility(View.VISIBLE);
            }

            myHolder.llContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = myHolder.llContent.getWidth();
                    int childCount = myHolder.llContent.getChildCount();
                    setViewWidth(myHolder.tvCon1, width / childCount);
                    setViewWidth(myHolder.tvCon2, width / childCount);
                    setViewWidth(myHolder.tvCon3, width / childCount);
                    setViewWidth(myHolder.tvCon4, width / childCount);
                    setViewWidth(myHolder.tvCon5, width / childCount);
                    setViewWidth(myHolder.tvCon6, width / childCount);
                    setViewWidth(myHolder.tvCon7, width / childCount);
                    setViewWidth(myHolder.tvCon8, width / childCount);
                    setViewWidth(myHolder.tvCon9, width / childCount);
                    setViewWidth(myHolder.tvCon10, width / childCount);
                }
            });
        }

        private void setViewWidth(TextView tv, int w) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
            lp.width = w;
            tv.setLayoutParams(lp);
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvMonth, tvDay, tvWeek, tvTotal,
                tvCon1, tvCon2, tvCon3, tvCon4, tvCon5,
                tvCon6, tvCon7, tvCon8, tvCon9, tvCon10;
        View groupTitle, ivCenterFlag;
        LinearLayout llContent;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvWeek = itemView.findViewById(R.id.tv_week);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvCon1 = itemView.findViewById(R.id.tv_content1);
            tvCon2 = itemView.findViewById(R.id.tv_content2);
            tvCon3 = itemView.findViewById(R.id.tv_content3);
            tvCon4 = itemView.findViewById(R.id.tv_content4);
            tvCon5 = itemView.findViewById(R.id.tv_content5);
            tvCon6 = itemView.findViewById(R.id.tv_content6);
            tvCon7 = itemView.findViewById(R.id.tv_content7);
            tvCon8 = itemView.findViewById(R.id.tv_content8);
            tvCon9 = itemView.findViewById(R.id.tv_content9);
            tvCon10 = itemView.findViewById(R.id.tv_content10);
            groupTitle = itemView.findViewById(R.id.ll_group_title);
            ivCenterFlag = itemView.findViewById(R.id.iv_custom);
            llContent = itemView.findViewById(R.id.ll_content);
        }
    }

    private void httpGetList(String endTime, String startTime, String docId) {
        String url = ServerAPI.getMyEarnDetailUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endTime", endTime);
        jsonObject.put("startTime", startTime);
        jsonObject.put("doctorId", docId);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                String sumFee = jsonObject.getJSONObject("sumObj").getString("sumFee");
                if (sumFee == null) {
                    tvTotal.setText("0.00");
                } else {
                    tvTotal.setText(sumFee);
                }
                String diagnoseSum = jsonObject.getJSONObject("sumObj").getString("diagnoseSum");
                tvNum.setText(diagnoseSum);
                String useSum = jsonObject.getJSONObject("sumObj").getString("useSum");
                tvWeight.setText(useSum);
                String weekStr = jsonObject.getJSONObject("sumObj").getString("weekStr");

                List<EarnListInfo> list = jsonObject.getJSONArray("list").toJavaList(EarnListInfo.class);
                for (int i = 0; i < list.size(); i++) {
                    EarnListInfo info = list.get(i);
                    info.setDay(info.getPayYMonth() + info.getPayDay());
                    info.setDayWeek(weekStr);
                }
                adapter.setmList(list);
                if (list == null || list.size() == 0) {
                    tvEmptyView.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyView.setVisibility(View.GONE);
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

