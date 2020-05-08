package com.zuojianyou.zybdoctor.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;

public class Mp3RecordService extends Service {

    MediaRecorder mediaRecorder;
    boolean isRecording = false;
    String filePath;

    public class RecordBind extends Binder {
        public Mp3RecordService getService() {
            return Mp3RecordService.this;
        }
    }

    RecordBind recordBind = new RecordBind();

    @Override
    public IBinder onBind(Intent intent) {
        return recordBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRecording = false;
        if (mediaRecorder != null) {
            if (isRecording) {
                isRecording = false;
                mediaRecorder.stop();
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public int startRecord() {
        if (!isRecording && mediaRecorder == null) {
            isRecording = true;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder
                    .AudioSource.MIC);
            mediaRecorder.setAudioSamplingRate(8000);
            // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
            mediaRecorder.setOutputFormat(MediaRecorder
                    .OutputFormat.AMR_NB);
            // 设置声音编码的格式
            mediaRecorder.setAudioEncoder(MediaRecorder
                    .AudioEncoder.AMR_NB);
            File fileDir = new File(getApplicationContext().getFilesDir() + File.separator + "record" + File.separator);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File soundFile = new File(fileDir, System.currentTimeMillis() + ".mp3");
            filePath = soundFile.getAbsolutePath();
            mediaRecorder.setOutputFile(filePath);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
            // 开始录音
            mediaRecorder.start();
            return 0;
        }
        return 1;
    }

    public int stopRecord() {
        if (isRecording && mediaRecorder != null) {
            isRecording = false;
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            return 0;
        }
        return 1;
    }

    public String getFilePath() {
        return filePath;
    }
}
