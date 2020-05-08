package com.zuojianyou.zybdoctor.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RecordControlView extends View {

    private OnTapListener onTapListener;
    private OnPressListener onPressListener;
    private OnUpListener onUpListener;

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public void setOnPressListener(OnPressListener onPressListener) {
        this.onPressListener = onPressListener;
    }

    public void setOnUpListener(OnUpListener onUpListener) {
        this.onUpListener = onUpListener;
    }

    private int viewWidth, viewHeight;

    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    private int angle = 0;

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
        invalidate();
    }

    public RecordControlView(Context context) {
        super(context);
    }

    public RecordControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(0xFFBBBBBB);
        paint.setAntiAlias(true);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 3 + progress, paint);

        Paint paint2 = new Paint();
        paint2.setColor(0xFFFFFFFF);
        paint2.setAntiAlias(true);
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 4 - progress, paint2);

        Paint paint3 = new Paint();
        paint3.setColor(0xFFFF0000);
        paint3.setAntiAlias(true);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(10);

        float left = (viewWidth - (viewWidth * 2 / 3f + progress * 2)) / 2f;
        RectF oval = new RectF(5 + left, 5 + left,
                getWidth() - 5 - left, getHeight() - 5 - left);

        canvas.drawArc(oval, -90, angle, false, paint3);
    }

    private ObjectAnimator animator;

    private void startAnimat() {
        animator = ObjectAnimator.ofInt(this, "progress", 0, 20);  //自定义progress属性
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (progress == 20)
                    startAnimat2();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (progress < 5 && onTapListener != null) {
                    onTapListener.onTap();
                }
                regainAnimat();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void stopAnimat() {
        if (animator != null)
            animator.cancel();
    }

    private void regainAnimat() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "progress", progress, 0);  //自定义progress属性
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

    private ObjectAnimator animator2;

    private void startAnimat2() {
        animator2 = ObjectAnimator.ofInt(this, "angle", 0, 360);  //自定义progress属性
        animator2.setDuration(10000);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (onPressListener != null) {
                    onPressListener.onPress();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onUpListener != null) {
                    onUpListener.onUp();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator2.start();
    }

    private void stopAnimat2() {
        if (animator2 != null)
            animator2.cancel();
    }

    public interface OnTapListener {
        public void onTap();
    }

    public interface OnPressListener {
        public void onPress();
    }

    public interface OnUpListener {
        public void onUp();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startAnimat();
                break;
            case MotionEvent.ACTION_UP:
                stopAnimat();
                stopAnimat2();
                break;
        }
        return true;
    }
}
