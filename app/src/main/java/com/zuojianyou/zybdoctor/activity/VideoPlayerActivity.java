package com.zuojianyou.zybdoctor.activity;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.units.ServerAPI;

public class VideoPlayerActivity extends BaseActivity {

    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoView = findViewById(R.id.video_view);
        String uri = getIntent().getStringExtra("uri");
        MediaController controller = new MediaController(this);
        videoView.setMediaController(controller);
        videoView.setVideoURI(Uri.parse(ServerAPI.FILL_DOMAIN + uri));
    }
}
