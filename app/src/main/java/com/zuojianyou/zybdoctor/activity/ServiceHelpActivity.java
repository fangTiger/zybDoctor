package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.units.FileUtils;
import com.zuojianyou.zybdoctor.units.HttpCallback;
import com.zuojianyou.zybdoctor.units.MyCallBack;
import com.zuojianyou.zybdoctor.units.ServerAPI;
import com.zuojianyou.zybdoctor.units.ToastUtils;
import com.zuojianyou.zybdoctor.views.ImageGlideDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceHelpActivity extends BaseActivity {

    View llFeedback, llTell;
    TextView btnFeedback, btnTell;

    EditText etFeedback;
    TextView tvTextNum, tvImgNum;

    RecyclerView rvImg;
    CertificateAdapter adapter;
    Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_help);
        findViewById(R.id.ib_act_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_call_service_tel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String tel = getResources().getString(R.string.service_tel_num);
                Uri data = Uri.parse("tel:" + tel.replaceAll("-", ""));
                intent.setData(data);
                startActivity(intent);
            }
        });
        btnSubmit = findViewById(R.id.btn_confirm);
        btnSubmit.setEnabled(false);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpSubmit();
            }
        });

        llFeedback = findViewById(R.id.ll_service_help_feedback_container);
        llTell = findViewById(R.id.ll_service_help_tell_container);
        btnFeedback = findViewById(R.id.btn_feedback_page);
        btnFeedback.setOnClickListener(onBtnPageClick);
        btnTell = findViewById(R.id.btn_service_tel_page);
        btnTell.setOnClickListener(onBtnPageClick);

        etFeedback = findViewById(R.id.et_feedback);
        etFeedback.addTextChangedListener(etFeedbackWatcher);
        tvTextNum = findViewById(R.id.tv_tag_feedback_num);
        tvImgNum = findViewById(R.id.tv_tag_img_num);

        rvImg = findViewById(R.id.rv_service_help_img);
        rvImg.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapter = new CertificateAdapter();
        rvImg.setAdapter(adapter);
    }

    View.OnClickListener onBtnPageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_feedback_page) {
                if (llFeedback.getVisibility() != View.VISIBLE) {
                    llFeedback.setVisibility(View.VISIBLE);
                    llTell.setVisibility(View.GONE);
                    btnFeedback.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
                    btnTell.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
                }
            } else if (v.getId() == R.id.btn_service_tel_page) {
                if (llTell.getVisibility() != View.VISIBLE) {
                    llTell.setVisibility(View.VISIBLE);
                    llFeedback.setVisibility(View.GONE);
                    btnFeedback.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGray));
                    btnTell.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextBlack));
                }
            }
        }
    };

    TextWatcher etFeedbackWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            tvTextNum.setText(s.length() + "/500");
            if (s.length() > 10) {
                btnSubmit.setEnabled(true);
            } else {
                btnSubmit.setEnabled(false);
            }
        }
    };

    class CertificateAdapter extends RecyclerView.Adapter<CertificateHolder> {

        List<String> imgUrls;

        public CertificateAdapter() {
            imgUrls = new ArrayList<>();
        }

        @NonNull
        @Override
        public CertificateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_certificate, parent, false);
            return new CertificateHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CertificateHolder holder, int position) {
            holder.tvTip.setVisibility(View.GONE);
            if (position < imgUrls.size()) {
                holder.btnDel.setVisibility(View.VISIBLE);
                holder.btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgUrls.remove(position);
                        tvImgNum.setText(imgUrls.size() + "/4");
                        notifyDataSetChanged();
                    }
                });
                Glide.with(getContext()).load(ServerAPI.FILL_DOMAIN + imgUrls.get(position)).into(holder.ivPhoto);
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageGlideDialog(getContext(), ServerAPI.FILL_DOMAIN + imgUrls.get(position), "").show();
                    }
                });
            } else {
                holder.btnDel.setVisibility(View.GONE);
                holder.ivPhoto.setImageResource(R.mipmap.ic_btn_add);
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImageSelect();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (imgUrls == null) {
                return 1;
            } else if (imgUrls.size() < 4) {
                return imgUrls.size() + 1;
            } else {
                return 4;
            }
        }
    }

    class CertificateHolder extends RecyclerView.ViewHolder {

        ImageView btnDel;
        ImageView ivPhoto;
        TextView tvTip;

        public CertificateHolder(@NonNull View itemView) {
            super(itemView);
            btnDel = itemView.findViewById(R.id.btn_del);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvTip = itemView.findViewById(R.id.tv_tip);
        }
    }

    //-----------------------图片选择---------------------------
    View imageSelectView;
    PopupWindow popupImageSelect;

    private void showImageSelect() {
        if (popupImageSelect == null) {
            imageSelectView = getLayoutInflater().inflate(R.layout.dialog_image_select, null);
            imageSelectView.findViewById(R.id.btn_dialog_image_select_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissImageSelect();
                }
            });
            imageSelectView.findViewById(R.id.btn_dialog_image_select_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissImageSelect();
                    takePhoto();
                }
            });
            imageSelectView.findViewById(R.id.btn_dialog_image_select_glide).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissImageSelect();
                    choosePic();
                }
            });
            popupImageSelect = new PopupWindow(imageSelectView, -1, -1, true);
        }
        if (!popupImageSelect.isShowing())
            popupImageSelect.showAtLocation(btnFeedback.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void dismissImageSelect() {
        popupImageSelect.dismiss();
    }


    public static final int OPEN_CAMERA_CODE = 204;//拍照
    public static final int OPEN_GALLERY_CODE = 205;//相册

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
            httpImageUpload(filePath);
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
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    String resultPath = jsonObject.getJSONObject("data").getString("url");
                    adapter.imgUrls.add(resultPath);
                    tvImgNum.setText(adapter.imgUrls.size() + "/4");
                    adapter.notifyDataSetChanged();
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

    private void httpSubmit() {
        showLoadView();
        String url = ServerAPI.getDocFeedBackUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("feedBack", etFeedback.getText().toString().trim());
        String imgUrlArr = getStringArr(adapter.imgUrls);
        if (imgUrlArr != null)
            jsonObject.put("uploadPath", imgUrlArr);
        entity.setBodyContent(jsonObject.toJSONString());
        x.http().post(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.show(getContext(),"提交成功！");
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

    private String getStringArr(List<String> list) {
        if (list == null || list.size() == 0) return null;
        StringBuffer sb = new StringBuffer();
        for (String str : list) {
            sb.append(str);
            sb.append(";");
        }
        return sb.toString();
    }
}
