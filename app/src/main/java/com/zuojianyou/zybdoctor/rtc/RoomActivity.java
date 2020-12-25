package com.zuojianyou.zybdoctor.rtc;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qiniu.droid.rtc.QNCameraSwitchResultCallback;
import com.qiniu.droid.rtc.QNRenderVideoCallback;
import com.qiniu.droid.rtc.QNRoomState;
import com.qiniu.droid.rtc.QNTextureView;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;
import com.qiniu.droid.rtc.QNUtil;
import com.zuojianyou.zybdoctor.R;
import com.zuojianyou.zybdoctor.activity.PatientInfoActivity;
import com.zuojianyou.zybdoctor.activity.RecipeOrderActivity;
import com.zuojianyou.zybdoctor.activity.TreatActivity;
import com.zuojianyou.zybdoctor.activity.VisitOrderActivity;
import com.zuojianyou.zybdoctor.adapter.BaseAdapter;
import com.zuojianyou.zybdoctor.adapter.DepartmentDialogAdapter;
import com.zuojianyou.zybdoctor.app.Constants;
import com.zuojianyou.zybdoctor.app.ObserverModeListener;
import com.zuojianyou.zybdoctor.application.MyApplication;
import com.zuojianyou.zybdoctor.base.data.SpData;
import com.zuojianyou.zybdoctor.beans.OfficeInfo;
import com.zuojianyou.zybdoctor.beans.treat.DicSick;
import com.zuojianyou.zybdoctor.beans.treat.TreatParameter;
import com.zuojianyou.zybdoctor.utils.FileUtils;
import com.zuojianyou.zybdoctor.utils.HttpCallback;
import com.zuojianyou.zybdoctor.utils.MyCallBack;
import com.zuojianyou.zybdoctor.utils.ServerAPI;
import com.zuojianyou.zybdoctor.utils.SettingsCompat;
import com.zuojianyou.zybdoctor.utils.TimeUtils;
import com.zuojianyou.zybdoctor.utils.ToastUtils;

import org.webrtc.VideoFrame;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.zuojianyou.zybdoctor.beans.treat.Report;

/**
 * @author: weiwei
 * @date: 2020/10/17
 * @description:
 */
public class RoomActivity extends AppCompatActivity implements RTCEngineKit.EventListener, View.OnClickListener{
    public static final String EXTRA_IS_FORM_FLOATING_WINDOW = "is_form_floating_window";
    public static final String EXTRA_REG_ID = "reg_id";
    public static final String EXTRA_CALLING_USER_ID = "calling_user_id";
    public static final String EXTRA_CALLING_USER_NAME = "calling_user_name";
    public static final String EXTRA_CALLING_USER_IMG = "calling_user_img";
    public static final String EXTRA_CALL_TYPE = "call_type";
    public static final String EXTRA_INVITER_USER_ID = "inviter_user_id";
    public static final String EXTRA_INVITER_USER_NAME = "inviter_user_name";
    public static final String EXTRA_INVITER_USER_IMG = "inviter_user_img";
    public static final String EXTRA_INVITER_TYPE = "inviter_type";

    public static final String EXTRA_CALL_DURATION = "call_duration";

    public static final String EXTRA_SUPPORT_PERSON_ID = "support_person_id";
    public static final String EXTRA_SUPPORT_MBR_ID = "support_mbr_id";
    public static final String EXTRA_SUPPORT_REG_ID = "support_reg_id";
    public static final String EXTRA_SUPPORT_FEE = "support_fee";

    public static final String EXTRA_IS_TARGET_ANSWERED = "is_target_answered";
    public static final String EXTRA_IS_EXPERT_ANSWERED = "is_expert_answered";
    public static final String EXTRA_INVITE_EXPERT_ID = "invite_expert_id";

    private String mRegId; //挂号id
    //呼叫被邀请人信息
    private String mCallingUserId;
    private String mCallingUserName;
    private String mCallingUserImg;
    private String mCallType; //1-远程问诊 2-专家远程协 3-专家远程问诊
    //对方是否已接听
    private boolean isTargetAnswered;

