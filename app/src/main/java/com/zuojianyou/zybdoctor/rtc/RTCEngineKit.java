package com.zuojianyou.zybdoctor.rtc;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.qiniu.droid.rtc.QNCameraSwitchResultCallback;
import com.qiniu.droid.rtc.QNCustomMessage;
import com.qiniu.droid.rtc.QNErrorCode;
import com.qiniu.droid.rtc.QNLocalAudioPacketCallback;
import com.qiniu.droid.rtc.QNRTCEngine;
import com.qiniu.droid.rtc.QNRTCEngineEventListener;
import com.qiniu.droid.rtc.QNRTCSetting;
import com.qiniu.droid.rtc.QNRemoteAudioPacketCallback;
import com.qiniu.droid.rtc.QNRoomState;
import com.qiniu.droid.rtc.QNSourceType;
import com.qiniu.droid.rtc.QNStatisticsReport;
import com.qiniu.droid.rtc.QNTextureView;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;
import com.qiniu.droid.rtc.QNVideoFormat;
import com.qiniu.droid.rtc.model.QNAudioDevice;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: weiwei
 * @date: 2020/11/1
 * @description:
 */
public class RTCEngineKit implements QNRTCEngineEventListener {
    private final static String TAG = "zyb_RTCEngineKit";
    private static RTCEngineKit rtcEngineKit;
    private Context context;
    private Handler mHandler = new Handler();
    private QNRTCEngine mEngine;

    private String mRoomToken;

    private List<QNTrackInfo> mLocalTrackList;
    private QNTrackInfo mLocalVideoTrack;
    private QNTrackInfo mLocalAudioTrack;

    private Map<String, List<QNTrackInfo>> mRemoteTrackMap = new LinkedHashMap<>();

    private RTCEngineKit(Context context){
        this.context = context;
        initQNRTCEngine();
        initLocalTrackInfoList();
    }

    public static RTCEngineKit getInstance(Context context) {
        if (rtcEngineKit == null) {
            rtcEngineKit = new RTCEngineKit(context);
        }

        return rtcEngineKit;
    }


    /**
     * 初始化 QNRTCEngine
     */
    private void initQNRTCEngine() {

        /**
         * 默认情况下，网络波动时 SDK 内部会降低帧率或者分辨率来保证带宽变化下的视频质量；
         * 如果打开分辨率保持开关，则只会调整帧率来适应网络波动。
         */
//        boolean isMaintainRes = preferences.getBoolean(Config.MAINTAIN_RES, false);

        // 1. VideoPreviewFormat 和 VideoEncodeFormat 建议保持一致
        // 2. 如果远端连麦出现回声的现象，可以通过配置 setLowAudioSampleRateEnabled(true) 或者 setAEC3Enabled(true) 后再做进一步测试，并将设备信息反馈给七牛技术支持
        QNVideoFormat format = new QNVideoFormat(1280, 720, 20);
        QNRTCSetting setting = new QNRTCSetting();
        setting.setCameraID(QNRTCSetting.CAMERA_FACING_ID.FRONT)
                .setHWCodecEnabled(true)
                .setMaintainResolution(false)
                .setVideoBitrate(1000 * 1000)
                .setLowAudioSampleRateEnabled(false)
                .setAEC3Enabled(false)
                .setVideoEncodeFormat(format)
                .setVideoPreviewFormat(format);
        mEngine = QNRTCEngine.createEngine(context, setting, this);
    }

