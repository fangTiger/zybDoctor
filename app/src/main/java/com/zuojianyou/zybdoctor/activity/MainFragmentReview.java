package com.zuojianyou.zybdoctor.activity;

import android.app.DatePickerDialog;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.AskNumInfo;
import com.zuojianyou.zybdoctor.beans.ReviewListInfo;
import com.zuojianyou.zybdoctor.data.SpData;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Calendar;
import java.util.List;

/**
 * fragment
 * 复诊
 */
public class MainFragmentReview extends Fragment {

    private String currentDate;

    private AskAdapter askAdapter;
    private List<ReviewListInfo> askList;
    private SwipeRefreshLayout refreshLayout;
    private View emptyView;
    private LinearLayout[] btnDate;
    private View[] dateDivider;

    private boolean isPad;

    BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        isPad = mainActivity.isPad(getContext());
        if (isPad) {
            btnDate = new LinearLayout[7];
            dateDivider = new View[7];
        } else {
            btnDate = new LinearLayout[4];
            dateDivider = new View[4];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_ask, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.iv_btn_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        httpPatientNum(getDateStr(calendar));
                        setRadioState(calendar);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        emptyView = view.findViewById(R.id.tv_fragment_ask_empty_view);

        RecyclerView rvAsk = view.findViewById(R.id.rv_fragment_ask_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvAsk.setLayoutManager(llm);
        rvAsk.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 20;
            }
        });
        askAdapter = new AskAdapter();
        rvAsk.setAdapter(askAdapter);

        refreshLayout = view.findViewById(R.id.srl_fragment_ask_content);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAskList(currentDate);
            }
        });

        btnDate[0] = view.findViewById(R.id.ll_btn_date1);
        btnDate[1] = view.findViewById(R.id.ll_btn_date2);
        btnDate[2] = view.findViewById(R.id.ll_btn_date3);
        btnDate[3] = view.findViewById(R.id.ll_btn_date4);
        if (isPad) {
            btnDate[4] = view.findViewById(R.id.ll_btn_date5);
            btnDate[5] = view.findViewById(R.id.ll_btn_date6);
            btnDate[6] = view.findViewById(R.id.ll_btn_date7);
        }

        dateDivider[0] = view.findViewById(R.id.ll_date_divider1);
        dateDivider[1] = view.findViewById(R.id.ll_date_divider2);
        dateDivider[2] = view.findViewById(R.id.ll_date_divider3);
        dateDivider[3] = view.findViewById(R.id.ll_date_divider4);
        if (isPad) {
            dateDivider[4] = view.findViewById(R.id.ll_date_divider5);
            dateDivider[5] = view.findViewById(R.id.ll_date_divider6);
            dateDivider[6] = view.findViewById(R.id.ll_date_divider7);
        }

        for (int i = 0; i < btnDate.length; i++) {
            btnDate[i].setTag(i);
            btnDate[i].setOnClickListener(btnDateClicked);
        }
        setRadioState(Calendar.getInstance());
        if (SpData.getAuthFlag().equals("9") || SpData.getAuthFlag().equals("8")) {
            httpPatientNum(getDateStr(Calendar.getInstance()));
        }
    }

    private void setRadioState(Calendar mCalendar) {
        for (int i = 0; i < btnDate.length; i++) {
            TextView tvDate = (TextView) btnDate[i].getChildAt(0);
            TextView tvWeek = (TextView) btnDate[i].getChildAt(1);

            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH) + 1;//获取月份
            String monthStr = month < 10 ? "0" + month : month + "";
            int day = mCalendar.get(Calendar.DATE);//获取日
            String dayStr = day < 10 ? "0" + day : day + "";
            tvDate.setText(monthStr + "-" + dayStr);
            tvDate.setTag(year + monthStr + dayStr);

            int week = mCalendar.get(Calendar.DAY_OF_WEEK);
            tvWeek.setText(getWeekText(week));

            mCalendar.add(Calendar.DATE, 1);
        }
        if (SpData.getAuthFlag().equals("9") || SpData.getAuthFlag().equals("8")) {
            btnDate[0].performClick();
        }
    }

    private String getWeekText(int num) {
        String week;
        switch (num) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
            default:
                week = "未知";
                break;
        }
        return week;
    }

    View.OnClickListener btnDateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            for (int i = 0; i < btnDate.length; i++) {
                if (i == index) {
                    btnDate[i].setBackgroundResource(R.drawable.time_select_bg);
                    LinearLayout ll = btnDate[i];
                    TextView tv0 = (TextView) ll.getChildAt(0);
                    tv0.setTextColor(0xffffffff);
                    TextView tv1 = (TextView) ll.getChildAt(1);
                    tv1.setTextColor(0xffffffff);
                    TextView tv2 = (TextView) ll.getChildAt(2);
                    tv2.setTextColor(0xffffffff);
                } else {
                    btnDate[i].setBackgroundColor(0x00000000);
                    LinearLayout ll = btnDate[i];
                    TextView tv0 = (TextView) ll.getChildAt(0);
                    tv0.setTextColor(0xff515151);
                    TextView tv1 = (TextView) ll.getChildAt(1);
                    tv1.setTextColor(0xff515151);
                    TextView tv2 = (TextView) ll.getChildAt(2);
                    tv2.setTextColor(0xff515151);
                }
            }
            for (int i = 0; i < dateDivider.length; i++) {
                if (i == index || i == index - 1) {
                    dateDivider[i].setVisibility(View.INVISIBLE);
                } else {
                    dateDivider[i].setVisibility(View.VISIBLE);
                }
            }
            currentDate = (String) btnDate[index].getChildAt(0).getTag();
            getAskList(currentDate);
        }
    };

    class AskAdapter extends RecyclerView.Adapter<AskHolder> {

        @NonNull
        @Override
        public AskHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_review_list, viewGroup, false);
            return new AskHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AskHolder askHolder, int i) {
            askHolder.setState(i);
        }

        @Override
        public int getItemCount() {
            return askList == null ? 0 : askList.size();
        }
    }

    class AskHolder extends RecyclerView.ViewHolder {

        TextView tvIndex, tvName, tvAge, tvSex, tvAdd, tvTime, tvCode, tvState;
        ImageView btnTip;

        public AskHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.index);
            tvName = itemView.findViewById(R.id.name);
            tvAge = itemView.findViewById(R.id.age);
            tvSex = itemView.findViewById(R.id.sex);
            tvAdd = itemView.findViewById(R.id.add);
            tvTime = itemView.findViewById(R.id.time);
            tvCode = itemView.findViewById(R.id.code);
            tvState = itemView.findViewById(R.id.state);
            btnTip = itemView.findViewById(R.id.tip);
        }

        public void setState(int position) {
            final ReviewListInfo info = askList.get(position);
            tvIndex.setText(String.valueOf(position + 1));
            tvIndex.setBackgroundResource(getIndexColor(info.getSourceObj().getKeyValue()));
            tvName.setText(info.getName());
            tvName.setCompoundDrawables(null, null, getTypeDrawable(info.getSourceObj().getKeyValue()), null);
            tvAge.setText(info.getAge() + "岁");
            tvSex.setText(info.getSexObj().getKeyName());
            tvTime.setText("复诊时间：" + info.getRevistDate());

            tvAdd.setText("籍贯：" + (info.getBirthCountyObj().getKeyValue().equals("156") ? "中国" : "外籍"));
            tvCode.setText("身份证号：" + getCardNum(info.getIdNumber()));
            tvState.setText(info.getRevistNoticeObj().getKeyName());
            btnTip.setTag(position);
            btnTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = (Integer) v.getTag();
                    httpTipSubmit(p);
                }
            });
            if (info.getRevistNoticeObj().getKeyValue().equals("0")) {
                tvState.setTextColor(0xffbe282c);
                btnTip.setVisibility(View.VISIBLE);
            } else {
                tvState.setTextColor(0xfff8b865);
                btnTip.setVisibility(View.INVISIBLE);
            }
        }

        private Drawable getTypeDrawable(String type) {
            //1现场门诊 2现场APP 3远程APP 4医生端APP
            Drawable drawable;
            if (type.equals("3")) {
                drawable = getResources().getDrawable(R.mipmap.ic_ask_tag_online);
            } else {
                drawable = getResources().getDrawable(R.mipmap.ic_ask_tag_offline);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            return drawable;
        }

        private int getIndexColor(String type) {
            if (type.equals("3")) {
                return R.color.color_ask_list_index_online;
            } else {
                return R.color.color_ask_list_index_offline;
            }
        }

        private String getCardNum(String cardNum) {
            if (cardNum == null || cardNum.length() < 10) {
                return "无";
            } else {
                return cardNum.substring(0, 3) + "******" + cardNum.substring(cardNum.length() - 4, cardNum.length());
            }
        }
    }

    private void getAskList(String date) {
        String url = ServerAPI.getReviewListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("revistDate", date);
        jsonObject.put("pageSize", "1000");
        jsonObject.put("pageNum", "1");
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject json = JSONObject.parseObject(result);
                if (json.getIntValue("code") == 0) {
                    askList = json.getJSONObject("data").getJSONArray("list").toJavaList(ReviewListInfo.class);
                    if (askList == null || askList.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }
                    askAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(false);
            }
        });
    }

    private void httpPatientNum(String date) {
        String url = ServerAPI.getReviewNumUrl(date);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<AskNumInfo> numInfos = JSONObject.parseArray(data, AskNumInfo.class);
                for (int i = 0; i < btnDate.length; i++) {
                    TextView tvNum = (TextView) btnDate[i].getChildAt(2);
                    tvNum.setText(numInfos.get(i).getRegistCount() + "人");
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

    private void httpTipSubmit(final int position) {
        if (baseActivity == null) {
            baseActivity = (BaseActivity) getActivity();
        }
        baseActivity.showLoadView();

        String url = ServerAPI.getRemandRevistUrl(askList.get(position).getDiagnoseId());
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                askList.get(position).getRevistNoticeObj().setKeyValue("1");
                askList.get(position).getRevistNoticeObj().setKeyName("已提醒");
                askAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                baseActivity.hiddenLoadView();
            }
        }));
    }

    private String getDateStr(Calendar mCalendar) {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) + 1;//获取月份
        String monthStr = month < 10 ? "0" + month : month + "";
        int day = mCalendar.get(Calendar.DATE);//获取日
        String dayStr = day < 10 ? "0" + day : day + "";
        return year + monthStr + dayStr;
    }


}
