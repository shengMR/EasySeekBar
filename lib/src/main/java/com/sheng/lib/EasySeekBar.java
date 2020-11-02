package com.sheng.lib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    private static final String TAG = "EasySeekBar";

    //region 缺省值
    private static final boolean DEFAULT_IS_CAN_TOUCH = true;
    private static final boolean DEFAULT_IS_ROUND = true;
    private static final boolean DEFAULT_IS_SHOW_FLOAT = false;
    private static final boolean DEFAULT_IS_SHOW_TEXT = false;
    private static final boolean DEFAULT_IS_THUMB_INNER_OFFSET = false;
    private static final boolean DEFAULT_IS_SHOW_BUBBLE = false;
    private static final boolean DEFAULT_IS_THUMB_PROGRESS_PART = false;
    private static final boolean DEFAULT_IS_DIY_AUTO_FIX = true;
    private static final boolean DEFAULT_IS_OPEN_ANIMATOR = true;
    private static final boolean DEFAULT_IS_THUMB_NO_OVER = true;
    private static final boolean DEFAULT_IS_THUMB_NO_OVER_THAN_MOVE = true;
    // bar
    private static final int DEFAULT_BAR_HEIGHT = 10;
    private static final int DEFAULT_BAR_STROKE_WIDTH = 0;
    private static final int DEFAULT_BAR_COLOR = Color.parseColor("#80DEEA");
    private static final int DEFAULT_BAR_STROKE_COLOR = Color.parseColor("#bbbfca");
    private static final int DEFAULT_BAR_SECOND_COLOR = Color.parseColor("#00ACC1");
    private static final int DEFAULT_SPACING = 3;
    // text
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#00ACC1");
    private static final int DEFAULT_TEXT_COLOR_LOW = Color.parseColor("#00ACC1");
    private static final int DEFAULT_TEXT_COLOR_HEIGHT = Color.parseColor("#00ACC1");
    private static final int DEFAULT_TEXT_SELECT_COLOR = Color.parseColor("#00ACC1");
    private static final int DEFAULT_TEXT_SIZE = 16;
    // thumb
    private static final int DEFAULT_THUMB_COLOR = Color.parseColor("#00ACC1");
    private static final int DEFAULT_THUMB_LOW_COLOR = Color.parseColor("#000000");
    private static final int DEFAULT_THUMB_HEIGHT_COLOR = Color.parseColor("#FFFFFF");
    private static final int DEFAULT_THUMB_SELECT_COLOR = Color.parseColor("#00ACC1");
    private static final int DEFAULT_THUMB_NORMAL_RADIUS = 10;
    private static final int DEFAULT_THUMB_SELECT_RADIUS = 10;
    private static final int DEFAULT_THUMB_WIDTH = -1;
    private static final int DEFAULT_THUMB_HEIGHT = -1;
    // bubble
    private static final int DEFAULT_BUBBLE_RADIUS = 10;
    private static final int DEFAULT_BUBBLE_COLOR = Color.parseColor("#00ACC1");
    private static final int DEFAULT_BUBBLE_TEXT_COLOR = Color.parseColor("#ffffff");
    private static final int DEFAULT_BUBBLE_TEXT_SIZE = 14;
    // progress
    private static final int DEFAULT_PROGRESS = 0;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_MIN = 0;
    private static final int DEFAULT_DIY_SELECT_INDEX = 0;
    private static final int DEFAULT_ANIMATOR_DURATION = 200;
    //endregion

    public static final int SEEKBAR_TYPE_SEEKBAR = 0;
    public static final int SEEKBAR_TYPE_DIY = 1;
    public static final int SEEKBAR_TYPE_PROGRESS = 2;
    public static final int SEEKBAR_TYPE_LOW_HEIGHT_THUMB = 3;

    public static final int BUBBLE_TYPE_CIRCLE = 0;
    public static final int BUBBLE_TYPE_RECT = 1;

    public static final int TOUCH_LOW_THUMB = 0;
    public static final int TOUCH_HEIGHT_THUMB = 1;
    public static final int TOUCH_NULL_THUMB = 2;

    public static final int TEXT_SHOW_UP = 0;
    public static final int TEXT_SHOW_DOWN = 1;
    public static final int TEXT_SHOW_INNER = 2;


    @IntDef({SEEKBAR_TYPE_DIY, SEEKBAR_TYPE_SEEKBAR, SEEKBAR_TYPE_PROGRESS, SEEKBAR_TYPE_LOW_HEIGHT_THUMB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SeekType {
    }

    @IntDef({BUBBLE_TYPE_CIRCLE, BUBBLE_TYPE_RECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BubbleType {
    }

    @IntDef({TOUCH_LOW_THUMB, TOUCH_HEIGHT_THUMB, TOUCH_NULL_THUMB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TouchType {
    }

    @IntDef({TEXT_SHOW_UP, TEXT_SHOW_DOWN, TEXT_SHOW_INNER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextShowType {
    }

    //region 监听器
    public interface OnLowOrHeightProgressChangeListener {

        void onLowStart(EasySeekBar easySeekBar, int progress);

        void onLowChange(EasySeekBar easySeekBar, int progress);

        void onLowStop(EasySeekBar easySeekBar, int progress);

        void onHeightStart(EasySeekBar easySeekBar, int progress);

        void onHeightChange(EasySeekBar easySeekBar, int progress);

        void onHeightStop(EasySeekBar easySeekBar, int progress);
    }

    public interface OnSeekBarProgressChangeListener {
        void onProgressStart(EasySeekBar easySeekBar, float progress);

        void onProgressChange(EasySeekBar easySeekBar, float progress);

        void onProgressStop(EasySeekBar easySeekBar, float progress);
    }

    public interface OnSeekBarDiyChangeListener {
        void onDiyChange(EasySeekBar easySeekBar, String text, int position);
    }

    public OnLowOrHeightProgressChangeListener mLowOrHeightListener;
    public OnSeekBarProgressChangeListener mProgressListener;
    public OnSeekBarDiyChangeListener mDiyListener;

    public void setSeekBarLowOrHeightListener(OnLowOrHeightProgressChangeListener listener) {
        this.mLowOrHeightListener = listener;
    }

    public void setSeekBarProgressListener(OnSeekBarProgressChangeListener listener) {
        this.mProgressListener = listener;
    }

    public void setSeekBarDiyChangeListener(OnSeekBarDiyChangeListener listener) {
        this.mDiyListener = listener;
    }
    //endregion

    // view field
    @SeekType
    private int seekType = SEEKBAR_TYPE_SEEKBAR; // bar 目前支持两种类型，一种可自定义底部文本，一种正常Seekbar
    @BubbleType
    public int bubbleType = BUBBLE_TYPE_CIRCLE; // 气泡目前支持两种，一种气泡，一种方形
    @TouchType
    public int touchType = TOUCH_NULL_THUMB;
    @TextShowType
    public int textShowType = TEXT_SHOW_DOWN;

    private boolean isShowText;
    private boolean isShowFloat;
    private boolean isCanTouch;
    private boolean isBarRound;
    private boolean isThumbInnerOffset;
    private boolean isShowBubble;
    private boolean isDiyAutoFit; // 自动贴合
    private boolean isThumbNoOver; // 两个thumb可飞跃
    private boolean isThumbNoOverThanMove; // 两个thumb可飞跃,之后继续滑动

    // bar
    private int barHeight;
    private int barColor;
    private int progressColor;
    private int barStrokeWidth;
    private int barStrokeColor;
    private Bitmap barBitmap;
    private Bitmap progressBitmap;

    // thumb
    private Thumb thumb = new Thumb();
    private Thumb thumbLow = new Thumb();
    private Thumb thumbHeight = new Thumb();
    private float itemSpace;

    // text
    private float textHeight;
    private int spacing;
    private int textColor;
    private int textColorForLow;
    private int textColorForHeight;
    private int textSelectColor;
    private int textSize;
    private TextShowHelper textShowHelper;

    // bubble
    private int bubbleRadius;
    private int bubbleColor;
    private int bubbleTextColor;
    private int bubbleTextSize;
    private WindowManager wm;
    private WindowManager.LayoutParams wmLayoutParams;
    private SmartInnerBubbleView bubbleView;
    private int[] bubbleViewPointByScreenXY;
    private int screenStateHeight;
    private BubbleViewHelper bubbleViewHelper;

    // view rect and paint
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint barXfermodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint barStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF barWrapperNoPaddingRectF;
    private RectF barDstRectF;
    private RectF progressDstRectF;
    private Bitmap srcB;
    private Bitmap destB;
    private boolean isDragging;
    // Animator
    private ValueAnimator valueAnimator;
    private ValueAnimator valueAnimatorForThumb;
    private ValueAnimator valueAnimatorForProgress;
    private boolean isOpenAnimator; // 使用动画
    private long animatorDuration;

    // view data
    private List<String> diyDatas = new ArrayList<>();
    private int diySelectIndex;
    private float diySelectIndexByAutoFit;
    private float max;
    private float min;
    private float lowMax;
    private float heightMin;
    private float progress;
    private float lowProgress;
    private float heightProgress;
    private float thumbProgress; // thumb的滑动和进度分开后，thumb的进度
    private boolean isUseThumbWAndH; // 是否使用自定义的Thumb宽高
    private boolean isThumbAndProgressPart; // thumb的滑动是否和进度分开
    private CalculateHelper calculateHelper = new CalculateHelper();


    //region 初始化
    public EasySeekBar(Context context) {
        this(context, null);
    }

    public EasySeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        initAttributes(context, attrs, defStyleAttr);
        initPaintAndRect();
        autoInit();

        // reference by https://github.com/woxingxiao/BubbleSeekBar
        if (isShowBubble) {
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wmLayoutParams = new WindowManager.LayoutParams();
            wmLayoutParams.gravity = Gravity.START | Gravity.TOP;
            wmLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            wmLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            wmLayoutParams.format = PixelFormat.TRANSLUCENT;
            wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            // MIUI禁止了开发者使用TYPE_TOAST，Android 7.1.1 对TYPE_TOAST的使用更严格
            if (EasySeekBarUtil.isMIUI() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                wmLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            } else {
                wmLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            bubbleViewPointByScreenXY = new int[2];
            bubbleView = new SmartInnerBubbleView(getContext());
            screenStateHeight = EasySeekBarUtil.getScreenState(context);
        }

    }

    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EasySeekBar, defStyleAttr, 0);

        seekType = a.getInt(R.styleable.EasySeekBar_ssb_bar_type, 0);
        bubbleType = a.getInt(R.styleable.EasySeekBar_ssb_bubble_type, 0);
        textShowType = a.getInt(R.styleable.EasySeekBar_ssb_text_show_type, 1);
        // bar
        barHeight = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_bar_height, EasySeekBarUtil.dp2px(getContext(), DEFAULT_BAR_HEIGHT));
        int seekBarImgId = a.getResourceId(R.styleable.EasySeekBar_ssb_bar_image, -1);
        if (seekBarImgId != -1) {
            barBitmap = BitmapFactory.decodeResource(getResources(), seekBarImgId);
        }
        barColor = a.getColor(R.styleable.EasySeekBar_ssb_bar_color, DEFAULT_BAR_COLOR);
        barStrokeWidth = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_bar_stroke_width, EasySeekBarUtil.dp2px(getContext(), DEFAULT_BAR_STROKE_WIDTH));
        barStrokeColor = a.getColor(R.styleable.EasySeekBar_ssb_bar_stroke_color, DEFAULT_BAR_STROKE_COLOR);
        int secondImgId = a.getResourceId(R.styleable.EasySeekBar_ssb_bar_progress_image, -1);
        if (secondImgId != -1) {
            progressBitmap = BitmapFactory.decodeResource(getResources(), secondImgId);
        }
        progressColor = a.getColor(R.styleable.EasySeekBar_ssb_bar_progress_color, DEFAULT_BAR_SECOND_COLOR);

        // thumb
        int thumbWidth = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_thumb_width, EasySeekBarUtil.dp2px(getContext(), DEFAULT_THUMB_WIDTH));
        int thumbHeight = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_thumb_height, EasySeekBarUtil.dp2px(getContext(), DEFAULT_THUMB_HEIGHT));
        int thumbRadiusForNormal = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_thumb_normal_radius, EasySeekBarUtil.dp2px(getContext(), DEFAULT_THUMB_NORMAL_RADIUS));
        int thumbRadiusForSelect = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_thumb_select_radius, EasySeekBarUtil.dp2px(getContext(), DEFAULT_THUMB_SELECT_RADIUS));
        int thumbImgId = a.getResourceId(R.styleable.EasySeekBar_ssb_thumb_image, -1);
        Bitmap thumbBitmap = null;
        if (thumbImgId != -1) {
            thumbBitmap = BitmapFactory.decodeResource(getResources(), thumbImgId);
        }
        int thumbColor = a.getColor(R.styleable.EasySeekBar_ssb_thumb_color, DEFAULT_THUMB_COLOR);
        int thumbLowColor = a.getColor(R.styleable.EasySeekBar_ssb_thumb_low_color, DEFAULT_THUMB_LOW_COLOR);
        int thumbHeightColor = a.getColor(R.styleable.EasySeekBar_ssb_thumb_height_color, DEFAULT_THUMB_HEIGHT_COLOR);
        int thumbSelectColor = a.getColor(R.styleable.EasySeekBar_ssb_thumb_select_color, DEFAULT_THUMB_SELECT_COLOR);
        if (thumbWidth > 0 && thumbHeight > 0) {
            isUseThumbWAndH = true;
        } else {
            isUseThumbWAndH = false;
        }
        thumb.thumbWidth = thumbWidth;
        thumb.thumbHeight = thumbHeight;
        thumb.thumbRadiusForNormal = thumbRadiusForNormal;
        thumb.thumbRadiusForSelect = thumbRadiusForSelect;
        thumb.thumbBitmap = thumbBitmap;
        thumb.thumbColor = thumbColor;
        thumb.thumbSelectColor = thumbSelectColor;

        thumbLow.thumbHeight = thumbHeight;
        thumbLow.thumbRadiusForNormal = thumbRadiusForNormal;
        thumbLow.thumbRadiusForSelect = thumbRadiusForSelect;
        thumbLow.thumbBitmap = thumbBitmap;
        thumbLow.thumbColor = thumbLowColor;
        thumbLow.thumbSelectColor = thumbSelectColor;

        this.thumbHeight.thumbHeight = thumbHeight;
        this.thumbHeight.thumbRadiusForNormal = thumbRadiusForNormal;
        this.thumbHeight.thumbRadiusForSelect = thumbRadiusForSelect;
        this.thumbHeight.thumbBitmap = thumbBitmap;
        this.thumbHeight.thumbColor = thumbHeightColor;
        this.thumbHeight.thumbSelectColor = thumbSelectColor;

        // text
        spacing = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_spacing, EasySeekBarUtil.dp2px(getContext(), DEFAULT_SPACING));
        textSize = a.getDimensionPixelSize(R.styleable.EasySeekBar_ssb_text_size, EasySeekBarUtil.sp2px(getContext(), DEFAULT_TEXT_SIZE));
        textColor = a.getColor(R.styleable.EasySeekBar_ssb_text_color, DEFAULT_TEXT_COLOR);
        textSelectColor = a.getColor(R.styleable.EasySeekBar_ssb_text_select_color, DEFAULT_TEXT_SELECT_COLOR);
        textColorForLow = a.getColor(R.styleable.EasySeekBar_ssb_text_color_for_low, DEFAULT_TEXT_COLOR_LOW);
        textColorForHeight = a.getColor(R.styleable.EasySeekBar_ssb_text_color_for_height, DEFAULT_TEXT_COLOR_HEIGHT);

        // bubble
        bubbleRadius = a.getDimensionPixelOffset(R.styleable.EasySeekBar_ssb_bubble_radius, EasySeekBarUtil.dp2px(getContext(), DEFAULT_BUBBLE_RADIUS));
        bubbleColor = a.getColor(R.styleable.EasySeekBar_ssb_bubble_color, DEFAULT_BUBBLE_COLOR);
        bubbleTextColor = a.getColor(R.styleable.EasySeekBar_ssb_bubble_text_color, DEFAULT_BUBBLE_TEXT_COLOR);
        bubbleTextSize = a.getDimensionPixelSize(R.styleable.EasySeekBar_ssb_bubble_text_size, EasySeekBarUtil.sp2px(getContext(), DEFAULT_BUBBLE_TEXT_SIZE));

        progress = a.getInt(R.styleable.EasySeekBar_ssb_progress, DEFAULT_PROGRESS);
        min = a.getInt(R.styleable.EasySeekBar_ssb_min, DEFAULT_MIN);
        max = a.getInt(R.styleable.EasySeekBar_ssb_max, DEFAULT_MAX);
        lowMax = a.getInt(R.styleable.EasySeekBar_ssb_low_max, (int) max);
        heightMin = a.getInt(R.styleable.EasySeekBar_ssb_height_min, (int) min);
        lowProgress = a.getInt(R.styleable.EasySeekBar_ssb_low_progress, (int) min);
        heightProgress = a.getInt(R.styleable.EasySeekBar_ssb_height_progress, (int) max);
        diySelectIndexByAutoFit = diySelectIndex = a.getInt(R.styleable.EasySeekBar_ssb_diy_select_index, DEFAULT_DIY_SELECT_INDEX);
        animatorDuration = a.getInt(R.styleable.EasySeekBar_ssb_animator_duration, DEFAULT_ANIMATOR_DURATION);

        isThumbInnerOffset = a.getBoolean(R.styleable.EasySeekBar_ssb_is_thumb_inner_offset, DEFAULT_IS_THUMB_INNER_OFFSET);
        isBarRound = a.getBoolean(R.styleable.EasySeekBar_ssb_is_bar_round, DEFAULT_IS_ROUND);
        isShowText = a.getBoolean(R.styleable.EasySeekBar_ssb_is_show_text, DEFAULT_IS_SHOW_TEXT);
        isShowFloat = a.getBoolean(R.styleable.EasySeekBar_ssb_is_show_float, DEFAULT_IS_SHOW_FLOAT);
        isShowBubble = a.getBoolean(R.styleable.EasySeekBar_ssb_is_show_bubble, DEFAULT_IS_SHOW_BUBBLE);
        isCanTouch = a.getBoolean(R.styleable.EasySeekBar_ssb_is_can_touch, DEFAULT_IS_CAN_TOUCH);
        isThumbAndProgressPart = a.getBoolean(R.styleable.EasySeekBar_ssb_is_thumb_progress_part, DEFAULT_IS_THUMB_PROGRESS_PART);
        isDiyAutoFit = a.getBoolean(R.styleable.EasySeekBar_ssb_is_diy_auto_fix, DEFAULT_IS_DIY_AUTO_FIX);
        isOpenAnimator = a.getBoolean(R.styleable.EasySeekBar_ssb_is_open_animator, DEFAULT_IS_OPEN_ANIMATOR);
        isThumbNoOver = a.getBoolean(R.styleable.EasySeekBar_ssb_is_thumb_no_over, DEFAULT_IS_THUMB_NO_OVER);
        isThumbNoOverThanMove = a.getBoolean(R.styleable.EasySeekBar_ssb_is_thumb_no_over_than_move, DEFAULT_IS_THUMB_NO_OVER_THAN_MOVE);

        a.recycle();
    }

    private void initPaintAndRect() {
        // text Paint
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        Paint.FontMetrics metrics = new Paint.FontMetrics();
        textPaint.getFontMetrics(metrics);
        textHeight = metrics.descent - metrics.ascent;

        // rect
        barWrapperNoPaddingRectF = new RectF();
        barDstRectF = new RectF();
        progressDstRectF = new RectF();
    }

    public void autoInit() {

        if (min >= max) {
            min = 0;
            max = 100;
        }

        if (progress < min) {
            progress = min;
        }

        if (progress > max) {
            progress = max;
        }

        if (lowMax > max) {
            lowMax = max;
        }

        if (heightMin < min) {
            heightMin = min;
        }


        if (isThumbInnerOffset) {
            thumb.thumbRadiusForSelect = thumb.thumbRadiusForNormal;
            thumbLow.thumbRadiusForSelect = thumbLow.thumbRadiusForNormal;
            thumbHeight.thumbRadiusForSelect = thumbHeight.thumbRadiusForNormal;
        }

        requestLayout();
    }
    //endregion

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (!isShowBubble)
            return;

        if (visibility != VISIBLE) {
            hideBubble();
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideBubble();
        super.onDetachedFromWindow();
    }

    //region 测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        if (isUseThumbWAndH) {
            if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                if (thumbLow.thumbHeight > barHeight + barStrokeWidth * 2) {
                    height += thumbLow.thumbHeight;
                } else {
                    height += barHeight + barStrokeWidth * 2;
                }
            } else {
                if (thumb.thumbHeight > barHeight + barStrokeWidth * 2) {
                    height += thumb.thumbHeight;
                } else {
                    height += barHeight + barStrokeWidth * 2;
                }
            }
        } else {
            if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                if (thumbLow.thumbRadiusForSelect * 2 > barHeight + barStrokeWidth * 2) {
                    height += thumbLow.thumbRadiusForSelect * 2;
                } else {
                    height += barHeight + barStrokeWidth * 2;
                }
            } else {
                if (thumb.thumbRadiusForSelect * 2 > barHeight + barStrokeWidth * 2) {
                    height += thumb.thumbRadiusForSelect * 2;
                } else {
                    height += barHeight + barStrokeWidth * 2;
                }
            }
        }

        if (isShowText) {
            if (textShowType != TEXT_SHOW_INNER) {
                height += spacing + textHeight;
            }
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);

        if (isShowBubble) {
            bubbleView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // Thumb + Bar
            barWrapperNoPaddingRectF.left = getPaddingLeft();
            barWrapperNoPaddingRectF.top = getPaddingTop();
            barWrapperNoPaddingRectF.right = getMeasuredWidth() - getPaddingRight();
            if (isShowText) {
                if (textShowType == TEXT_SHOW_UP) {
                    barWrapperNoPaddingRectF.left = getPaddingLeft();
                    barWrapperNoPaddingRectF.top = getPaddingTop() + textHeight + spacing;
                    barWrapperNoPaddingRectF.right = getMeasuredWidth() - getPaddingRight();
                    barWrapperNoPaddingRectF.bottom = getMeasuredHeight() - getPaddingBottom();
                } else if (textShowType == TEXT_SHOW_DOWN) {
                    barWrapperNoPaddingRectF.bottom = getMeasuredHeight() - getPaddingBottom() - textHeight - spacing;
                } else {
                    barWrapperNoPaddingRectF.bottom = getMeasuredHeight() - getPaddingBottom();
                }
            } else {
                barWrapperNoPaddingRectF.bottom = getMeasuredHeight() - getPaddingBottom();
            }
            // Bar
            float halfStrokeWidth = barStrokeWidth / 2f;
            if (isThumbInnerOffset) {
                if (isUseThumbWAndH) {
                    barDstRectF.left = barWrapperNoPaddingRectF.left + halfStrokeWidth;
                    barDstRectF.top = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f - barHeight / 2f - halfStrokeWidth;
                    barDstRectF.right = barWrapperNoPaddingRectF.right - halfStrokeWidth;
                    barDstRectF.bottom = barDstRectF.top + barHeight + barStrokeWidth;
                    calculateHelper.barDistance = barDstRectF.width() - barStrokeWidth - thumb.thumbWidth;
                    calculateHelper.barLeft = barDstRectF.left + halfStrokeWidth + thumb.thumbWidth / 2f;
                    calculateHelper.barRight = calculateHelper.barLeft + calculateHelper.barDistance;
                } else {
                    barDstRectF.left = barWrapperNoPaddingRectF.left + halfStrokeWidth;
                    barDstRectF.top = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f - barHeight / 2f - halfStrokeWidth;
                    barDstRectF.right = barWrapperNoPaddingRectF.right - halfStrokeWidth;
                    barDstRectF.bottom = barDstRectF.top + barHeight + barStrokeWidth;
                    calculateHelper.barDistance = barDstRectF.width() - barStrokeWidth - thumb.thumbRadiusForNormal * 2;
                    calculateHelper.barLeft = barDstRectF.left + halfStrokeWidth + thumb.thumbRadiusForNormal;
                    calculateHelper.barRight = calculateHelper.barLeft + calculateHelper.barDistance;
                }
            } else {
                if (isUseThumbWAndH) {
                    if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                        barDstRectF.left = barWrapperNoPaddingRectF.left + thumbLow.thumbWidth / 2f - halfStrokeWidth;
                        barDstRectF.top = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f - barHeight / 2f - halfStrokeWidth;
                        barDstRectF.right = barWrapperNoPaddingRectF.right - thumbLow.thumbWidth / 2f + halfStrokeWidth;
                        barDstRectF.bottom = barDstRectF.top + barHeight + barStrokeWidth;
                        calculateHelper.barDistance = barDstRectF.width() - barStrokeWidth;
                        calculateHelper.barLeft = barDstRectF.left + halfStrokeWidth;
                        calculateHelper.barRight = calculateHelper.barLeft + calculateHelper.barDistance;
                    } else {
                        barDstRectF.left = barWrapperNoPaddingRectF.left + thumb.thumbRadiusForNormal - halfStrokeWidth;
                        barDstRectF.top = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f - barHeight / 2f - halfStrokeWidth;
                        barDstRectF.right = barWrapperNoPaddingRectF.right - thumb.thumbRadiusForNormal + halfStrokeWidth;
                        barDstRectF.bottom = barDstRectF.top + barHeight + barStrokeWidth;
                        calculateHelper.barDistance = barDstRectF.width() - barStrokeWidth;
                        calculateHelper.barLeft = barDstRectF.left + halfStrokeWidth;
                        calculateHelper.barRight = calculateHelper.barLeft + calculateHelper.barDistance;
                    }
                } else {
                    if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                        barDstRectF.left = barWrapperNoPaddingRectF.left + thumbLow.thumbRadiusForSelect - halfStrokeWidth;
                        barDstRectF.top = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f - barHeight / 2f - halfStrokeWidth;
                        barDstRectF.right = barWrapperNoPaddingRectF.right - thumbLow.thumbRadiusForSelect + halfStrokeWidth;
                        barDstRectF.bottom = barDstRectF.top + barHeight + barStrokeWidth;
                        calculateHelper.barDistance = barDstRectF.width() - barStrokeWidth;
                        calculateHelper.barLeft = barDstRectF.left + halfStrokeWidth;
                        calculateHelper.barRight = calculateHelper.barLeft + calculateHelper.barDistance;
                    } else {
                        barDstRectF.left = barWrapperNoPaddingRectF.left + thumb.thumbRadiusForSelect - halfStrokeWidth;
                        barDstRectF.top = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f - barHeight / 2f - halfStrokeWidth;
                        barDstRectF.right = barWrapperNoPaddingRectF.right - thumb.thumbRadiusForSelect + halfStrokeWidth;
                        barDstRectF.bottom = barDstRectF.top + barHeight + barStrokeWidth;
                        calculateHelper.barDistance = barDstRectF.width() - barStrokeWidth;
                        calculateHelper.barLeft = barDstRectF.left + halfStrokeWidth;
                        calculateHelper.barRight = calculateHelper.barLeft + calculateHelper.barDistance;
                    }
                }
            }
            progressDstRectF.left = barDstRectF.left;
            progressDstRectF.top = barDstRectF.top;
            progressDstRectF.bottom = barDstRectF.bottom;

            if (seekType == SEEKBAR_TYPE_DIY) {
                int splitCount = diyDatas.size();
                if (splitCount > 1) {
                    itemSpace = calculateHelper.barDistance / (splitCount - 1);
                    thumb.thumbCenterX = calculateHelper.barLeft + diySelectIndex * itemSpace;
                    progressDstRectF.right = thumb.thumbCenterX;
                    thumb.thumbCenterY = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f;
                }
            } else {
                if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                    // 初始化ThumbXY
                    float delta = max - min;
                    thumbLow.thumbCenterX = (lowProgress - min) * 1.0f / delta * calculateHelper.barDistance + calculateHelper.barLeft;
                    thumbLow.thumbCenterY = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f;
                    thumbHeight.thumbCenterX = (heightProgress - min) * 1.0f / delta * calculateHelper.barDistance + calculateHelper.barLeft;
                    thumbHeight.thumbCenterY = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f;
                    calculateHelper.lowMaxRight = (lowMax - min) * 1.0f / delta * calculateHelper.barDistance + calculateHelper.barLeft;
                    calculateHelper.heightMinLeft = (heightMin - min) * 1.0f / delta * calculateHelper.barDistance + calculateHelper.barLeft;
                } else {
                    // 初始化ThumbXY
                    float delta = max - min;
                    if (isThumbAndProgressPart) {
                        thumb.thumbCenterX = (thumbProgress - min) * 1.0f / delta * calculateHelper.barDistance + calculateHelper.barLeft;
                        thumb.thumbCenterY = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f;
                        progressDstRectF.right = (progress - min) * 1.0f / delta * calculateHelper.barDistance + calculateHelper.barLeft;
                    } else {
                        thumb.thumbCenterX = (progress - min) * 1.0f / delta * calculateHelper.barDistance + calculateHelper.barLeft;
                        thumb.thumbCenterY = barWrapperNoPaddingRectF.top + barWrapperNoPaddingRectF.height() / 2f;
                        progressDstRectF.right = thumb.thumbCenterX;
                    }
                }
            }
        }
    }
    //endregion

    //region 绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (seekType == SEEKBAR_TYPE_DIY) {  // 自定义
            drawDirType(canvas);
        } else if (seekType == SEEKBAR_TYPE_SEEKBAR) { // 普通的Seekbar
            drawSeekBarType(canvas);
        } else if (seekType == SEEKBAR_TYPE_PROGRESS) {
            drawProgressType(canvas);
        } else if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            drawLowHeightType(canvas);
        }
    }

    private void drawLowHeightType(Canvas canvas) {
        // 1,绘制背景色，以及进度色
        drawBar(canvas);

        // 2,绘制边框
        drawStroke(canvas);

        // 3,绘制滑块
        drawThumb(canvas);

        // 4,绘制文本
        drawText(canvas);
    }

    //region 绘制ProgressBar
    private void drawProgressType(Canvas canvas) {
        // 1,绘制背景色，以及进度色
        makeDestBitmap();
        makeSrcBitmap();
        drawBar(canvas);
        drawSecondBar(canvas);

        // 2,绘制边框
        drawStroke(canvas);
    }
    //endregion

    //region 绘制正常SeekBar
    private void drawSeekBarType(Canvas canvas) {

        // 1,绘制背景色，以及进度色
        makeDestBitmap();
        makeSrcBitmap();
        drawBar(canvas);
        drawSecondBar(canvas);

        // 2,绘制边框
        drawStroke(canvas);

        // 3,绘制滑块
        drawThumb(canvas);

        // 4,绘制文本
        drawText(canvas);
    }

    private void makeDestBitmap() {
        Bitmap destBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas destCanvas = new Canvas(destBitmap);
        if (progressBitmap != null) {
            destCanvas.drawBitmap(progressBitmap, null, barDstRectF, null);
        } else {
            Paint srcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            srcPaint.setStyle(Paint.Style.FILL);
            srcPaint.setColor(progressColor);
            if (isBarRound) {
                destCanvas.drawRoundRect(
                        barDstRectF,
                        barDstRectF.height() / 2f,
                        barDstRectF.height() / 2f,
                        srcPaint
                );
            } else {
                destCanvas.drawRect(
                        barDstRectF,
                        srcPaint
                );
            }
        }
        if (destB != null) {
            destB.recycle();
            destB = null;
        }
        destB = destBitmap;
    }

    private void makeSrcBitmap() {
        Bitmap srcBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas srcCanvas = new Canvas(srcBitmap);
        Paint destPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        destPaint.setStyle(Paint.Style.FILL);
        destPaint.setColor(Color.WHITE);
        if (seekType == SEEKBAR_TYPE_PROGRESS) {
            srcCanvas.drawRoundRect(
                    progressDstRectF,
                    progressDstRectF.height() / 2f,
                    progressDstRectF.height() / 2f,
                    destPaint
            );
        } else {
            srcCanvas.drawRect(
                    progressDstRectF,
                    destPaint
            );
        }
        if (srcB != null) {
            srcB.recycle();
            srcB = null;
        }
        srcB = srcBitmap;
    }

    private void drawBar(Canvas canvas) {
        if (barBitmap != null) {
            canvas.drawBitmap(barBitmap, null, barDstRectF, null);
        } else {
            Paint srcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            srcPaint.setStyle(Paint.Style.FILL);
            srcPaint.setColor(barColor);
            if (isBarRound) {
                canvas.drawRoundRect(
                        barDstRectF,
                        barDstRectF.height() / 2f,
                        barDstRectF.height() / 2f,
                        srcPaint
                );
            } else {
                canvas.drawRect(
                        barDstRectF,
                        srcPaint
                );
            }
        }
    }

    private void drawSecondBar(Canvas canvas) {
        barXfermodePaint.setColor(Color.WHITE);
        int sc = canvas.saveLayer(
                0F,
                0F,
                getWidth(),
                getHeight(),
                null,
                Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(destB, 0f, 0f, barXfermodePaint);
        barXfermodePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(srcB, 0f, 0f, barXfermodePaint);
        barXfermodePaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    private void drawStroke(Canvas canvas) {
        if (barStrokeWidth != 0) {
            barStrokePaint.setColor(barStrokeColor);
            barStrokePaint.setStyle(Paint.Style.STROKE);
            barStrokePaint.setStrokeWidth(barStrokeWidth);

            if (isBarRound) {
                canvas.drawRoundRect(
                        barDstRectF,
                        barDstRectF.height() / 2f,
                        barDstRectF.height() / 2f,
                        barStrokePaint
                );
            } else {
                canvas.drawRect(
                        barDstRectF,
                        barStrokePaint
                );
            }
        }
    }

    private void drawThumb(Canvas canvas) {
        if (seekType == SEEKBAR_TYPE_SEEKBAR) {
            if (thumb.thumbBitmap != null) {
                canvas.drawBitmap(thumb.thumbBitmap, null, thumb.thumbDstRectF(), null);
            } else {
                thumbPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                thumbPaint.setColor(thumb.thumbColor);
                if (isThumbInnerOffset) {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            canvas.drawRoundRect(thumb.thumbDstRectF(), thumb.thumbDstRectF().width() / 2, thumb.thumbDstRectF().width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(thumb.thumbCenterX, thumb.thumbCenterY, thumb.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        canvas.drawRect(thumb.thumbDstRectF(), thumbPaint);
                    }
                } else {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            canvas.drawRoundRect(thumb.thumbDstRectF(), thumb.thumbDstRectF().width() / 2, thumb.thumbDstRectF().width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(thumb.thumbCenterX, thumb.thumbCenterY, isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        canvas.drawRect(thumb.thumbDstRectF(), thumbPaint);
                    }
                }
            }
        } else if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            if (thumbLow.thumbBitmap != null) {
                canvas.drawBitmap(thumbLow.thumbBitmap, null, thumbLow.thumbDstRectF(), null);
            } else {
                thumbPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                thumbPaint.setColor(thumbLow.thumbColor);
                if (isThumbInnerOffset) {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            canvas.drawRoundRect(thumbLow.thumbDstRectF(), thumbLow.thumbDstRectF().width() / 2, thumbLow.thumbDstRectF().width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(thumbLow.thumbCenterX, thumbLow.thumbCenterY, thumbLow.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        canvas.drawRect(thumbLow.thumbDstRectF(), thumbPaint);
                    }
                } else {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            canvas.drawRoundRect(thumbLow.thumbDstRectF(), thumbLow.thumbDstRectF().width() / 2, thumbLow.thumbDstRectF().width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(thumbLow.thumbCenterX, thumbLow.thumbCenterY, isDragging ? thumbLow.thumbRadiusForSelect : thumbLow.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        canvas.drawRect(thumbLow.thumbDstRectF(), thumbPaint);
                    }
                }
            }

            if (thumbHeight.thumbBitmap != null) {
                canvas.drawBitmap(thumbHeight.thumbBitmap, null, thumbHeight.thumbDstRectF(), null);
            } else {
                thumbPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                thumbPaint.setColor(thumbHeight.thumbColor);
                if (isThumbInnerOffset) {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            canvas.drawRoundRect(thumbHeight.thumbDstRectF(), thumbHeight.thumbDstRectF().width() / 2, thumbHeight.thumbDstRectF().width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(thumbHeight.thumbCenterX, thumbHeight.thumbCenterY, thumbHeight.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        canvas.drawRect(thumbHeight.thumbDstRectF(), thumbPaint);
                    }
                } else {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            canvas.drawRoundRect(thumbHeight.thumbDstRectF(), thumbHeight.thumbDstRectF().width() / 2, thumbHeight.thumbDstRectF().width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(thumbHeight.thumbCenterX, thumbHeight.thumbCenterY, isDragging ? thumbHeight.thumbRadiusForSelect : thumbHeight.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        canvas.drawRect(thumbHeight.thumbDstRectF(), thumbPaint);
                    }
                }
            }
        }
    }

    private void drawText(Canvas canvas) {
        // 跟随Thumb移动的文本
        if (isShowText) {
            if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                // Low
                textPaint.setTextSize(textSize);
                textPaint.setColor(textColorForLow);
                Paint.FontMetrics metrics = new Paint.FontMetrics();
                textPaint.getFontMetrics(metrics);
                float itemX = thumbLow.thumbCenterX;
                float itemY;
                if (textShowType == TEXT_SHOW_UP) {
                    itemY = barWrapperNoPaddingRectF.top - spacing - metrics.descent;
                } else if (textShowType == TEXT_SHOW_DOWN) {
                    itemY = barWrapperNoPaddingRectF.bottom + spacing + textHeight - metrics.descent;
                } else {
                    itemY = thumbLow.thumbCenterY + textHeight / 2f - metrics.descent;
                }
                String text = "";
                if (textShowHelper != null) {
                    text = textShowHelper.getTextByProgress((int) lowProgress);
                    if (TextUtils.isEmpty(text)) {
                        text = "";
                    }
                } else {
                    if (isShowFloat) {
                        text = EasySeekBarUtil.formatFloat(lowProgress);
                    } else {
                        text = EasySeekBarUtil.formatInt(lowProgress);
                    }
                }
                canvas.drawText(text, itemX, itemY, textPaint);

                // Height
                textPaint.setTextSize(textSize);
                textPaint.setColor(textColorForHeight);
                textPaint.getFontMetrics(metrics);
                itemX = thumbHeight.thumbCenterX;
                if (textShowType == TEXT_SHOW_UP) {
                    itemY = barWrapperNoPaddingRectF.top - spacing - metrics.descent;
                } else if (textShowType == TEXT_SHOW_DOWN) {
                    itemY = barWrapperNoPaddingRectF.bottom + spacing + textHeight - metrics.descent;
                } else {
                    itemY = thumbHeight.thumbCenterY + textHeight / 2f - metrics.descent;
                }
                text = "";
                if (textShowHelper != null) {
                    text = textShowHelper.getTextByProgress((int) heightProgress);
                    if (TextUtils.isEmpty(text)) {
                        text = "";
                    }
                } else {
                    if (isShowFloat) {
                        text = EasySeekBarUtil.formatFloat(heightProgress);
                    } else {
                        text = EasySeekBarUtil.formatInt(heightProgress);
                    }
                }
                canvas.drawText(text, itemX, itemY, textPaint);
            } else {
                textPaint.setTextSize(textSize);
                textPaint.setColor(textColor);
                Paint.FontMetrics metrics = new Paint.FontMetrics();
                textPaint.getFontMetrics(metrics);
                float itemX = thumb.thumbCenterX;
                float itemY;
                if (textShowType == TEXT_SHOW_UP) {
                    itemY = barWrapperNoPaddingRectF.top - spacing - metrics.descent;
                } else if (textShowType == TEXT_SHOW_DOWN) {
                    itemY = barWrapperNoPaddingRectF.bottom + spacing + textHeight - metrics.descent;
                } else {
                    itemY = thumb.thumbCenterY + textHeight / 2f - metrics.descent;
                }
                String text = "";
                if (textShowHelper != null) {
                    text = textShowHelper.getTextByProgress((int) progress);

                    if (TextUtils.isEmpty(text)) {
                        text = "";
                    }
                } else {
                    if (isShowFloat) {
                        text = EasySeekBarUtil.formatFloat(progress);
                    } else {
                        text = EasySeekBarUtil.formatInt(progress);
                    }
                }
                canvas.drawText(text, itemX, itemY, textPaint);
            }
        }

    }
    //endregion

    //region 绘制自定义Bar
    private void drawDirType(Canvas canvas) {

        if (diyDatas.size() <= 1) {
            return;
        }

        // 1,绘制背景色，以及进度色
        makeDestBitmap();
        makeSrcBitmap();
        drawBar(canvas);
        drawSecondBar(canvas);

        // 2,绘制边框
        drawStroke(canvas);

        // 3,绘制分割块
        drawSplitBlock(canvas);
    }

    private void drawSplitBlock(Canvas canvas) {
        // 绘制分割的进度
        int splitCount = diyDatas.size();
        if (isThumbInnerOffset) {
            if (isUseThumbWAndH) {
                itemSpace = (barDstRectF.width() - barStrokeWidth - thumb.thumbWidth) - 1.0f / (splitCount - 1);
            } else {
                itemSpace = (barDstRectF.width() - barStrokeWidth - thumb.thumbRadiusForNormal) - 1.0f / (splitCount - 1);
            }
        } else {
            itemSpace = barDstRectF.width() * 1.0f / (splitCount - 1);
        }
        float itemX = 0;
        float itemY = thumb.thumbCenterY;
        for (int i = 0; i < splitCount; i++) {
            itemX = barDstRectF.left + i * itemSpace;
            if (thumb.thumbBitmap != null) {
                if (isUseThumbWAndH) {
                    thumb.thumbDstRectF.left = itemX - thumb.thumbWidth / 2f;
                    thumb.thumbDstRectF.top = thumb.thumbCenterY - thumb.thumbHeight / 2f;
                    thumb.thumbDstRectF.right = itemX + thumb.thumbWidth / 2f;
                    thumb.thumbDstRectF.bottom = thumb.thumbCenterY + thumb.thumbHeight / 2f;
                } else {
                    thumb.thumbDstRectF.left = itemX - (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                    thumb.thumbDstRectF.top = thumb.thumbCenterY - (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                    thumb.thumbDstRectF.right = itemX + (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                    thumb.thumbDstRectF.bottom = thumb.thumbCenterY + (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                }
                canvas.drawBitmap(thumb.thumbBitmap, null, thumb.thumbDstRectF, null);
            } else {
                thumbPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (isDiyAutoFit) {
                    if (i <= diySelectIndexByAutoFit) {
                        thumbPaint.setColor(thumb.thumbSelectColor);
                    } else {
                        thumbPaint.setColor(thumb.thumbColor);
                    }
                } else {
                    if (i <= diySelectIndex) {
                        thumbPaint.setColor(thumb.thumbSelectColor);
                    } else {
                        thumbPaint.setColor(thumb.thumbColor);
                    }
                }

                if (isThumbInnerOffset) {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            thumb.thumbDstRectF.left = itemX - thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.top = thumb.thumbCenterY - thumb.thumbHeight / 2f;
                            thumb.thumbDstRectF.right = itemX + thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.bottom = thumb.thumbCenterY + thumb.thumbHeight / 2f;
                            canvas.drawRoundRect(thumb.thumbDstRectF, thumb.thumbDstRectF.width() / 2, thumb.thumbDstRectF.width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(itemX, thumb.thumbCenterY, thumb.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        if (isUseThumbWAndH) {
                            thumb.thumbDstRectF.left = itemX - thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.top = thumb.thumbCenterY - thumb.thumbHeight / 2f;
                            thumb.thumbDstRectF.right = itemX + thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.bottom = thumb.thumbCenterY + thumb.thumbHeight / 2f;
                        } else {
                            thumb.thumbDstRectF.left = itemX - thumb.thumbRadiusForNormal;
                            thumb.thumbDstRectF.top = thumb.thumbCenterY - thumb.thumbRadiusForNormal;
                            thumb.thumbDstRectF.right = itemX + thumb.thumbRadiusForNormal;
                            thumb.thumbDstRectF.bottom = thumb.thumbCenterY + thumb.thumbRadiusForNormal;
                        }
                        canvas.drawRect(thumb.thumbDstRectF, thumbPaint);
                    }
                } else {
                    if (isBarRound) {
                        if (isUseThumbWAndH) {
                            thumb.thumbDstRectF.left = itemX - thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.top = thumb.thumbCenterY - thumb.thumbHeight / 2f;
                            thumb.thumbDstRectF.right = itemX + thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.bottom = thumb.thumbCenterY + thumb.thumbHeight / 2f;
                            canvas.drawRoundRect(thumb.thumbDstRectF, thumb.thumbDstRectF.width() / 2, thumb.thumbDstRectF.width() / 2, thumbPaint);
                        } else {
                            canvas.drawCircle(itemX, thumb.thumbCenterY, isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal, thumbPaint);
                        }
                    } else {
                        if (isUseThumbWAndH) {
                            thumb.thumbDstRectF.left = itemX - thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.top = thumb.thumbCenterY - thumb.thumbHeight / 2f;
                            thumb.thumbDstRectF.right = itemX + thumb.thumbWidth / 2f;
                            thumb.thumbDstRectF.bottom = thumb.thumbCenterY + thumb.thumbHeight / 2f;
                        } else {
                            thumb.thumbDstRectF.left = itemX - (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                            thumb.thumbDstRectF.top = thumb.thumbCenterY - (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                            thumb.thumbDstRectF.right = itemX + (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                            thumb.thumbDstRectF.bottom = thumb.thumbCenterY + (isDragging ? thumb.thumbRadiusForSelect : thumb.thumbRadiusForNormal);
                        }
                        canvas.drawRect(thumb.thumbDstRectF, thumbPaint);
                    }
                }
            }
            if (isShowText) {
                if (isDiyAutoFit) {
                    if (i <= diySelectIndexByAutoFit) {
                        textPaint.setColor(textSelectColor);
                    } else {
                        textPaint.setColor(textColor);
                    }
                } else {
                    if (i <= diySelectIndex) {
                        textPaint.setColor(textSelectColor);
                    } else {
                        textPaint.setColor(textColor);
                    }
                }
                Paint.FontMetrics metrics = new Paint.FontMetrics();
                textPaint.getFontMetrics(metrics);
                float textY;
                if (textShowType == TEXT_SHOW_UP) {
                    textY = itemY - thumb.thumbRadiusForSelect - spacing - metrics.descent;
                } else {
                    textY = itemY + thumb.thumbRadiusForSelect + spacing + textHeight - metrics.descent;
                }
                canvas.drawText(diyDatas.get(i), itemX, textY, textPaint);
            }
        }
    }
    //endregion

    //endregion

    //region 事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!this.isEnabled()) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // 事件：两种方式执行：1，可触摸Bar任意位置都触发/2，必须按在Thumb上面
                boolean active = seekType != SEEKBAR_TYPE_LOW_HEIGHT_THUMB && isCanTouch && isTouchInBar(event)
                        || seekType != SEEKBAR_TYPE_LOW_HEIGHT_THUMB && isTouchInThumb(event)
                        || seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB && (touchType = isTouchInLowOrHeightThumb(event)) != TOUCH_NULL_THUMB;
                if (active) {
                    if (seekType == SEEKBAR_TYPE_DIY) {
                        onStartTrackingTouch();
                        attemptClaimDrag();
                    } else if (seekType == SEEKBAR_TYPE_SEEKBAR) {
                        repairThumbRange(event);
                        startDrag(event);
                        if (isShowBubble) {
                            showBubble();
                        }
                    } else if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                        startDrag(event);
                    }
                }
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (seekType == SEEKBAR_TYPE_DIY) {
                    if (isDragging) {
                        repairThumbRange(event);
                        trackTouchEvent(event);
                        for (int i = 0; i < diyDatas.size(); i++) {
                            if (isThumbInnerOffset) {
                                if (isUseThumbWAndH) {
                                    float itemLeft = barDstRectF.left + barStrokeWidth / 2f + itemSpace * i - .5f;
                                    float itemRight = itemLeft + itemSpace;
                                    if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                        if (isDiyAutoFit) {
                                            float delta = (thumb.thumbCenterX - (barDstRectF.left + barStrokeWidth / 2f + thumb.thumbWidth / 2f + i * itemSpace)) / itemSpace;
                                            diySelectIndex = i;
                                            diySelectIndexByAutoFit = diySelectIndex + delta;
                                        } else {
                                            diySelectIndexByAutoFit = diySelectIndex = i;
                                        }
                                        break;
                                    }
                                } else {
                                    float itemLeft = barDstRectF.left + barStrokeWidth / 2f + itemSpace * i - .5f;
                                    float itemRight = itemLeft + itemSpace;
                                    if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                        if (isDiyAutoFit) {
                                            float delta = (thumb.thumbCenterX - (barDstRectF.left + barStrokeWidth / 2f + thumb.thumbRadiusForNormal / 2f + i * itemSpace)) / itemSpace;
                                            diySelectIndex = i;
                                            diySelectIndexByAutoFit = diySelectIndex + delta;
                                        } else {
                                            diySelectIndexByAutoFit = diySelectIndex = i;
                                        }
                                        break;
                                    }
                                }
                            } else {
                                float itemLeft = barDstRectF.left - thumb.thumbRadiusForSelect + itemSpace * i - .5f;
                                float itemRight = itemLeft + itemSpace;
                                if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                    if (isDiyAutoFit) {
                                        diySelectIndex = i;
                                        float delta = (thumb.thumbCenterX - (barDstRectF.left + i * itemSpace)) / itemSpace;
                                        diySelectIndexByAutoFit = diySelectIndex + delta;
                                    } else {
                                        diySelectIndexByAutoFit = diySelectIndex = i;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else if (seekType == SEEKBAR_TYPE_SEEKBAR) {
                    if (isDragging) {
                        repairThumbRange(event);
                        trackTouchEvent(event);
                        if (isShowBubble) {
                            showBubble();
                        }
                        if (mProgressListener != null) {
                            mProgressListener.onProgressChange(this, progress);
                        }
                    }
                } else if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                    if (isDragging) {
                        repairThumbRange(event);
                        trackTouchEvent(event);
                        if (touchType == TOUCH_LOW_THUMB) {
                            if (mLowOrHeightListener != null) {
                                mLowOrHeightListener.onLowChange(this, (int) lowProgress);
                            }
                        } else {
                            if (mLowOrHeightListener != null) {
                                mLowOrHeightListener.onHeightChange(this, (int) heightProgress);
                            }
                        }
                    }
                }

                invalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                // 自定义SeekBar
                if (seekType == SEEKBAR_TYPE_DIY) {
                    if (isDragging) {
                        repairThumbRange(event);
                        trackTouchEvent(event);
                        if (isDiyAutoFit) {
                            for (int i = 0; i < diyDatas.size(); i++) {
                                if (isThumbInnerOffset) {
                                    if (isUseThumbWAndH) {
                                        float itemMiddle = calculateHelper.barLeft + itemSpace * i - .5f;
                                        float itemLeft = itemMiddle - itemSpace / 2f;
                                        float itemRight = itemMiddle + itemSpace / 2f;
                                        if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                            diySelectIndex = i;
                                            if (mDiyListener != null) {
                                                mDiyListener.onDiyChange(this, diyDatas.get(diySelectIndex), diySelectIndex);
                                            }
                                            break;
                                        }
                                    } else {
                                        float itemMiddle = calculateHelper.barLeft + itemSpace * i - .5f;
                                        float itemLeft = itemMiddle - itemSpace / 2f;
                                        float itemRight = itemMiddle + itemSpace / 2f;

                                        if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                            diySelectIndex = i;
                                            if (mDiyListener != null) {
                                                mDiyListener.onDiyChange(this, diyDatas.get(diySelectIndex), diySelectIndex);
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    float itemMiddle = calculateHelper.barLeft + itemSpace * i - .5f;
                                    float itemLeft = itemMiddle - itemSpace / 2f;
                                    float itemRight = itemMiddle + itemSpace / 2f;
                                    if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                        diySelectIndex = i;
                                        if (mDiyListener != null) {
                                            mDiyListener.onDiyChange(this, diyDatas.get(diySelectIndex), diySelectIndex);
                                        }
                                        break;
                                    }
                                }
                            }

                            if (isOpenAnimator) {
                                if (valueAnimator != null) {
                                    valueAnimator.cancel();
                                    valueAnimator = null;
                                }

                                valueAnimator = ValueAnimator.ofFloat(diySelectIndexByAutoFit, diySelectIndex);
                                valueAnimator.setDuration(animatorDuration);
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        float animatedValue = (float) animation.getAnimatedValue();
                                        diySelectIndexByAutoFit = animatedValue;
                                        if (isThumbInnerOffset) {
                                            thumb.thumbCenterX = calculateHelper.barLeft + diySelectIndexByAutoFit * itemSpace;
                                        } else {
                                            thumb.thumbCenterX = calculateHelper.barLeft + diySelectIndexByAutoFit * itemSpace;
                                        }
                                        progressDstRectF.right = thumb.thumbCenterX;
                                        invalidate();
                                    }
                                });
                                valueAnimator.start();
                            } else {
                                diySelectIndexByAutoFit = diySelectIndex;
                                if (isThumbInnerOffset) {
                                    thumb.thumbCenterX = calculateHelper.barLeft + diySelectIndexByAutoFit * itemSpace;
                                } else {
                                    thumb.thumbCenterX = calculateHelper.barLeft + diySelectIndexByAutoFit * itemSpace;
                                }
                                progressDstRectF.right = thumb.thumbCenterX;
                            }

                        } else {
                            for (int i = 0; i < diyDatas.size(); i++) {
                                if (isThumbInnerOffset) {
                                    if (isUseThumbWAndH) {
                                        float itemLeft = calculateHelper.barLeft + itemSpace * i - .5f;
                                        float itemRight = itemLeft + itemSpace;
                                        if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                            diySelectIndex = i;
                                            if (mDiyListener != null) {
                                                mDiyListener.onDiyChange(this, diyDatas.get(diySelectIndex), diySelectIndex);
                                            }
                                            break;
                                        }
                                    } else {
                                        float itemLeft = calculateHelper.barLeft + itemSpace * i - .5f;
                                        float itemRight = itemLeft + itemSpace;
                                        if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                            diySelectIndex = i;
                                            if (mDiyListener != null) {
                                                mDiyListener.onDiyChange(this, diyDatas.get(diySelectIndex), diySelectIndex);
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    float itemLeft = calculateHelper.barLeft + itemSpace * i - .5f;
                                    float itemRight = itemLeft + itemSpace;
                                    if (thumb.thumbCenterX >= itemLeft && thumb.thumbCenterX <= itemRight) {
                                        diySelectIndex = i;
                                        if (mDiyListener != null) {
                                            mDiyListener.onDiyChange(this, diyDatas.get(diySelectIndex), diySelectIndex);
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        onStopTrackingTouch();
                        invalidate();
                    }
                }
                // 正常SeekBar
                else if (seekType == SEEKBAR_TYPE_SEEKBAR) {
                    if (isDragging) {
                        repairThumbRange(event);
                        trackTouchEvent(event);
                        onStopTrackingTouch();
                        invalidate();
                    }
                } else if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                    if (isDragging) {
                        repairThumbRange(event);
                        trackTouchEvent(event);
                        onStopTrackingTouch();
                        invalidate();
                    }
                }
                if (isShowBubble) {
                    hideBubble();
                }
                break;
        }

        return true;

    }

    private void startDrag(MotionEvent event) {
        if (seekType == SEEKBAR_TYPE_SEEKBAR) {
            onStartTrackingTouch();
            trackTouchEvent(event);
            attemptClaimDrag();
        } else if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            if (touchType == TOUCH_LOW_THUMB) {
                onStartTrackingTouch();
                attemptClaimDrag();
            } else if (touchType == TOUCH_HEIGHT_THUMB) {
                onStartTrackingTouch();
                attemptClaimDrag();
            }
        }
    }

    private void onStartTrackingTouch() {
        isDragging = true;
        if (seekType == SEEKBAR_TYPE_SEEKBAR) {
            if (mProgressListener != null) {
                mProgressListener.onProgressStart(this, progress);
            }
        } else if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            if (touchType == TOUCH_LOW_THUMB) {
                if (mLowOrHeightListener != null) {
                    mLowOrHeightListener.onLowStart(this, (int) lowProgress);
                }
            } else if (touchType == TOUCH_HEIGHT_THUMB) {
                if (mLowOrHeightListener != null) {
                    mLowOrHeightListener.onHeightStart(this, (int) heightProgress);
                }
            }
        }

    }

    private void trackTouchEvent(MotionEvent event) {
        float x = 0;
        float xLow = 0;
        float xHeight = 0;
        if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            xLow = thumbLow.thumbCenterX;
            xHeight = thumbHeight.thumbCenterX;
        } else {
            x = thumb.thumbCenterX;
        }
        float scale = 0F;
        float scaleLow = 0F;
        float scaleHeight = 0F;

        float calcBarlongWidth = calculateHelper.barDistance;
        float minboundaryX = calculateHelper.barLeft;
        float maxboundaryX = calculateHelper.barRight;

        if (isThumbInnerOffset) {
            if (isUseThumbWAndH) {
                if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                    if (xLow <= minboundaryX) {
                        scaleLow = 0F;
                    } else if (xLow >= maxboundaryX) {
                        scaleLow = 1F;
                    } else {
                        scaleLow = (xLow - minboundaryX) / calcBarlongWidth;
                    }
                    if (xHeight <= minboundaryX) {
                        scaleHeight = 0F;
                    } else if (xHeight >= maxboundaryX) {
                        scaleHeight = 1F;
                    } else {
                        scaleHeight = (xHeight - minboundaryX) / calcBarlongWidth;
                    }
                } else {
                    if (x <= minboundaryX) {
                        scale = 0F;
                    } else if (x >= maxboundaryX) {
                        scale = 1F;
                    } else {
                        scale = (x - minboundaryX) / calcBarlongWidth;
                    }
                }
            } else {
                if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                    if (xLow <= minboundaryX) {
                        scaleLow = 0F;
                    } else if (xLow >= maxboundaryX) {
                        scaleLow = 1F;
                    } else {
                        scaleLow = (xLow - minboundaryX) / calcBarlongWidth;
                    }
                    if (xHeight <= minboundaryX) {
                        scaleHeight = 0F;
                    } else if (xHeight >= maxboundaryX) {
                        scaleHeight = 1F;
                    } else {
                        scaleHeight = (xHeight - minboundaryX) / calcBarlongWidth;
                    }
                } else {
                    if (x <= minboundaryX) {
                        scale = 0F;
                    } else if (x >= maxboundaryX) {
                        scale = 1F;
                    } else {
                        scale = (x - minboundaryX) / calcBarlongWidth;
                    }
                }
            }
        } else {

            if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
                if (xLow <= minboundaryX) {
                    scaleLow = 0F;
                } else if (xLow >= maxboundaryX) {
                    scaleLow = 1F;
                } else {
                    scaleLow = (xLow - minboundaryX) / calcBarlongWidth;
                }
                if (xHeight <= minboundaryX) {
                    scaleHeight = 0F;
                } else if (xHeight >= maxboundaryX) {
                    scaleHeight = 1F;
                } else {
                    scaleHeight = (xHeight - minboundaryX) / calcBarlongWidth;
                }
            } else {
                if (x <= minboundaryX) {
                    scale = 0F;
                } else if (x >= maxboundaryX) {
                    scale = 1F;
                } else {
                    scale = (x - minboundaryX) / calcBarlongWidth;
                }
            }
        }

        float range = max - min;
        if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            lowProgress = scaleLow * range + min;
            heightProgress = scaleHeight * range + min;
        } else {
            if (isThumbAndProgressPart) {
                thumbProgress = scale * range + min;
            } else {
                progress = scale * range + min;
            }
        }
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void onStopTrackingTouch() {
        isDragging = false;
        if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            if (touchType == TOUCH_LOW_THUMB) {
                if (mLowOrHeightListener != null) {
                    mLowOrHeightListener.onLowStop(this, (int) lowProgress);
                }
            } else if (touchType == TOUCH_HEIGHT_THUMB) {
                if (mLowOrHeightListener != null) {
                    mLowOrHeightListener.onHeightStop(this, (int) heightProgress);
                }
            }
        } else {
            if (mProgressListener != null) {
                mProgressListener.onProgressStop(this, progress);
            }
        }
    }

    public boolean isTouchInBar(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (seekType == SEEKBAR_TYPE_DIY) {
            if (x < barWrapperNoPaddingRectF.left
                    || x > barWrapperNoPaddingRectF.right
                    || y < barWrapperNoPaddingRectF.top
                    || y > barWrapperNoPaddingRectF.bottom + textHeight + spacing) {
                return false;
            }
        } else {
            if (x < barWrapperNoPaddingRectF.left
                    || x > barWrapperNoPaddingRectF.right
                    || y < barWrapperNoPaddingRectF.top
                    || y > barWrapperNoPaddingRectF.bottom) {
                return false;
            }
        }
        return true;
    }

    public boolean isTouchInThumb(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (isUseThumbWAndH) {
            if (x < thumb.thumbCenterX - thumb.thumbWidth / 2f
                    || x > thumb.thumbCenterX + thumb.thumbWidth / 2f
                    || y < thumb.thumbCenterY - thumb.thumbHeight / 2f
                    || y > thumb.thumbCenterY + thumb.thumbHeight / 2f) {
                return false;
            }
        } else {
            if (x < thumb.thumbCenterX - thumb.thumbRadiusForSelect
                    || x > thumb.thumbCenterX + thumb.thumbRadiusForSelect
                    || y < thumb.thumbCenterY - thumb.thumbRadiusForSelect
                    || y > thumb.thumbCenterY + thumb.thumbRadiusForSelect) {
                return false;
            }
        }
        return true;
    }

    public int isTouchInLowOrHeightThumb(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (isUseThumbWAndH) {
            if (x > thumbHeight.thumbCenterX - thumbHeight.thumbWidth / 2f
                    && x < thumbHeight.thumbCenterX + thumbHeight.thumbWidth / 2f
                    && y > thumbHeight.thumbCenterY - thumbHeight.thumbHeight / 2f
                    && y < thumbHeight.thumbCenterY + thumbHeight.thumbHeight / 2f) {
                return TOUCH_HEIGHT_THUMB;
            }

            if (x > thumbLow.thumbCenterX - thumbLow.thumbWidth / 2f
                    && x < thumbLow.thumbCenterX + thumbLow.thumbWidth / 2f
                    && y > thumbLow.thumbCenterY - thumbLow.thumbHeight / 2f
                    && y < thumbLow.thumbCenterY + thumbLow.thumbHeight / 2f) {
                return TOUCH_LOW_THUMB;
            }
        } else {
            if (x > thumbHeight.thumbCenterX - thumbHeight.thumbRadiusForSelect
                    && x < thumbHeight.thumbCenterX + thumbHeight.thumbRadiusForSelect
                    && y > thumbHeight.thumbCenterY - thumbHeight.thumbRadiusForSelect
                    && y < thumbHeight.thumbCenterY + thumbHeight.thumbRadiusForSelect) {
                return TOUCH_HEIGHT_THUMB;
            }

            if (x > thumbLow.thumbCenterX - thumbLow.thumbRadiusForSelect
                    && x < thumbLow.thumbCenterX + thumbLow.thumbRadiusForSelect
                    && y > thumbLow.thumbCenterY - thumbLow.thumbRadiusForSelect
                    && y < thumbLow.thumbCenterY + thumbLow.thumbRadiusForSelect) {
                return TOUCH_LOW_THUMB;
            }
        }
        return TOUCH_NULL_THUMB;
    }

    public void repairThumbRange(MotionEvent event) {
        float x = event.getX();
        float changeX = x;
        float right = 0;

        if (seekType == SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            if (touchType == TOUCH_LOW_THUMB) {
                if (x < calculateHelper.barLeft) {
                    changeX = calculateHelper.barLeft;
                }

                if (x > calculateHelper.lowMaxRight) {
                    changeX = calculateHelper.lowMaxRight;
                }
                right = calculateHelper.barRight;
            } else if (touchType == TOUCH_HEIGHT_THUMB) {
                if (x < calculateHelper.heightMinLeft) {
                    changeX = calculateHelper.heightMinLeft;
                }

                if (x > calculateHelper.barRight) {
                    changeX = calculateHelper.barRight;
                }
                right = calculateHelper.barRight;
            }
        } else {
            if (x < calculateHelper.barLeft) {
                changeX = calculateHelper.barLeft;
            }

            if (x > calculateHelper.barRight) {
                changeX = calculateHelper.barRight;
            }
        }

        if (seekType == SEEKBAR_TYPE_SEEKBAR && event.getAction() == MotionEvent.ACTION_DOWN && isOpenAnimator) {
            if (valueAnimator != null) {
                valueAnimator.cancel();
                valueAnimator = null;
            }
            valueAnimator = ValueAnimator.ofFloat(thumb.thumbCenterX, changeX);
            valueAnimator.setDuration(animatorDuration);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    thumb.thumbCenterX = value;
                    if (!isThumbAndProgressPart) {
                        progressDstRectF.right = thumb.thumbCenterX;
                    }
                    invalidate();
                }
            });
            valueAnimator.start();
            return;
        } else {
            if (isOpenAnimator) {
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    valueAnimator = null;
                }
            }
        }
        if (seekType != SEEKBAR_TYPE_LOW_HEIGHT_THUMB) {
            thumb.thumbCenterX = changeX;
            if (!isThumbAndProgressPart) {
                progressDstRectF.right = thumb.thumbCenterX;
            }
        } else {
            if (touchType == TOUCH_LOW_THUMB) {
                if (isThumbNoOver) {
                    if (changeX >= thumbHeight.thumbCenterX) {
                        changeX = thumbHeight.thumbCenterX;
                        thumbLow.thumbCenterX = changeX;
                        if (isThumbNoOverThanMove) {
                            touchType = TOUCH_HEIGHT_THUMB;
                        }
                    } else {
                        thumbLow.thumbCenterX = changeX;
                    }
                } else {
                    thumbLow.thumbCenterX = changeX;
                }
            } else {
                if (isThumbNoOver) {
                    if (changeX <= thumbLow.thumbCenterX) {
                        changeX = thumbLow.thumbCenterX;
                        thumbHeight.thumbCenterX = changeX;
                        if (isThumbNoOverThanMove || thumbHeight.thumbCenterX >= right) {
                            touchType = TOUCH_LOW_THUMB;
                        }
                    } else {
                        thumbHeight.thumbCenterX = changeX;
                    }
                } else {
                    thumbHeight.thumbCenterX = changeX;
                }
            }
        }
    }
    //endregion

    //region BubbleView
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
            setMeasuredDimension(bubbleRadius * 3, bubbleRadius * 3);

            if (bubbleType == BUBBLE_TYPE_CIRCLE) {
                mBubbleCircleRectF.set(
                        getMeasuredWidth() / 2f - bubbleRadius,
                        0,
                        getMeasuredWidth() / 2f + bubbleRadius,
                        getMeasuredHeight() - bubbleRadius);
            } else if (bubbleType == BUBBLE_TYPE_RECT) {
                mBubbleRoundRectF.set(
                        getMeasuredWidth() / 2f - bubbleRadius,
                        bubbleRadius / 2f,
                        getMeasuredWidth() / 2f + bubbleRadius,
                        getMeasuredHeight() - bubbleRadius / 2f);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (bubbleType == BUBBLE_TYPE_CIRCLE) {
                // set path
                mPath.reset();
                mPath.moveTo(getMeasuredWidth() / 2f, getMeasuredHeight() - bubbleRadius / 3f);
                float finalLeftX = (float) (getMeasuredWidth() / 2f - Math.sqrt(3) / 2f * bubbleRadius);
                float finalLeftY = 3 / 2f * bubbleRadius;
                mPath.quadTo(finalLeftX - EasySeekBarUtil.dp2px(getContext(), 2),
                        finalLeftY - EasySeekBarUtil.dp2px(getContext(), 2), finalLeftX, finalLeftY);
                mPath.arcTo(mBubbleCircleRectF, 150, 240);
                float finalRightX = (float) (getMeasuredWidth() / 2f + Math.sqrt(3) / 2f * bubbleRadius);
                float finalRightY = 3 / 2f * bubbleRadius;
                mPath.quadTo(finalRightX + EasySeekBarUtil.dp2px(getContext(), 2),
                        finalRightY - EasySeekBarUtil.dp2px(getContext(), 2),
                        getMeasuredWidth() / 2f, getMeasuredHeight() - bubbleRadius / 3f);
                mPath.close();

                mBubblePaint.setColor(bubbleColor);
                canvas.drawPath(mPath, mBubblePaint);

                mBubblePaint.setTextSize(bubbleTextSize);
                mBubblePaint.setColor(bubbleTextColor);
                mBubblePaint.setTextAlign(Paint.Align.CENTER);
                mBubblePaint.getFontMetrics(mFontMetrics);
                float textY = mBubbleCircleRectF.height() / 2f +
                        (mFontMetrics.descent - mFontMetrics.ascent) / 2 - mFontMetrics.descent;
                canvas.drawText(mProgressText, getMeasuredWidth() / 2, textY, mBubblePaint);

            } else if (bubbleType == BUBBLE_TYPE_RECT) {
                // set path
                mPath.reset();
                mPath.moveTo(getMeasuredWidth() / 2f, getMeasuredHeight() - bubbleRadius / 3f);
                mPath.lineTo(mBubbleRoundRectF.left + EasySeekBarUtil.dp2px(getContext(), (int) (bubbleRadius / 2f)),
                        mBubbleRoundRectF.bottom);
                mPath.lineTo(mBubbleRoundRectF.right - EasySeekBarUtil.dp2px(getContext(), (int) (bubbleRadius / 2f)),
                        mBubbleRoundRectF.bottom);
                mPath.close();

                mBubblePaint.setColor(bubbleColor);
                canvas.drawPath(mPath, mBubblePaint);
                canvas.drawRoundRect(mBubbleRoundRectF,
                        EasySeekBarUtil.dp2px(getContext(), 5),
                        EasySeekBarUtil.dp2px(getContext(), 5),
                        mBubblePaint);

                mBubblePaint.setTextSize(bubbleTextSize);
                mBubblePaint.setColor(bubbleTextColor);
                mBubblePaint.setTextAlign(Paint.Align.CENTER);
                mBubblePaint.getFontMetrics(mFontMetrics);
                float textY = bubbleRadius / 2f + mBubbleRoundRectF.height() / 2f +
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

        if (bubbleView != null && bubbleView.getParent() != null) {

            getLocationOnScreen(bubbleViewPointByScreenXY);
            wmLayoutParams.x = (int) (bubbleViewPointByScreenXY[0] + thumb.thumbCenterX - bubbleView.getMeasuredWidth() / 2f);
            wmLayoutParams.y = (int) (bubbleViewPointByScreenXY[1] - bubbleView.getMeasuredHeight() - screenStateHeight);
            wm.updateViewLayout(bubbleView, wmLayoutParams);
        } else {
            getLocationOnScreen(bubbleViewPointByScreenXY);
            wmLayoutParams.x = (int) (bubbleViewPointByScreenXY[0] + thumb.thumbCenterX - bubbleView.getMeasuredWidth() / 2f);
            wmLayoutParams.y = (int) (bubbleViewPointByScreenXY[1] - bubbleView.getMeasuredHeight() - screenStateHeight);
            wm.addView(bubbleView, wmLayoutParams);
        }

        String diyTextByProgress = "";
        if (bubbleViewHelper != null) {
            if (isThumbAndProgressPart) {
                diyTextByProgress = bubbleViewHelper.getDiyTextByProgress((int) thumbProgress);
            } else {
                diyTextByProgress = bubbleViewHelper.getDiyTextByProgress((int) progress);
            }
        }

        if (isThumbAndProgressPart) {
            bubbleView.setBubbleText(bubbleViewHelper == null ? (isShowFloat ? EasySeekBarUtil.formatFloat(thumbProgress) :
                    EasySeekBarUtil.formatInt(thumbProgress)) : diyTextByProgress);
        } else {
            bubbleView.setBubbleText(bubbleViewHelper == null ? (isShowFloat ? EasySeekBarUtil.formatFloat(progress) :
                    EasySeekBarUtil.formatInt(progress)) : diyTextByProgress);
        }

    }

    // reference by https://github.com/woxingxiao/BubbleSeekBar
    // hint bubble
    private void hideBubble() {
        if (!isShowBubble) {
            return;
        }

        if (bubbleView != null && bubbleView.getParent() != null) {
            wm.removeView(bubbleView);
        }

    }

    public void setBubbleViewHelper(BubbleViewHelper helper) {
        this.bubbleViewHelper = helper;
    }
    //endregion

    //region 公开 API
    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
        requestLayout();
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
        requestLayout();
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
        postInvalidate();
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        postInvalidate();
    }

    public void setThumbColor(int thumbColor) {
        this.thumb.thumbColor = thumbColor;
        postInvalidate();
    }

    public void setThumbSelectColor(int thumbSelectColor) {
        this.thumb.thumbSelectColor = thumbSelectColor;
        postInvalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        postInvalidate();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.textSelectColor = textSelectColor;
        postInvalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = EasySeekBarUtil.sp2px(getContext(), textSize);
        textPaint.setTextSize(textSize);
        Paint.FontMetrics metrics = new Paint.FontMetrics();
        textPaint.getFontMetrics(metrics);
        textHeight = metrics.descent - metrics.ascent;
        requestLayout();
    }

    public void setItems(String... items) {
        setItems(Arrays.asList(items));
    }

    public void setItems(List<String> items) {
        if (items.size() <= 0) {
            return;
        }
        diyDatas.clear();
        diyDatas.addAll(items);
        seekType = SEEKBAR_TYPE_DIY;
        requestLayout();
    }

    public void setSelectIndex(final int index) {

        this.post(new Runnable() {
            @Override
            public void run() {
                int splitCount = diyDatas.size();
                if (index < splitCount) {
                    diySelectIndex = index;
                }
                if (splitCount > 1) {
                    if (isThumbInnerOffset) {
                        if (isUseThumbWAndH) {
                            itemSpace = (barDstRectF.width() - barStrokeWidth - thumb.thumbWidth) - 1.0f / (splitCount - 1);
                        } else {
                            itemSpace = (barDstRectF.width() - barStrokeWidth - thumb.thumbRadiusForNormal) - 1.0f / (splitCount - 1);
                        }
                        thumb.thumbCenterX = barDstRectF.left + barStrokeWidth / 2f + thumb.thumbWidth / 2f + index * itemSpace;
                    } else {
                        itemSpace = barDstRectF.width() * 1.0f / (splitCount - 1);
                        thumb.thumbCenterX = barDstRectF.left + index * itemSpace;
                    }
                    progressDstRectF.right = thumb.thumbCenterX;

                }
                postInvalidate();
            }
        });
    }

    public int getSelectIndex() {
        return this.diySelectIndex;
    }

    public void setThumbProgress(final int progress) {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (isThumbAndProgressPart) {
                    thumbProgress = progress;
                    float delta = max - min;
                    float result = (progress - min) * 1.0f / delta * barDstRectF.width() + barDstRectF.left;
                    if (isOpenAnimator) {
                        if (valueAnimatorForThumb != null) {
                            valueAnimatorForThumb.cancel();
                            valueAnimatorForThumb = null;
                        }
                        valueAnimatorForThumb = ValueAnimator.ofFloat(thumb.thumbCenterX, result);
                        valueAnimatorForThumb.setDuration(animatorDuration);
                        valueAnimatorForThumb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                thumb.thumbCenterX = value;
                                invalidate();
                            }
                        });
                        valueAnimatorForThumb.start();
                    } else {
                        thumb.thumbCenterX = result;
                    }
                }
                postInvalidate();
            }
        });
    }

    public int getThumbProgress() {
        return (int) this.thumbProgress;
    }

    public void setProgress(final int progress) {
        this.progress = progress;
        this.post(new Runnable() {
            @Override
            public void run() {
                float delta = max - min;
                if (isThumbAndProgressPart) {
                    float result = (progress - min) * 1.0f / delta * barDstRectF.width() + barDstRectF.left;
                    if (isOpenAnimator) {
                        if (valueAnimatorForProgress != null) {
                            valueAnimatorForProgress.cancel();
                            valueAnimatorForProgress = null;
                        }
                        valueAnimatorForProgress = ValueAnimator.ofFloat(progressDstRectF.right, result);
                        valueAnimatorForProgress.setDuration(animatorDuration);
                        valueAnimatorForProgress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                progressDstRectF.right = value;
                                invalidate();
                            }
                        });
                        valueAnimatorForProgress.start();
                    } else {
                        progressDstRectF.right = result;
                    }
                } else {
                    float result = (progress - min) * 1.0f / delta * barDstRectF.width() + barDstRectF.left;
                    if (isOpenAnimator) {
                        if (valueAnimator != null) {
                            valueAnimator.cancel();
                            valueAnimator = null;
                        }
                        valueAnimator = ValueAnimator.ofFloat(progressDstRectF.right, result);
                        valueAnimator.setDuration(animatorDuration);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                thumb.thumbCenterX = value;
                                progressDstRectF.right = value;
                                invalidate();
                            }
                        });
                        valueAnimator.start();
                    } else {
                        thumb.thumbCenterX = result;
                        progressDstRectF.right = result;
                    }
                }
                postInvalidate();
            }
        });
    }

    public float getProgress() {
        return this.progress;
    }

    public int getProgressForInt() {
        return (int) this.progress;
    }

    public void setLowProgress(int lowProgress) {
        this.lowProgress = lowProgress;
        requestLayout();
    }

    public int getLowProgress() {
        return (int) this.lowProgress;
    }

    public void setHeightProgress(int heightProgress) {
        this.heightProgress = heightProgress;
        requestLayout();
    }

    public int getHeightProgress() {
        return (int) this.heightProgress;
    }

    public void setMax(int max) {
        this.max = max;
        autoInit();
    }

    public int getMax() {
        return (int) max;
    }

    public void setMin(int min) {
        this.min = min;
        autoInit();
    }

    public int getMin() {
        return (int) min;
    }

    public void setTextShowHelper(TextShowHelper helper) {
        this.textShowHelper = helper;
    }

    public void setShowBubble(boolean show) {
        this.isShowBubble = show;
        postInvalidate();
    }
    //endregion

    //region 内部类
    private class Thumb {
        float thumbCenterX;
        float thumbCenterY;
        int thumbRadiusForNormal; // 未选中thumb半径
        int thumbRadiusForSelect; // 选中thumb半径
        int thumbWidth;
        int thumbHeight;
        Bitmap thumbBitmap;
        int thumbColor;
        int thumbSelectColor;
        RectF thumbDstRectF = new RectF();

        public RectF thumbDstRectF() {
            if (isThumbInnerOffset) {
                if (isUseThumbWAndH) {
                    thumbDstRectF.left = thumbCenterX - thumbWidth / 2f;
                    thumbDstRectF.top = thumbCenterY - thumbHeight / 2f;
                    thumbDstRectF.right = thumbCenterX + thumbWidth / 2f;
                    thumbDstRectF.bottom = thumbCenterY + thumbHeight / 2f;
                } else {
                    thumbDstRectF.left = thumbCenterX - thumbRadiusForNormal;
                    thumbDstRectF.top = thumbCenterY - thumbRadiusForNormal;
                    thumbDstRectF.right = thumbCenterX + thumbRadiusForNormal;
                    thumbDstRectF.bottom = thumbCenterY + thumbRadiusForNormal;
                }
            } else {
                if (isUseThumbWAndH) {
                    thumbDstRectF.left = thumbCenterX - thumbWidth / 2f;
                    thumbDstRectF.top = thumbCenterY - thumbHeight / 2f;
                    thumbDstRectF.right = thumbCenterX + thumbWidth / 2f;
                    thumbDstRectF.bottom = thumbCenterY + thumbHeight / 2f;
                } else {
                    thumbDstRectF.left = thumbCenterX - (isDragging ? thumbRadiusForSelect : thumbRadiusForNormal);
                    thumbDstRectF.top = thumbCenterY - (isDragging ? thumbRadiusForSelect : thumbRadiusForNormal);
                    thumbDstRectF.right = thumbCenterX + (isDragging ? thumbRadiusForSelect : thumbRadiusForNormal);
                    thumbDstRectF.bottom = thumbCenterY + (isDragging ? thumbRadiusForSelect : thumbRadiusForNormal);
                }
            }

            return thumbDstRectF;
        }
    }

    private class CalculateHelper {
        float barDistance; // Bar的有效长度(剔除边框)
        float barLeft; // Bar的有效左侧(剔除边框)
        float barRight; // Bar的有效右侧侧(剔除边框)
        float lowMaxRight; // 两个滑动块需要
        float heightMinLeft; // 两个滑动块需要
    }
    //endregion

}
