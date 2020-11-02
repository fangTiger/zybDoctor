package com.zuojianyou.zybdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.utils.CameraUtils;
import com.zuojianyou.zybdoctor.views.RecordControlView;

import java.io.File;

public class VideoRecordActivity extends BaseActivity {

    private SurfaceView surfaceView;
    private CameraUtils cameraUtils;
    private String imagePath, videoPath, name;
    private ImageButton btnChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        surfaceView = findViewById(R.id.surface_view);
        btnChange = findViewById(R.id.btn_change);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        videoPath = getFilesDir() + File.separator + "videos" + File.separator;
        imagePath = getFilesDir() + File.separator + "images" + File.separator;
        name = String.valueOf(System.currentTimeMillis());

        cameraUtils = new CameraUtils(isPad(getContext()));
        cameraUtils.create(surfaceView, this);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUtils.changeCamera();
            }
        });

        RecordControlView recordControlView = findViewById(R.id.record_control_view);
        recordControlView.setOnTapListener(new RecordControlView.OnTapListener() {
            @Override
            public void onTap() {
                cameraUtils.takePicture(imagePath, name);
                cameraUtils.setOnImageCreated(new CameraUtils.OnImageCreated() {
                    @Override
                    public void onCreated() {
                        Intent data = new Intent();
                        data.putExtra("type", "image");
                        String path = imagePath + File.separator + name + ".jpeg";
                        data.putExtra("path", path);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                Log.d("onTouch", "拍照");
            }
        });
        recordControlView.setOnPressListener(new RecordControlView.OnPressListener() {
            @Override
            public void onPress() {
                cameraUtils.startRecord(videoPath, name);
                Log.d("onTouch", "录制开始");
            }
        });
        recordControlView.setOnUpListener(new RecordControlView.OnUpListener() {
            @Override
            public void onUp() {
                cameraUtils.stopRecord();
                Intent data = new Intent();
                data.putExtra("type", "video");
                String path = videoPath  + name + ".mp4";
                data.putExtra("path", path);
                setResult(RESULT_OK, data);
                finish();
                Log.d("onTouch", "录制结束");
            }
        });
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
    }
}
