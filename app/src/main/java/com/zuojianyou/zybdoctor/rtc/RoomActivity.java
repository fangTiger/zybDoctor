package com.zuojianyou.zybdoctor.rtc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qiniu.droid.rtc.QNCameraSwitchResultCallback;
import com.qiniu.droid.rtc.QNRenderVideoCallback;
import com.qiniu.droid.rtc.QNTextureView;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;
import com.qiniu.droid.rtc.QNUtil;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.utils.SettingsCompat;

import org.webrtc.VideoFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: weiwei
 * @date: 2020/10/17
 * @description:
 */
public class RoomActivity extends AppCompatActivity implements RTCEngineKit.EventListener, View.OnClickListener{


//    private String mRoomToken = "c_VBzUkwPIQ-Uij9b3a3Ikg8YNJE-S60-b8puOE0:d0RSahXoxrSFP241Rz9rc4UglLY=:eyJhcHBJZCI6ImY5czhzdW1iYiIsInJvb21OYW1lIjoidGVzdDEiLCJ1c2VySWQiOiIyMjIyIiwiZXhwaXJlQXQiOjE2MDQ3OTg4OTAsInBlcm1pc3Npb24iOiJ1c2VyIn0=";
    private String mRoomToken ="c_VBzUkwPIQ-Uij9b3a3Ikg8YNJE-S60-b8puOE0:IEWKRa9uhUYo2DSVdK-DJ4nwlso=:eyJhcHBJZCI6ImY5czhzdW1iYiIsInJvb21OYW1lIjoidGVzdDEiLCJ1c2VySWQiOiIxMTExIiwiZXhwaXJlQXQiOjE2MDQ3OTg4OTAsInBlcm1pc3Npb24iOiJ1c2VyIn0=";
    private Handler mHandler = new Handler();
    private RTCEngineKit mRTCEngineKit;
    private boolean mIsJoinedRoom = false;
    private boolean mIsShowFloatingView;
    private boolean mIsCalling;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private int mCaptureMode = Config.CAMERA_CAPTURE;

    private FrameLayout fullWindowFl;
    private FrameLayout small1WindowFl;
    private FrameLayout small2WindowFl;

    private QNTextureView textureView;
    private QNTextureView textureView1;
    private QNTextureView textureView2;

    private ImageButton menuBtn, minimizeBtn, switchBtn, answerBtn;
    private LinearLayout menuLl;
    private TextView facePictureTv, tonguePictureTv, handPictureTv, sickPictureTv;
    private RelativeLayout infoRl;
    private ImageView photoIv;
    private TextView nameTv;

    private VideoFrame mVideoFrame;

    private Map<String, QNTextureView> mRemoteWindowMap = new HashMap<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

        setContentView(R.layout.activity_room);

        fullWindowFl = findViewById(R.id.full_window_fl);
        small1WindowFl = findViewById(R.id.small1_window_fl);
        small2WindowFl = findViewById(R.id.small2_window_fl);
        textureView = findViewById(R.id.texture_view);
        textureView1 = findViewById(R.id.texture_view1);
        textureView2 = findViewById(R.id.texture_view2);
        menuBtn = findViewById(R.id.menu_btn);
        menuLl = findViewById(R.id.menu_ll);
        facePictureTv = findViewById(R.id.face_picture_tv);
        tonguePictureTv = findViewById(R.id.tongue_picture_tv);
        handPictureTv = findViewById(R.id.hand_picture_tv);
        sickPictureTv = findViewById(R.id.sick_picture_tv);
        minimizeBtn = findViewById(R.id.minimize_btn);
        switchBtn = findViewById(R.id.switch_btn);
        answerBtn = findViewById(R.id.answer_btn);
        infoRl = findViewById(R.id.info_rl);
        photoIv = findViewById(R.id.photo_iv);
        nameTv = findViewById(R.id.name_tv);

        menuBtn.setOnClickListener(this);
        minimizeBtn.setOnClickListener(this);
        switchBtn.setOnClickListener(this);

        findViewById(R.id.hangup_btn).setOnClickListener(this);

        small1WindowFl.setOnClickListener(this);
        small2WindowFl.setOnClickListener(this);

        textureView1.setRenderVideoCallback(new QNRenderVideoCallback() {
            @Override
            public void onRenderingFrame(VideoFrame videoFrame) {
                mVideoFrame = videoFrame;
            }
        });


