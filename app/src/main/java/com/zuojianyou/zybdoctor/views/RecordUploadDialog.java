package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.units.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordUploadDialog extends Dialog {

    String filePath;
    String id;//挂号id用于上传语音
    OnUploadSuccessListener onUploadSuccessListener;

    TextView tvTag;

    public RecordUploadDialog(Context context, String filePath, String id) {
        super(context, R.style.AlertDialog);
        this.filePath = filePath;
        this.id = id;
//        setCanceledOnTouchOutside(false);
    }

    public void setOnUploadSuccessListener(OnUploadSuccessListener onUploadSuccessListener) {
        this.onUploadSuccessListener = onUploadSuccessListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_record_upload);
        tvTag = findViewById(R.id.tv_dialog_record_upload_tag);
        httpFileUpload();
    }

    private void httpFileUpload() {
        String url = ServerAPI.getFileUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        File file = new File(filePath);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("file", file));
        mList.add(new KeyValue("extra", id));
        mList.add(new KeyValue("path", "voice"));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("httpFileUpload", result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    tvTag.setText("上传成功！");
                    String url = jsonObject.getJSONObject("data").getString("url");
                    if (onUploadSuccessListener != null)
                        onUploadSuccessListener.onUploadSuccess(url);
                    dismiss();
                } else {
                    tvTag.setText("上传失败！");
                    Toast.makeText(getContext(), jsonObject.getString("errMsg"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("httpFileUpload", ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onWaiting() {
                tvTag.setText("等待上传...");
            }

            @Override
            public void onStarted() {
                tvTag.setText("正在上传...");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d("httpFileUpload", current + "/" + total);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                long progress = current * 100 / total;
                progressBar.setProgress((int) progress);
            }
        });

    }

    public interface OnUploadSuccessListener {
        void onUploadSuccess(String url);
    }
}
