package com.zuojianyou.zybdoctor.activity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.DispensaryMedInfo;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.TimeUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class DispensaryActivity extends BaseActivity {

    String diaId, doctor, sicker, payTime;

    List<DispensaryMedInfo> disList;
    RecyclerView rvContent;
    DispensaryAdapter adapter;

    boolean isPad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensary);
        isPad = isPad(getContext());

        diaId = getIntent().getStringExtra("diaId");
        addTextToTextView(R.id.tv_act_dispensary_code, diaId);
        doctor = getIntent().getStringExtra("doctor");
        addTextToTextView(R.id.tv_act_dispensary_doctor, doctor);
        sicker = getIntent().getStringExtra("sicker");
        addTextToTextView(R.id.tv_act_dispensary_sicker, sicker);
        payTime = getIntent().getStringExtra("payTime");
        addTextToTextView(R.id.tv_act_dispensary_pay_time, TimeUtils.toNormMin(payTime));

        findViewById(R.id.btn_act_dispensary_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disList != null) {
                    httpSubmit();
                }
            }
        });
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rvContent = findViewById(R.id.rv_act_dispensary_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvContent.setLayoutManager(llm);
        rvContent.addItemDecoration(new RecyclerView.ItemDecoration() {

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
                    c.drawLine(left, top, right, bottom, mPaint);
                }
            }

        });
        adapter = new DispensaryAdapter();
        rvContent.setAdapter(adapter);

        httpGetList();
    }

    private void addTextToTextView(int id, String text) {
        TextView tv = findViewById(id);
        tv.setText(tv.getText() + text);
    }

    class DispensaryAdapter extends RecyclerView.Adapter<DisHolder> {

        @NonNull
        @Override
        public DisHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_dispensary_med, viewGroup, false);
            return new DisHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DisHolder disHolder, int i) {
            DispensaryMedInfo dis = disList.get(i);
            if(isPad){
                disHolder.tvIndex.setText(String.valueOf(i));
                disHolder.tvCode.setText(dis.getMedicineId());
                disHolder.tvName.setText(dis.getGdName());
                disHolder.tvType.setText(dis.getBtName());
                disHolder.tvAdd.setText(dis.getSourceArea());
                disHolder.tvSupply.setText(dis.getManager());
                disHolder.tvStore.setText(dis.getStore() + dis.getMedUnit());
                disHolder.tvNum.setText(dis.getUseNum() + dis.getMedUnit());
            }else{
                disHolder.tvIndex.setText(String.valueOf(i));
                disHolder.tvCode.setText("药品编号："+dis.getMedicineId());
                disHolder.tvName.setText("药品名称："+dis.getGdName());
                disHolder.tvType.setText("药品分类："+dis.getBtName());
                disHolder.tvAdd.setText("药品产地："+dis.getSourceArea());
                disHolder.tvSupply.setText("直供："+dis.getManager());
                disHolder.tvStore.setText("库存："+dis.getStore() + dis.getMedUnit());
                disHolder.tvNum.setText("出库量："+dis.getUseNum() + dis.getMedUnit());
            }

        }

        @Override
        public int getItemCount() {
            return disList == null ? 0 : disList.size();
        }
    }

    class DisHolder extends RecyclerView.ViewHolder {

        TextView tvIndex, tvCode, tvName, tvType, tvNum, tvStore, tvAdd, tvSupply;

        public DisHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_dis_med_item_index);
            tvCode = itemView.findViewById(R.id.tv_dis_med_item_code);
            tvName = itemView.findViewById(R.id.tv_dis_med_item_name);
            tvType = itemView.findViewById(R.id.tv_dis_med_item_type);
            tvNum = itemView.findViewById(R.id.tv_dis_med_item_num);
            tvStore = itemView.findViewById(R.id.tv_dis_med_item_store);
            tvAdd = itemView.findViewById(R.id.tv_dis_med_item_add);
            tvSupply = itemView.findViewById(R.id.tv_dis_med_item_supply);
        }

    }

    private void httpGetList() {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getOutMedUrl(diaId);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                disList = JSONObject.parseArray(data, DispensaryMedInfo.class);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenLoadView();
            }
        }));
    }

    private void httpSubmit() {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getExecuteOutMedUrl();
        RequestParams entity = new RequestParams(url);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("diagnoseId", diaId);
        jsonObject.put("medArr", JSONObject.toJSON(disList));
        entity.setBodyContent(jsonObject.toJSONString());
        ServerAPI.addHeader(entity);
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(getContext(), "出库成功！", Toast.LENGTH_SHORT).show();
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
                hiddenLoadView();
            }
        }));
    }


}
