package com.zuojianyou.zybdoctor.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 生成头像
 *
 * @author jc.Ren
 * @date 2015/09/06
 */
public class ImageCutView extends FrameLayout {

    public static final String TAG = "ImageCutView";

    //    public float aspectRatio = 0.65f;//宽高比例
    public float aspectRatio = 1f;//宽高比例
    public int MIN_WIDTH = 600;//头像最小宽度
    public int MIN_HEIGHT = Math.round(MIN_WIDTH * aspectRatio);//头像最小高度
    public static final int POINT = 60;//四个角触摸点边长
    public static final int SHADE_COLOR = 0x66000000;//遮罩颜色

    //触摸点标记
    public static final int MODE_BODY = 5;
    public static final int MODE_LEFT_TOP = 1;
    public static final int MODE_RIGHT_TOP = 2;
    public static final int MODE_LEFT_BOTTOM = 3;
    public static final int MODE_RIGHT_BOTTOM = 4;
    public static final int MODE_NULL = 0;

    private ImageView imageView = null;//被剪辑照片的ImageView
    private int height;//ImageCutView高度
    private int width;//ImageCutView宽度
    private int left;//相框左坐标
    private int top;//相框上坐标
    private int right;//相框右坐标
    private int bottom;//相框底坐标
    private int curWidth;//当前宽度
    private int curHeight;//当前高度
    private int touchMode;//触摸点标记
    private float downX;//按下x坐标
    private float downY;//按下y坐标

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        MIN_HEIGHT = Math.round(MIN_WIDTH * aspectRatio);
        invalidate();
    }

    public ImageCutView(Context context) {
        this(context, null, 0);
        // TODO Auto-generated constructor stub
    }

    public ImageCutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public ImageCutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(context);
    }

    /**
     * 初始化ImageView并添加到当前FrameLayout
     */
    private void init(Context context) {
        //layout容器不设置背景不会调用draw方法
        setBackgroundColor(0x00000000);

        imageView = new ImageView(context);
        imageView.setDrawingCacheEnabled(true);
        imageView.setBackgroundColor(0xff000000);
        addView(imageView);
    }

    /**
     * 测量并计算出各个数值
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        init();
    }

    private void init() {
        top = (height - MIN_HEIGHT) / 2;
        bottom = (height + MIN_HEIGHT) / 2;
        left = (width - MIN_WIDTH) / 2;
        right = (width + MIN_WIDTH) / 2;
        curWidth = MIN_WIDTH;
        curHeight = MIN_HEIGHT;
    }

    public void reSetCutBox() {
        init();
        invalidate();
    }

    /**
     * 设置被剪辑照片
     *
     * @param bitmap
     */
    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * 获取剪辑后照片
     *
     * @return
     */
    public Bitmap getImageBitmap() {
        Bitmap bitmap = imageView.getDrawingCache();
        if (left < 0) left = 0;
        if (top < 0) top = 0;
        if (curWidth + left > width) curWidth = width - left;
        if (curHeight + top > height) curHeight = height - top;
        return Bitmap.createBitmap(bitmap, left, top, curWidth, curHeight);
    }

    /**
     * 画剪辑框
     */
    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.draw(canvas);
        canvas.drawBitmap(getShape(), 0, 0, null);
    }

    /**
     * 剪辑框bitmap生成
     *
     * @return
     */
    private Bitmap getShape() {

        //透明阴影遮罩（argb_8888才可以画透明图片）
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(SHADE_COLOR);

        //画剪辑边框
        Paint rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5);
        canvas.drawRect(left, top, right, bottom, rectPaint);

        //删除边框中阴影
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(left, top, right, bottom, clearPaint);

        //画触摸点
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(0xffffffff);
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(left, top, POINT / 2, circlePaint);
        canvas.drawCircle(right, top, POINT / 2, circlePaint);
        canvas.drawCircle(left, bottom, POINT / 2, circlePaint);
        canvas.drawCircle(right, bottom, POINT / 2, circlePaint);

        return bitmap;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                if (isInController(downX, downY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchMode == MODE_BODY) {
                    touchMove(event);
                    return true;
                } else if (touchMode != MODE_NULL) {
                    touchScale(event);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchMode = MODE_NULL;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 按下点是否在控制区
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInController(float x, float y) {
        boolean flag = true;
        if (isTouchConner(x, y, left, top)) {
            touchMode = MODE_LEFT_TOP;
        } else if (isTouchConner(x, y, right, top)) {
            touchMode = MODE_RIGHT_TOP;
        } else if (isTouchConner(x, y, left, bottom)) {
            touchMode = MODE_LEFT_BOTTOM;
        } else if (isTouchConner(x, y, right, bottom)) {
            touchMode = MODE_RIGHT_BOTTOM;
        } else if (isTouchBody(x, y)) {
            touchMode = MODE_BODY;
        } else {
            touchMode = MODE_NULL;
            flag = false;
        }
        return flag;
    }

    /**
     * 是否按下四个角的一角（缩放）
     *
     * @param x
     * @param y
     * @param px
     * @param py
     * @return
     */
    private boolean isTouchConner(float x, float y, float px, float py) {
        boolean flag = false;
        if (x > px - POINT && x < px + POINT && y > py - POINT
                && y < py + POINT) {
            flag = true;
        }
        return flag;
    }

    /**
     * 是否按下图像（位移）
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchBody(float x, float y) {
        boolean flag = false;
        if (x > left + POINT && x < right - POINT && y > top + POINT
                && y < bottom - POINT) {
            flag = true;
        }
        return flag;
    }

    /**
     * 移动计算
     *
     * @param event
     */
    private void touchMove(MotionEvent event) {
        float moveX = event.getX() - downX;
        float moveY = event.getY() - downY;
        top += moveY;
        bottom += moveY;
        left += moveX;
        right += moveX;
        if (top < 0) {
            top = 0;
            bottom = curHeight;
        }
        if (left < 0) {
            left = 0;
            right = curWidth;
        }
        if (bottom > height) {
            bottom = height;
            top = height - curHeight;
        }
        if (right > width) {
            right = width;
            left = width - curWidth;
        }
        downX = event.getX();
        downY = event.getY();
        invalidate();
    }

    /**
     * 缩放计算
     *
     * @param event
     */
    private void touchScale(MotionEvent event) {
        float moveX = event.getX() - downX;
        float moveY = event.getY() - downY;
        moveLine(moveX, moveY);
        downX = event.getX();
        downY = event.getY();
        invalidate();
    }

    /**
     * 判断缩放过小或者出界
     *
     * @param x
     * @param y
     */
    private void moveLine(float x, float y) {
        int m = (int) Math.abs(x);
        if (Math.abs(x) < Math.abs(y)) {
            m = (int) Math.abs(y);
        }
        switch (touchMode) {
            case MODE_LEFT_TOP:
                if (x > 0 && y > 0) {
                    if (curWidth - m > MIN_WIDTH) {
                        setCurWidth(curWidth - m);
                        top = bottom - curHeight;
                        left = right - curWidth;
                    } else {
                        setCurWidth(MIN_WIDTH);
                        top = bottom - curHeight;
                        left = right - curWidth;
                    }
                } else if (x < 0 && y < 0) {
                    if (left - m < 0) {
                        left = 0;
                        setCurWidth(right);
                        top = bottom - curHeight;
                    } else if (top - m < 0) {
                        top = 0;
                        setCurHeight(bottom);
                        left = right - curWidth;
                    } else {
                        setCurWidth(curWidth + m);
                        top = bottom - curHeight;
                        left = right - curWidth;
                    }
                }
                break;
            case MODE_LEFT_BOTTOM:
                if (x < 0 && y > 0) {
                    if (left - m < 0) {
                        left = 0;
                        setCurWidth(right);
                        bottom = top + curHeight;
                    } else if (bottom + m > height) {
                        bottom = height;
                        setCurHeight(bottom - top);
                        left = right - curWidth;
                    } else {
                        setCurWidth(curWidth + m);
                        left = right - curWidth;
                        bottom = top + curHeight;
                    }
                } else if (x > 0 && y < 0) {
                    if (curWidth - m > MIN_WIDTH) {
                        setCurWidth(curWidth - m);
                        left = right - curWidth;
                        bottom = top + curHeight;
                    } else {
                        setCurWidth(MIN_WIDTH);
                        left = right - curWidth;
                        bottom = top + curHeight;
                    }
                }
                break;
            case MODE_RIGHT_TOP:
                if (x > 0 && y < 0) {
                    if (right + m > width) {
                        right = width;
                        setCurWidth(right - left);
                        top = bottom - curHeight;
                    } else if (top - m < 0) {
                        top = 0;
                        setCurHeight(bottom - top);
                        right = left + curWidth;
                    } else {
                        setCurWidth(curWidth + m);
                        right = left + curWidth;
                        top = bottom - curHeight;
                    }
                } else if (x < 0 && y > 0) {
                    if (curWidth - m > MIN_WIDTH) {
                        setCurWidth(curWidth - m);
                        right = left + curWidth;
                        top = bottom - curHeight;
                    } else {
                        setCurWidth(MIN_WIDTH);
                        top = bottom - curHeight;
                        right = left + curWidth;
                    }
                }
                break;
            case MODE_RIGHT_BOTTOM:
                if (x > 0 && y > 0) {
                    if (right + m > width) {
                        right = width;
                        setCurWidth(right - left);
                        bottom = top + curHeight;
                    } else if (bottom + m > height) {
                        bottom = height;
                        setCurHeight(bottom - top);
                        right = left + curWidth;
                    } else {
                        setCurWidth(curWidth + m);
                        right = left + curWidth;
                        bottom = top + curHeight;
                    }
                } else if (x < 0 && y < 0) {
                    if (curWidth - m > MIN_WIDTH) {
                        if (curWidth - m > MIN_WIDTH) {
                            setCurWidth(curWidth - m);
                            right = left + curWidth;
                            bottom = top + curHeight;
                        } else {
                            setCurWidth(MIN_WIDTH);
                            right = left + curWidth;
                            bottom = top + curHeight;
                        }
                    }
                }
                break;
        }

    }

    private void setCurWidth(int mWidth) {
        curWidth = mWidth;
        curHeight = Math.round(mWidth * aspectRatio);
    }

    private void setCurHeight(int mHeight) {
        curHeight = mHeight;
        curWidth = Math.round(mHeight / aspectRatio);
    }

}