    /**
     * 初始化本地音视频 track
     * 关于 Track 的概念介绍 https://doc.qnsdk.com/rtn/android/docs/preparation#5
     */
    private void initLocalTrackInfoList() {
        mLocalTrackList = new ArrayList<>();
        mLocalAudioTrack = mEngine.createTrackInfoBuilder()
                .setSourceType(QNSourceType.AUDIO)
                .setMaster(true)
                .create();
        mEngine.setLocalAudioPacketCallback(mLocalAudioTrack, new QNLocalAudioPacketCallback() {
            @Override
            public int onPutExtraData(ByteBuffer extraData, int extraDataMaxSize) {
                // 可以向 extraData 填充自定义数据并在对端通过 QNRemoteAudioPacketCallback 解析
//                if (mAddExtraAudioData) {
//                    extraData.rewind();
//                    extraData.put((byte) 0x11);
//                    extraData.flip();
//                    return extraData.remaining();
//                }
                return 0;
            }

            @Override
            public int onSetMaxEncryptSize(int frameSize) {
                // 当需要根据自己的算法加密音频数据时，需要在该方法告知 SDK 加密后的最大数据大小
//                if (mEnableAudioEncrypt) {
//                    return frameSize + 10;
//                }
                return 0;
            }

            @Override
            public int onEncrypt(ByteBuffer frame, int frameSize, ByteBuffer encryptedFrame) {
                // 自主加密接口，将加密后的数据放置到 encryptedFrame 中，并返回加密后大小
//                if (mEnableAudioEncrypt) {
//                    encryptedFrame.rewind();
//                    frame.rewind();
//                    encryptedFrame.put((byte) 0x18);
//                    encryptedFrame.put((byte) 0x19);
//                    encryptedFrame.put(frame);
//                    encryptedFrame.flip();
//                    return encryptedFrame.remaining();
//                }
                return 0;
            }
        });
        mLocalTrackList.add(mLocalAudioTrack);

        // 创建 Camera 采集的视频 Track
        mLocalVideoTrack = mEngine.createTrackInfoBuilder()
                .setSourceType(QNSourceType.VIDEO_CAMERA)
                .setMaster(true)
                .setTag("camera").create();
        mLocalTrackList.add(mLocalVideoTrack);

    }

    public Map<String, List<QNTrackInfo>> getRemoteTrackMap() {
        return mRemoteTrackMap;
    }

    public void setLocalRenderTextureWindow(QNTextureView textureView){
        if (mEngine != null) {
            mEngine.setRenderTextureWindow(mLocalVideoTrack, textureView);
        }
    }

    public void setRenderTextureWindow(QNTrackInfo info, QNTextureView textureView){
        if (mEngine != null) {
            mEngine.setRenderTextureWindow(info, textureView);
        }
    }

    public void startCapture(){
        if (mEngine != null) {
            mEngine.startCapture();
        }
    }

    public void stopCapture(){
        if (mEngine != null) {
            mEngine.stopCapture();
        }
    }

    public void switchCamera(QNCameraSwitchResultCallback var1){
        if (mEngine != null) {
            mEngine.switchCamera(var1);
        }
    }

    public void joinRoom(String roomToken){
        mRoomToken = roomToken;
        if (mEngine != null) {
            mEngine.joinRoom(mRoomToken);
        }
    }

    public void leaveRoom(){
        if (mEngine != null) {
            mEngine.leaveRoom();
        }
    }


    public void destroy (){
        if (mEngine != null) {
            mEngine.destroy();
        }
        rtcEngineKit = null;
    }

    /**
     * 房间状态改变时会回调此方法
     * 房间状态回调只需要做提示用户，或者更新相关 UI； 不需要再做加入房间或者重新发布等其他操作！
     * @param state 房间状态，可参考 {@link QNRoomState}
     */
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
                mEngine.publishTracks(mLocalTrackList);
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

    /**
     * 当退出房间执行完毕后触发该回调，可用于切换房间
     */
    @Override
    public void onRoomLeft() {

    }

    /**
     * 远端用户加入房间时会回调此方法
     * @see QNRTCEngine#joinRoom(String, String) 可指定 userData 字段
     *
     * @param remoteUserId 远端用户的 userId
     * @param userData 透传字段，用户自定义内容
     */
    @Override
    public void onRemoteUserJoined(String remoteUserId, String userData) {
//        updateRemoteLogText("onRemoteUserJoined:remoteUserId = " + remoteUserId + " ,userData = " + userData);
//        if (mIsAdmin) {
//            userJoinedForStreaming(remoteUserId, userData);
//        }
    }

