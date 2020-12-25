package com.zuojianyou.zybdoctor.rtc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.core.app.NotificationCompat;

import com.qiniu.droid.rtc.QNRoomState;
import com.qiniu.droid.rtc.QNTextureView;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;
import com.zuojianyou.zybdoctor.BuildConfig;
import com.zuojianyou.zybdoctor.R;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by dds on 2018/7/26.
 * android_shuai@163.com
 * 悬浮窗界面
 */
public class FloatingVideoService extends Service implements RTCEngineKit.EventListener{
    private static boolean isStarted = false;
    private static final int NOTIFICATION_ID = 1;

    private Intent resumeActivityIntent;

    private  RTCEngineKit mEngineKit;

    private WindowManager wm;
    private View floatView;
    private WindowManager.LayoutParams params;

    private String mSupportPersonId;
    private String mSupportMbrId;
    private String mSupportRegId;
    private float mSupportFee;

    //呼叫被邀请人信息
    private String mCallingUserId;
    private String mCallingUserName;
    private String mCallingUserImg;
    private String mCallType;

    //接听邀请人信息
    private String mInviterUserId;
    private String mInviterUserName;
    private String mInviterUserImg;
    private String mInviterType;
    private int mDuration;
    
    private boolean isTargetAnswered;
    private boolean isExpertAnswered;
    private String mInviteExpertId;
    private String mRegId;
    

    private Timer mTimer = new Timer();

    public FloatingVideoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isStarted) {
            return START_NOT_STICKY;
        }
        isStarted = true;
        if (intent != null) {
            mDuration = intent.getIntExtra(RoomActivity.EXTRA_CALL_DURATION, 0);
            mCallingUserId = intent.getStringExtra(RoomActivity.EXTRA_CALLING_USER_ID);
            mCallingUserName = intent.getStringExtra(RoomActivity.EXTRA_CALLING_USER_NAME);
            mCallingUserImg = intent.getStringExtra(RoomActivity.EXTRA_CALLING_USER_IMG);
            mCallType = intent.getStringExtra(RoomActivity.EXTRA_CALL_TYPE);
            mInviterUserId = intent.getStringExtra(RoomActivity.EXTRA_INVITER_USER_ID);
            mInviterUserName = intent.getStringExtra(RoomActivity.EXTRA_INVITER_USER_NAME);
            mInviterUserImg = intent.getStringExtra(RoomActivity.EXTRA_INVITER_USER_IMG);
            mInviterType = intent.getStringExtra(RoomActivity.EXTRA_INVITER_TYPE);

            mSupportPersonId = intent.getStringExtra(RoomActivity.EXTRA_SUPPORT_PERSON_ID);
            mSupportMbrId = intent.getStringExtra(RoomActivity.EXTRA_SUPPORT_MBR_ID);
            mSupportRegId = intent.getStringExtra(RoomActivity.EXTRA_SUPPORT_REG_ID);
            mSupportFee= intent.getFloatExtra(RoomActivity.EXTRA_SUPPORT_FEE, 0);

            isTargetAnswered = intent.getBooleanExtra(RoomActivity.EXTRA_IS_TARGET_ANSWERED, false);
            isExpertAnswered = intent.getBooleanExtra(RoomActivity.EXTRA_IS_EXPERT_ANSWERED, false);
            mInviteExpertId = intent.getStringExtra(RoomActivity.EXTRA_INVITE_EXPERT_ID);
            mRegId = intent.getStringExtra(RoomActivity.EXTRA_REG_ID);
        }

        mEngineKit = RTCEngineKit.getInstance(getApplicationContext());
        mEngineKit.setEventListener(this);
