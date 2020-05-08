package com.zuojianyou.zybdoctor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.ArticleListInfo;
import com.zuojianyou.zybdoctor.beans.ArticleMenuInfo;
import com.zuojianyou.zybdoctor.beans.DoctorInfo;
import com.zuojianyou.zybdoctor.constants.BroadcastAction;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.TimeUtils;
import com.zuojianyou.zybdoctor.views.ImageSelectDialog;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MyBlogActivity extends BaseActivity {

    public final int CREATE_ARTICLE_CODE = 111;
    public final int EDIT_ARTICLE_CODE = 112;
    public final int DETAIL_ARTICLE_CODE = 10000;

    public final int PAGE_SIZE = 20;

    private TextView emptyView, tvLoadTip;
    private View llLoad, pbLoad;
    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView scrollView;
    private RecyclerView rvRecom;
    private MyAdapter adapter;
    private LineRecomAdapter lineAdapter;
    private List<ArticleListInfo> articleList;

    private boolean noMore = false;

    private int pubTyp = 1;
    private String columnId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_blog);
        registerMyReceiver();
        articleList = new ArrayList<>();
        findViewById(R.id.ib_act_my_blog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshLayout = findViewById(R.id.refresh_layout_frag_home);
        refreshLayout.setOnRefreshListener(refreshListener);
        scrollView = findViewById(R.id.nsv_frag_home_content);
        scrollView.setOnScrollChangeListener(scrollListener);

        rvRecom = findViewById(R.id.rv_fragment_home_recom);
        rvRecom.setHasFixedSize(true);
        rvRecom.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRecom.setLayoutManager(layoutManager);
        rvRecom.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        });
        adapter = new MyAdapter();
        lineAdapter = new LineRecomAdapter();

        emptyView = findViewById(R.id.frag_home_empty_view);
        tvLoadTip = findViewById(R.id.tv_frag_home_load_tip);
        llLoad = findViewById(R.id.ll_frag_home_load);
        pbLoad = findViewById(R.id.pb_frag_home_load);

        findViewById(R.id.ib_act_my_blog_create_article).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelectDialog dialog = new ImageSelectDialog(getContext());
                dialog.show();
            }
        });

        httpGetMineInfo();

        getMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterMyReceiver();
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

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_act_my_blog_article_list, viewGroup, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            ArticleListInfo article = articleList.get(i);
            myHolder.tvTitle.setText(article.getTitle());
            myHolder.tvTheme.setText(article.getSubtitle());
            myHolder.tvAuthor.setText(article.getAuthorName());
            myHolder.tvTime.setText(TimeUtils.toNormDay(article.getPubTime()));
            myHolder.tvLikeNum.setText(String.valueOf(article.getLikeNum()));
            myHolder.tvCollectNum.setText(String.valueOf(article.getFavNum()));
            myHolder.itemView.setTag(i);
            myHolder.itemView.setOnClickListener(onItemClick);
        }

        @Override
        public int getItemCount() {
            return articleList == null ? 0 : articleList.size();
        }
    }

    View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
            ArticleListInfo article = articleList.get(position);
            intent.putExtra("article", JSONObject.toJSONString(article));
            startActivityForResult(intent, DETAIL_ARTICLE_CODE + position);
        }
    };

    View.OnLongClickListener onItemLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final int position = (Integer) v.getTag();
            new AlertDialog.Builder(getContext())
                    .setTitle("提示").setMessage("请选择操作。")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArticleListInfo article = articleList.get(position);
                            httpDelArticle(article.getPaId());
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
            return true;
        }
    };

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvTheme, tvAuthor, tvTime, tvLikeNum, tvCollectNum;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_act_article_detail_title);
            tvTheme = itemView.findViewById(R.id.tv_act_article_detail_con);
            tvAuthor = itemView.findViewById(R.id.tv_act_article_detail_author);
            tvTime = itemView.findViewById(R.id.tv_act_article_detail_time);
            tvLikeNum = itemView.findViewById(R.id.tv_act_article_detail_like);
            tvCollectNum = itemView.findViewById(R.id.tv_act_article_detail_collect);
        }
    }

    class LineRecomAdapter extends RecyclerView.Adapter<LineHolder> {

        @NonNull
        @Override
        public LineHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_line_recom, viewGroup, false);
            return new LineHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LineHolder lineHolder, int i) {
            ArticleListInfo article = articleList.get(i);
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + article.getShPic())
                    .apply(RequestOptions.circleCropTransform()).into(lineHolder.ivPhoto);
            lineHolder.tvAuthor.setText(article.getAuthorName());
            lineHolder.tvTime.setText(article.getBeforeTime());
            lineHolder.tvCon.setText(article.getCon());
            lineHolder.itemView.setTag(i);
            lineHolder.itemView.setOnLongClickListener(onItemLongClick);

            for (int index = 0; index < lineHolder.ivPic.length; index++) {
                lineHolder.ivPic[index].setVisibility(View.GONE);
            }
            lineHolder.videoView.setVisibility(View.GONE);
            if (article.getUploadTyp().equals("1")) {
                if (article.getPubPic() != null && article.getPubPic().length() > 0) {
                    String[] pubPic = article.getPubPic().split("\\;");
                    for (int index = 0; index < pubPic.length; index++) {
                        lineHolder.ivPic[index].setVisibility(View.VISIBLE);
                        Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + pubPic[index]).into(lineHolder.ivPic[index]);
                    }
                }
            } else {
                lineHolder.videoView.setVisibility(View.VISIBLE);
                lineHolder.videoView.setTag(R.id.tag_video_path, article.getPubPic());
                lineHolder.videoView.setVideoURI(Uri.parse(ServerAPI.FILL_DOMAIN + article.getPubPic()));
                lineHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                lineHolder.videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = (String) v.getTag(R.id.tag_video_path);
                        Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
                        intent.putExtra("uri", uri);
                        getContext().startActivity(intent);
                    }
                });
                lineHolder.videoView.setTag(i);
                lineHolder.videoView.setOnLongClickListener(onItemLongClick);
            }
        }

        @Override
        public int getItemCount() {
            return articleList == null ? 0 : articleList.size();
        }
    }

    class LineHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        TextView tvAuthor, tvCon, tvTime;
        ImageView[] ivPic = new ImageView[6];
        VideoView videoView;

        public LineHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_recom_item_photo);
            tvAuthor = itemView.findViewById(R.id.tv_recom_item_author);
            tvCon = itemView.findViewById(R.id.tv_recom_item_con);
            tvTime = itemView.findViewById(R.id.tv_recom_item_time);
            ivPic[0] = itemView.findViewById(R.id.iv_recom_item_1);
            ivPic[1] = itemView.findViewById(R.id.iv_recom_item_2);
            ivPic[2] = itemView.findViewById(R.id.iv_recom_item_3);
            ivPic[3] = itemView.findViewById(R.id.iv_recom_item_4);
            ivPic[4] = itemView.findViewById(R.id.iv_recom_item_5);
            ivPic[5] = itemView.findViewById(R.id.iv_recom_item_6);
            videoView = itemView.findViewById(R.id.video_view_recom_item);
        }
    }

    private void httpGetMineInfo() {
        showLoadView();
        String url = ServerAPI.getDocInfoUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                DoctorInfo info = JSONObject.parseObject(data, DoctorInfo.class);
                ImageView ivPhoto = findViewById(R.id.iv_my_blog_photo);
                Glide.with(getContext())
                        .load(ServerAPI.FILL_DOMAIN + info.getShPic())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivPhoto);
                setViewText(R.id.tv_my_blog_name, info.getName());
                setViewText(R.id.tv_my_blog_office, info.getHospName() + " " + info.getDocOffiName());
                setViewText(R.id.tv_my_blog_adept, info.getGoodAt());
                setViewText(R.id.tv_my_blog_position, info.getCity());
                setViewText(R.id.tv_my_blog_tag, info.getGoodAt());
                setViewText(R.id.tv_my_blog_desc, info.getWdesc());
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

    private void setViewText(int id, String text) {
        TextView tv = findViewById(id);
        tv.setText(text);
    }

    protected void httpGetList(boolean isRefresh) {
        if (noMore && !isRefresh) return;
        if (isRefresh) {
            articleList.clear();
            if (rvRecom.getAdapter() != null)
                rvRecom.getAdapter().notifyDataSetChanged();
        }
        loadBegin();
        String url = ServerAPI.getMyArticleListUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageSize", PAGE_SIZE);
        jsonObject.put("pageNum", articleList.size() / PAGE_SIZE + 1);
        jsonObject.put("pubTyp", pubTyp);
        jsonObject.put("columnId", columnId == null ? "" : columnId);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                List list = jsonObject.getJSONArray("list").toJavaList(ArticleListInfo.class);
                if (list != null && list.size() > 0) {
                    articleList.addAll(list);
                    if (pubTyp == 1) {
                        if (rvRecom.getAdapter() == null || rvRecom.getAdapter() != adapter) {
                            rvRecom.setAdapter(adapter);
                        }
                    } else {
                        if (rvRecom.getAdapter() == null || rvRecom.getAdapter() != lineAdapter) {
                            rvRecom.setAdapter(lineAdapter);
                        }
                    }
                    rvRecom.getAdapter().notifyDataSetChanged();
                }
                if (list == null || list.size() < PAGE_SIZE) {
                    loadFinish();
                } else {
                    loadEnd();
                }
                if (articleList == null || articleList.size() == 0) {
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

    private void httpDelArticle(String id) {
        showLoadView();
        String url = ServerAPI.getDelArticleUrl(id);
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().request(HttpMethod.DELETE, entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                httpGetList(true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_ARTICLE_CODE) {
            if (resultCode == RESULT_OK) {
                httpGetList(true);
            }
        } else if (requestCode == EDIT_ARTICLE_CODE) {
            if (resultCode == RESULT_OK) {
                httpGetList(true);
            }
        } else {
            if (resultCode == RESULT_OK) {
                int position = requestCode % DETAIL_ARTICLE_CODE;
                int likeNumber = data.getIntExtra("likeNumber", 0);
                int collectNumber = data.getIntExtra("collectNumber", 0);
                String collectState = data.getStringExtra("collectState");
                String likeState = data.getStringExtra("likeState");
                ArticleListInfo article = articleList.get(position);
                article.setLikeNum(likeNumber);
                article.setFavNum(collectNumber);
                article.getFavObj().setKeyValue(collectState);
                article.getLikeObj().setKeyValue(likeState);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void getMenu() {
        String url = ServerAPI.getArticleMenuUrl();
        RequestParams entity = new RequestParams(url);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {

                RadioGroup rgMenu = findViewById(R.id.rg_frag_home_menu);
                ((RadioButton) rgMenu.getChildAt(0)).setChecked(true);
                rgMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rb_frag_home_menu_yk:
                                pubTyp = 1;
                                findViewById(R.id.rg_frag_home_menu_type).setVisibility(View.VISIBLE);
                                break;
                            case R.id.rb_frag_home_menu_dt:
                                pubTyp = 2;
                                findViewById(R.id.rg_frag_home_menu_type).setVisibility(View.GONE);
                                break;
                        }
                        httpGetList(true);
                    }
                });

                List<ArticleMenuInfo> menuList = JSONObject.parseArray(data, ArticleMenuInfo.class);
                RadioGroup rgType = findViewById(R.id.rg_frag_home_menu_type);
                for (int i = 0; i < menuList.size(); i++) {
                    RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.rb_article_menu_border, null);
                    rb.setText(menuList.get(i).getColumnName());
                    rb.setTag(menuList.get(i).getColumnId());
                    rb.setId(i);
                    if (i != menuList.size() - 1) {
                        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(-2, -2);
                        layoutParams.setMarginEnd(12);
                        rb.setLayoutParams(layoutParams);
                    }
                    rgType.addView(rb);
                }

                rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton rb = (RadioButton) group.getChildAt(checkedId);
                        String id = (String) rb.getTag();
                        columnId = id;
                        httpGetList(true);
                    }
                });
                ((RadioButton) rgType.getChildAt(0)).setChecked(true);
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

    MyBroadcastReceiver myBroadcastReceiver;

    private void registerMyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastAction.ACTION_ARTICLE_CREATED);
        myBroadcastReceiver=new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void unRegisterMyReceiver() {
        unregisterReceiver(myBroadcastReceiver);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            httpGetList(true);
        }
    }

}
