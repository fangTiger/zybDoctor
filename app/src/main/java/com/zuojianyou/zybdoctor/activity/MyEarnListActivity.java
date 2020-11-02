package com.zuojianyou.zybdoctor.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.EarnListInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyEarnListActivity extends BaseActivity {

    TextView tvTotal;
    TextView tvBegin, tvEnd;
    Button btnSearch;

    TextView tvEmptyView;
    RecyclerView recyclerView;
    MyAdapter adapter;

    boolean isMain;
    boolean isPad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_earn_list);

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
        Calendar calendar = Calendar.getInstance();
        tvEnd = findViewById(R.id.btn_end_time);
        tvEnd.setText(getDateTime(calendar));
        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month++;
                        String monthStr = month < 10 ? "0" + month : month + "";
                        String dayStr = day < 10 ? "0" + day : day + "";
                        tvEnd.setText(year + "-" + monthStr + "-" + dayStr);
                        if (!isPad) btnSearch.performClick();
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tvBegin = findViewById(R.id.btn_begin_time);
        calendar.add(Calendar.DATE, -2);
        tvBegin.setText(getDateTime(calendar));
        tvBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month++;
                        String monthStr = month < 10 ? "0" + month : month + "";
                        String dayStr = day < 10 ? "0" + day : day + "";
                        tvBegin.setText(year + "-" + monthStr + "-" + dayStr);
                        if (!isPad) btnSearch.performClick();
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpGetList();
            }
        });
        btnSearch.performClick();
    }

    private String getDateTime(Calendar mCalendar) {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) + 1;//获取月份
        String monthStr = month < 10 ? "0" + month : month + "";
        int day = mCalendar.get(Calendar.DATE);//获取日
        String dayStr = day < 10 ? "0" + day : day + "";
        return year + "-" + monthStr + "-" + dayStr;
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
                myHolder.tvTotal.setText(earn.getDaySettleMoney());
            else
                myHolder.tvTotal.setText("+" + earn.getDaySettleMoney());
            if (isMain) {
                SpannableString ss = new SpannableString("医师：" + earn.getDocName());
                StyleSpan span = new StyleSpan(Typeface.BOLD);
                ss.setSpan(span, 3, ss.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                myHolder.tvCon1.setText(ss);
//                myHolder.tvCon1.setText("医师：" + earn.getDocName());
                myHolder.tvCon2.setText("就诊人数：" + earn.getDiagnoseNum());
            } else {
                SpannableString ss = new SpannableString("就诊人数：" + earn.getDiagnoseNum());
                StyleSpan span = new StyleSpan(Typeface.BOLD);
                ss.setSpan(span, 5, ss.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                myHolder.tvCon1.setText(ss);
//                myHolder.tvCon1.setText("就诊人数：" + earn.getDiagnoseNum());
                myHolder.tvCon2.setText("药剂副数：" + earn.getUseNum());
            }
            myHolder.tvCon3.setText("问诊费：" + earn.getDiagnoseFee());
            myHolder.tvCon4.setText("煎药费：" + earn.getDealFee());
            myHolder.tvCon5.setText("运费：" + earn.getExpensFee());
            myHolder.tvCon6.setText("基础药费：" + earn.getMedicineFeeBasic());
            myHolder.tvCon7.setText("系数药费：" + earn.getExtraFee());
            myHolder.tvCon8.setText("成品药费：" + earn.getBoxMedicineFee());
            myHolder.tvCon9.setText("技术服务：" + earn.getTecFee());
            myHolder.tvCon10.setText("收费合计：" + earn.getSumFee());

            if (i > 0 && isPad) {
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

            myHolder.itemView.setTag(earn);
            myHolder.itemView.setOnClickListener(onItemClick);
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

    View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EarnListInfo info = (EarnListInfo) v.getTag();
            Intent intent = new Intent(getContext(), MyEarnDetailActivity.class);
            intent.putExtra("date", info.getDay());
            intent.putExtra("docId", info.getDoctorId());
            startActivity(intent);
        }
    };

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvMonth, tvDay, tvWeek, tvTotal,
                tvCon1, tvCon2, tvCon3, tvCon4, tvCon5,
                tvCon6, tvCon7, tvCon8, tvCon9, tvCon10;
        View groupTitle;
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
            llContent = itemView.findViewById(R.id.ll_content);
        }
    }

    private void httpGetList() {
        String url = ServerAPI.getMyEarnListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endTime", tvEnd.getText().toString().replaceAll("-", ""));
        jsonObject.put("startTime", tvBegin.getText().toString().replaceAll("-", ""));
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                String sumFee = jsonObject.getJSONObject("sumObj").getString("sumFee");
                if (sumFee == null) {
                    tvTotal.setText("0.00元");
                } else {
                    tvTotal.setText(sumFee + "元");
                }
                isMain = jsonObject.getJSONObject("sumObj").getBooleanValue("mainFlag");

                List<EarnListInfo> list = getHttpResult(data);
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

    private List<EarnListInfo> getHttpResult(String data) {
        List<EarnListInfo> list = new ArrayList<>();
        JSONObject result = JSONObject.parseObject(data, Feature.OrderedField);
        JSONObject mapList = result.getJSONObject("mapList");
        JSONArray monthArray = getMapArray(mapList);
        for (int i = 0; i < monthArray.size(); i++) {
            JSONArray dayArray = getMapArray(monthArray.getJSONObject(i));
            for (int j = 0; j < dayArray.size(); j++) {
                JSONObject dayObj = dayArray.getJSONObject(j).getJSONObject("dayObj");
                String dayWeek = dayObj.getString("dayWeek");
                String daySettleMoney = dayObj.getString("daySettleMoney");
                List<EarnListInfo> mList = dayArray.getJSONObject(j)
                        .getJSONArray("dayList")
                        .toJavaList(EarnListInfo.class);
                for (EarnListInfo info : mList) {
                    info.setDayWeek(dayWeek);
                    info.setDaySettleMoney(daySettleMoney);
                    info.setDay(info.getPayYMonth() + info.getPayDay());
                    list.add(info);
                }
            }
        }
        return list;
    }

    private JSONArray getMapArray(JSONObject mapList) {
        JSONArray jsonArray = new JSONArray();
        Map map = mapList.getInnerMap();
        Iterator<Map.Entry<String, JSONObject>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, JSONObject> entry = entries.next();
            String key = entry.getKey();
            JSONObject value = entry.getValue();
            jsonArray.add(value);
        }
        return jsonArray;
    }


}