    @Override
    public void onRemoteUserReconnecting(String remoteUserId) {
//        logAndToast("远端用户: " + remoteUserId + " 重连中");
    }

    @Override
    public void onRemoteUserReconnected(String remoteUserId) {
//        logAndToast("远端用户: " + remoteUserId + " 重连成功");
    }

    /**
     * 远端用户离开房间时会回调此方法
     *
     * @param remoteUserId 远端离开用户的 userId
     */
    @Override
    public void onRemoteUserLeft(final String remoteUserId) {
        if (mEngine.getUserList().size() <= 1){
            onKickedOut("");
        }
//        updateRemoteLogText("onRemoteUserLeft:remoteUserId = " + remoteUserId);
//        if (mIsAdmin) {
//            userLeftForStreaming(remoteUserId);
//        }

    }

    /**
     * 本地 tracks 成功发布时会回调此方法
     *
     * @param trackInfoList 已发布的 tracks 列表
     */
    @Override
    public void onLocalPublished(List<QNTrackInfo> trackInfoList) {
//        updateRemoteLogText("onLocalPublished");
        mEngine.enableStatistics();
//        if (mIsAdmin) {
//            mRoomUsersMergeOption.onTracksPublished(mUserId, mLocalTrackList);
//            resetMergeStream();
//        }
    }

    /**
     * 远端用户 tracks 成功发布时会回调此方法
     *
     * @param remoteUserId 远端用户 userId
     * @param trackInfoList 远端用户发布的 tracks 列表
     */
    @Override
    public void onRemotePublished(String remoteUserId, List<QNTrackInfo> trackInfoList) {
//        updateRemoteLogText("onRemotePublished:remoteUserId = " + remoteUserId);
        for (QNTrackInfo info : trackInfoList) {
            if (info.isAudio()) {
                mEngine.setRemoteAudioPacketCallback(info, new QNRemoteAudioPacketCallback() {
                    @Override
                    public void onGetExtraData(ByteBuffer extraData, int extraDataSize) {
                        // 如果对端有发送自定义数据，则数据会在 extraData
//                        if (extraDataSize > 0) {
//                            extraData.rewind();
//                            Log.i(TAG, "extra size " + extraDataSize + " data " + extraData.get());
//                        }
                    }

                    @Override
                    public int onSetMaxDecryptSize(int encryptedFrameSize) {
                        // 当需要根据自己的算法解密音频数据时，需要在该方法告知 SDK 解密后的最大数据大小
//                        if (mEnableAudioEncrypt) {
//                            return encryptedFrameSize;
//                        } else {
//                            return 0;
//                        }
                        return 0;
                    }

                    @Override
                    public int onDecrypt(ByteBuffer encryptedFrame, int encryptedSize, ByteBuffer frame) {
                        // 自主解密接口，将解密后的数据放置到 frame 中，并返回解密后大小
//                        if (mEnableAudioEncrypt) {
//                            encryptedFrame.rewind();
//                            frame.rewind();
//                            if (encryptedFrame.get(0) == 0x18 && encryptedFrame.get(1) == 0x19) {
//                                encryptedFrame.position(2);
//                                frame.put(encryptedFrame);
//                                return encryptedSize - 2;
//                            }
//                        }
                        return 0;
                    }
                });
            }
        }
//        mRoomUsersMergeOption.onTracksPublished(remoteUserId, trackInfoList);
        // 如果希望在远端发布音视频的时候，自动配置合流，则可以在此处重新调用 setMergeStreamLayouts 进行配置
//        if (mIsAdmin) {
//            resetMergeStream();
//        }
    }