        findViewById(R.id.capture_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mVideoFrame != null) {
                    QNUtil.saveFrame(mVideoFrame, Environment.getExternalStorageDirectory().getAbsolutePath() + "/tt.jpg", new QNUtil.FrameSavedCallback() {
                        @Override
                        public void onSaveSuccess() {

                        }

                        @Override
                        public void onSaveError(String s) {

                        }
                    });
                }
            }
        });



        initRTC();
    }

    private void initRTC(){
        stopService(new Intent(getApplicationContext(),FloatingVideoService.class));
        mRTCEngineKit = RTCEngineKit.getInstance(getApplicationContext());
        mRTCEngineKit.setEventListener(this);
        mRTCEngineKit.setLocalRenderTextureWindow(textureView);

        for (String remoteUserId : mRTCEngineKit.getRemoteTrackMap().keySet()) {
            addRemoteWindow(remoteUserId, mRTCEngineKit.getRemoteTrackMap().get(remoteUserId));
        }
    }

    private void addRemoteWindow(String remoteUserId, List<QNTrackInfo> trackInfoList){
        infoRl.setVisibility(View.GONE);
        menuBtn.setVisibility(View.VISIBLE);
        minimizeBtn.setVisibility(View.VISIBLE);
        switchBtn.setVisibility(View.VISIBLE);
        for(QNTrackInfo track : trackInfoList) {
            if (track.getTrackKind().equals(QNTrackKind.VIDEO)) {
                if (small1WindowFl.getVisibility() == View.VISIBLE) {
                    small2WindowFl.setVisibility(View.VISIBLE);
                    if (small2WindowFl.getChildCount() > 0) {
                        QNTextureView view = (QNTextureView) small2WindowFl.getChildAt(0);
                        mRTCEngineKit.setRenderTextureWindow(track, view);
                        mRemoteWindowMap.put(remoteUserId,view);
                    }

                } else {
                    small1WindowFl.setVisibility(View.VISIBLE);
                    if (small1WindowFl.getChildCount() > 0) {
                        QNTextureView view = (QNTextureView) small1WindowFl.getChildAt(0);
                        mRTCEngineKit.setRenderTextureWindow(track, view);
                        mRemoteWindowMap.put(remoteUserId,view);
                    }
                }
                break;
            }
        }

    }

    private void removeRemoteWindow (String remoteUserId){
        QNTextureView view = mRemoteWindowMap.remove(remoteUserId);
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup.getId() == R.id.full_window_fl) {
                if (small1WindowFl.getVisibility() == View.VISIBLE && small1WindowFl.getChildCount() > 0) {
                    View v = small1WindowFl.getChildAt(0);
                    small1WindowFl.removeAllViews();
                    viewGroup.addView(v);
                    viewGroup.removeView(view);
                    small1WindowFl.addView(view);

                    small1WindowFl.setVisibility(View.GONE);
                } else if (small2WindowFl.getVisibility() == View.VISIBLE && small2WindowFl.getChildCount() > 0){
                    View v = small2WindowFl.getChildAt(0);
                    small2WindowFl.removeAllViews();
                    viewGroup.addView(v);
                    viewGroup.removeView(view);
                    small2WindowFl.addView(view);

                    small2WindowFl.setVisibility(View.GONE);
                }
            } else {
                viewGroup.setVisibility(View.GONE);
            }
        }
    }


    // 显示小窗
    private void showFloatingView() {
        if (!checkOverlayPermission()) {
            return;
        }
        mIsShowFloatingView = true;
        Intent intent = new Intent(getApplicationContext(), FloatingVideoService.class);
//        intent.putExtra(EXTRA_TARGET, targetId);
//        intent.putExtra(EXTRA_AUDIO_ONLY, isAudioOnly);
//        intent.putExtra(EXTRA_MO, isOutgoing);
        startService(intent);
        finish();
    }

    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SettingsCompat.setDrawOverlays(this, true);
            if (!SettingsCompat.canDrawOverlays(this)) {
                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_LONG).show();
                SettingsCompat.manageDrawOverlays(this);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.small1_window_fl: {
                if (small1WindowFl.getChildCount() > 0 && fullWindowFl.getChildCount() > 0) {
                    View smallView = small1WindowFl.getChildAt(0);
                    View fullView = fullWindowFl.getChildAt(0);

                    small1WindowFl.removeView(smallView);
                    fullWindowFl.addView(smallView);

                    fullWindowFl.removeView(fullView);
                    small1WindowFl.addView(fullView);
                }

                break;
            }
            case R.id.small2_window_fl: {
                if (small2WindowFl.getChildCount() > 0 && fullWindowFl.getChildCount() > 0) {
                    View smallView = small2WindowFl.getChildAt(0);
                    View fullView = fullWindowFl.getChildAt(0);

                    small2WindowFl.removeView(smallView);
                    fullWindowFl.addView(smallView);

                    fullWindowFl.removeView(fullView);
                    small2WindowFl.addView(fullView);
                }

                break;
            }
            case R.id.menu_btn:
                if (menuLl.getVisibility() == View.VISIBLE) {
                    menuLl.setVisibility(View.GONE);
                } else {
                    menuLl.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.minimize_btn:
                showFloatingView();
                break;
            case R.id.switch_btn:
                if (mRTCEngineKit != null) {
                    mRTCEngineKit.switchCamera(new QNCameraSwitchResultCallback() {
                        @Override
                        public void onCameraSwitchDone(boolean b) {

                        }

                        @Override
                        public void onCameraSwitchError(String s) {

                        }
                    });
                }
                break;
            case R.id.hangup_btn:
                if (mRTCEngineKit != null) {
                    mRTCEngineKit.leaveRoom();
                }

                finish();
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开始视频采集
        mRTCEngineKit.startCapture();
        if (!mIsJoinedRoom) {
            // 加入房间
            mRTCEngineKit.joinRoom(mRoomToken);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 停止视频采集
        if (!mIsShowFloatingView) {
            mRTCEngineKit.stopCapture();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mIsShowFloatingView) {
            mRTCEngineKit.destroy();
        }

    }

    @Override
    public void onBackPressed() {
        mRTCEngineKit.leaveRoom();
        finish();
    }




    /**
     * 成功订阅远端用户的 tracks 时会回调此方法
     *
     * @param remoteUserId 远端用户 userId
     * @param trackInfoList 订阅的远端用户 tracks 列表
     */
    @Override
    public void onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList) {
//        updateRemoteLogText("onSubscribed:remoteUserId = " + remoteUserId);
//        if (mTrackWindowMgr != null) {
//            mTrackWindowMgr.addTrackInfo(remoteUserId, trackInfoList);
//        }
        addRemoteWindow(remoteUserId, trackInfoList);
    }

    @Override
    public void onRemoteUnpublished(String remoteUserId, List<QNTrackInfo> trackInfoList) {
        removeRemoteWindow(remoteUserId);
    }


    /**
     * 当自己被踢出房间时会回调此方法
     *
     * @param userId 踢人方的 userId
     */
    @Override
    public void onKickedOut(String userId) {

//        finish();
    }

}
