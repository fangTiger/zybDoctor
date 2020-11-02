package com.zuojianyou.zybdoctor.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.ArticleInfo;
import com.zuojianyou.zybdoctor.utils.FileUtils;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ArticleEditActivity extends BaseActivity {

    public static final int OPEN_CAMERA_CODE = 204;//拍照
    public static final int OPEN_GALLERY_CODE = 205;//相册

    String resultPath = "";

    TextView tvTitle, tvTime;
    EditText etTitle, etTheme, etContent;
//    ImageView ivSurface;
    SimpleDateFormat sdf;
    ArticleInfo article;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_edit);
        String strArticle = getIntent().getStringExtra("article");
        if (strArticle != null) {
            article = JSONObject.parseObject(strArticle, ArticleInfo.class);
        }
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
                if (TextUtils.isEmpty(etTitle.getText())) {
                    Toast.makeText(getApplicationContext(), "请填写标题！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etTheme.getText())) {
                    Toast.makeText(getApplicationContext(), "请填写摘要！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etContent.getText())) {
                    Toast.makeText(getApplicationContext(), "请填写正文！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (article == null) {
                    article = new ArticleInfo();
                }
                article.setCon(etContent.getText().toString());
                article.setPubPic("");
                article.setPubTyp("2");
                article.setUploadTyp("1");

                httpSubmitArticle();
            }
        });
        tvTitle = findViewById(R.id.tv_act_article_edit_title);
        tvTime = findViewById(R.id.tv_act_article_edit_time);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (article != null) {
//            tvTime.setText(TimeUtils.toNormDay(article.getPubTime()));
            tvTime.setText(sdf.format(new Date()));
            tvTitle.setText("修改文章");
        } else {
            tvTime.setText(sdf.format(new Date()));
            tvTitle.setText("新建文章");
        }
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = null;
                try {
                    date = sdf.parse(tvTime.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date == null) date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        Date date = calendar.getTime();
                        tvTime.setText(sdf.format(date));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etTitle = findViewById(R.id.et_act_article_edit_title);
        etTheme = findViewById(R.id.et_act_article_edit_theme);
        etContent = findViewById(R.id.et_act_article_edit_content);
//        ivSurface = findViewById(R.id.iv_act_article_edit_surface);
//        ivSurface.setOnClickListener(btnSurfaceClick);
        if (article != null) {
            etContent.setText(article.getCon());
//            Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + article.getCoverPath()).into(ivSurface);
        }
    }

    View.OnClickListener btnSurfaceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(getContext())
                    .setTitle("提示").setMessage("请选择操作。")
                    .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            takePhoto();
                            dialog.dismiss();
                        }
                    }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setNegativeButton("相册", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    choosePic();
                    dialog.dismiss();
                }
            }).create().show();
        }
    };

    private String mTempPhotoPath;
    private Uri imageUri;

    private void takePhoto() {
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(getContext().getFilesDir() + File.separator + "images" + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File photoFile = new File(fileDir, System.currentTimeMillis() + ".jpeg");
        mTempPhotoPath = photoFile.getAbsolutePath();
        imageUri = FileProvider.getUriForFile(this, getContext().getPackageName() + ".android7.FileProvider", photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, OPEN_CAMERA_CODE);
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
            String filePath = null;
            switch (requestCode) {
                case OPEN_CAMERA_CODE:
                    filePath = mTempPhotoPath;
                    break;
                case OPEN_GALLERY_CODE:
                    Uri uri = data.getData();
                    filePath = FileUtils.getFilePathByUri(this, uri);
                    break;
            }
            //TODO
//            Glide.with(getContext()).load(filePath).into(ivSurface);
            httpImageUpload(filePath);
        }
    }

    private void httpSubmitArticle() {
        if (!checkNetwork()) return;
        showLoadView();
        String url = ServerAPI.getArticleCommitUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setBodyContent(JSONObject.toJSONString(article));
        Log.d("ArticleEdit", JSONObject.toJSONString(article));
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(getApplicationContext(), "提交成功！", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void addImage(String imagePath){
        final LinearLayout ll= findViewById(R.id.ll_article_img);
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
//                String imgPath = (String) v.getTag(R.id.tag_image_path);
//                new ImageGlideDialog(getContext(), imgPath, imgName).show();
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
}
