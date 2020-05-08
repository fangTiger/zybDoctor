package com.zuojianyou.zybdoctor.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.beans.treat.Record;
import com.zuojianyou.zybdoctor.service.Mp3PlayerService;

import java.util.List;

public class RecordListDialog extends Dialog {

    Mp3PlayerService mp3Player;
    List<Record> list;
    ListView listView;
    ImageButton btnPlay;
    TextView tvName;
    SeekBar seekBar;
    ProgressBar progressBar;
    TimerThread timerThread;

    public RecordListDialog(Context context, Mp3PlayerService mp3Player, List<Record> list) {
        super(context, R.style.AlertDialog);
        this.mp3Player = mp3Player;
        this.mp3Player.setOnStartListener(onStartListener);
        this.mp3Player.setOnCompleteListener(onCompleteListener);
        this.list = list;
    }

    Mp3PlayerService.OnStartListener onStartListener = new Mp3PlayerService.OnStartListener() {
        @Override
        public void onStart() {
            progressBar.setVisibility(View.GONE);
            btnPlay.setImageResource(R.mipmap.ic_audio_puase);
            seekBar.setMax(mp3Player.getDuration());
            timerThread = new TimerThread();
            timerThread.start();
        }
    };

    Mp3PlayerService.OnCompleteListener onCompleteListener = new Mp3PlayerService.OnCompleteListener() {
        @Override
        public void onComplete() {
            btnPlay.setImageResource(R.mipmap.ic_audio_play);
            timerThread.shut();
            timerThread = null;
            seekBar.setProgress(seekBar.getMax());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_record_list);
        findViewById(R.id.btn_dialog_record_list_close).setOnClickListener(btnCloseClicked);
        seekBar = findViewById(R.id.seekBar);
//        seekBar.setEnabled(false);
        progressBar = findViewById(R.id.pb_record_player);
        tvName = findViewById(R.id.tv_dialog_record_list_name);
        btnPlay = findViewById(R.id.btn_dialog_record_list_play);
        btnPlay.setOnClickListener(btnPlayClick);
        listView = findViewById(R.id.lv_dialog_record_list);
        listView.setEmptyView(findViewById(R.id.tv_dialog_record_list_empty_view));
        listView.setAdapter(new RecordAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvName.setText((position + 1) + ".mp3");
                mp3Player.play(list.get(position).getWurl());
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    View.OnClickListener btnPlayClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mp3Player.isPlaying()) {
                mp3Player.pause();
                btnPlay.setImageResource(R.mipmap.ic_audio_play);
                timerThread.shut();
                timerThread = null;
            } else {
                mp3Player.play();
                btnPlay.setImageResource(R.mipmap.ic_audio_puase);
                timerThread = new TimerThread();
                timerThread.start();
            }
        }
    };

    View.OnClickListener btnCloseClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mp3Player.isPlaying()) {
                mp3Player.stop();
            }
            if (timerThread != null) {
                timerThread.shut();
                timerThread = null;
            }
            dismiss();
        }
    };

    class RecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setText((position + 1) + ".mp3");
            textView.setPadding(16, 16, 16, 16);
            convertView = textView;
            return convertView;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                seekBar.setProgress(mp3Player.getPosition());
            }
        }
    };

    class TimerThread extends Thread {

        private boolean isRun = true;

        public void shut() {
            isRun = false;
        }

        @Override
        public void run() {
            super.run();
            while (isRun) {
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