    //邀请专家是否已接听
    private boolean isExpertAnswered;

    //接听邀请人信息
    private String mInviterUserId;
    private String mInviterUserName;
    private String mInviterUserImg;
    private String mInviterType;  //1-远程问诊 2-专家远程协 3-专家远程问诊

    private String mSupportPersonId;
    private String mSupportMbrId;
    private String mSupportRegId;
    private float mSupportFee;

    private String mRoomToken;

    private String mInviteExpertId;

    private RTCEngineKit mRTCEngineKit;
    private boolean mIsJoinedRoom = false;
    private boolean mIsShowFloatingView;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private int mCaptureMode = Config.CAMERA_CAPTURE;

    private FrameLayout fullWindowFl;
    private FrameLayout small1WindowFl;
    private FrameLayout small2WindowFl;

    private QNTextureView textureView;
    private QNTextureView textureView1;
    private QNTextureView textureView2;

    private ImageButton menuBtn, minimizeBtn;
    private LinearLayout menuLl, inviteExpertLl;
    private TextView facePictureTv, tonguePictureTv, handPictureTv, sickPictureTv;
    private RelativeLayout infoRl;
    private ImageView photoIv, expertIv;
    private TextView nameTv;
    private TextView durationTv, inviteExpertTv, hangupTv, answerTv, switchTv;

    private int mDuration; //秒

    private ArrayList<Report> reportList = new ArrayList<>();

    private Map<String, QNTextureView> mRemoteWindowMap = new HashMap<>();
    private boolean isFormFloatingWindow;
    private String mPicName;
    private VideoFrame mVideoFrame;
    private NotificationManager mNotificationManager;
    private Timer mTimer = new Timer();

    private final int CALL_TIME_OUT_WHAT = 1;
    private final int HIDE_VIEW_WHAT = 2;
    private final int INVITE_TIME_OUT_WHAT = 3;

    private Handler mHandler = new RoomHandle(this);

    private static class RoomHandle extends Handler {
        private WeakReference<Context> reference;//弱引用

        public RoomHandle(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context context = reference.get();
            if (null != context && context instanceof RoomActivity) {
                RoomActivity activity = (RoomActivity) context;
                if (activity.CALL_TIME_OUT_WHAT == msg.what ) {
                    removeMessages(activity.CALL_TIME_OUT_WHAT);
                    ToastUtils.show(activity.getApplicationContext(),"对方无应答");

                    if (!TextUtils.isEmpty(activity.mCallingUserId)) {
                        activity.qnrtcOptFlag(activity.mCallingUserId);
                    }

                    if (activity.mRTCEngineKit != null) {
                        activity.mRTCEngineKit.leaveRoom();
                    }
                    activity.qnrtcLeaveRoom();
                    activity.finish();
                } else if (activity.HIDE_VIEW_WHAT == msg.what) {
                    removeMessages(activity.HIDE_VIEW_WHAT);
                    activity.hangupTv.setVisibility(View.GONE);
                    activity.menuBtn.setVisibility(View.GONE);
                    activity.menuLl.setVisibility(View.GONE);
                    activity.minimizeBtn.setVisibility(View.GONE);
                    activity.switchTv.setVisibility(View.GONE);
                    activity.inviteExpertLl.setVisibility(View.GONE);
                    activity.durationTv.setVisibility(View.GONE);
                } else if (activity.INVITE_TIME_OUT_WHAT == msg.what) {
                    removeMessages(activity.INVITE_TIME_OUT_WHAT);
                    if (!TextUtils.isEmpty(activity.mInviteExpertId)) {
                        activity.qnrtcOptFlag(activity.mInviteExpertId);
                    }
                    activity.expertIv.setImageResource(R.drawable.ic_invite);
                    activity.inviteExpertTv.setText("邀请专家");
                    activity.inviteExpertLl.setEnabled(true);
                    ToastUtils.show(activity.getApplicationContext(), "邀请失败");
                }
            }
        }
    }