    /**
     * 远端用户 tracks 成功取消发布时会回调此方法
     *
     * @param remoteUserId 远端用户 userId
     * @param trackInfoList 远端用户取消发布的 tracks 列表
     */
    @Override
    public void onRemoteUnpublished(final String remoteUserId, List<QNTrackInfo> trackInfoList) {
        mRemoteTrackMap.remove(remoteUserId);
        if (mEventListener != null) {
            mEventListener.onRemoteUnpublished(remoteUserId, trackInfoList);
        }
//        updateRemoteLogText("onRemoteUnpublished:remoteUserId = " + remoteUserId);
//        if (mTrackWindowMgr != null) {
//            mTrackWindowMgr.removeTrackInfo(remoteUserId, trackInfoList);
//        }
//        mRoomUsersMergeOption.onTracksUnPublished(remoteUserId, trackInfoList);
//        if (mIsAdmin) {
//            resetMergeStream();
//        }
    }

    /**
     * 远端用户成功操作静默 tracks 时会回调此方法
     *
     * @param remoteUserId 远端用户 userId
     * @param trackInfoList 远端用户静默的 tracks 列表，是否静默可以通过读取 {@link QNTrackInfo} 的 isMuted() 方法获取
     */
    @Override
    public void onRemoteUserMuted(String remoteUserId, List<QNTrackInfo> trackInfoList) {
//        updateRemoteLogText("onRemoteUserMuted:remoteUserId = " + remoteUserId);
//        if (mTrackWindowMgr != null) {
//            mTrackWindowMgr.onTrackInfoMuted(remoteUserId);
//        }
    }

