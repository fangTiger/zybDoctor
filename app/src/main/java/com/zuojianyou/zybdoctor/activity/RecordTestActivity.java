package com.zuojianyou.zybdoctor.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.view.View;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.service.Mp3RecordService;

public class RecordTestActivity extends BaseActivity {

    Mp3RecordService mp3Record;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_test);

        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Mp3RecordService.RecordBind recordBind = (Mp3RecordService.RecordBind) service;
                mp3Record = recordBind.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intentRecord = new Intent(this, Mp3RecordService.class);
        bindService(intentRecord, conn, Service.BIND_AUTO_CREATE);

        findViewById(R.id.btn_act_record_test_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp3Record.startRecord();
            }
        });

        findViewById(R.id.btn_act_record_test_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp3Record.stopRecord();
            }
        });
    }
}
