package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.app.BroadcastAction;
import com.zuojianyou.zybdoctor.utils.FileUtils;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.ToastUtils;
import com.zuojianyou.zybdoctor.views.ImageGlideDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArticleCreateActivity extends BaseActivity {

    public static final int OPEN_CAMERA_CODE = 204;//拍照
    public static final int OPEN_GALLERY_CODE = 205;//相册

    String resultPath = "";
    EditText etContent;
    int uploadType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_create);
        etContent = findViewById(R.id.et_act_article_edit_content);
        findViewById(R.id.ib_act_article_edit_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.ib_article_btn_add_img).setOnClickListener(btnSurfaceClick);
        findViewById(R.id.ib_act_article_edit_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etContent.getText())) {
                    ToastUtils.show(getContext(), "请填写内容！");
                    return;
                }
                httpSubmitArticle();
            }
        });

        int type = getIntent().getIntExtra("type", -1);
        if (type == 1) {
            choosePic();
        } else if (type == 2) {
            takePhoto();
        } else {
            findViewById(R.id.ib_article_btn_add_img).performClick();
        }
    }

    PopupWindow popupImageSelect;
    View.OnClickListener btnSurfaceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = getLayoutInflater().inflate(R.layout.dialog_image_select, null);
            view.findViewById(R.id.btn_dialog_image_select_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePhoto();
                    popupImageSelect.dismiss();
                }
            });
            view.findViewById(R.id.btn_dialog_image_select_glide).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choosePic();
                    popupImageSelect.dismiss();
                }
            });
            view.findViewById(R.id.btn_dialog_image_select_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupImageSelect.dismiss();
                }
            });
            if (popupImageSelect == null)
                popupImageSelect = new PopupWindow(view, -1, -1, true);
            popupImageSelect.showAtLocation(etContent.getRootView(), Gravity.CENTER, 0, 0);
        }
    };

    private String mTempPhotoPath;
    private Uri imageUri;

    private void takePhoto() {
        Intent intent = new Intent(this, VideoRecordActivity.class);
        startActivityForResult(intent, OPEN_CAMERA_CODE);
    }

    private void choosePic() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, OPEN_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_CAMERA_CODE:
                    String type = data.getStringExtra("type");
                    String path = data.getStringExtra("path");
                    if (type.equals("image")) {
                        httpImageUpload(path);
                    } else {
                        httpVideoUpload(path);
                    }
                    break;
                case OPEN_GALLERY_CODE:
                    Uri uri = data.getData();
                    String filePath = FileUtils.getFilePathByUri(this, uri);
                    httpImageUpload(filePath);
                    break;
            }
        }
    }

    private void httpImageUpload(String imgPath) {
        String url = ServerAPI.getFileUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        File file = new File(imgPath);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("file", file));
        mList.add(new KeyValue("extra", ""));
        mList.add(new KeyValue("path", "inquiry/images"));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("httpImageUpload", result);
                uploadType = 1;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    resultPath = jsonObject.getJSONObject("data").getString("url");
                    addImage(resultPath);
                } else {
                    Toast.makeText(getContext(), jsonObject.getString("errMsg"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("httpImageUpload", ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenLoadView();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                showLoadView();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d("httpImageUpload", current + "/" + total);
            }
        });

    }

    private void addImage(String imagePath) {
        final LinearLayout ll = findViewById(R.id.ll_article_img);
        final View view = getLayoutInflater().inflate(R.layout.item_treat_img, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.setMargins(0, 0, 12, 0);
        view.setLayoutParams(layoutParams);
        view.setTag(imagePath);
        ImageView imageView = view.findViewById(R.id.iv_img);
        Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + imagePath).into(imageView);
        imageView.setTag(R.id.tag_image_path, ServerAPI.FILL_DOMAIN + imagePath);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgPath = (String) v.getTag(R.id.tag_image_path);
                new ImageGlideDialog(getContext(), imgPath, "").show();
            }
        });
        TextView tvName = view.findViewById(R.id.tv_position_name);
        tvName.setVisibility(View.GONE);
        View btnDel = view.findViewById(R.id.btn_del);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < ll.getChildCount() - 1; i++) {
                    if (ll.getChildAt(i) == view) {
                        ll.removeViewAt(i);
                    }
                }
            }
        });
        ll.addView(view, ll.getChildCount() - 1);
    }

    private String getPubPic() {
        if (uploadType == 1) {
            LinearLayout ll = findViewById(R.id.ll_article_img);
            if (ll.getChildCount() <= 1) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ll.getChildCount() - 1; i++) {
                if (i > 0) sb.append(";");
                sb.append((String) ll.getChildAt(i).getTag());
            }
            return sb.toString();
        } else if (uploadType == 2) {
            VideoView videoView = findViewById(R.id.video_view);
            return (String) videoView.getTag();
        }
        return "";
    }

    private void httpVideoUpload(String vdoPath) {
        String url = ServerAPI.getFileUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        File file = new File(vdoPath);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("file", file));
        mList.add(new KeyValue("extra", ""));
        mList.add(new KeyValue("path", "videos"));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                uploadType = 2;
                Log.d("httpVideoUpload", result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    resultPath = jsonObject.getJSONObject("data").getString("url");
                    findViewById(R.id.ll_article_img).setVisibility(View.GONE);
                    VideoView videoView = findViewById(R.id.video_view);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(Uri.parse(ServerAPI.FILL_DOMAIN + resultPath));
                    videoView.start();
                    videoView.setTag(resultPath);
                } else {
                    Toast.makeText(getContext(), jsonObject.getString("errMsg"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("httpVideoUpload", ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hiddenLoadView();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                showLoadView();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d("httpVideoUpload", current + "/" + total);
            }
        });

    }

    private void httpSubmitArticle() {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getArticleCommitUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("con", etContent.getText().toString());
        jsonObject.put("pubPic", getPubPic());
        jsonObject.put("uploadTyp", uploadType);
        jsonObject.put("pubTyp", "2");
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                Intent intent = new Intent();
                intent.setAction(BroadcastAction.ACTION_ARTICLE_CREATED);
                sendBroadcast(intent);
//                Intent intent = new Intent();
//                intent.setAction(ACTION_TREAT_BACK);
//                sendBroadcast(intent);
                Toast.makeText(getApplicationContext(), "提交成功！", Toast.LENGTH_SHORT).show();
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
