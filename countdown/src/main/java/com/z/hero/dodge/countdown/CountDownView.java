package com.z.hero.dodge.countdown;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Linzheng on 2017/7/24.
 * <br>Email:z.hero.dodge@gamil.com</br>
 */
public class CountDownView extends View {

    private static final int DEFAULT_VIEW_SIZE = 100;
    private static final int DEFAULT_COLOR = 0xb3000000;
    private static final int DEFAULT_DURATION = 3 * 1000;
    private static final int DEFAULT_STROKE_SIZE = 10;
    private static final int DEFAULT_STROKE_COLOR = 0xFFFFB200;
    private static final int DEFAULT_STROKE_BG_SIZE = 16;
    private static final int DEFAULT_STROKE_BG_COLOR = 0xFF1B0A25;

    private int mColor = DEFAULT_COLOR;
    private long mDuration = DEFAULT_DURATION;
    private int mStrokeSize = DEFAULT_STROKE_SIZE;
    private int mStrokeColor_0 = DEFAULT_STROKE_COLOR;
    private int mStrokeColor_1 = 0;
    private int mStrokeBgSize = DEFAULT_STROKE_BG_SIZE;
    private int mStrokeBgColor = DEFAULT_STROKE_BG_COLOR;

    private Paint mBgPaint = new Paint();
    private Paint mStrokePaint = new Paint();
    private Paint mStrokeBgPaint = new Paint();
    private RectF oval = new RectF();

    private ValueAnimator animator;
    private CountChangeListener mCountChangeListener;
    private boolean mBlockAnimationEnd = false;     // 是否拦截mBlockAnimationEnd 事件
    private float mSweepAngle = 360f;
    private int mClickCount = 0;

    public CountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
        initAnimation();
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CountDownView, defStyleAttr, 0);
        mColor = ta.getColor(R.styleable.CountDownView_cdv_bg, DEFAULT_COLOR);
        mDuration = ta.getInteger(R.styleable.CountDownView_cdv_duration, DEFAULT_DURATION);
        mStrokeColor_0 = ta.getColor(R.styleable.CountDownView_cdv_stroke_color_0, DEFAULT_STROKE_COLOR);
        mStrokeColor_1 = ta.getColor(R.styleable.CountDownView_cdv_stroke_color_1, 0);
        mStrokeSize = ta.getDimensionPixelSize(R.styleable.CountDownView_cdv_stroke_size, DEFAULT_STROKE_SIZE);
        mStrokeBgColor = ta.getColor(R.styleable.CountDownView_cdv_stroke_bg_color, DEFAULT_STROKE_BG_COLOR);
        mStrokeBgSize = ta.getDimensionPixelOffset(R.styleable.CountDownView_cdv_stroke_bg_size, DEFAULT_STROKE_BG_SIZE);
        ta.recycle();
        // 背景
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mColor);
        mBgPaint.setStyle(Paint.Style.FILL);

        // 进度条背景
        mStrokeBgPaint.setAntiAlias(true);
        mStrokeBgPaint.setColor(mStrokeBgColor);
        mStrokeBgPaint.setStrokeWidth(mStrokeBgSize);
        mStrokeBgPaint.setStyle(Paint.Style.STROKE);
        // 进度条
        if (mStrokeColor_1 == 0) {
            setStrokeColor(mStrokeColor_0);
        } else {
            setStrokeColor(mStrokeColor_0, mStrokeColor_1);
        }
    }

    private void initAnimation() {
        // 动画
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(mDuration);
        animator.addUpdateListener(animation -> {
            float f = (float) animation.getAnimatedValue();
            mSweepAngle = 360 * f;
            invalidate();
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBlockAnimationEnd = false;
                mClickCount++;
                if (mCountChangeListener != null) {
                    mCountChangeListener.onCurrentCount(mClickCount);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mBlockAnimationEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mBlockAnimationEnd) {
                    if (mCountChangeListener != null) {
                        mCountChangeListener.onCountdownEnd(mClickCount);
                    }
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) / 2;
        float offset = Math.max(mStrokeSize, mStrokeBgSize) / 2;
        oval.left = width / 2 - radius + offset;
        oval.top = height / 2 - radius + offset;
        oval.right = width / 2 + radius - offset;
        oval.bottom = height / 2 + radius - offset;
        canvas.rotate(-90, width / 2, height / 2);
        canvas.drawCircle(width / 2, height / 2, radius - offset, mBgPaint);
        canvas.drawCircle(width / 2, height / 2, radius - offset, mStrokeBgPaint);
        canvas.drawArc(oval, 0f, mSweepAngle, false, mStrokePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSize(DEFAULT_VIEW_SIZE, widthMeasureSpec),
                getSize(DEFAULT_VIEW_SIZE, heightMeasureSpec));
    }


    public int getSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        if (animator.isStarted()) {
            animator.cancel();
        }
        animator.start();
    }

    /**
     * 设置起始个数
     */
    public void setStartCount(int count) {
        this.mClickCount = count;
    }

    /**
     * 连击监听
     * @param countChangeListener 连击监听
     */
    public void setCountChangeListener(CountChangeListener countChangeListener) {
        mCountChangeListener = countChangeListener;
    }

    /**
     * 设置动画持续时间
     * @param duration time
     */
    public void setDuration(long duration) {
        if (mDuration != duration && animator != null) {
            mDuration = duration;
            animator.setDuration(duration);
        }
    }

    public long getDuration() {
        return mDuration;
    }

    /**
     * 背景颜色
     */
    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            mBgPaint.setColor(color);
        }
    }

    /**
     * 进度条宽度
     * @param strokeSize px
     */
    public void setStrokeSize(int strokeSize) {
        if (strokeSize < 0) {
            throw new IllegalArgumentException("stroke size is < 0");
        }
        if (mStrokeSize != strokeSize) {
            mStrokeSize = strokeSize;
            mStrokePaint.setStrokeWidth(mStrokeSize);
        }
    }

    /**
     * 进度条颜色
     */
    public void setStrokeColor(int strokeColor) {
        mStrokeColor_0 = strokeColor;
        mStrokePaint.reset();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStrokeWidth(mStrokeSize);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setColor(mStrokeColor_0);
    }

    public void setStrokeColor(int topColor, int bottomColor) {
        mStrokeColor_0 = topColor;
        mStrokeColor_1 = bottomColor;
        int[] colors = new int[]{mStrokeColor_0, mStrokeColor_1, mStrokeColor_0};
        float[] positions = new float[]{0f, 0.5f, 1.0f};
        mStrokePaint.reset();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStrokeWidth(mStrokeSize);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setShader(new SweepGradient(getWidth() / 2, getHeight() / 2, colors, positions));
    }


    /**
     * 进度条背景宽度
     * @param strokeBgSize px
     */
    public void setStrokeBgSize(int strokeBgSize) {
        if (strokeBgSize < 0) {
            throw new IllegalArgumentException("stroke size is < 0");
        }
        if (mStrokeBgSize != strokeBgSize) {
            mStrokeBgSize = strokeBgSize;
            mStrokeBgPaint.setStrokeWidth(mStrokeBgSize);
        }
    }

    /**
     * 进度条背景颜色
     */
    public void setStrokeBgColor(int strokeBgColor) {
        if (mStrokeBgColor != strokeBgColor) {
            mStrokeBgColor = strokeBgColor;
            mStrokeBgPaint.setColor(mStrokeBgColor);
        }
    }

    /**
     * 倒计时监听
     */
    public interface CountChangeListener {
        void onCurrentCount(int count);

        void onCountdownEnd(int count);
    }
}

