package com.oushangfeng.marqueelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Oubowu on 2016/6/24 11:19.
 */
public class MarqueeLayout extends ViewGroup {

    public static final int ORIENTATION_UP = 1;
    public static final int ORIENTATION_DOWN = 2;
    public static final int ORIENTATION_LEFT = 3;
    public static final int ORIENTATION_RIGHT = 4;

    private int mOrientation;
    private int mItemCount;
    private int mCurrentPosition;
    private int mScrollDistance;
    private int mSwitchTime;
    private int mScrollTime;

    private Timer mTimer;

    private Scroller mScroller;

    /**
     * 判断是否手动调用start()的标志位，用于在onWindowFocusChanged辨识是否进行暂停恢复的操作
     */
    private boolean mIsStart;
    /**
     * 是否开启滚动时子View的缩放和透明度动画
     */
    private boolean mEnableAlphaAnim;
    private boolean mEnableScaleAnim;

    private MarqueeLayoutAdapter mAdapter;

    private MarqueeObserver mMarqueeObserver = new MarqueeObserver();

    public MarqueeLayout(Context context) {
        super(context);
    }

    public MarqueeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MarqueeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MarqueeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MarqueeLayout);
        mSwitchTime = ta.getInt(R.styleable.MarqueeLayout_switchTime, 2500);
        mScrollTime = ta.getInt(R.styleable.MarqueeLayout_scrollTime, 1000);
        mOrientation = ta.getInt(R.styleable.MarqueeLayout_orientation, ORIENTATION_UP);
        mEnableAlphaAnim = ta.getBoolean(R.styleable.MarqueeLayout_enableAlphaAnim, false);
        mEnableScaleAnim = ta.getBoolean(R.styleable.MarqueeLayout_enableScaleAnim, false);
        ta.recycle();
        mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 声明临时变量存储父容器的期望值
        int parentDesireHeight = 0;
        int parentDesireWidth = 0;

        int tmpWidth = 0;
        int tmpHeight = 0;

        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                // 获取子元素的布局参数
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                // 测量子元素并考虑外边距
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                // 计算父容器的期望值
                parentDesireWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                // 取子控件最大宽度
                tmpWidth = Math.max(tmpWidth, parentDesireWidth);
                parentDesireHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                // 取子控件最大高度
                tmpHeight = Math.max(tmpHeight, parentDesireHeight);
            }
            parentDesireWidth = tmpWidth;
            parentDesireHeight = tmpHeight;
            // 考虑父容器内边距
            parentDesireWidth += getPaddingLeft() + getPaddingRight();
            parentDesireHeight += getPaddingTop() + getPaddingBottom();
            // Log.e("TAG", "MarqueeLayout-100行-onMeasure(): " + parentDesireWidth + ";" + parentDesireHeight + ";" + getSuggestedMinimumWidth() + ";" + getSuggestedMinimumHeight());
            // 尝试比较建议最小值和期望值的大小并取大值
            parentDesireWidth = Math.max(parentDesireWidth, getSuggestedMinimumWidth());
            parentDesireHeight = Math.max(parentDesireHeight, getSuggestedMinimumHeight());
        }
        // 设置最终测量值
        setMeasuredDimension(resolveSize(parentDesireWidth, widthMeasureSpec), resolveSize(parentDesireHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 声明一个临时变量存储高度倍增值
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        if (mOrientation == ORIENTATION_DOWN || mOrientation == ORIENTATION_UP) {
            final int height = getMeasuredHeight();
            mScrollDistance = height;
            int multiHeight = 0;
            // 垂直方向跑马灯
            for (int i = 0; i < getChildCount(); i++) {
                // 遍历子元素并对其进行定位布局
                final View child = getChildAt(i);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                if (i == 0 && multiHeight == 0 && mOrientation == ORIENTATION_DOWN) {
                    multiHeight = -height;
                    mCurrentPosition = 1;
                }
                // 垂直方向的话，因为布局高度定死为子控件最大的高度，所以子控件一律位置垂直居中，paddingTop和marginTop均失效
                child.layout(paddingLeft + lp.leftMargin, (height - child.getMeasuredHeight()) / 2 + multiHeight, child.getMeasuredWidth() + paddingLeft + lp.leftMargin,
                        (height - child.getMeasuredHeight()) / 2 + child.getMeasuredHeight() + multiHeight);
                multiHeight += height;
            }
        } else {
            final int width = getMeasuredWidth();
            mScrollDistance = width;
            int multiWidth = 0;
            // 水平方向跑马灯
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                if (i == 0 && multiWidth == 0 && mOrientation == ORIENTATION_RIGHT) {
                    multiWidth = -width;
                    mCurrentPosition = 1;
                }
                // 水平方向，因为布局宽度定死为子控件最大的宽度，所以子控件一律位置水平居中，paddingLeft和marginLeft均失效
                child.layout((width - child.getMeasuredWidth()) / 2 + multiWidth, paddingTop + lp.topMargin,
                        (width - child.getMeasuredWidth()) / 2 + child.getMeasuredWidth() + multiWidth, child.getMeasuredHeight() + paddingTop + lp.topMargin);
                multiWidth += width;
            }
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    public void computeScroll() {

        if (mItemCount == 0) {
            return;
        }

        if (mScroller.computeScrollOffset()) {
            if (mOrientation == ORIENTATION_DOWN || mOrientation == ORIENTATION_UP) {
                scrollTo(0, mScroller.getCurrY());
                handleScrollAnim();
            } else {
                scrollTo(mScroller.getCurrX(), 0);
                handleScrollAnim();
            }
            invalidate();
        } else if (mTimer != null) {
            switch (mOrientation) {
                case ORIENTATION_UP:
                    if (mCurrentPosition >= mItemCount - 1) {
                        // 滚动到最后一个时，迅速回到第一个，造成轮播的假象
                        fastScroll(-mCurrentPosition * mScrollDistance);
                        mCurrentPosition = 0;
                    }
                    break;
                case ORIENTATION_DOWN:
                    if (mCurrentPosition <= 0) {
                        fastScroll((mItemCount - 1) * mScrollDistance);
                        mCurrentPosition = mItemCount - 1;
                    }
                    break;
                case ORIENTATION_LEFT:
                    if (mCurrentPosition >= mItemCount - 1) {
                        fastScroll(-mCurrentPosition * mScrollDistance);
                        mCurrentPosition = 0;
                    }
                    break;
                case ORIENTATION_RIGHT:
                    if (mCurrentPosition <= 0) {
                        fastScroll((mItemCount - 1) * mScrollDistance);
                        mCurrentPosition = mItemCount - 1;
                    }
                    break;
            }
            invalidate();
        }
    }

    private void smoothScroll(int distance) {
        if (mOrientation == ORIENTATION_DOWN || mOrientation == ORIENTATION_UP) {
            mScroller.startScroll(0, mScroller.getFinalY(), 0, distance, mScrollTime);
        } else {
            mScroller.startScroll(mScroller.getFinalX(), 0, distance, 0, mScrollTime);
        }
    }

    private void fastScroll(int distance) {
        if (mOrientation == ORIENTATION_DOWN || mOrientation == ORIENTATION_UP) {
            mScroller.startScroll(0, mScroller.getFinalY(), 0, distance, 0);
        } else {
            mScroller.startScroll(mScroller.getFinalX(), 0, distance, 0, 0);
        }
    }

    private void handleScrollAnim() {
        if (!mEnableAlphaAnim && !mEnableScaleAnim) {
            return;
        }

        float rate = 0;
        boolean notReachBorder = false;
        int relativeChildPosition = 0;

        switch (mOrientation) {
            case ORIENTATION_UP:
                rate = (mScroller.getCurrY() - mScroller.getStartY()) * 1.0f / (mScroller.getFinalY() - mScroller.getStartY()) / 2.0f + 0.5f;
                notReachBorder = mCurrentPosition != 0;
                relativeChildPosition = mCurrentPosition - 1;
                break;
            case ORIENTATION_DOWN:
                rate = (mScroller.getCurrY() - mScroller.getStartY()) * 1.0f / (mScroller.getFinalY() - mScroller.getStartY()) / 2.0f + 0.5f;
                notReachBorder = mCurrentPosition != mItemCount - 1;
                relativeChildPosition = mCurrentPosition + 1;
                break;
            case ORIENTATION_LEFT:
                rate = (mScroller.getCurrX() - mScroller.getStartX()) * 1.0f / (mScroller.getFinalX() - mScroller.getStartX()) / 2.0f + 0.5f;
                notReachBorder = mCurrentPosition != 0;
                relativeChildPosition = mCurrentPosition - 1;
                break;
            case ORIENTATION_RIGHT:
                rate = (mScroller.getCurrX() - mScroller.getStartX()) * 1.0f / (mScroller.getFinalX() - mScroller.getStartX()) / 2.0f + 0.5f;
                notReachBorder = mCurrentPosition != mItemCount - 1;
                relativeChildPosition = mCurrentPosition + 1;
                break;
        }

        if (notReachBorder) {
            playAnim(getChildAt(mCurrentPosition), mEnableAlphaAnim, mEnableScaleAnim, rate);
            playAnim(getChildAt(relativeChildPosition), mEnableAlphaAnim, mEnableScaleAnim, 1.5f - rate);
        } else {
            playAnim(getChildAt(mCurrentPosition), mEnableAlphaAnim, mEnableScaleAnim, 1);
        }

    }

    private void playAnim(View view, boolean enableAlphaAnim, boolean enableScaleAnim, float rate) {
        if (enableAlphaAnim) {
            ViewCompat.setAlpha(view, rate);
        }
        if (enableScaleAnim) {
            ViewCompat.setScaleX(view, rate);
            ViewCompat.setScaleY(view, rate);
        }
    }

    // 生成默认的布局参数
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    // 生成布局参数,将布局参数包装成我们的
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    // 生成布局参数,从属性配置中生成我们的布局参数
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    // 查当前布局参数是否是我们定义的类型这在code声明布局参数时常常用到
    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    private class SwitchTimerTask extends TimerTask {

        @Override
        public void run() {
            switch (mOrientation) {
                case ORIENTATION_UP:
                    mCurrentPosition++;
                    if (mCurrentPosition >= mItemCount) {
                        mCurrentPosition = 0;
                        fastScroll(-getScrollY());
                    } else {
                        smoothScroll(mScrollDistance);
                    }
                    break;
                case ORIENTATION_DOWN:
                    mCurrentPosition--;
                    if (mCurrentPosition < 0) {
                        mCurrentPosition = 1;
                        fastScroll(-getScrollY());
                    } else {
                        smoothScroll(-mScrollDistance);
                    }
                    break;
                case ORIENTATION_LEFT:
                    mCurrentPosition++;
                    if (mCurrentPosition >= mItemCount) {
                        mCurrentPosition = 0;
                        fastScroll(-getScrollX());
                    } else {
                        smoothScroll(mScrollDistance);
                    }
                    break;
                case ORIENTATION_RIGHT:
                    mCurrentPosition--;
                    if (mCurrentPosition < 0) {
                        mCurrentPosition = 1;
                        fastScroll(-getScrollX());
                    } else {
                        smoothScroll(-mScrollDistance);
                    }
                    break;
            }
            postInvalidate();
        }

    }

    /**
     * 开始轮播
     */
    public void start() {
        if (getChildCount() <= 1 || mTimer != null) {
            // 小于等于1没必要轮播
            return;
        }
        mIsStart = true;
        mTimer = new Timer();
        mTimer.schedule(new SwitchTimerTask(), mSwitchTime, mSwitchTime);
    }

    /**
     * 停止轮播
     */
    public void stop() {
        if (mTimer == null) {
            return;
        }
        mIsStart = false;
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    private class MarqueeObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            addChildView(mAdapter);
        }
    }

    public void setAdapter(MarqueeLayoutAdapter adapter) {

        adapter.registerDataSetObserver(mMarqueeObserver);

        addChildView(adapter);

    }

    private void addChildView(MarqueeLayoutAdapter adapter) {

        mAdapter = adapter;

        removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            final View child = adapter.getView(i, null, this);
            addView(child);
        }

        if (adapter.getCount() > 1) {
            switch (getOrientation()) {
                case MarqueeLayout.ORIENTATION_UP:
                case MarqueeLayout.ORIENTATION_LEFT:
                    addView(adapter.getView(0, null, this), getChildCount());
                    break;
                case MarqueeLayout.ORIENTATION_DOWN:
                case MarqueeLayout.ORIENTATION_RIGHT:
                    addView(adapter.getView(getChildCount() - 1, null, this), 0);
                    break;
            }
            mItemCount = adapter.getCount() + 1;
        } else {
            mItemCount = adapter.getCount();
        }

        if (mItemCount <= 1) {
            mScroller.forceFinished(true);
            scrollTo(0, 0);
        }

    }

    public int getOrientation() {
        return mOrientation;
    }

    public int getItemCount() {
        return mItemCount;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            carryOn();
        } else {
            pause();
        }
    }

    private void carryOn() {
        if (mIsStart && mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new SwitchTimerTask(), mSwitchTime, mSwitchTime);
        }
    }

    private void pause() {
        if (mIsStart && mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

}
