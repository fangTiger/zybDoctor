package com.zuojianyou.zybdoctor.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.OrderListInfo;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.views.TextInput;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyOrderListActivity extends BaseListActivity {

    View headerView;
    RadioGroup radioGroup;
    TextView tvKeyword, tvTimeDuration, tvBeginTime, tvEndTime;
    View btnClear, btnOneWeed, btnOneMonth, btnThreeMonth, btnOneYear, btnConfirm;
    View llTimeContainer;

    private String state, keyword, startTime, endTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("我的订单");
        headerView = getLayoutInflater().inflate(R.layout.activity_my_order_list_header, null);
        setHeader(headerView);

        tvKeyword = headerView.findViewById(R.id.tv_my_order_list_keyword);
        tvKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInput textInput = new TextInput("关键字", tvKeyword);
                textInput.setTip("请输入患者姓名、手机号、确认病症");
                textInput.show();
            }
        });
        tvKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    keyword = null;
                } else {
                    keyword = s.toString();
                }
                updateAndSend();
            }
        });
        tvTimeDuration = headerView.findViewById(R.id.tv_time_duration);
        tvTimeDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llTimeContainer.getVisibility() != View.VISIBLE) {
                    llTimeContainer.setVisibility(View.VISIBLE);
                } else {
                    llTimeContainer.setVisibility(View.GONE);
                }
            }
        });
        tvBeginTime = headerView.findViewById(R.id.tv_time_begin);
        tvBeginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar curTime = getTime(startTime);
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        tvBeginTime.setText(getTime(calendar));
                        startTime = getTime(year, month, dayOfMonth);
//                        llTimeContainer.setVisibility(View.GONE);
                    }
                }, curTime.get(Calendar.YEAR), curTime.get(Calendar.MONTH), curTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tvEndTime = headerView.findViewById(R.id.tv_time_end);
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar curTime = getTime(endTime);
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        tvEndTime.setText(getTime(calendar));
                        endTime = getTime(year, month, dayOfMonth);
