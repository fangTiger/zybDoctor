package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.zuojianyou.zybdoctor.data.SpData;
import com.zuojianyou.zybdoctor.units.DocAuthStateUtils;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.TimeUtils;
import com.zuojianyou.zybdoctor.views.ImageGlideDialog;
import com.zuojianyou.zybdoctor.views.ImageSelectDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment
 * 首页
 */
public class MainFragmentHome extends Fragment {

    public final int PAGE_SIZE = 20;

    private TextView emptyView, tvLoadTip;
    private View llLoad, pbLoad;
    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView scrollView;
    private RecyclerView rvRecom;
    private GridRecomAdapter gridAdapter;
    private LineRecomAdapter lineAdapter;
    private List<ArticleListInfo> articleList;

    private boolean noMore = false;

    private int pubTyp;
    private String columnId;

    ViewPager viewPager;//banner
    LinearLayout linearLayout;//banner显示索引
    boolean isShow;//控制自动循环
    boolean isTouch = false;//是否正在触摸滑动
    AutoDisplay autoDisplay;//异步自动循环线程
    LoopAdapter loopAdapter;//适配器

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager vp = view.findViewById(R.id.vp_index_home_banner);
        if (vp != null) {
            linearLayout = view.findViewById(R.id.ll_index_home_banner_index);

            viewPager = view.findViewById(R.id.vp_index_home_banner);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == 0) {
                        isTouch = false;
                        int current = viewPager.getCurrentItem();
                        if (current == loopAdapter.getCount() - 1) {
                            current = 1;
                            viewPager.setCurrentItem(current, false);
                        } else if (current == 0) {
                            current = loopAdapter.getCount() - 2;
                            viewPager.setCurrentItem(current, false);
                        }
                        setIndexViewImage(current - 1);
                    } else {
                        isTouch = true;
                    }
                }
            });

            List<Integer> banners = new ArrayList<>();
            banners.add(R.mipmap.test_banner_01);
            banners.add(R.mipmap.test_banner_02);
            banners.add(R.mipmap.test_banner_01);
            banners.add(R.mipmap.test_banner_02);

            Integer firstNode = banners.get(0);
            Integer endNode = banners.get(banners.size() - 1);
            banners.add(0, endNode);
            banners.add(firstNode);

            loopAdapter = new LoopAdapter(banners);
            viewPager.setAdapter(loopAdapter);

            for (int i = 0; i < banners.size() - 2; i++) {
                ImageView ivIndex = new ImageView(getContext());
                linearLayout.addView(ivIndex);
            }

            viewPager.setCurrentItem(1, false);
            setIndexViewImage(0);

            autoDisplay = new AutoDisplay();
            autoDisplay.execute();
            isShow = true;
        }

        refreshLayout = view.findViewById(R.id.refresh_layout_frag_home);
        refreshLayout.setOnRefreshListener(refreshListener);
        scrollView = view.findViewById(R.id.nsv_frag_home_content);
        scrollView.setOnScrollChangeListener(scrollListener);

        rvRecom = view.findViewById(R.id.rv_fragment_home_recom);
        rvRecom.setHasFixedSize(true);
        rvRecom.setNestedScrollingEnabled(false);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.isPad(getContext())) {
            GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
            rvRecom.setLayoutManager(glm);
            rvRecom.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    if (parent.getChildAdapterPosition(view) % 2 == 0) {
                        outRect.right = 10;
                    } else {
                        outRect.left = 10;
                    }
                }
            });
        } else {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            rvRecom.setLayoutManager(llm);
        }
        gridAdapter = new GridRecomAdapter();
        lineAdapter = new LineRecomAdapter();