//        session = SkyEngineKit.Instance().getCurrentSession();
//        if (session == null || EnumType.CallState.Idle == session.getState()) {
//            stopSelf();
//        }
        resumeActivityIntent = new Intent(this, RoomActivity.class);

        resumeActivityIntent.putExtra(RoomActivity.EXTRA_SUPPORT_PERSON_ID, mSupportPersonId);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_SUPPORT_MBR_ID, mSupportMbrId);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_SUPPORT_REG_ID, mSupportRegId);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_SUPPORT_FEE, mSupportFee);

        resumeActivityIntent.putExtra(RoomActivity.EXTRA_REG_ID, mRegId);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_IS_TARGET_ANSWERED, isTargetAnswered);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_IS_EXPERT_ANSWERED, isExpertAnswered);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_INVITE_EXPERT_ID, mInviteExpertId);
        
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_CALL_DURATION, mDuration);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_IS_FORM_FLOATING_WINDOW, true);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_CALLING_USER_ID, mCallingUserId);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_CALLING_USER_NAME, mCallingUserName);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_CALLING_USER_IMG, mCallingUserImg);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_CALL_TYPE, mCallType);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_INVITER_USER_ID, mInviterUserId);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_INVITER_USER_NAME, mInviterUserName);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_INVITER_USER_IMG, mInviterUserImg);
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_INVITER_TYPE, mInviterType);
//        resumeActivityIntent.putExtra(CallSingleActivity.EXTRA_MO, intent.getBooleanExtra(CallSingleActivity.EXTRA_MO, false));
//        resumeActivityIntent.putExtra(CallSingleActivity.EXTRA_AUDIO_ONLY, intent.getBooleanExtra(CallSingleActivity.EXTRA_AUDIO_ONLY, false));
//        resumeActivityIntent.putExtra(CallSingleActivity.EXTRA_TARGET, intent.getStringExtra(CallSingleActivity.EXTRA_TARGET));
        resumeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resumeActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = BuildConfig.APPLICATION_ID + ".video";
            String channelName = "video";
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("通话中...")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(NOTIFICATION_ID, builder.build());
        try {
            showFloatingWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDuration++;
            }
        }, 0, 1000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            wm.removeView(floatView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isStarted = false;
        mTimer.cancel();
        super.onDestroy();
    }

    private void showFloatingWindow() {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();

        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.type = type;
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        params.format = PixelFormat.TRANSLUCENT;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;
        params.x = getResources().getDisplayMetrics().widthPixels;
        params.y = 0;

        floatView = LayoutInflater.from(this).inflate(R.layout.video_float_view, null);
        floatView.setOnTouchListener(onTouchListener);
        wm.addView(floatView, params);
        showVideoInfo();

    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float lastX, lastY;
        int oldOffsetX, oldOffsetY;
        int tag = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            float x = event.getX();
            float y = event.getY();

            if (tag == 0) {
                oldOffsetX = params.x;
                oldOffsetY = params.y;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                lastX = x;
                lastY = y;
            } else if (action == MotionEvent.ACTION_MOVE) {
                // 减小偏移量,防止过度抖动
                params.x += (int) (x - lastX) / 3;
                params.y += (int) (y - lastY) / 3;
                tag = 1;
                wm.updateViewLayout(v, params);
            } else if (action == MotionEvent.ACTION_UP) {
                floatView.performClick();
                int newOffsetX = params.x;
                int newOffsetY = params.y;
                if (Math.abs(oldOffsetX - newOffsetX) <= 20 && Math.abs(oldOffsetY - newOffsetY) <= 20) {
                    clickToResume();
                } else {
                    tag = 0;
                }
            }
            return true;
        }
    };

    private void clickToResume() {
        resumeActivityIntent.putExtra(RoomActivity.EXTRA_CALL_DURATION, mDuration);
        startActivity(resumeActivityIntent);
        stopSelf();
    }

//    private void refreshCallDurationInfo(TextView timeView) {
//        CallSession session = SkyEngineKit.Instance().getCurrentSession();
//        if (session == null || !session.isAudioOnly()) {
//            return;
//        }
//
//        long duration = (System.currentTimeMillis() - session.getStartTime()) / 1000;
//        if (duration >= 3600) {
//            timeView.setText(String.format(Locale.getDefault(), "%d:%02d:%02d",
//                    duration / 3600, (duration % 3600) / 60, (duration % 60)));
//        } else {
//            timeView.setText(String.format(Locale.getDefault(), "%02d:%02d",
//                    (duration % 3600) / 60, (duration % 60)));
//        }
//        handler.postDelayed(() -> refreshCallDurationInfo(timeView), 1000);
//    }


    private void showVideoInfo() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        QNTextureView textureView = floatView.findViewById(R.id.texture_view);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textureView.getLayoutParams();

        params.width = width/4;
        params.height = params.width *16 /9;
        textureView.setLayoutParams(params);

        Map<String, List<QNTrackInfo>> map = mEngineKit.getRemoteTrackMap();
        for (List<QNTrackInfo> list :  map.values()) {
            for (QNTrackInfo track : list) {
                if (track.getTrackKind().equals(QNTrackKind.VIDEO)) {
                    mEngineKit.setRenderTextureWindow(track, textureView);
                    return;
                }
            }

        }

    }

    @Override
    public void onRoomStateChanged(QNRoomState state) {
        Log.i("onRoomStateChanged", "onRoomStateChanged:" + state.name());
        switch (state) {
            case IDLE:
//                if (mIsAdmin) {
//                    userLeftForStreaming(mUserId);
//                }
                break;
            case RECONNECTING:
//                logAndToast(getString(R.string.reconnecting_to_room));
//                mControlFragment.stopTimer();
                break;
            case CONNECTED:
//                if (mIsAdmin) {
//                    userJoinedForStreaming(mUserId, "");
//                }
                // 加入房间后可以进行 tracks 的发布
//                mEngine.publishTracks(mLocalTrackList);
//                logAndToast(getString(R.string.connected_to_room));
//                mIsJoinedRoom = true;
//                mControlFragment.startTimer();
                break;
            case RECONNECTED:
//                logAndToast(getString(R.string.connected_to_room));
//                mControlFragment.startTimer();
                break;
            case CONNECTING:
//                logAndToast(getString(R.string.connecting_to, mRoomId));
                break;
        }
    }

    @Override
    public void onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList) {

    }

    @Override
    public void onRemoteUnpublished(String remoteUserId, List<QNTrackInfo> trackInfoList) {

    }

    @Override
    public void onKickedOut(String userId) {
        stopSelf();
    }
}