//                        llTimeContainer.setVisibility(View.GONE);
                    }
                }, curTime.get(Calendar.YEAR), curTime.get(Calendar.MONTH), curTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Calendar calendar = Calendar.getInstance();
        tvEndTime.setText(getTime(calendar));
        endTime = getTime(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        tvBeginTime.setText(getTime(calendar));
        startTime = getTime(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        btnClear = headerView.findViewById(R.id.btn_my_order_list_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvKeyword.setText(null);
            }
        });
        btnOneWeed = headerView.findViewById(R.id.btn_time_one_week);
        btnOneWeed.setOnClickListener(onTimeClicked);
        btnOneMonth = headerView.findViewById(R.id.btn_time_one_month);
        btnOneMonth.setOnClickListener(onTimeClicked);
        btnThreeMonth = headerView.findViewById(R.id.btn_time_three_month);
        btnThreeMonth.setOnClickListener(onTimeClicked);
        btnOneYear = headerView.findViewById(R.id.btn_time_one_year);
        btnOneYear.setOnClickListener(onTimeClicked);
        btnConfirm = headerView.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llTimeContainer.setVisibility(View.GONE);
                tvTimeDuration.setText(tvBeginTime.getText() + "-" + tvEndTime.getText());
                updateAndSend();
            }
        });
        llTimeContainer = headerView.findViewById(R.id.ll_my_order_list_time_container);

        state = "0";
        radioGroup = headerView.findViewById(R.id.rg_order_list_menu);
        RadioButton rbAll = (RadioButton) radioGroup.getChildAt(0);
        rbAll.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                state = (String) headerView.findViewById(checkedId).getTag();
                updateAndSend();
            }
        });

    }

    View.OnClickListener onTimeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar calendar = Calendar.getInstance();
            tvEndTime.setText(getTime(calendar));
            endTime = getTime(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            switch (v.getId()) {
                case R.id.btn_time_one_week:
                    calendar.add(Calendar.DATE, -6);
                    break;
                case R.id.btn_time_one_month:
                    calendar.add(Calendar.MONTH, -1);
                    calendar.add(Calendar.DATE, 1);
                    break;
                case R.id.btn_time_three_month:
                    calendar.add(Calendar.MONTH, -3);
                    calendar.add(Calendar.DATE, 1);
                    break;
                case R.id.btn_time_one_year:
                    calendar.add(Calendar.YEAR, -1);
                    calendar.add(Calendar.DATE, 1);
                    break;
            }
            tvBeginTime.setText(getTime(calendar));
            startTime = getTime(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            btnConfirm.performClick();
        }
    };

    private void updateAndSend() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", state);
        if (keyword != null)
            jsonObject.put("keyWd", keyword);
        if (startTime != null)
            jsonObject.put("startTime", startTime);
        if (endTime != null)
            jsonObject.put("endTime", endTime);
        baseListObject.requestBody = jsonObject;
        httpGetList(true);
    }

    @Override
    public BaseListObject createBaseListObject() {
        baseListObject.requestUrl = ServerAPI.getMyOrderListUrl();
        baseListObject.clazz = OrderListInfo.class;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "0");
        jsonObject.put("keyWd", "");
        baseListObject.requestBody = jsonObject;
        baseListObject.itemDecoration = itemDecoration;
        return baseListObject;
    }

    @Override
    public void onRequestOk(String data) {

    }

    BaseListObject baseListObject = new BaseListObject() {
        @Override
        protected RecyclerView.LayoutManager createLayoutManager() {
            return new LinearLayoutManager(getContext());
        }

        @Override
        protected RecyclerView.Adapter createAdapter() {
            return new MyAdapter();
        }
    };

    RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = 20;
            outRect.left = 32;
            outRect.right = 32;
        }
    };

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_review_list, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            OrderListInfo order = (OrderListInfo) baseListObject.list.get(i);
            myHolder.tvIndex.setText(String.valueOf(i + 1));
            myHolder.tvIndex.setBackgroundResource(getIndexColor(order.getSourceObj().getKeyValue()));
            myHolder.tvName.setText(order.getName());
            myHolder.tvName.setCompoundDrawables(null, null, getTypeDrawable(order.getSourceObj().getKeyValue()), null);
            myHolder.tvAge.setText(order.getAge() + "岁");
            myHolder.tvSex.setText(order.getSexObj().getKeyName());
            myHolder.tvTime.setText("问诊时间：" + order.getDiagnoseTime());
            myHolder.tvAdd.setVisibility(View.GONE);
            myHolder.tvCode.setVisibility(View.GONE);
            myHolder.tvState.setText(order.getDiagFlagObj().getKeyName());
            myHolder.itemView.setTag(i);
            myHolder.itemView.setOnClickListener(onItemClick);

        }

        @Override
        public int getItemCount() {
            return baseListObject.list == null ? 0 : baseListObject.list.size();
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

    View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Intent intent = new Intent(getContext(), TreatActivity.class);
            OrderListInfo order = (OrderListInfo) baseListObject.list.get(position);
            intent.putExtra("diaId", order.getDiagnoseId());
            boolean payState = !order.getDiagFlagObj().getKeyValue().equals("2");
            intent.putExtra("payState", payState);
            startActivity(intent);
        }
    };

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvIndex, tvName, tvAge, tvSex, tvAdd, tvTime, tvCode, tvState;
        ImageView btnTip;

        public MyHolder(@NonNull View itemView) {
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
            btnTip.setVisibility(View.INVISIBLE);
        }
    }

    private String getTime(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        String yearStr = String.valueOf(year);
        int month = calendar.get(Calendar.MONTH) + 1;//获取月份
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);
        int day = calendar.get(Calendar.DATE);//获取日
        String dayStr = day < 10 ? "0" + day : String.valueOf(day);
        return String.format("%s/%s/%s", yearStr, monthStr, dayStr);
    }

    private String getTime(int year, int month, int day) {
        String yearStr = String.valueOf(year);
        month++;
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);
        String dayStr = day < 10 ? "0" + day : String.valueOf(day);
        return String.format("%s%s%s", yearStr, monthStr, dayStr);
    }

    private Calendar getTime(String time) {
        if (time == null) return Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }
}
