package yijiabaoforpad.yijiabao.aihuizhongyi.com.switchdemo;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aihui077 on 2017/12/18.
 */

public class SwitchView extends View {

    // 按钮向左时背景色
    private int colorLeft;
    // 按钮向右时背景色
    private int colorRight;
    // 按钮向左时笑脸背景色
    private int colorFaceLeft;
    // 按钮向右时笑脸背景色
    private int colorFaceRight;

    // 画笔颜色
    private int paintBgColor;
    // 笑脸画笔颜色
    private int paintFaceColor;

    // 画笔
    private Paint paint;

    // 按钮背景path
    private Path backgroundpath;
    // 笑脸背景path
    private Path faceBackPath;
    // 笑脸嘴巴Path
    private Path faceMouthPath;
    // 笑脸左边眼睛
    private Path faceEyeLeftPath;
    // 笑脸右边path
    private Path faceEyeRightPath;

    // 默认控件宽度
    private int wight=100;
    // 默认控件按高度
    private int height=30;

    // 动画持续时间
    private int animationDuration=1000;
    // 按钮是否打开
    private boolean isOpen=false;

    private RectF faceRecf;
    private RectF faceLeftEyeRectF;
    private RectF faceRightEyeRectF;
    private RectF faceMouthRectF;

    // 当前动画是否在进行中
    private boolean isAnimtion=false;