//        rvRecom.setAdapter(gridAdapter);

        emptyView = view.findViewById(R.id.frag_home_empty_view);
        tvLoadTip = view.findViewById(R.id.tv_frag_home_load_tip);
        llLoad = view.findViewById(R.id.ll_frag_home_load);
        pbLoad = view.findViewById(R.id.pb_frag_home_load);

        view.findViewById(R.id.btn_frag_home_my_blog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), MyBlogActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            Intent intent = new Intent(getActivity(), MyBlogActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });

        view.findViewById(R.id.btn_frag_home_reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });

        view.findViewById(R.id.btn_frag_home_my_earn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    Intent intent = new Intent(getActivity(), MyEarnListActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            Intent intent = new Intent(getActivity(), MyEarnListActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }
            }
        });

        view.findViewById(R.id.btn_frag_home_article_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = SpData.getAuthFlag();
                if (flag.equals("9") || flag.equals("8")) {
                    ImageSelectDialog dialog = new ImageSelectDialog(getContext());
                    dialog.show();
                } else {
                    DocAuthStateUtils authUtils = new DocAuthStateUtils(new DocAuthStateUtils.OnAuth() {
                        @Override
                        public void onAuth() {
                            ImageSelectDialog dialog = new ImageSelectDialog(getContext());
                            dialog.show();
                        }
                    });
                    authUtils.httpGetAuthed(v);
                }

            }
        });
        getMenu();
    }

    public void setIndexViewImage(int index) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) linearLayout.getChildAt(i);
            if (i == index) {
                imageView.setImageResource(R.mipmap.index_home_icon_banner_visiable);
            } else {
                imageView.setImageResource(R.mipmap.index_home_icon_banner_invisiable);
            }
        }
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

    class BannerAdapter extends PagerAdapter {

        int[] src = new int[]{R.mipmap.test_banner_01, R.mipmap.test_banner_02, R.mipmap.test_banner_03};

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(src[position]);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class GridRecomAdapter extends RecyclerView.Adapter<RecomHolder> {

        @NonNull
        @Override
        public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_home_recom, viewGroup, false);
            return new RecomHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecomHolder recomHolder, int i) {
            ArticleListInfo article = articleList.get(i);
            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + article.getCoverPath())
                    .thumbnail(0.4f).into(recomHolder.ivSurface);
            recomHolder.tvTitle.setText(article.getTitle());
            recomHolder.tvTheme.setText(article.getSubtitle());
            recomHolder.tvTime.setText(TimeUtils.toNormDay(article.getPubTime()));
            recomHolder.itemView.setTag(i);
            recomHolder.itemView.setOnClickListener(onItemClick);
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
//            startActivityForResult(intent, DETAIL_ARTICLE_CODE + position);
            getActivity().startActivity(intent);
        }
    };

    class RecomHolder extends RecyclerView.ViewHolder {

        ImageView ivSurface;
        TextView tvTitle, tvTheme, tvTime;

        public RecomHolder(@NonNull View itemView) {
            super(itemView);
            ivSurface = itemView.findViewById(R.id.iv_home_recom_item_img);
            tvTitle = itemView.findViewById(R.id.tv_home_recom_item_title);
            tvTheme = itemView.findViewById(R.id.tv_home_recom_item_theme);
            tvTime = itemView.findViewById(R.id.tv_home_recom_item_time);
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
            for (int index = 0; index < lineHolder.ivPic.length; index++) {
                lineHolder.ivPic[index].setVisibility(View.GONE);
            }
            lineHolder.videoView.setVisibility(View.GONE);
            if (article.getUploadTyp().equals("1")) {
                if (article.getPubPic() != null && article.getPubPic().length() > 0) {
                    String[] pubPic = article.getPubPic().split("\\;");
                    for (int index = 0; index < pubPic.length; index++) {
                        final int position = index;
                        lineHolder.ivPic[index].setVisibility(View.VISIBLE);
                        lineHolder.ivPic[index].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new ImageGlideDialog(getContext(), ServerAPI.FILL_DOMAIN + pubPic[position], "").show();
                            }
                        });
                        Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + pubPic[index])
                                .thumbnail(0.4f).into(lineHolder.ivPic[index]);
                    }
                }
            } else if (article.getPubPic().length() > 0) {
                lineHolder.videoView.setVisibility(View.VISIBLE);
                lineHolder.videoView.setTag(article.getPubPic());
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
                        String uri = (String) v.getTag();
                        Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
                        intent.putExtra("uri", uri);
                        getContext().startActivity(intent);
                    }
                });
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

    protected void httpGetList(boolean isRefresh) {
        if (noMore && !isRefresh) return;
        if (isRefresh) {
            articleList.clear();
            if (rvRecom.getAdapter() != null)
                rvRecom.getAdapter().notifyDataSetChanged();
        }
        loadBegin();
        String url = ServerAPI.getAllArticleListUrl();
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
                        if (rvRecom.getAdapter() == null || rvRecom.getAdapter() != gridAdapter) {
                            rvRecom.setAdapter(gridAdapter);
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

    private void getMenu() {
        String url = ServerAPI.getArticleMenuUrl();
        RequestParams entity = new RequestParams(url);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                List<ArticleMenuInfo> menuList = JSONObject.parseArray(data, ArticleMenuInfo.class);
                ArticleMenuInfo menuDt = new ArticleMenuInfo();
                menuDt.setColumnName("动态");
                menuDt.setColumnId("");
                menuList.add(0, menuDt);
                RadioGroup rgMenu = getView().findViewById(R.id.rg_frag_home_menu);
                for (int i = 0; i < menuList.size(); i++) {
                    RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.rb_article_menu_divider, null);
                    rb.setText(menuList.get(i).getColumnName());
                    rb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    rb.setTag(menuList.get(i).getColumnId());
                    rb.setId(i);
                    rgMenu.addView(rb);
                }

                rgMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton rb = (RadioButton) group.getChildAt(checkedId);
                        String id = (String) rb.getTag();
                        if (id.equals("")) {
                            pubTyp = 2;
                        } else {
                            pubTyp = 1;
                        }
                        columnId = id;
                        httpGetList(true);
                    }
                });
                ((RadioButton) rgMenu.getChildAt(0)).setChecked(true);
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

    public class AutoDisplay extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            while (isShow) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            super.onProgressUpdate(values);
            if (!isTouch) {
                int pageIndex = viewPager.getCurrentItem();
                pageIndex++;
                viewPager.setCurrentItem(pageIndex);
            }
        }
    }

    class LoopAdapter extends PagerAdapter {

        private List<Integer> banners;

        public LoopAdapter(List<Integer> banners) {
            this.banners = banners;
        }

        public void setData(List<Integer> banners) {
            this.banners = banners;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return banners == null ? 0 : banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(banners.get(position));
            imageView.setTag(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            for (int i = 0; i < container.getChildCount(); i++) {
                int tag = (Integer) container.getChildAt(i).getTag();
                if (tag == position) {
                    container.removeViewAt(i);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loopAdapter != null && loopAdapter.getCount() > 0) {
            isShow = true;
            autoDisplay = new AutoDisplay();
            autoDisplay.execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isShow = false;
        autoDisplay = null;
    }


}
