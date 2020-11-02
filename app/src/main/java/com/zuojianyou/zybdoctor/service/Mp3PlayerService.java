package com.zuojianyou.zybdoctor.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.zuojianyou.zybdoctor.utils.ServerAPI;

import java.io.IOException;

public class Mp3PlayerService extends Service {

    MediaPlayer mediaPlayer;
    OnStartListener onStartListener;
    OnCompleteListener onCompleteListener;

    public void setOnStartListener(OnStartListener onStartListener) {
        this.onStartListener = onStartListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public class PlayerBind extends Binder {
        public Mp3PlayerService getService() {
            return Mp3PlayerService.this;
        }
    }

    PlayerBind playerBind = new PlayerBind();

    @Override
    public IBinder onBind(Intent intent) {
        return playerBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public int play(String path) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(ServerAPI.FILL_DOMAIN + path);
            mediaPlayer.setLooping(false);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    if (onStartListener != null) {
                        onStartListener.onStart();
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(onCompleteListener!=null){
                        onCompleteListener.onComplete();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public int play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            return 0;
        }
        return 1;
    }

    public int pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            return 0;
        }
        return 1;
    }

    public int stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            return 0;
        }
        return 1;
    }

    public int replay() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.start();
            return 0;
        }
        return 1;
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        return mediaPlayer.isPlaying();
    }

    public int getPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) return mediaPlayer.getDuration();
        return 0;
    }

    public interface OnStartListener {
        void onStart();
    }

    public interface OnCompleteListener {
        void onComplete();
    }


}
