package com.sheng.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @attr ref R.styleable#ssb_seekbarType
 * @attr ref R.styleable#ssb_bubbleType
 * @attr ref R.styleable#ssb_seekbarHeight
 * @attr ref R.styleable#ssb_normalRadius
 * @attr ref R.styleable#ssb_selectRadius
 * @attr ref R.styleable#ssb_spaceHeight
 * @attr ref R.styleable#ssb_bubbleRadius
 * @attr ref R.styleable#ssb_seekbarImg
 * @attr ref R.styleable#ssb_thumbImg
 * @attr ref R.styleable#ssb_isShowText
 * @attr ref R.styleable#ssb_isShowFloat
 * @attr ref R.styleable#ssb_isShowBubble
 * @attr ref R.styleable#ssb_seekbarFirstColor
 * @attr ref R.styleable#ssb_seekbarSecondColor
 * @attr ref R.styleable#ssb_seekbarTextColor
 * @attr ref R.styleable#ssb_seekbarBubbleColor
 * @attr ref R.styleable#ssb_seekbarBubbleTextColor
 */
public class EasySeekBar extends View {

    private static final String TAG = "MyTouchSeekBar";

    // type enum
    public SEEKBAR_TYPE mSeekbarType = SEEKBAR_TYPE.PROGRESS;

    public BUBBLE_TYPE mBubbleType = BUBBLE_TYPE.ROUND_RECT;

    private enum SEEKBAR_TYPE {
        PROGRESS, DIY
    }

    private enum BUBBLE_TYPE {
        CIRCLE, ROUND_RECT
    }

    // listener
    public interface OnSeekBarProgressChangeListener {
        void onProgressStart(EasySeekBar easySeekBar, float mProgress);

        void onProgressChange(EasySeekBar easySeekBar, float mProgress);

        void onProgressStop(EasySeekBar easySeekBar, float mProgress);
    }

    public interface OnSeekBarDiyChangeListener {
        void onDiyChange(EasySeekBar easySeekBar, String text, int position);
    }

    public OnSeekBarProgressChangeListener mProgressListener;
    public OnSeekBarDiyChangeListener mDiyListener;

    public void setSeekBarProgressListener(OnSeekBarProgressChangeListener listener) {
        this.mProgressListener = listener;
    }

    public void setSeekBarDiyChangeListener(OnSeekBarDiyChangeListener listener) {
        this.mDiyListener = listener;
    }

    // view field
    private int mSeekbarHeight;
    private int mNormalSeekBarRadius;
    private int mSelectSeekBarRadius;
    private int mTextHeight;
    private int mSpaceHeight;
    private int mItemSpace;
    private int mThumbCenterX;
    private int mThumbCenterY;
    private int mBubbleRadius;
    private int mSeekBarFirstColor;
    private int mSeekBarSecondColor;
    private int mThumbColor;
    private int mThumbSelectColor;
    private int mTextColor;
    private int mTextSelectColor;
    private int mBubbleColor;
    private int mBubbleTextColor;
    private boolean isShowFloat;
    private boolean isShowText;
    private boolean isShowBubble;
    private boolean isAlwayShowBubble;
    private boolean isCanTouch;
    private boolean isSeekBarRound;
    private int mTextSize;
    private int mBubbleTextSize;

    // view rect and paint
    private Rect mSeekBarWrapperRect;
    private Rect mThumbSrcRect;
    private Rect mThumbDstRect;
    private Bitmap mThumbBitmap;
    private Rect mSeekBarSrcRect;
    private Rect mSeekBarDstRect;
    private Bitmap mSeekBarBitmap;
    private Paint mSeekBarPaint;
    private Paint mCirclePaint;
    private Paint mTextPaint;

    // view data
    private List<String> mDatas;
    private int mSelectIndex = -1;
    private float mMax;
    private float mMin;
    private float mDeltaProgress;
    private float mProgress;
    private boolean isThumbPress;

    // view bubble
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private SmartInnerBubbleView mBubbleView;
    private int[] mBubbleViewPoint;
    private int mScreenState;

    public EasySeekBar(Context context) {
        this(context, null);
    }