    /**
     * 成功订阅远端用户的 tracks 时会回调此方法
     *
     * @param remoteUserId 远端用户 userId
     * @param trackInfoList 订阅的远端用户 tracks 列表
     */
    @Override
    public void onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList) {
        mRemoteTrackMap.put(remoteUserId, trackInfoList);
        if (mEventListener != null) {
            mEventListener.onSubscribed(remoteUserId, trackInfoList);
        }
    }

    /**
     * 订阅远端用户 Track 的 QNTrackSubConfiguration 变化时会回调此方法
     *
     * @param remoteUserId 远端用户 userId
     * @param trackInfoList 订阅的远端用户 tracks 列表
     */
    @Override
    public void onSubscribedProfileChanged(String remoteUserId, List<QNTrackInfo> trackInfoList) {
    }

    /**
     * 当自己被踢出房间时会回调此方法
     *
     * @param userId 踢人方的 userId
     */
    @Override
    public void onKickedOut(String userId) {
        if (mEventListener != null) {
            mEventListener.onKickedOut(userId);
        }
//        ToastUtils.s(RoomActivity.this, getString(R.string.kicked_by_admin));
//        finish();
    }

    /**
     * 当媒体状态更新时会回调此方法
     *
     * QNStatisticsReport#audioPacketLostRate（音频丢包率）和 QNStatisticsReport#videoPacketLostRate (视频丢包率)
     * 可以用来向用户提示自己网络状态不佳（比如，连续一段时间丢包路超过 10%）。
     *
     * @param report 媒体信息，详情请参考 {@link QNStatisticsReport}
     */
    @Override
    public void onStatisticsUpdated(final QNStatisticsReport report) {
//        if (report.userId == null || report.userId.equals(mUserId)) {
//            if (QNTrackKind.AUDIO.equals(report.trackKind)) {
//                final String log = "音频码率:" + report.audioBitrate / 1000 + "kbps \n" +
//                        "音频丢包率:" + report.audioPacketLostRate;
//                mControlFragment.updateLocalAudioLogText(log);
//            } else if (QNTrackKind.VIDEO.equals(report.trackKind)) {
//                final String log = "视频码率:" + report.videoBitrate / 1000 + "kbps \n" +
//                        "视频丢包率:" + report.videoPacketLostRate + " \n" +
//                        "视频的宽:" + report.width + " \n" +
//                        "视频的高:" + report.height + " \n" +
//                        "视频的帧率:" + report.frameRate;
//                mControlFragment.updateLocalVideoLogText(log);
//            }
//        }
    }

    /**
     * 当收到远端用户的媒体状态时会回调此方法
     *
     * QNStatisticsReport#audioPacketLostRate（音频丢包率）和 QNStatisticsReport#videoPacketLostRate (视频丢包率)
     * 可以用来向用户提示对方用户网络状态不佳（比如，连续一段时间丢包路超过 10%）。
     *
     * @param reports 媒体信息，详情请参考 {@link QNStatisticsReport}
     */
    @Override
    public void onRemoteStatisticsUpdated(List<QNStatisticsReport> reports) {
        for (QNStatisticsReport report : reports) {
            int lost = report.trackKind.equals(QNTrackKind.VIDEO) ? report.videoPacketLostRate : report.audioPacketLostRate;
            Log.i("onRemoteStatisticsUpdated", "remote user " + report.userId
                    + " rtt " + report.rtt
                    + " grade " + report.networkGrade
                    + " track " + report.trackId
                    + " kind " + (report.trackKind.name())
                    + " lostRate " + lost);
        }
    }

    /**
     * 当音频路由发生变化时会回调此方法
     *
     * @param routing 音频设备, 详情请参考{@link QNAudioDevice}
     */
    @Override
    public void onAudioRouteChanged(QNAudioDevice routing) {
//        updateRemoteLogText("onAudioRouteChanged: " + routing.name());
    }

    /**
     * 当合流任务创建成功的时候会回调此方法
     *
     * @param mergeJobId 合流任务 id
     */
    @Override
    public void onCreateMergeJobSuccess(String mergeJobId) {

    }

    /**
     * 当单路流转推任务创建成功的时候会回调此方法
     *
     * @param forwardJobId 转推任务 ID
     */
    @Override
    public void onCreateForwardJobSuccess(String forwardJobId) {

    }

    /**
     * 当发生错误时会回调此方法
     *
     * @param errorCode 错误码，详情请参考 {@link QNErrorCode}
     * @param description 错误描述
     */
    @Override
    public void onError(int errorCode, String description) {
        /**
         * 关于错误异常的相关处理，都应在该回调中完成; 需要处理的错误码及建议处理逻辑如下:
         *
         *【TOKEN 相关】
         * 1. QNErrorCode.ERROR_TOKEN_INVALID 和 QNErrorCode.ERROR_TOKEN_ERROR 表示您提供的房间 token 不符合七牛 token 签算规则,
         *    详情请参考【服务端开发说明.RoomToken 签发服务】https://doc.qnsdk.com/rtn/docs/server_overview#1
         * 2. QNErrorCode.ERROR_TOKEN_EXPIRED 表示您的房间 token 过期, 需要重新生成 token 再加入；
         *
         *【房间设置相关】以下情况可以与您的业务服务开发确认具体设置
         * 1. QNErrorCode.ERROR_ROOM_FULL 当房间已加入人数超过每个房间的人数限制触发；请确认后台服务的设置；
         * 2. QNErrorCode.ERROR_PLAYER_ALREADY_EXIST 后台如果配置为开启【禁止自动踢人】,则同一用户重复加入/未正常退出再加入会触发此错误，您的业务可根据实际情况选择配置；
         * 3. QNErrorCode.ERROR_NO_PERMISSION 用户对于特定操作，如合流需要配置权限，禁止出现未授权的用户操作；
         * 4. QNErrorCode.ERROR_ROOM_CLOSED 房间已被管理员关闭；
         *
         *【其他错误】
         * 1. QNErrorCode.ERROR_AUTH_FAIL 服务验证时出错，可能为服务网络异常。建议重新尝试加入房间；
         * 2. QNErrorCode.ERROR_PUBLISH_FAIL 发布失败, 会有如下3种情况:
         * 1 ）请确认成功加入房间后，再执行发布操作
         * 2 ）请确定对于音频/视频 Track，分别最多只能有一路为 master
         * 3 ）请确认您的网络状况是否正常

         * 3. QNErrorCode.ERROR_RECONNECT_TOKEN_ERROR 内部重连后出错，一般出现在网络非常不稳定时出现，建议提示用户并尝试重新加入房间；
         * 4. QNErrorCode.ERROR_INVALID_PARAMETER 服务交互参数错误，请在开发时注意合流、踢人动作等参数的设置。
         * 5. QNErrorCode.ERROR_DEVICE_CAMERA 系统摄像头错误, 建议提醒用户检查
         */
        switch (errorCode) {
            case QNErrorCode.ERROR_TOKEN_INVALID:
            case QNErrorCode.ERROR_TOKEN_ERROR:
//                logAndToast("roomToken 错误，请检查后重新生成，再加入房间");
                break;
            case QNErrorCode.ERROR_TOKEN_EXPIRED:
//                logAndToast("roomToken过期");
//                mRoomToken = QNAppServer.getInstance().requestRoomToken(RoomActivity.this, mUserId, mRoomId);
//                mEngine.joinRoom(mRoomToken);
                break;
            case QNErrorCode.ERROR_ROOM_FULL:
//                logAndToast("房间人数已满!");
                break;
            case QNErrorCode.ERROR_PLAYER_ALREADY_EXIST:
//                logAndToast("不允许同一用户重复加入");
                break;
            case QNErrorCode.ERROR_NO_PERMISSION:
//                logAndToast("请检查用户权限:" + description);
                break;
            case QNErrorCode.ERROR_INVALID_PARAMETER:
//                logAndToast("请检查参数设置:" + description);
                break;
            case QNErrorCode.ERROR_PUBLISH_FAIL: {
                if (mEngine.getRoomState() != QNRoomState.CONNECTED
                        && mEngine.getRoomState() != QNRoomState.RECONNECTED) {
//                    logAndToast("发布失败，请加入房间发布: " + description);
                    mEngine.joinRoom(mRoomToken);
                } else {
//                    logAndToast("发布失败: " + description);
//                    mEngine.publishTracks(mLocalTrackList);
                }
            }
            break;
            case QNErrorCode.ERROR_AUTH_FAIL:
            case QNErrorCode.ERROR_RECONNECT_TOKEN_ERROR: {
                // reset TrackWindowMgr
//                mTrackWindowMgr.reset();
                // display local videoTrack
//                List<QNTrackInfo> localTrackListExcludeScreenTrack = new ArrayList<>(mLocalTrackList);
//                localTrackListExcludeScreenTrack.remove(mLocalScreenTrack);
//                mTrackWindowMgr.addTrackInfo(mUserId, localTrackListExcludeScreenTrack);
                if (errorCode == QNErrorCode.ERROR_RECONNECT_TOKEN_ERROR) {
//                    logAndToast("ERROR_RECONNECT_TOKEN_ERROR 即将重连，请注意网络质量！");
                }
                if (errorCode == QNErrorCode.ERROR_AUTH_FAIL) {
//                    logAndToast("ERROR_AUTH_FAIL 即将重连");
                }
                // rejoin Room
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEngine.joinRoom(mRoomToken);
                    }
                }, 1000);
            }
            break;
            case QNErrorCode.ERROR_ROOM_CLOSED:
//                reportError("房间被关闭");
                break;
            case QNErrorCode.ERROR_DEVICE_CAMERA:
//                logAndToast("请检查摄像头权限，或者被占用");
                break;
            default:
//                logAndToast("errorCode:" + errorCode + " description:" + description);
                break;
        }
    }

    /**
     * 当收到自定义消息时回调此方法
     *
     * @param message 自定义信息，详情请参考 {@link QNCustomMessage}
     */
    @Override
    public void onMessageReceived(QNCustomMessage message) {

    }

    private EventListener mEventListener;

    public void setEventListener(EventListener listener) {
        this.mEventListener = listener;
    }

    public interface EventListener {
        void onSubscribed(String remoteUserId, List<QNTrackInfo> trackInfoList);

        void onRemoteUnpublished(final String remoteUserId, List<QNTrackInfo> trackInfoList);

        void onKickedOut(String userId);
    }
}
