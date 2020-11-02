package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.zuojianyou.zybdoctor.beans.SickSampleItem;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.TimeUtils;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class SickSampleActivity extends BaseListActivity {

    View headerView;
    EditText etKeyword;
    View btnSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("个人病案库");
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        lp.setMargins(16, 0, 16, 0);
        recyclerView.setLayoutParams(lp);
        headerView = getLayoutInflater().inflate(R.layout.activity_sick_sample_header, null);
        setHeader(headerView);
        etKeyword = findViewById(R.id.et_keyword);
        btnSearch = findViewById(R.id.btn_search_by_keyword);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etKeyword.getText())) {
                    baseListObject.requestBody = null;
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("keyWd", etKeyword.getText().toString().trim());
                    baseListObject.requestBody = jsonObject;
                }
                httpGetList(true);
            }
        });
    }

    @Override
    public BaseListObject createBaseListObject() {
        baseListObject.requestUrl = ServerAPI.getMySickSample();
        baseListObject.clazz = SickSampleItem.class;
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

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = 1;
        }
    };

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_act_sick_sample_list, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            SickSampleItem item = (SickSampleItem) baseListObject.list.get(i);
            myHolder.tvName.setText(item.getMbrName());
            myHolder.tvAge.setText(item.getAge());
            myHolder.tvSex.setText(getSex(item.getSex()));
            myHolder.tvPhone.setText(item.getPhone());
            myHolder.tvSickName.setText(item.getRecipeName());
            myHolder.tvDiagName.setText(item.getSickName());
            int comm;
            try {
                comm = Integer.parseInt(item.getCureComm());
            } catch (Exception e) {
                comm = 0;
            }
            if (comm == 0) {
                myHolder.tvDiagScore.setText("评分");
                myHolder.tvDiagScore.setTag(item.getDiagnoseId());
                myHolder.tvDiagScore.setOnClickListener(onDiagScore);
            } else {
                myHolder.tvDiagScore.setText("★" + item.getCureComm() + ".0");
                myHolder.tvDiagScore.setOnClickListener(null);
            }
            myHolder.tvDiagMed.setText(getMedString(item.getMedList()));
            myHolder.tvDiagTime.setText(TimeUtils.toNormMin(item.getDiagnoseTime()));
            myHolder.itemView.setTag(item);
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SickSampleItem item = (SickSampleItem) v.getTag();
                    Intent data = new Intent();
                    data.putExtra("medicines", JSONObject.toJSONString(item));
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
            myHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = getLayoutInflater().inflate(R.layout.popup_common_alert, null);
                    PopupWindow popupWindow = new PopupWindow(view, -1, -1);
                    TextView tv = view.findViewById(R.id.tv_alert_msg);
                    tv.setText("保存到个人处方库？");
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
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("cnName", item.getSickName());
                            jsonObject.put("westName", item.getWestern());
                            jsonObject.put("therapies", item.getTherapy());
                            jsonObject.put("pathogeny", item.getPathogeny());
                            jsonObject.put("tisane", item.getInstruction());
                            jsonObject.put("doctorAdvice", item.getDoctorAdvice());
                            jsonObject.put("medList", item.getMedList());
                            Intent intent = new Intent(getContext(), MyRecipeAddActivity.class);
                            intent.putExtra("type", MyRecipeAddActivity.TYPE_SAVE);
                            intent.putExtra("data", jsonObject.toJSONString());
                            startActivity(intent);
                        }
                    });
                    popupWindow.showAtLocation(etKeyword.getRootView(), Gravity.CENTER, 0, 0);
                    return true;
                }
            });
        }

        private String getSex(String code) {
            if (code == null) return null;
            String sex = null;
            switch (code) {
                case "1":
                    sex = "男";
                    break;
                case "2":
                    sex = "女";
                    break;
                case "3":
                    sex = "保密";
                    break;
            }
            return sex;
        }

        @Override
        public int getItemCount() {
            return baseListObject.list == null ? 0 : baseListObject.list.size();
        }

    }

    PopupWindow popupWindow;
    ImageView[] ivScore;
    View popupView;

    View.OnClickListener onDiagScore = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String diagId = (String) v.getTag();
            popupView = getLayoutInflater().inflate(R.layout.popup_diag_score, null);
            ivScore = new ImageView[5];
            ivScore[0] = popupView.findViewById(R.id.iv_score_1);
            ivScore[1] = popupView.findViewById(R.id.iv_score_2);
            ivScore[2] = popupView.findViewById(R.id.iv_score_3);
            ivScore[3] = popupView.findViewById(R.id.iv_score_4);
            ivScore[4] = popupView.findViewById(R.id.iv_score_5);
            for (int i = 0; i < ivScore.length; i++) {
                ivScore[i].setOnClickListener(onScoreClick);
            }
            popupView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            popupView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (score == 0) {
                        ToastUtils.show(getContext(), "请选择级别！");
                        return;
                    }
                    httpDiagScore(diagId, score);
                }
            });
            popupWindow = new PopupWindow(popupView, -2, -2);
            popupWindow.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
        }
    };

    View.OnClickListener onScoreClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_score_1:
                    initIvScore(0);
                    break;
                case R.id.iv_score_2:
                    initIvScore(1);
                    break;
                case R.id.iv_score_3:
                    initIvScore(2);
                    break;
                case R.id.iv_score_4:
                    initIvScore(3);
                    break;
                case R.id.iv_score_5:
                    initIvScore(4);
                    break;
            }
        }
    };

    int score = 0;

    private void initIvScore(int index) {
        score = index + 1;
        for (int i = 0; i < ivScore.length; i++) {
            if (index >= i) {
                ivScore[i].setImageResource(R.mipmap.ic_mine_star_light);
            } else {
                ivScore[i].setImageResource(R.mipmap.ic_mine_star_grey);
            }
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvSex, tvAge, tvPhone, tvSickName, tvDiagName, tvDiagTime, tvDiagScore, tvDiagMed;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_patient_name);
            tvSex = itemView.findViewById(R.id.tv_patient_sex);
            tvAge = itemView.findViewById(R.id.tv_patient_age);
            tvPhone = itemView.findViewById(R.id.tv_patient_phone);
            tvSickName = itemView.findViewById(R.id.tv_sick_name);
            tvDiagName = itemView.findViewById(R.id.tv_diag_name);
            tvDiagTime = itemView.findViewById(R.id.tv_diag_time);
            tvDiagScore = itemView.findViewById(R.id.tv_diag_score);
            tvDiagMed = itemView.findViewById(R.id.tv_diag_med);
        }
    }

    private void httpDiagScore(String id, int score) {
        String url = ServerAPI.getMyDiagCure();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("diagnoseId", id);
        jsonObject.put("cureComm", score);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                popupWindow.dismiss();
                List<SickSampleItem> list = baseListObject.list;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getDiagnoseId().equals(id)) {
                        list.get(i).setCureComm(String.valueOf(score));
                        break;
                    }
                }
                baseListObject.adapter.notifyDataSetChanged();
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
