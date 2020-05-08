package com.zuojianyou.zybdoctor.units;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AiFaceUtils {
    private int displayOrientation = 0;
    private Camera camera;
    /*** 标识当前是前摄像头还是后摄像头  back:0  front:1*/
    private int backOrFront = 0;
    private SurfaceHolder.Callback callback;
    private Context context;
    private SurfaceView surfaceView;
    /***photo的height ,width*/
    private int heightPhoto, widthPhoto;

    private boolean isPad;
    private boolean previewGetePass = false;

    public AiFaceUtils(boolean isPad) {
        this.isPad = isPad;
        if (!isPad) displayOrientation = 90;
    }

    public void create(SurfaceView surfaceView, Context context) {
        this.context = context;
        this.surfaceView = surfaceView;
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.setKeepScreenOn(true);
        callback = new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                camera = Camera.open();
                getCameraSize();
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                doChange(holder);
                focus();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.setPreviewCallback(null);
                    camera.release();
                    camera = null;
                }
            }
        };
        surfaceView.getHolder().addCallback(callback);
    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (previewGetePass) {
                previewGetePass = false;
                if (onImageCreated != null) {
                    Log.d("PreviewCallback", "data size=" + data.length);
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    if (image != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                        onImageCreated.onCreated(stream.toByteArray(), backOrFront);
                        try {
                            stream.close();
                        } catch (IOException ex) {
                            Log.d("PreviewCallback", "stream close err!");
                        }
                    }
                }
            }
        }
    };

    public boolean isPreviewGetePass() {
        return previewGetePass;
    }

    public void setPreviewGetePass(boolean previewGetePass) {
        this.previewGetePass = previewGetePass;
    }

    /***
     * 获取SupportedVideoSizes 控制输出视频width在300到600之间(尽可能小)
     * 获取PictureSize的大小(控制在w：1000-2000之间)
     */
    public void getCameraSize() {
        Camera.Parameters parameters = camera.getParameters();

        List<Camera.Size> photoSize = parameters.getSupportedPictureSizes();
        for (int i = 0; i < photoSize.size(); i++) {
            int width1 = photoSize.get(i).width;
            int height1 = photoSize.get(i).height;
            if (width1 >= 1000 && width1 <= 2000) {
                if (height1 >= 600 && height1 <= 2000) {
                    widthPhoto = width1;
                    heightPhoto = height1;
                }
            }
        }
        Log.d("AiFaceUtils", "width=" + widthPhoto + " height=" + heightPhoto);
    }

    private void doChange(SurfaceHolder holder) {
        try {
            camera.setPreviewCallback(previewCallback);
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(displayOrientation);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        openFaceDetection();
    }

    /**
     * 切换摄像头
     */
    public void changeCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && backOrFront == 0) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.stopFaceDetection();
                camera.release();
                camera = null;
                camera = Camera.open(i);
                try {
                    camera.setPreviewDisplay(surfaceView.getHolder());
                    camera.setDisplayOrientation(displayOrientation);
                    camera.setPreviewCallback(previewCallback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                backOrFront = 1;
                camera.startPreview();
                openFaceDetection();
                break;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK && backOrFront == 1) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.stopFaceDetection();
                camera.release();
                camera = null;
                camera = Camera.open(i);
                try {
                    camera.setPreviewDisplay(surfaceView.getHolder());
                    camera.setDisplayOrientation(displayOrientation);
                    camera.setPreviewCallback(previewCallback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
                backOrFront = 0;
                openFaceDetection();
                break;
            }
        }
    }

    public void stop() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
        }
    }

    public void destroy() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

//    public void takePicture() {
//        camera.takePicture(null, null, new PictureCallBack());
//    }

    /**
     * 聚焦
     */
    public void focus() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureSize(widthPhoto, heightPhoto);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
        camera.cancelAutoFocus();
    }

    /*** 拍照功能*/
//    private class PictureCallBack implements Camera.PictureCallback {
//
//        @Override
//        public void onPictureTaken(byte[] bytes, Camera camera) {
//            camera.startPreview();
//            if (onImageCreated != null) {
//                onImageCreated.onCreated(bytes, backOrFront);
//            }
//        }
//    }

    public interface OnImageCreated {
        void onCreated(byte[] bytes, int backOrFront);
    }

    public OnImageCreated onImageCreated;

    public void setOnImageCreated(OnImageCreated onImageCreated) {
        this.onImageCreated = onImageCreated;
    }


    private void openFaceDetection() {
        if (camera != null) {
            camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                @Override
                public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                    if (faces.length > 0 && onFaceDetected != null) {
                        onFaceDetected.onDetected();
//                        previewGetePass = true;
                    }
                }
            });
            camera.startFaceDetection();
        }
    }

    public interface OnFaceDetected {
        void onDetected();
    }

    public OnFaceDetected onFaceDetected;

    public void setOnFaceDetected(OnFaceDetected onFaceDetected) {
        this.onFaceDetected = onFaceDetected;
    }
}

