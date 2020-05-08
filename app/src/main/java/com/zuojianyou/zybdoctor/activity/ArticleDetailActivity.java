package com.zuojianyou.zybdoctor.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.ArticleListInfo;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.TimeUtils;
import com.zuojianyou.zybdoctor.wxapi.WXShare;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArticleDetailActivity extends BaseActivity {

    NestedScrollView scrollView;
    View closeContainer;

    ArticleListInfo article;
    TextView tvLikeNum, tvCollectNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        findViewById(R.id.ib_act_article_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.ib_act_article_detail_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSharePopup();
            }
        });
        String strArticle = getIntent().getStringExtra("article");
        article = JSONObject.parseObject(strArticle, ArticleListInfo.class);
        setViewText(R.id.tv_act_article_detail_title, article.getTitle());
        setViewText(R.id.tv_act_article_detail_author, article.getAuthorName());
        setViewText(R.id.tv_act_article_detail_time, TimeUtils.toNormDay(article.getPubTime()));
//        setContent(article.getCon());
        setViewText(R.id.tv_act_article_detail_collect, String.valueOf(article.getFavNum()));
        setViewText(R.id.tv_act_article_detail_like, String.valueOf(article.getLikeNum()));
        ImageView ivSurface = findViewById(R.id.iv_act_article_detail_surface);
        Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + article.getCoverPath()).into(ivSurface);

        scrollView = findViewById(R.id.nsv_act_article_detail_content);
        closeContainer = findViewById(R.id.rl_act_article_detail_drawer);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    if (closeContainer.getVisibility() != View.GONE) {
                        closeDrawer();
                    }
                }
                if (scrollY < oldScrollY) {
                    if (closeContainer.getVisibility() != View.VISIBLE) {
                        openDrawer();
                    }
                }
            }
        });

        tvLikeNum = findViewById(R.id.tv_act_article_detail_like);
        tvLikeNum.setCompoundDrawables(null, null,
                getSupportDrawable(article.getLikeObj().getKeyValue()), null);
        tvLikeNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpArticleLike();
            }
        });

        tvCollectNum = findViewById(R.id.tv_act_article_detail_collect);
        tvCollectNum.setCompoundDrawables(null, null,
                getCollectDrawable(article.getFavObj().getKeyValue()), null);
        tvCollectNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpArticleCollect();
            }
        });

        initWebView();
        setWebViewContent(article.getCon());
    }

    private void openDrawer() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(closeContainer, "translationY",
                closeContainer.getHeight(), 0f);
        animator.setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                closeContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void closeDrawer() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(closeContainer, "translationY",
                0f, closeContainer.getHeight());
        animator.setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                closeContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void setViewText(int id, String str) {
        TextView tv = findViewById(id);
        tv.setText(str);
    }

    private Drawable getSupportDrawable(String value) {
        if (value.equals("0")) {
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_article_support);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            return drawable;
        } else {
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_article_supported);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            return drawable;
        }
    }

    private Drawable getCollectDrawable(String value) {
        if (value.equals("0")) {
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_article_collect);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            return drawable;
        } else {
            Drawable drawable = getResources().getDrawable(R.mipmap.icon_article_collected);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            return drawable;
        }
    }

    private void httpArticleLike() {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getLikeArticleUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.addParameter("paId", article.getPaId());
        String type = article.getLikeObj().getKeyValue().equals("1") ? "0" : "1";
        entity.addParameter("type", type);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                String type = article.getLikeObj().getKeyValue().equals("1") ? "0" : "1";
                article.getLikeObj().setKeyValue(type);
                if (type.equals("1")) {
                    article.setLikeNum(article.getLikeNum() + 1);
                } else {
                    article.setLikeNum(article.getLikeNum() - 1);
                }
                tvLikeNum.setText(String.valueOf(article.getLikeNum()));
                tvLikeNum.setCompoundDrawables(null, null,
                        getSupportDrawable(article.getLikeObj().getKeyValue()), null);
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

    private void httpArticleCollect() {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getCollectArticleUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.addParameter("paId", article.getPaId());
        String type = article.getFavObj().getKeyValue().equals("1") ? "0" : "1";
        entity.addParameter("type", type);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                String type = article.getFavObj().getKeyValue().equals("1") ? "0" : "1";
                article.getFavObj().setKeyValue(type);
                if (type.equals("1")) {
                    article.setFavNum(article.getFavNum() + 1);
                } else {
                    article.setFavNum(article.getFavNum() - 1);
                }
                tvCollectNum.setText(String.valueOf(article.getFavNum()));
                tvCollectNum.setCompoundDrawables(null, null,
                        getCollectDrawable(article.getFavObj().getKeyValue()), null);
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("likeNumber", article.getLikeNum());
        intent.putExtra("collectNumber", article.getFavNum());
        intent.putExtra("likeState", article.getLikeObj().getKeyValue());
        intent.putExtra("collectState", article.getFavObj().getKeyValue());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    CharSequence charSequence = (CharSequence) msg.obj;
                    if (charSequence != null) {
                        TextView tvCon = findViewById(R.id.tv_act_article_detail_con);
                        tvCon.setText(charSequence);
                        tvCon.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void setContent(final String con) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Html.ImageGetter imageGetter = new Html.ImageGetter() {

                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable;
                        drawable = getImageNetwork(source);
                        if (drawable != null) {
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        } else if (drawable == null) {
                            return null;
                        }
                        return drawable;
                    }
                };
                CharSequence charSequence = Html.fromHtml(con.trim(), imageGetter, null);
                Message ms = Message.obtain();
                ms.what = 1;
                ms.obj = charSequence;
                mHandler.sendMessage(ms);
            }
        }).start();
    }

    public Drawable getImageNetwork(String imageUrl) {
        URL myFileUrl = null;
        Drawable drawable = null;
        try {
            myFileUrl = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            // 在这一步最好先将图片进行压缩，避免消耗内存过多
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            drawable = new BitmapDrawable(bitmap);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    //-----------webview---------------------------------
    private WebView webView;

    private void initWebView() {
        webView = findViewById(R.id.wv_act_article_detail_con);

//        webView.addJavascriptInterface(this, "android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
        webSettings.setAllowFileAccess(false);//允许使用js
        webSettings.setSavePassword(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

//        h5需要设置缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }
    };

    private void setWebViewContent(String html) {
        String standard = "<html> \n" +
                "<head> \n" +
                "<style type=\"text/css\"> \n" +
                "body {font-size:13px;}\n" +
                "</style> \n" +
                "</head> \n" +
                "<body>" +
                "<script type='text/javascript'>" +
                "window.onload = function(){\n" +
                "var $img = document.getElementsByTagName('img');\n" +
                "for(var p in  $img){\n" +
                " $img[p].style.width = '100%';\n" +
                "$img[p].style.height ='auto'\n" +
                "}\n" +
                "}" +
                "</script>" +
                html
                + "</body>" +
                "</html>";

        String mimeType = "text/html";
        String enCoding = "utf-8";
        webView.loadDataWithBaseURL(null, standard, mimeType, enCoding, null);
    }

    PopupWindow popupShare;

    private void showSharePopup() {
        if (popupShare == null) {
            View view = getLayoutInflater().inflate(R.layout.popup_share_selector, null);
            view.findViewById(R.id.tv_choose_share_btn_wechat).setOnClickListener(onShareTypeClick);
            view.findViewById(R.id.tv_choose_share_btn_wxblog).setOnClickListener(onShareTypeClick);
            view.findViewById(R.id.iv_btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupShare.dismiss();
                }
            });
            popupShare = new PopupWindow(view, -1, -2, true);
        }
        popupShare.showAtLocation(scrollView.getRootView(), Gravity.BOTTOM, 0, 0);
    }

    View.OnClickListener onShareTypeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String type;
            if (v.getId() == R.id.tv_choose_share_btn_wechat) {
                type = WXShare.SHARE_TYPE_CHAT;
            } else {
                type = WXShare.SHARE_TYPE_BLOG;
            }
            String url = ServerAPI.getAticleShareUrl(article.getPaId());
            String title = article.getTitle();
            String desc = article.getSubtitle();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_login_logo);
            WXShare wxShare = new WXShare(getContext());
            wxShare.shareWeb(url, title, desc, bitmap, type);
            popupShare.dismiss();
        }
    };

}
