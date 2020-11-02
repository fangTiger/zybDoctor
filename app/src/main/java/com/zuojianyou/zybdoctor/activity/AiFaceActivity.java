package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.utils.AiFaceUtils;
import com.zuojianyou.zybdoctor.utils.ServerAPI;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AiFaceActivity extends BaseActivity {

    private SurfaceView surfaceView;
    private AiFaceUtils cameraUtils;
    private ImageButton btnChange;
    private TextView tvTip;
    private boolean isUpload = false;
    private boolean isDetected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_face);

        surfaceView = findViewById(R.id.surface_view);
        btnChange = findViewById(R.id.btn_change);
        tvTip = findViewById(R.id.tv_ai_face_tip);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cameraUtils = new AiFaceUtils(isPad(getContext()));
        cameraUtils.create(surfaceView, this);
        cameraUtils.setOnImageCreated(new AiFaceUtils.OnImageCreated() {
            @Override
            public void onCreated(byte[] bytes, int backOrFront) {
//                new AsyncSaveImg(bytes).start();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(backOrFront == 0 ? 90 : 270);
                Bitmap dstBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                String imagePath = getFilesDir() + File.separator + "images" + File.separator;
                File file1 = new File(imagePath);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File file = new File(imagePath, "tempAi.jpeg");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    FileOutputStream out = new FileOutputStream(file);
//                    dstBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    if(isPad){
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    }else{
                        dstBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    }
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                httpImageUpload(file);
            }
        });
        cameraUtils.setOnFaceDetected(new AiFaceUtils.OnFaceDetected() {
            @Override
            public void onDetected() {
                if (isUpload || isDetected) return;
                cameraUtils.setPreviewGetePass(true);
                isUpload = true;
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUtils.changeCamera();
            }
        });
    }

    class AsyncSaveImg extends Thread {

        byte[] bytes;

        public AsyncSaveImg(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void run() {
            super.run();
            String imagePath = getFilesDir() + File.separator + "images" + File.separator;
            File file1 = new File(imagePath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            File file = new File(imagePath, "tempAi.jpeg");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                try {
                    fos.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            httpImageUpload(file);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraUtils.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraUtils.destroy();
        if (cancelable != null && !cancelable.isCancelled()) {
            cancelable.cancel();
        }
    }

    Callback.Cancelable cancelable;

    private void httpImageUpload(File file) {
        String url = ServerAPI.getAiPhotoUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("image", file));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        cancelable = x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("httpImageUpload", "result:" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    isDetected = true;
                    Intent intent = new Intent();
                    intent.putExtra("type", 0);
                    intent.putExtra("data", jsonObject.getString("data"));
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (jsonObject.getIntValue("code") == 1) {
                    isDetected = true;
                    httpImageUpload2(file);
                } else {
                    tvTip.setText(jsonObject.getString("errMsg"));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("httpImageUpload", "error:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                isUpload = false;
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d("httpImageUpload", "loading:" + current + "/" + total);
            }
        });
    }

    private void httpImageUpload2(File file) {
        String url = ServerAPI.getFileUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("file", file));
        mList.add(new KeyValue("extra", ""));
        mList.add(new KeyValue("path", "face"));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                file.delete();
                Log.d("httpImageUpload", result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("type", 1);
                    intent.putExtra("url", jsonObject.getJSONObject("data").getString("url"));
                    setResult(RESULT_OK, intent);
                    finish();
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
}