    private boolean isInit=false;

    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    public SwitchView(Context context) {
        this(context,null);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwitchView, defStyleAttr, 0);
        colorLeft=a.getColor(R.styleable.SwitchView_colorLeft,0xffcccccc);
        colorRight=a.getColor(R.styleable.SwitchView_colorRight,0xff33ccff);
        colorFaceLeft=a.getColor(R.styleable.SwitchView_colorFaceLeft, Color.BLUE);
        colorFaceRight=a.getColor(R.styleable.SwitchView_colorFaceRight,Color.YELLOW);
        paintBgColor=colorLeft;
        paintFaceColor=colorFaceLeft;
        init();
    }

    private void init(){
        paint=new Paint();
        paint.setAntiAlias(true);// 抗锯齿
        paint.setDither(true); // 防抖动
        paint.setStyle(Paint.Style.FILL); // 设置画笔铺满
        Log.e("123",">>>>>init");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("123",">>>>>onMeasure");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //  这里的意思是，获取specMode类型，如果设置了宽高或者MATCH_PARENT，则用设置的宽高，否则就用默认的，包括WARP_CONTENT
        if (widthMode == MeasureSpec.EXACTLY) {
            wight = widthSize;
        } else {
            wight=100;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height=30;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("123",">>>>>onSizeChanged");
        isInit=true;
        wight=w;
        height=h;
        backgroundpath=new Path();

        float top = 0;
        float left = 0;
        float bottom = h;
        float right = w;

        // 先画左边半圆
        RectF backgroundRecf = new RectF(left,top,bottom,bottom);
        backgroundpath.arcTo(backgroundRecf,90,180);
        // 然后画剩下部分
        backgroundRecf.left = right - bottom;
        backgroundRecf.right = right;
        backgroundpath.arcTo(backgroundRecf,270,180);
        backgroundpath.close();

        // 画笑脸的圆，这里不知道为什么，画360度会直接消失，所以画359度
        float faceTop= (float) (h*0.1);
        float faceLeft= (float) (h*0.1);
        float faceRight= (float) (h*0.9);
        faceBackPath=new Path();
        faceRecf=new RectF(faceTop,faceLeft,faceRight,faceRight);
        faceBackPath.arcTo(faceRecf,90,359);

        faceMouthClosePath();
        faceEyeLeftClosePath();
        faceEyeRightClosePath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("123",">>>>>onDraw");
        drawBack(canvas);
        drawFaceBack(canvas);
        drawFaceMouth(canvas);
        drawFaceEyeLeft(canvas);
        drawFaceEyeRight(canvas);
    }

    /**
     *   画按钮背景
     * @param canvas
     */
    private void drawBack(Canvas canvas){
        paint.setColor(paintBgColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(backgroundpath,paint);
    }

    /**
     *  画笑脸背景
     * @param canvas
     */
    private void drawFaceBack(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(paintFaceColor);
        canvas.drawPath(faceBackPath,paint);
    }

    /**
     *  画嘴巴
     * @param canvas
     */
    private void drawFaceMouth(Canvas canvas){
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) (height*0.04));
        canvas.drawPath(faceMouthPath,paint);
    }

    /**
     *   画左边眼睛
     * @param canvas
     */
    private void drawFaceEyeLeft(Canvas canvas){
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(faceEyeLeftPath,paint);
    }

    /**
     * 画右边眼睛
     * @param canvas
     */
    private void drawFaceEyeRight(Canvas canvas){
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(faceEyeRightPath,paint);
    }

    /**
     *  按钮关闭嘴巴path
     */
    private void faceMouthClosePath(){
        // 画嘴巴
        float center= (float) ((height*0.1+height*0.9)/2);
        float mouthTop= (float) (center-(height*0.2));
        float mouthLeft= (float) (center-(height*0.2));
        float mouthRight= (float) (center+(height*0.2));
        float mountBottom= (float) (center+(height*0.15 ));
        faceMouthRectF =new RectF(mouthLeft,mouthTop,mouthRight,mountBottom);
        faceMouthPath=new Path();
        faceMouthPath.arcTo(faceMouthRectF,45,90);
    }

    /**
     *  按钮关闭左眼path
     */
    private void faceEyeLeftClosePath(){
        // 画左边眼睛
        float center= (float) ((height*0.1+height*0.9)/2);
        float eyeTop= (float) (center-(height*0.1));
        float eyeLeft= (float) (center-(height*0.15));
        float eyeRight= (float) (eyeLeft+height*0.06);
        float eyeBottom= (float) (eyeTop+height*0.06);
        faceLeftEyeRectF=new RectF(eyeLeft,eyeTop,eyeRight,eyeBottom);
        faceEyeLeftPath=new Path();
        faceEyeLeftPath.arcTo(faceLeftEyeRectF,0,359);
    }

    /**
     *   按钮关闭右边眼睛path
     */
    private void faceEyeRightClosePath(){
        // 画右边眼睛
        float center= (float) ((height*0.1+height*0.9)/2);
        float eTop= (float) (center-(height*0.1));
        float eLeft= (float) (center+(height*0.09));
        float eRight= (float) (eLeft+height*0.06);
        float eBottom= (float) (eTop+height*0.06);
        faceRightEyeRectF=new RectF(eLeft,eTop,eRight,eBottom);
        faceEyeRightPath=new Path();
        faceEyeRightPath.arcTo(faceRightEyeRectF,0,359);
    }

    /**
     *   手势点击
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                if(isAnimtion){
                    return true;
                }
                startFaceTranslate();
                startOpenColorAnimation();
                startOpenColorFaceAnimation();
                return true;
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     *   打开按钮颜色动画
     */
    private void startOpenColorAnimation(){
        int colorFrom,colorTo;
        if(isOpen){
            colorFrom=colorRight;
            colorTo=colorLeft;
        }else {
            colorFrom=colorLeft;
            colorTo=colorRight;
        }
        // 按钮背景颜色渐变动画
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setInterpolator(mInterpolator);
        colorAnimation.setDuration(animationDuration); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                paintBgColor = (int)animator.getAnimatedValue();
                postInvalidate();
            }

        });
        colorAnimation.start();
    }

    /**
     *  笑脸颜色渐变
     */
    private void startOpenColorFaceAnimation(){
        int colorFrom,colorTo;
        if(isOpen){
            colorFrom=colorFaceRight;
            colorTo=colorFaceLeft;
        }else {
            colorFrom=colorFaceLeft;
            colorTo=colorFaceRight;
        }
        // 按钮背景颜色渐变动画
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setInterpolator(mInterpolator);
        colorAnimation.setDuration(animationDuration); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                paintFaceColor = (int)animator.getAnimatedValue();
            }

        });
        colorAnimation.start();
    }

    /**
     *   笑脸移动动画
     */
    private void startFaceTranslate(){
        isAnimtion=true;
        float from ;
        float to;
        if(isOpen){
            from=(float) (wight-(height*0.9));
            to= (float) (height*0.1);
        }else {
            from= (float) 0;
            to= (float) (wight-height);
        }
        // 按钮背景颜色渐变动画
        ValueAnimator tranAnimation = ValueAnimator.ofFloat(from, to);
        tranAnimation.setInterpolator(mInterpolator);
        tranAnimation.setDuration(animationDuration); // milliseconds
        tranAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                // 这里根据当前动画进度，重绘RectF，使笑脸背景移动
                float value = (float) animator.getAnimatedValue();
                float faceTop= (float) (height*0.1);
                float faceLeft,faceRight;
                if(isOpen){
                    faceLeft=value;
                    faceRight=(float) (value+(height*0.9)-(height*0.1));
                }else {
                    faceLeft=(float) (value + (height * 0.1));
                    faceRight = (float) (value + (height * 0.9));
                }
                float faceBottom= (float) (height*0.9);
                faceBackPath=new Path();
                faceRecf=new RectF(faceLeft,faceTop,faceRight,faceBottom);
                faceBackPath.arcTo(faceRecf,90,359);

                // 笑脸嘴巴移动
                startFaceMouthTransle(faceLeft,faceRight);
                // 左边眼睛移动动画
                startFaceEyeLeftTransle(faceLeft,faceRight);
                // 右边眼睛移动动画
                startFaceEyeRightTransle(faceLeft,faceRight);
                invalidate();
            }
        });
        tranAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isOpen=!isOpen;
                isAnimtion=false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        tranAnimation.start();
    }

    /**
     *   笑脸嘴巴移动动画
     * @param left
     * @param right
     */
    private void startFaceMouthTransle(float left,float right){
        // 画嘴巴
        float center= (float) ((left+right)/2);
        float cen= (float) ((height*0.1+height*0.9)/2);
        float mouthTop= (float) (cen-(height*0.2));
        float mouthLeft= (float) (center-(height*0.2));
        float mouthRight= (float) (center+(height*0.2));
        float mountBottom= (float) (cen+(height*0.15 ));
        faceMouthRectF =new RectF(mouthLeft,mouthTop,mouthRight,mountBottom);
        faceMouthPath=new Path();
        faceMouthPath.arcTo(faceMouthRectF,45,90);
    }

    /**
     *  左边眼睛移动动画
     * @param left
     * @param right
     */
    private void startFaceEyeLeftTransle(float left,float right){
        float center= (float) ((left+right)/2);
        float cen= (float) ((height*0.1+height*0.9)/2);
        // 画左边眼睛
        float eyeTop= (float) (cen-(height*0.1));
        float eyeLeft= (float) (center-(height*0.15));
        float eyeRight= (float) (eyeLeft+height*0.06);
        float eyeBottom= (float) (eyeTop+height*0.06);
        faceLeftEyeRectF=new RectF(eyeLeft,eyeTop,eyeRight,eyeBottom);
        faceEyeLeftPath=new Path();
        faceEyeLeftPath.arcTo(faceLeftEyeRectF,0,359);
    }

    /**
     *   右边眼睛移动动画
     * @param left
     * @param right
     */
    private void startFaceEyeRightTransle(float left,float right){
        float center= (float) ((left+right)/2);
        float cen= (float) ((height*0.1+height*0.9)/2);
        // 画右边眼睛
        float eTop= (float) (cen-(height*0.1));
        float eLeft= (float) (center+(height*0.09));
        float eRight= (float) (eLeft+height*0.06);
        float eBottom= (float) (eTop+height*0.06);
        faceRightEyeRectF=new RectF(eLeft,eTop,eRight,eBottom);
        faceEyeRightPath=new Path();
        faceEyeRightPath.arcTo(faceRightEyeRectF,0,359);
    }

    /**
     *   设置动画持续时间
     * @param duration
     */
    public void setAnimationDuration(int duration){
        this.animationDuration=duration;
    }

    /**
     *  设置按钮是否直接打开
     */
    public void setSwitchIsOpen(final boolean p){
        Log.e("123",">>>>>setSwitchIsOpen");
        if(isInit) {
            this.isOpen = !p;
            startFaceTranslate();
            startOpenColorAnimation();
            startOpenColorFaceAnimation();
        }else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg=new Message();
                    msg.what=1001;
                    msg.obj=p;
                    handler.sendMessage(msg);
                }
            },100);
        }
    }

    /**
     *   这里加handler是因为一开始时，先执行了setSwitchIsOpen，然后才执行ondraw，所以需要延迟100
     */
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1001){
                setSwitchIsOpen((Boolean) msg.obj);
            }
        }
    };
}