    private QNRenderVideoCallback mQNRenderVideoCallback = new QNRenderVideoCallback(){

        @Override
        public void onRenderingFrame(VideoFrame videoFrame) {
            if (videoFrame != null) {
                mVideoFrame = videoFrame;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (fullWindowFl.getChildCount() > 0) {
                            QNTextureView textureView = (QNTextureView) fullWindowFl.getChildAt(0);
                            textureView.setRenderVideoCallback(null);
                        }
                        showScreenshotDialog();
                    }
                });
            }
        }
    };

    private ObserverModeListener messageListener = new ObserverModeListener() {
        @Override
        public void toUpate(Bundle bundle) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (bundle != null) {
                        String userId = bundle.getString("user_id");
                       // 1-接受 2-拒绝 8-取消
                        String flag = bundle.getString("flag");
                        if ("7".equals(flag)) {  //网站和app 都登录 同时 呼叫  网站监听了
                            ToastUtils.show(getApplicationContext(),"网站已接听");
                            finish();
                        }  else if ("8".equals(flag)){ //呼叫方取消
                            ToastUtils.show(getApplicationContext(),"对方取消呼叫");
                            if (mIsJoinedRoom) {
                                if (mRTCEngineKit != null) {
                                    mRTCEngineKit.leaveRoom();
                                }
                                qnrtcLeaveRoom();
                            }
                            finish();

                        } else if ("2".equals(flag)) {   //对方拒绝
                            ToastUtils.show(getApplicationContext(),"对方拒绝");
                            if (!TextUtils.isEmpty(mInviteExpertId) && mInviteExpertId.equals(userId)) {
                                mInviteExpertId = null;
                                expertIv.setImageResource(R.drawable.ic_invite);
                                inviteExpertTv.setText("邀请专家");
                                inviteExpertLl.setEnabled(true);

                            } else {
                                if (mRTCEngineKit != null) {
                                    mRTCEngineKit.leaveRoom();
                                }
                                qnrtcLeaveRoom();
                                finish();
                            }

                        } else if("1".equals(flag)) {  //app 接收方接收  网站接收
                            if (!TextUtils.isEmpty(mInviteExpertId) && mInviteExpertId.equals(userId)) {
                                isExpertAnswered = true;
                            } else {
                                isTargetAnswered = true;
                            }
                        }
                    }

                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

      //| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED

        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        MyApplication.getInstance().registerObserver(Constants.VIDEO_CALL_MESSAGE_ACTION, messageListener);

        mSupportPersonId = getIntent().getStringExtra(EXTRA_SUPPORT_PERSON_ID);
        mSupportMbrId = getIntent().getStringExtra(EXTRA_SUPPORT_MBR_ID);
        mSupportRegId = getIntent().getStringExtra(EXTRA_SUPPORT_REG_ID);
        mSupportFee= getIntent().getFloatExtra(EXTRA_SUPPORT_FEE, 0);

        isTargetAnswered = getIntent().getBooleanExtra(EXTRA_IS_TARGET_ANSWERED, false);
        isExpertAnswered = getIntent().getBooleanExtra(EXTRA_IS_EXPERT_ANSWERED, false);
        mInviteExpertId = getIntent().getStringExtra(EXTRA_INVITE_EXPERT_ID);

        mDuration = getIntent().getIntExtra(EXTRA_CALL_DURATION, 0);
        mRegId = getIntent().getStringExtra(EXTRA_REG_ID);
        mCallingUserId = getIntent().getStringExtra(EXTRA_CALLING_USER_ID);
        mCallingUserName = getIntent().getStringExtra(RoomActivity.EXTRA_CALLING_USER_NAME);
        mCallingUserImg = getIntent().getStringExtra(RoomActivity.EXTRA_CALLING_USER_IMG);
        mCallType = getIntent().getStringExtra(RoomActivity.EXTRA_CALL_TYPE);

        mInviterUserId = getIntent().getStringExtra(RoomActivity.EXTRA_INVITER_USER_ID);
        mInviterUserName = getIntent().getStringExtra(RoomActivity.EXTRA_INVITER_USER_NAME);
        mInviterUserImg = getIntent().getStringExtra(RoomActivity.EXTRA_INVITER_USER_IMG);
        mInviterType = getIntent().getStringExtra(RoomActivity.EXTRA_INVITER_TYPE);
//        mCallingUserId = "1615593834975030000";
//        mCallingUserId = "1615906328254450000";

        isFormFloatingWindow = getIntent().getBooleanExtra(EXTRA_IS_FORM_FLOATING_WINDOW, false);
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
        switchTv = findViewById(R.id.switch_tv);
        answerTv = findViewById(R.id.answer_tv);
        hangupTv = findViewById(R.id.hangup_tv);
        infoRl = findViewById(R.id.info_rl);
        photoIv = findViewById(R.id.photo_iv);
        nameTv = findViewById(R.id.name_tv);
        expertIv = findViewById(R.id.expert_iv);
        inviteExpertTv = findViewById(R.id.invite_expert_tv);
        inviteExpertLl = findViewById(R.id.invite_expert_ll);
        durationTv = findViewById(R.id.duration_tv);

        menuBtn.setOnClickListener(this);
        facePictureTv.setOnClickListener(this);
        tonguePictureTv.setOnClickListener(this);
        handPictureTv.setOnClickListener(this);
        sickPictureTv.setOnClickListener(this);
        minimizeBtn.setOnClickListener(this);
        switchTv.setOnClickListener(this);
        answerTv.setOnClickListener(this);
        inviteExpertLl.setOnClickListener(this);
        hangupTv.setOnClickListener(this);
        fullWindowFl.setOnClickListener(this);
        small1WindowFl.setOnClickListener(this);
        small2WindowFl.setOnClickListener(this);

        if (TextUtils.isEmpty(mCallingUserId)) {
            nameTv.setText(mInviterUserName);
            Glide.with(getApplicationContext()).load(Constants.FILL_DOMAIN + mInviterUserImg).into(photoIv);
        } else {
            nameTv.setText(mCallingUserName);
            Glide.with(getApplicationContext()).load(Constants.FILL_DOMAIN + mCallingUserImg).into(photoIv);

            hangupTv.setText("取消");
        }

        mRoomToken = getIntent().getStringExtra("room_token");
        if (!TextUtils.isEmpty(mRoomToken)) {
            answerTv.setVisibility(View.VISIBLE);
        }

        initRTC();

        if (!isFormFloatingWindow ) {
            if (TextUtils.isEmpty(mRoomToken)) {
                getQnrtcRoomToken(mCallingUserId, mCallType);
                mRTCEngineKit.shouldStartRing();
            }
        }
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        updateDurationTv();
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

        if (mRTCEngineKit.getRemoteTrackMap().size() == 1) {
            hangupTv.setText("挂断");
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mDuration++;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateDurationTv();
                        }
                    });
                }
            }, 0, 1000);
            durationTv.setVisibility(View.VISIBLE);
            infoRl.setVisibility(View.GONE);
            if ("2".equals(mInviterType) || "3".equals(mCallType)) {
                menuBtn.setVisibility(View.GONE);
            } else {
                menuBtn.setVisibility(View.VISIBLE);
            }

            minimizeBtn.setVisibility(View.VISIBLE);
            switchTv.setVisibility(View.VISIBLE);
            if ("1".equals(mCallType)) {
                inviteExpertLl.setVisibility(View.VISIBLE);
            } else {
                inviteExpertLl.setVisibility(View.GONE);
            }
            mHandler.sendEmptyMessageDelayed(HIDE_VIEW_WHAT, 5000);

            if (small1WindowFl.getChildCount() > 0 && fullWindowFl.getChildCount() > 0) {
                View smallView = small1WindowFl.getChildAt(0);
                View fullView = fullWindowFl.getChildAt(0);

                small1WindowFl.removeView(smallView);
                fullWindowFl.addView(smallView);

                fullWindowFl.removeView(fullView);
                small1WindowFl.addView(fullView);
            }
        } else if (mRTCEngineKit.getRemoteTrackMap().size() == 2) {
            if (!TextUtils.isEmpty(mInviteExpertId)) {
                inviteExpertTv.setText((String)inviteExpertTv.getTag());
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

    private void startTreatActivity(){
        Intent intent = new Intent(getApplicationContext(), TreatActivity.class);
        intent.putExtra(RoomActivity.EXTRA_CALL_TYPE, "1");
        intent.putExtra("personid", mSupportPersonId);
        intent.putExtra("mbrId", mSupportMbrId);
        intent.putExtra("regId", mSupportRegId);
        intent.putExtra("fee", mSupportFee);
        if (reportList.size() > 0) {
            intent.putParcelableArrayListExtra("reportList", reportList);
        }
        startActivity(intent);
    }


    // 显示小窗
    private void showFloatingView() {
        if (!checkOverlayPermission()) {
            return;
        }

        mIsShowFloatingView = true;
        Intent intent = new Intent(getApplicationContext(), FloatingVideoService.class);

        intent.putExtra(EXTRA_CALL_DURATION,mDuration);
        intent.putExtra(EXTRA_CALLING_USER_ID,mCallingUserId);
        intent.putExtra(EXTRA_CALLING_USER_NAME,mCallingUserName);
        intent.putExtra(EXTRA_CALLING_USER_IMG,mCallingUserImg);
        intent.putExtra(EXTRA_CALL_TYPE, mCallType);

        intent.putExtra(EXTRA_INVITER_USER_ID, mInviterUserId);
        intent.putExtra(EXTRA_INVITER_USER_NAME, mInviterUserName);
        intent.putExtra(EXTRA_INVITER_USER_IMG, mInviterUserImg);
        intent.putExtra(EXTRA_INVITER_TYPE, mInviterType);

        intent.putExtra(EXTRA_SUPPORT_PERSON_ID, mSupportPersonId);
        intent.putExtra(EXTRA_SUPPORT_MBR_ID, mSupportMbrId);
        intent.putExtra(EXTRA_SUPPORT_REG_ID, mSupportRegId);
        intent.putExtra(EXTRA_SUPPORT_FEE, mSupportFee);

        intent.putExtra(EXTRA_REG_ID, mRegId);
        intent.putExtra(EXTRA_IS_TARGET_ANSWERED, isTargetAnswered);
        intent.putExtra(EXTRA_IS_EXPERT_ANSWERED, isExpertAnswered);
        intent.putExtra(EXTRA_INVITE_EXPERT_ID, mInviteExpertId);

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

    //呼叫获取token  type 呼叫类型 1-远程问诊 2-专家远程协助 3-专家远程问诊
    private void getQnrtcRoomToken(String userId, String type){
        String url;
        if ("1".equals(type)) {
            url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getQnrtcRoomId/"+SpData.getPersonId()+"/"+userId+"/2/"+ type;
        } else {
            url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getQnrtcRoomId/"+SpData.getPersonId()+"/"+userId+"/2/"+ type + "_"+ mRegId;
        }

        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                if (mRTCEngineKit == null) {
                    return;
                }
                Log.e("zyb", "getQnrtcRoomId:" + data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                if ("1".equals(type) || "3".equals(type)) {
                    mRoomToken = jsonObject.getString("token");
                    mRTCEngineKit.joinRoom(mRoomToken);
                    mHandler.sendEmptyMessageDelayed(CALL_TIME_OUT_WHAT,60 * 1000);
                } else if ("2".equals(type)) {
                    if (mRTCEngineKit.getRemoteTrackMap().size() > 0) {
                        mHandler.sendEmptyMessageDelayed(INVITE_TIME_OUT_WHAT,60 * 1000);
                    } else {
                        mRoomToken = jsonObject.getString("token");
                        mRTCEngineKit.joinRoom(mRoomToken);
                        mHandler.sendEmptyMessageDelayed(CALL_TIME_OUT_WHAT,60 * 1000);
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("zyb", "getQnrtcRoomId ex:" + ex.getMessage());
                if ("1".equals(type)) {
                    ToastUtils.show(getApplicationContext(),ex.getMessage());
                    finish();
                } else if ("2".equals(type)) {
                    inviteExpertTv.setText("邀请专家");
                    inviteExpertLl.setEnabled(true);
                    ToastUtils.show(getApplicationContext(), ex.getMessage());
                }

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    /**
     *
     * @param flag 1-网站接受 2-拒绝 9-APP接受
     */
    private void qnrtcUpdFlag(String flag){
        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getQnrtcUpdFlag/"+SpData.getPersonId()+"/"+flag;
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                if ("3".equals(mInviterType) && !TextUtils.isEmpty(data)) {
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    mSupportPersonId = jsonObject.getString("personId");
                    mSupportMbrId = jsonObject.getString("mbrId");
                    mSupportRegId = jsonObject.getString("registrationId");
                    mSupportFee= jsonObject.getFloat("fee");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    private void qnrtcLeaveRoom(){

        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getQnrtcLeaveRoom/"+SpData.getPersonId();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                //JSONObject jsonObject = JSONObject.parseObject(data);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    //呼叫方取消等操作
    private void qnrtcOptFlag(String accId){
        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getQnrtcOptFlag/"+accId+"/8";
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                //JSONObject jsonObject = JSONObject.parseObject(data);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    //随机获取科室医生信息
    private void getOffDocInfo(int officeId){
        inviteExpertTv.setText("平台为您分配专家...");
        inviteExpertLl.setEnabled(false);
        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getOffDocInfo/"+officeId+"/1/" + SpData.getPersonId();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                Log.e("zyb", "getOffDocInfo:" + data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                mInviteExpertId = jsonObject.getString("personid");
                String docName = jsonObject.getString("docName");
                String personimg = jsonObject.getString("personimg");
                if (!TextUtils.isEmpty(mInviteExpertId)) {
                    getQnrtcRoomToken(mInviteExpertId, "2");
                    Glide.with(getApplicationContext()).load(ServerAPI.FILL_DOMAIN + personimg).apply(new RequestOptions().circleCrop()).into(expertIv);
                    inviteExpertTv.setText("正在连接" + docName);
                    inviteExpertTv.setTag(docName);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                inviteExpertTv.setText("邀请专家");
                inviteExpertLl.setEnabled(true);
                ToastUtils.show(getApplicationContext(), "邀请失败");
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }

    //获取科室列表
    private void getOfficeList(){
        String url = ServerAPI.BASE_DOMAIN + "/third/qnrtc/getOfficeList/" + SpData.getPersonId() + "/1";
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        x.http().get(entity, new HttpCallback(new MyCallBack() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject = JSONObject.parseObject(data);
                List<OfficeInfo> list = jsonObject.getJSONArray("officeList").toJavaList(OfficeInfo.class);

                showDepartmentDialog(list);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show(getApplicationContext(),"邀请失败");
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        }));
    }


    private void showScreenshotDialog(){
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.screenshot_dialog, null);
        TextView titleTv = contentView.findViewById(R.id.title_tv);
        titleTv.setText(mPicName);

        ImageView pictureIv = contentView.findViewById(R.id.picture_iv);

        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.setCancelable(false);

        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        contentView.setLayoutParams(layoutParams);
        dialog.show();

        String path = Environment.getExternalStorageDirectory() + File.separator + "zybDocImages" + File.separator + System.currentTimeMillis() + ".jpg";
        QNUtil.saveFrame(mVideoFrame, path, new QNUtil.FrameSavedCallback() {
            @Override
            public void onSaveSuccess() {
                Glide.with(getApplicationContext()).load(path).into(pictureIv);
                mVideoFrame = null;
            }

            @Override
            public void onSaveError(String s) {
                dialog.dismiss();
                mVideoFrame = null;
                ToastUtils.show(getApplicationContext(), "图片保存失败");
            }
        });

        contentView.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FileUtils.deleteFile(new File(path));
            }
        });

        contentView.findViewById(R.id.save_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mIsShowFloatingView) {
                    Intent intent = new Intent("action_video_capture");
                    intent.putExtra("path", path);
                    intent.putExtra("name", mPicName);
                    sendBroadcast(intent);
                } else {
                    httpImageUpload(path,mPicName,mSupportRegId);
                }
            }
        });

    }

    private void showDepartmentDialog(List<OfficeInfo> list){
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.department_dialog, null);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DepartmentDialogAdapter adapter = new DepartmentDialogAdapter(getApplicationContext(),list);
        recyclerView.setAdapter(adapter);

        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        contentView.setLayoutParams(layoutParams);
        dialog.show();

        adapter.setOnItemViewClickListener(new BaseAdapter.OnItemViewClickListener() {
            @Override
            public void onItemViewClick(View view, int position) {
                dialog.dismiss();
                if (list.size() > position) {
                    getOffDocInfo(list.get(position).getOfficeId());
                }
            }
        });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setRenderVideoCallback(){
        if (fullWindowFl.getChildCount() > 0) {
            QNTextureView textureView = (QNTextureView) fullWindowFl.getChildAt(0);
            textureView.setRenderVideoCallback(mQNRenderVideoCallback);
        }

    }

    private void updateDurationTv(){
        durationTv.setText(TimeUtils.getFormatTime(mDuration));
    }


    private void httpImageUpload(String imgPath, String picName, String id) {
        String url = ServerAPI.getFileUploadUrl();
        RequestParams entity = new RequestParams(url);
        ServerAPI.addHeader(entity);
        entity.setMultipart(true);
        File file = new File(imgPath);
        entity.addBodyParameter("File", file);
        List<KeyValue> mList = new ArrayList<>();
        mList.add(new KeyValue("file", file));
        mList.add(new KeyValue("extra", id));
        mList.add(new KeyValue("path", "inquiry/images"));
        MultipartBody multipartBody = new MultipartBody(mList, "UTF-8");
        entity.setRequestBody(multipartBody);
        x.http().post(entity, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("httpImageUpload", result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getIntValue("code") == 0) {
                    ToastUtils.show(getApplicationContext(),"保存成功");
                    String resultPath = jsonObject.getJSONObject("data").getString("url");
                    String dataValue;
                    if (picName.equals("面部")) {
                        dataValue = "1";
                    } else if (picName.equals("舌质、舌苔")) {
                        dataValue = "2";
                    } else if (picName.equals("手掌")) {
                        dataValue = "3";
                    } else {
                        dataValue = "4";
                    }

                    Report report = new Report();
                    report.setTyp("3");
                    report.setIsNorm("2");
                    report.setPosition(dataValue);
                    report.setPositionName(picName);
                    report.setUrl(resultPath);
                    reportList.add(report);
                } else {
                    ToastUtils.show(getApplicationContext(),jsonObject.getString("errMsg"));
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

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_window_fl:
                if (mRTCEngineKit.getRemoteTrackMap().size() > 0) {
                    mHandler.removeMessages(HIDE_VIEW_WHAT);
                    hangupTv.setVisibility(View.VISIBLE);
                    switchTv.setVisibility(View.VISIBLE);
                    minimizeBtn.setVisibility(View.VISIBLE);
                    durationTv.setVisibility(View.VISIBLE);

                    if ("2".equals(mInviterType) || "3".equals(mCallType)) {
                        menuBtn.setVisibility(View.GONE);
                    } else {
                        menuBtn.setVisibility(View.VISIBLE);
                    }
                    if ("1".equals(mCallType)) {
                        inviteExpertLl.setVisibility(View.VISIBLE);
                    } else {
                        inviteExpertLl.setVisibility(View.GONE);
                    }

                    mHandler.sendEmptyMessageDelayed(HIDE_VIEW_WHAT, 5000);
                }

                break;
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
            case R.id.minimize_btn:
                showFloatingView();
                break;
            case R.id.switch_tv:
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
            case R.id.hangup_tv:
                mRTCEngineKit.shouldStopRing();
                if (mNotificationManager != null) {
                    mNotificationManager.cancelAll();
                }
                if (!isTargetAnswered && !TextUtils.isEmpty(mCallingUserId)) {
                    qnrtcOptFlag(mCallingUserId);
                }
                if (!isExpertAnswered && !TextUtils.isEmpty(mInviteExpertId)) {
                    qnrtcOptFlag(mInviteExpertId);
                }

                if (mIsJoinedRoom) {
                    if (mRTCEngineKit != null) {
                        mRTCEngineKit.leaveRoom();
                    }
                    qnrtcLeaveRoom();

                } else if (TextUtils.isEmpty(mCallingUserId) && !TextUtils.isEmpty(mInviterUserId)){
                    qnrtcUpdFlag("2");
                }
                finish();
                break;
            case R.id.answer_tv:
                if (!mIsJoinedRoom && !TextUtils.isEmpty(mRoomToken)) {
                    // 加入房间
                    mRTCEngineKit.joinRoom(mRoomToken);
                    answerTv.setVisibility(View.GONE);
                    qnrtcUpdFlag("9");
                    mRTCEngineKit.shouldStopRing();
                    if (mNotificationManager != null) {
                        mNotificationManager.cancelAll();
                    }
                }
                break;
            case R.id.menu_btn:
                if (menuLl.getVisibility() == View.VISIBLE) {
                    menuLl.setVisibility(View.GONE);
                } else {
                    menuLl.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.face_picture_tv: {
                menuLl.setVisibility(View.GONE);
                mPicName = "面部";
                setRenderVideoCallback();
                break;
            }
            case R.id.tongue_picture_tv: {
                menuLl.setVisibility(View.GONE);
                mPicName = "舌质、舌苔";
                setRenderVideoCallback();
                break;
            }
            case R.id.hand_picture_tv: {
                menuLl.setVisibility(View.GONE);
                mPicName = "手掌";
                setRenderVideoCallback();
                break;
            }
            case R.id.sick_picture_tv: {
                menuLl.setVisibility(View.GONE);
                mPicName = "病灶处";
                setRenderVideoCallback();
                break;
            }
            case R.id.invite_expert_ll:
                if (mRTCEngineKit.getRemoteTrackMap().size() < 2) {
                    getOfficeList();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开始视频采集
        mRTCEngineKit.startCapture();
//        if (!mIsJoinedRoom && !TextUtils.isEmpty(mRoomToken)) {
//            // 加入房间
//            mRTCEngineKit.joinRoom(mRoomToken);
//        }
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
        mHandler.removeCallbacksAndMessages(null);
        if (!mIsShowFloatingView) {
            mRTCEngineKit.destroy();
        }
        MyApplication.getInstance().unRegisterObserver(Constants.VIDEO_CALL_MESSAGE_ACTION, messageListener);
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (!TextUtils.isEmpty(mSupportRegId) && !mIsShowFloatingView) {
            startTreatActivity();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
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
                mIsJoinedRoom = true;
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


    /**
     * 成功订阅远端用户的 tracks 时会回调此方法
     *
     * @param remoteUserId 远端用户 userId
     * @param trackInfoList 订阅的远端用户 tracks 列表
     */
    @Override
    public void onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList) {
        Log.e("zyb", "onSubscribed:" + remoteUserId);
//        updateRemoteLogText("onSubscribed:remoteUserId = " + remoteUserId);
//        if (mTrackWindowMgr != null) {
//            mTrackWindowMgr.addTrackInfo(remoteUserId, trackInfoList);
//        }
        mHandler.removeMessages(CALL_TIME_OUT_WHAT);
        mHandler.removeMessages(INVITE_TIME_OUT_WHAT);
        mRTCEngineKit.shouldStopRing();
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
        ToastUtils.show(getApplicationContext(), "对方挂断");
        mRTCEngineKit.leaveRoom();
        qnrtcLeaveRoom();
        finish();
    }

}