    public EasySeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasySeekBar, defStyleAttr, 0);
        mSeekbarType = a.getInt(R.styleable.EasySeekBar_ssb_seekbarType, 0) == 0 ? SEEKBAR_TYPE.PROGRESS : SEEKBAR_TYPE.DIY;
        mBubbleType = a.getInt(R.styleable.EasySeekBar_ssb_bubbleType, 0) == 0 ? BUBBLE_TYPE.CIRCLE : BUBBLE_TYPE.ROUND_RECT;
        mSeekbarHeight = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_seekbarHeight, EasySeekBarUtil.dp2px(getContext(), 10));
        mNormalSeekBarRadius = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_normalRadius, EasySeekBarUtil.dp2px(getContext(), 10));
        mSelectSeekBarRadius = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_selectRadius, EasySeekBarUtil.dp2px(getContext(), 20));
        mSpaceHeight = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_spaceHeight, EasySeekBarUtil.dp2px(getContext(), 3));
        mBubbleRadius = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_bubbleRadius, EasySeekBarUtil.dp2px(getContext(), 10));
        int seekBarImgId = a.getResourceId(R.styleable.EasySeekBar_ssb_seekbarImg, -1);
        if (seekBarImgId != -1) {
            mSeekBarBitmap = BitmapFactory.decodeResource(getResources(), seekBarImgId);
        }

        int thumbImgId = a.getResourceId(R.styleable.EasySeekBar_ssb_thumbImg, -1);
        if (thumbImgId != -1) {
            mThumbBitmap = BitmapFactory.decodeResource(getResources(), thumbImgId);
        }

        isShowFloat = a.getBoolean(R.styleable.EasySeekBar_ssb_isShowFloat, false);
        isShowBubble = a.getBoolean(R.styleable.EasySeekBar_ssb_isShowBubble, false);
        isAlwayShowBubble = a.getBoolean(R.styleable.EasySeekBar_ssb_isAlwayShowBubble, false);
        isShowText = a.getBoolean(R.styleable.EasySeekBar_ssb_isShowText, false);
        isCanTouch = a.getBoolean(R.styleable.EasySeekBar_ssb_isCanTouch, false);
        isSeekBarRound = a.getBoolean(R.styleable.EasySeekBar_ssb_isSeekBarRound, false);
        mSeekBarFirstColor = a.getColor(R.styleable.EasySeekBar_ssb_seekbarFirstColor, Color.rgb(0x00, 0xFF, 0x7F));
        mSeekBarSecondColor = a.getColor(R.styleable.EasySeekBar_ssb_seekbarSecondColor, Color.rgb(0x00, 0xEE, 0x00));
        mThumbColor = a.getColor(R.styleable.EasySeekBar_ssb_thumbColor, Color.rgb(0x00, 0xEE, 0x00));
        mThumbSelectColor = a.getColor(R.styleable.EasySeekBar_ssb_thumbSelectColor, Color.rgb(0xFF, 0xEE, 0x00));
        mTextColor = a.getColor(R.styleable.EasySeekBar_ssb_seekbarTextColor, Color.rgb(0x00, 0x00, 0x00));
        mTextSelectColor = a.getColor(R.styleable.EasySeekBar_ssb_seekbarTextSelectColor, Color.rgb(0xFF, 0xEE, 0x00));
        mBubbleColor = a.getColor(R.styleable.EasySeekBar_ssb_seekbarBubbleColor, Color.rgb(0x00, 0xEE, 0x00));
        mBubbleTextColor = a.getColor(R.styleable.EasySeekBar_ssb_seekbarBubbleTextColor, Color.rgb(0x00, 0x00, 0x00));
        mTextSize = a.getDimensionPixelSize(R.styleable.EasySeekBar_ssb_seekbarTextSize, EasySeekBarUtil.sp2px(getContext(), 16));
        mBubbleTextSize = a.getDimensionPixelSize(R.styleable.EasySeekBar_ssb_seekbarBubbleTextSize, EasySeekBarUtil.sp2px(getContext(), 14));
        mProgress = a.getInt(R.styleable.EasySeekBar_ssb_progress, 0);
        mMin = a.getInt(R.styleable.EasySeekBar_ssb_minProgress, 0);
        mMax = a.getInt(R.styleable.EasySeekBar_ssb_maxProgress, 100);
        a.recycle();

        initPaintAndRect();
        autoInit();

        mDatas = new ArrayList<>();


        // reference by https://github.com/woxingxiao/BubbleSeekBar
        if (isShowBubble) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.gravity = Gravity.START | Gravity.TOP;
            mLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.format = PixelFormat.TRANSLUCENT;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            // MIUI禁止了开发者使用TYPE_TOAST，Android 7.1.1 对TYPE_TOAST的使用更严格
            if (EasySeekBarUtil.isMIUI() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            mBubbleViewPoint = new int[2];
            mBubbleView = new SmartInnerBubbleView(getContext());
            mScreenState = EasySeekBarUtil.getScreenState(context);
        }
    }

    private void initPaintAndRect() {
        // text Paint
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetrics metrics = new Paint.FontMetrics();
        mTextPaint.getFontMetrics(metrics);
        mTextHeight = (int) (metrics.descent - metrics.ascent);

        // thumb Paint
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);

        // seekbar Paint
        mSeekBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSeekBarPaint.setStyle(Paint.Style.FILL);

        // rect
        mSeekBarWrapperRect = new Rect();
        mSeekBarSrcRect = new Rect();
        mSeekBarDstRect = new Rect();
        mThumbSrcRect = new Rect();
        mThumbDstRect = new Rect();
    }

    public void autoInit() {

        if (mMin >= mMax) {
            mMin = 0;
            mMax = 100;
        }

        if (mProgress < mMin) {
            mProgress = mMin;
        }

        if (mProgress > mMax) {
            mProgress = mMax;
        }

        mDeltaProgress = mMax - mMin;

        if (mNormalSeekBarRadius * 2 < mSeekbarHeight) {
            mNormalSeekBarRadius = (int) (mSeekbarHeight * 1.5f);
        }

        if (mSelectSeekBarRadius < mNormalSeekBarRadius) {
            mSelectSeekBarRadius = (int) (mNormalSeekBarRadius * 1.5f);
        }

        requestLayout();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (!isShowBubble)
            return;

        if (visibility != VISIBLE) {
            hideBubble();
        } else {
            if (isAlwayShowBubble) {
                showBubble();
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideBubble();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        height += mSpaceHeight;
        height += mSelectSeekBarRadius * 2;
        if (isShowText) {
            height += mSpaceHeight + mTextHeight + mSpaceHeight;
        } else {
            height += mSpaceHeight;
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);

        if (isShowBubble) {
            mBubbleView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mSeekBarWrapperRect.left = getPaddingLeft();
            mSeekBarWrapperRect.top = getPaddingTop() + mSpaceHeight;
            mSeekBarWrapperRect.right = getMeasuredWidth() - getPaddingRight();
            if (isShowText) {
                mSeekBarWrapperRect.bottom = getMeasuredHeight() - mSpaceHeight - mTextHeight - mSpaceHeight;
            } else {
                mSeekBarWrapperRect.bottom = getMeasuredHeight() - mSpaceHeight;
            }
            // seekbar position
            mSeekBarDstRect.left = mSeekBarWrapperRect.left + mSelectSeekBarRadius;
            mSeekBarDstRect.top = mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2 - mSeekbarHeight / 2;
            mSeekBarDstRect.right = mSeekBarWrapperRect.right - mSelectSeekBarRadius;
            mSeekBarDstRect.bottom = mSeekBarDstRect.top + mSeekbarHeight;

            // thumb start position
            mThumbCenterX = (int) ((mProgress - mMin) * 1.0f / mDeltaProgress *
                    mSeekBarDstRect.width() + mSeekBarDstRect.left);
            mThumbCenterY = (int) (mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // seekbar type is diy
        if (mSeekbarType == SEEKBAR_TYPE.DIY) {

            int size = mDatas.size();
            if (size <= 1) {
                return;
            }

            // draw seekbar
            if (mSeekBarBitmap != null) {
                mSeekBarSrcRect.left = 0;
                mSeekBarSrcRect.top = 0;
                mSeekBarSrcRect.right = mSeekBarBitmap.getWidth();
                mSeekBarSrcRect.bottom = mSeekBarBitmap.getHeight();
                canvas.drawBitmap(mSeekBarBitmap, mSeekBarSrcRect, mSeekBarDstRect, null);

            } else {
                mSeekBarPaint.setStrokeWidth(mSeekbarHeight);
                mSeekBarPaint.setColor(mSeekBarFirstColor);
                if (isSeekBarRound) {
                    mSeekBarPaint.setStrokeCap(Paint.Cap.ROUND);
                } else {
                    mSeekBarPaint.setStrokeCap(Paint.Cap.SQUARE);
                }
                canvas.drawLine(
                        mSeekBarWrapperRect.left + mSelectSeekBarRadius,
                        mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2,
                        mSeekBarWrapperRect.right - mSelectSeekBarRadius,
                        mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2,
                        mSeekBarPaint);
            }

            // draw circle and text
            mItemSpace = (mSeekBarDstRect.width()) / (size - 1);
            int itemX = 0;
            int itemY = (int) (mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2f);
            for (int i = 0; i < size; i++) {
                itemX = mSeekBarDstRect.left + i * mItemSpace;
                if (mSelectIndex == i) {
                    mCirclePaint.setStyle(Paint.Style.FILL);
                    mCirclePaint.setColor(mThumbSelectColor);
                    canvas.drawCircle(itemX, itemY, mSelectSeekBarRadius, mCirclePaint);
                    if (isShowText) {
                        mTextPaint.setColor(mTextSelectColor);
                        canvas.drawText(mDatas.get(i), itemX, itemY + mSelectSeekBarRadius + mTextHeight, mTextPaint);
                    }
                } else {
                    mCirclePaint.setStyle(Paint.Style.FILL);
                    mCirclePaint.setColor(mThumbColor);
                    canvas.drawCircle(itemX, itemY, mNormalSeekBarRadius, mCirclePaint);
                    if (isShowText) {
                        mTextPaint.setTextSize(mTextSize);
                        mTextPaint.setColor(mTextColor);
                        canvas.drawText(mDatas.get(i), itemX, itemY + mSelectSeekBarRadius + mTextHeight, mTextPaint);
                    }
                }
            }
        }
        // seekbar type is progress
        else if (mSeekbarType == SEEKBAR_TYPE.PROGRESS) {

            // get thumb position x and y
            mThumbCenterX = (int) ((mProgress - mMin) * 1.0f / mDeltaProgress *
                    (mSeekBarWrapperRect.width() - mSelectSeekBarRadius * 2) + mSeekBarDstRect.left);
            mThumbCenterY = mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2;

            // draw seekbar
            if (mSeekBarBitmap != null) {

                mSeekBarSrcRect.left = 0;
                mSeekBarSrcRect.top = 0;
                mSeekBarSrcRect.right = mSeekBarBitmap.getWidth();
                mSeekBarSrcRect.bottom = mSeekBarBitmap.getHeight();
                canvas.drawBitmap(mSeekBarBitmap, mSeekBarSrcRect, mSeekBarDstRect, null);

            } else {
                mSeekBarPaint.setStrokeWidth(mSeekbarHeight);
                mSeekBarPaint.setColor(mSeekBarFirstColor);
                if (isSeekBarRound) {
                    mSeekBarPaint.setStrokeCap(Paint.Cap.ROUND);
                } else {
                    mSeekBarPaint.setStrokeCap(Paint.Cap.SQUARE);
                }
                canvas.drawLine(
                        mSeekBarWrapperRect.left + mSelectSeekBarRadius,
                        mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2,
                        mSeekBarWrapperRect.right - mSelectSeekBarRadius,
                        mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2,
                        mSeekBarPaint);
                mSeekBarPaint.setColor(mSeekBarSecondColor);
                canvas.drawLine(
                        mSeekBarWrapperRect.left + mSelectSeekBarRadius,
                        mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2,
                        mThumbCenterX,
                        mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2,
                        mSeekBarPaint);
            }


            if (mThumbBitmap != null) {
                mThumbDstRect.left = mThumbCenterX - (isThumbPress ? mSelectSeekBarRadius : mNormalSeekBarRadius);
                mThumbDstRect.top = mThumbCenterY - (isThumbPress ? mSelectSeekBarRadius : mNormalSeekBarRadius);
                mThumbDstRect.right = mThumbCenterX + (isThumbPress ? mSelectSeekBarRadius : mNormalSeekBarRadius);
                mThumbDstRect.bottom = mThumbCenterY + (isThumbPress ? mSelectSeekBarRadius : mNormalSeekBarRadius);

                mThumbSrcRect.left = 0;
                mThumbSrcRect.top = 0;
                mThumbSrcRect.right = mThumbBitmap.getWidth();
                mThumbSrcRect.bottom = mThumbBitmap.getHeight();
                canvas.drawBitmap(mThumbBitmap, mThumbSrcRect, mThumbDstRect, null);
            } else {
                mCirclePaint.setStyle(Paint.Style.FILL);
                mCirclePaint.setColor(mThumbColor);
                canvas.drawCircle(mThumbCenterX, mThumbCenterY, isThumbPress ? mSelectSeekBarRadius : mNormalSeekBarRadius, mCirclePaint);
            }
            // draw circle and text
            if (isShowText) {
                int itemX = mThumbCenterX;
                int itemY = mSeekBarWrapperRect.top + mSeekBarWrapperRect.height() / 2;
                mTextPaint.setTextSize(mTextSize);
                mTextPaint.setColor(mTextColor);
                String mText = "";
                if (isShowFloat) {
                    mText = EasySeekBarUtil.formatFloat(mProgress);
                } else {
                    mText = EasySeekBarUtil.formatInt(mProgress);
                }
                canvas.drawText(mText, itemX, itemY + mSelectSeekBarRadius + mTextHeight, mTextPaint);
            }

            if (isAlwayShowBubble) {
                showBubble();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!this.isEnabled()) {
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (mSeekbarType == SEEKBAR_TYPE.PROGRESS) {
                    // 按在Thumb上面
                    if (isTouchInThumb(x, y)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        isThumbPress = true;
                        if (isShowBubble) {
                            showBubble();
                        }
                    } else {
                        isThumbPress = false;
                    }

                    if (isTouchInSeekbar(x, y)) {
                        if (isCanTouch) {
                            calculateThumb(x, y);
                            mProgress = (float) ((mThumbCenterX - mSeekBarDstRect.left) * 1.0 /
                                    mSeekBarDstRect.width() * 1.0 * mDeltaProgress) + mMin;
                            isThumbPress = true;
                            if (isShowBubble) {
                                showBubble();
                            }
                        }
                    }

                    if (mProgressListener != null) {
                        mProgressListener.onProgressStart(this, mProgress);
                    }
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:

                if (mSeekbarType == SEEKBAR_TYPE.PROGRESS) {
                    if (isThumbPress) {
                        calculateThumb(x, y);
                        mProgress = (float) ((mThumbCenterX - mSeekBarDstRect.left) * 1.0 /
                                mSeekBarDstRect.width() * 1.0 * mDeltaProgress) + mMin;
                        if (isShowBubble) {
                            showBubble();
                        }
                        if (mProgressListener != null) {
                            mProgressListener.onProgressChange(this, mProgress);
                        }
                    }
                }

                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (mSeekbarType == SEEKBAR_TYPE.DIY) {
                    if (isTouchInSeekbar(x, y)) {
                        for (int i = 0; i < mDatas.size(); i++) {
                            int itemLeft = mSeekBarWrapperRect.left + mItemSpace * i;
                            int itemRight = itemLeft + mSelectSeekBarRadius * 2;
                            if (x > itemLeft && x < itemRight) {
                                mSelectIndex = i;
                                if (mDiyListener != null) {
                                    mDiyListener.onDiyChange(this, mDatas.get(mSelectIndex), mSelectIndex);
                                }
                            }
                        }
                    }
                } else if (mSeekbarType == SEEKBAR_TYPE.PROGRESS) {
                    if (isThumbPress) {
                        calculateThumb(x, y);
                        mProgress = (float) ((mThumbCenterX - mSeekBarDstRect.left) * 1.0 /
                                mSeekBarDstRect.width() * 1.0 * mDeltaProgress) + mMin;
                        if (mProgressListener != null) {
                            mProgressListener.onProgressStop(this, mProgress);
                        }
                        invalidate();
                    }
                }

                isThumbPress = false;
                if (!isAlwayShowBubble) {
                    hideBubble();
                }
                break;
        }

        return true;

    }


    // is touch on the seekbar ?
    public boolean isTouchInSeekbar(float x, float y) {
        if (x < mSeekBarWrapperRect.left
                || x > mSeekBarWrapperRect.right
                || y < mSeekBarWrapperRect.top
                || y > mSeekBarWrapperRect.bottom) {
            return false;
        }
        return true;
    }

    // is touch on the thumb ?
    public boolean isTouchInThumb(float x, float y) {
        if (x < mThumbCenterX - mSelectSeekBarRadius
                || x > mThumbCenterX + mSelectSeekBarRadius
                || y < mThumbCenterY - mSelectSeekBarRadius
                || y > mThumbCenterY + mSelectSeekBarRadius) {
            return false;
        }
        return true;
    }

    // calculate Thumb position
    public void calculateThumb(float x, float y) {
        float changeX = x;
        if (x < mSeekBarWrapperRect.left + mSelectSeekBarRadius) {
            changeX = mSeekBarWrapperRect.left + mSelectSeekBarRadius;
        }

        if (x > mSeekBarWrapperRect.right - mSelectSeekBarRadius) {
            changeX = mSeekBarWrapperRect.right - mSelectSeekBarRadius;
        }
        mThumbCenterX = (int) changeX;
    }

    // reference by https://github.com/woxingxiao/BubbleSeekBar
    private class SmartInnerBubbleView extends View {

        private Paint.FontMetrics mFontMetrics;
        private RectF mBubbleCircleRectF;
        private RectF mBubbleRoundRectF;
        private Paint mBubblePaint;
        private Path mPath;
        private String mProgressText = "";

        public SmartInnerBubbleView(Context context) {
            this(context, null);
        }

        public SmartInnerBubbleView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public SmartInnerBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBubblePaint.setColor(Color.RED);

            mPath = new Path();
            mBubbleCircleRectF = new RectF();
            mBubbleRoundRectF = new RectF();
            mFontMetrics = new Paint.FontMetrics();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(mBubbleRadius * 3, mBubbleRadius * 3);

            if (mBubbleType == BUBBLE_TYPE.CIRCLE) {
                mBubbleCircleRectF.set(
                        getMeasuredWidth() / 2f - mBubbleRadius,
                        0,
                        getMeasuredWidth() / 2f + mBubbleRadius,
                        getMeasuredHeight() - mBubbleRadius);
            } else if (mBubbleType == BUBBLE_TYPE.ROUND_RECT) {
                mBubbleRoundRectF.set(
                        getMeasuredWidth() / 2f - mBubbleRadius,
                        mBubbleRadius / 2f,
                        getMeasuredWidth() / 2f + mBubbleRadius,
                        getMeasuredHeight() - mBubbleRadius / 2f);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mBubbleType == BUBBLE_TYPE.CIRCLE) {
                // set path
                mPath.reset();
                mPath.moveTo(getMeasuredWidth() / 2f, getMeasuredHeight() - mBubbleRadius / 3f);
                float finalLeftX = (float) (getMeasuredWidth() / 2f - Math.sqrt(3) / 2f * mBubbleRadius);
                float finalLeftY = 3 / 2f * mBubbleRadius;
                mPath.quadTo(finalLeftX - EasySeekBarUtil.dp2px(getContext(), 2),
                        finalLeftY - EasySeekBarUtil.dp2px(getContext(), 2), finalLeftX, finalLeftY);
                mPath.arcTo(mBubbleCircleRectF, 150, 240);
                float finalRightX = (float) (getMeasuredWidth() / 2f + Math.sqrt(3) / 2f * mBubbleRadius);
                float finalRightY = 3 / 2f * mBubbleRadius;
                mPath.quadTo(finalRightX + EasySeekBarUtil.dp2px(getContext(), 2),
                        finalRightY - EasySeekBarUtil.dp2px(getContext(), 2),
                        getMeasuredWidth() / 2f, getMeasuredHeight() - mBubbleRadius / 3f);
                mPath.close();

                mBubblePaint.setColor(mBubbleColor);
                canvas.drawPath(mPath, mBubblePaint);

                mBubblePaint.setTextSize(mBubbleTextSize);
                mBubblePaint.setColor(mBubbleTextColor);
                mBubblePaint.setTextAlign(Paint.Align.CENTER);
                mBubblePaint.getFontMetrics(mFontMetrics);
                float textY = mBubbleCircleRectF.height() / 2f +
                        (mFontMetrics.descent - mFontMetrics.ascent) / 2 - mFontMetrics.descent;
                canvas.drawText(mProgressText, getMeasuredWidth() / 2, textY, mBubblePaint);

            } else if (mBubbleType == BUBBLE_TYPE.ROUND_RECT) {
                // set path
                mPath.reset();
                mPath.moveTo(getMeasuredWidth() / 2f, getMeasuredHeight() - mBubbleRadius / 3f);
                mPath.lineTo(mBubbleRoundRectF.left + EasySeekBarUtil.dp2px(getContext(), (int) (mBubbleRadius / 2f)),
                        mBubbleRoundRectF.bottom);
                mPath.lineTo(mBubbleRoundRectF.right - EasySeekBarUtil.dp2px(getContext(), (int) (mBubbleRadius / 2f)),
                        mBubbleRoundRectF.bottom);
                mPath.close();

                mBubblePaint.setColor(mBubbleColor);
                canvas.drawPath(mPath, mBubblePaint);
                canvas.drawRoundRect(mBubbleRoundRectF,
                        EasySeekBarUtil.dp2px(getContext(), 5),
                        EasySeekBarUtil.dp2px(getContext(), 5),
                        mBubblePaint);

                mBubblePaint.setTextSize(mBubbleTextSize);
                mBubblePaint.setColor(mBubbleTextColor);
                mBubblePaint.setTextAlign(Paint.Align.CENTER);
                mBubblePaint.getFontMetrics(mFontMetrics);
                float textY = mBubbleRadius / 2f + mBubbleRoundRectF.height() / 2f +
                        (mFontMetrics.descent - mFontMetrics.ascent) / 2 - mFontMetrics.descent;
                canvas.drawText(mProgressText, getMeasuredWidth() / 2, textY, mBubblePaint);
            }
        }

        public void setBubbleText(String text) {
            this.mProgressText = text;
            invalidate();
        }

    }

    // reference by https://github.com/woxingxiao/BubbleSeekBar
    // show bubble
    private void showBubble() {
        if (!isShowBubble) {
            return;
        }

        if (mBubbleView != null && mBubbleView.getParent() != null) {

            getLocationOnScreen(mBubbleViewPoint);
            mLayoutParams.x = (int) (mBubbleViewPoint[0] + mThumbCenterX - mBubbleView.getMeasuredWidth() / 2f);
            mLayoutParams.y = (int) (mBubbleViewPoint[1] - mBubbleView.getMeasuredHeight() - mScreenState);
            mWindowManager.updateViewLayout(mBubbleView, mLayoutParams);
        } else {
            getLocationOnScreen(mBubbleViewPoint);
            mLayoutParams.x = (int) (mBubbleViewPoint[0] + mThumbCenterX - mBubbleView.getMeasuredWidth() / 2f);
            mLayoutParams.y = (int) (mBubbleViewPoint[1] - mBubbleView.getMeasuredHeight() - mScreenState);
            mWindowManager.addView(mBubbleView, mLayoutParams);
        }

        mBubbleView.setBubbleText(isShowFloat ? EasySeekBarUtil.formatFloat(mProgress) :
                EasySeekBarUtil.formatInt(mProgress));
    }

    // reference by https://github.com/woxingxiao/BubbleSeekBar
    // hint bubble
    private void hideBubble() {
        if (!isShowBubble) {
            return;
        }

        if (mBubbleView != null && mBubbleView.getParent() != null) {
            mWindowManager.removeView(mBubbleView);
        }

    }


    // set and get
    public void setItems(String... items) {
        if (items.length <= 0) {
            return;
        }
        mDatas.clear();
        mDatas.addAll(Arrays.asList(items));
        mSeekbarType = SEEKBAR_TYPE.DIY;
        requestLayout();
    }

    public void setItems(List<String> items) {
        if (items.size() <= 0) {
            return;
        }
        mDatas.clear();
        mDatas.addAll(items);
        mSeekbarType = SEEKBAR_TYPE.DIY;
        requestLayout();
    }

    public void setSeekbarHeight(int mSeekbarHeight) {
        this.mSeekbarHeight = mSeekbarHeight;
        autoInit();
    }

    public void setSpaceHeight(int mSpaceHeight) {
        this.mSpaceHeight = mSpaceHeight;
        requestLayout();
    }

    public void setBubbleRadius(int mBubbleRadius) {
        this.mBubbleRadius = mBubbleRadius;
        requestLayout();
    }

    public void setSeekBarFirstColor(int mSeekBarFirstColor) {
        this.mSeekBarFirstColor = mSeekBarFirstColor;
        postInvalidate();
    }

    public void setSeekBarSecondColor(int mSeekBarSecondColor) {
        this.mSeekBarSecondColor = mSeekBarSecondColor;
        postInvalidate();
    }

    public void setThumbColor(int mThumbColor) {
        this.mThumbColor = mThumbColor;
        postInvalidate();
    }

    public void setThumbSelectColor(int mThumbSelectColor) {
        this.mThumbSelectColor = mThumbSelectColor;
        postInvalidate();
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        postInvalidate();
    }

    public void setTextSelectColor(int mTextSelectColor) {
        this.mTextSelectColor = mTextSelectColor;
        postInvalidate();
    }

    public void setBubbleColor(int mBubbleColor) {
        this.mBubbleColor = mBubbleColor;
    }

    public void setBubbleTextColor(int mBubbleTextColor) {
        this.mBubbleTextColor = mBubbleTextColor;
    }

    public void setShowFloat(boolean showFloat) {
        isShowFloat = showFloat;
        postInvalidate();
    }

    public void setShowText(boolean showText) {
        isShowText = showText;
        postInvalidate();
    }

    public void setShowBubble(boolean showBubble) {
        isShowBubble = showBubble;
    }

    public void setAlwayShowBubble(boolean alwayShowBubble) {
        isAlwayShowBubble = alwayShowBubble;
    }

    public void setCanTouch(boolean canTouch) {
        isCanTouch = canTouch;
    }

    public void setSeekBarRound(boolean seekBarRound) {
        isSeekBarRound = seekBarRound;
        postInvalidate();
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetrics metrics = new Paint.FontMetrics();
        mTextPaint.getFontMetrics(metrics);
        mTextHeight = (int) (metrics.descent - metrics.ascent);
        postInvalidate();
    }

    public void setBubbleTextSize(int mBubbleTextSize) {
        this.mBubbleTextSize = mBubbleTextSize;
    }

    public void setSelectIndex(int mSelectIndex) {
        this.mSelectIndex = mSelectIndex;
        postInvalidate();
    }

    public void setMax(float mMax) {
        this.mMax = mMax;
        autoInit();
        postInvalidate();
    }

    public void setMin(float mMin) {
        this.mMin = mMin;
        autoInit();
        postInvalidate();
    }

    public void setProgress(float mProgress) {
        this.mProgress = mProgress;
        autoInit();
        postInvalidate();
    }

    public float getProgress() {
        return this.mProgress;
    }

    public float getMaxProgress() {
        return mMax;
    }

    public float getMinProgress() {
        return mMin;
    }
}
