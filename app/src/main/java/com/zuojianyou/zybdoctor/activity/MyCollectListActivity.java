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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.ArticleListInfo;
import com.zuojianyou.zybdoctor.units.ServerAPI;

public class MyCollectListActivity extends BaseListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("我的收藏");
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        layoutParams.setMargins(0, 20, 0, 0);
    }

    @Override
    public BaseListObject createBaseListObject() {
        baseListObject.requestUrl = ServerAPI.getMyCollectListUrl();
        baseListObject.clazz = ArticleListInfo.class;
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
            View view = getLayoutInflater().inflate(R.layout.item_act_my_collect_list, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            ArticleListInfo article = (ArticleListInfo) baseListObject.list.get(i);
            myHolder.tvTitle.setText(article.getTitle());
            myHolder.tvContent.setText(article.getSubtitle());
            myHolder.itemView.setTag(i);
            myHolder.itemView.setOnClickListener(onItemClick);
        }

        @Override
        public int getItemCount() {
            return baseListObject.list == null ? 0 : baseListObject.list.size();
        }
    }

    View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
            ArticleListInfo article = (ArticleListInfo) baseListObject.list.get(position);
            intent.putExtra("article", JSONObject.toJSONString(article));
            startActivityForResult(intent, position);
        }
    };

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_act_my_collect_list_item_title);
            tvContent = itemView.findViewById(R.id.tv_act_my_collect_list_item_content);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int position = requestCode;
            String collectState = data.getStringExtra("collectState");
            if (collectState.equals("0")) {
                baseListObject.list.remove(position);
                baseListObject.adapter.notifyDataSetChanged();
                return;
            }
            String likeState = data.getStringExtra("likeState");
            ArticleListInfo article = (ArticleListInfo) baseListObject.list.get(position);
            int number = data.getIntExtra("likeNum", 0);
            article.setLikeNum(number);
            article.getLikeObj().setKeyValue(likeState);
//            baseListObject.adapter.notifyDataSetChanged();
        }
    }
}
