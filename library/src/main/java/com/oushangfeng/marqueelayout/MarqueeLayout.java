package com.oushangfeng.marqueelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Oubowu on 2016/6/24 11:19.
 */
public class MarqueeLayout extends ViewGroup {

    private Timer mTimer;

    private int mItemCount;
    private int mCurrentPosition;
    private Scroller mScroller;
    private int mScrollDistance;

    private int mSwitchTime;
    private int mScrollTime;

    private int mOrientation;

    public static final int ORIENTATION_UP = 1;
    public static final int ORIENTATION_DOWN = 2;
    public static final int ORIENTATION_LEFT = 3;
    public static final int ORIENTATION_RIGHT = 4;

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
            // 尝试比较建议最小值和期望值的大小并取大值
            parentDesireWidth = Math.max(parentDesireWidth, getSuggestedMinimumWidth());
            parentDesireHeight = Math.max(parentDesireHeight, getSuggestedMinimumHeight());
            mScrollDistance = mOrientation == ORIENTATION_DOWN || mOrientation == ORIENTATION_UP ? parentDesireHeight : parentDesireWidth;
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
            int multiHeight = 0;
            // 垂直方向跑马灯
            for (int i = 0; i < getChildCount(); i++) {
                // 遍历子元素并对其进行定位布局
                final View child = getChildAt(i);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                if (i == 0 && multiHeight == 0 && mOrientation == ORIENTATION_DOWN) {
                    // multiHeight = -(child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                    multiHeight = -height;
                    mCurrentPosition = 1;
                }
                // child.layout(paddingLeft + lp.leftMargin,  multiHeight + paddingTop + lp.topMargin,
                //        child.getMeasuredWidth() + paddingLeft + lp.leftMargin,  child.getMeasuredHeight() + multiHeight + paddingTop + lp.topMargin);
                // multiHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                // 垂直方向的话，因为布局高度定死为子控件最大的高度，所以子控件一律位置垂直居中，paddingTop和marginTop均失效
                child.layout(paddingLeft + lp.leftMargin, (height - child.getMeasuredHeight()) / 2 + multiHeight, child.getMeasuredWidth() + paddingLeft + lp.leftMargin,
                        (height - child.getMeasuredHeight()) / 2 + child.getMeasuredHeight() + multiHeight);
                multiHeight += height;
            }
        } else {
            final int width = getMeasuredWidth();
            int multiWidth = 0;
            // 水平方向跑马灯
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                if (i == 0 && multiWidth == 0 && mOrientation == ORIENTATION_RIGHT) {
                    // multiWidth = -(child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                    multiWidth = -width;
                    mCurrentPosition = 1;
                }
                // child.layout(multiWidth + paddingLeft + lp.leftMargin, paddingTop + lp.topMargin, child.getMeasuredWidth() + multiWidth + paddingLeft + lp.leftMargin,
                //        child.getMeasuredHeight() + paddingTop + lp.topMargin);
                // multiWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
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

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mOrientation == ORIENTATION_DOWN || mOrientation == ORIENTATION_UP) {
                scrollTo(0, mScroller.getCurrY());
            } else {
                scrollTo(mScroller.getCurrX(), 0);
            }
            invalidate();
        } else if (mTimer != null) {
            switch (mOrientation) {
                case ORIENTATION_UP:
                    if (mCurrentPosition >= mItemCount - 1) {
                        // 滚动到最后一个时，迅速回到第一个，造成轮播的假象
                        fastScroll(-mCurrentPosition * mScrollDistance);
                        mCurrentPosition = 0;
                        invalidate();
                    }
                    break;
                case ORIENTATION_DOWN:
                    if (mCurrentPosition <= 0) {
                        fastScroll((mItemCount - 1) * mScrollDistance);
                        mCurrentPosition = mItemCount - 1;
                        invalidate();
                    }
                    break;
                case ORIENTATION_LEFT:
                    if (mCurrentPosition >= mItemCount - 1) {
                        fastScroll(-mCurrentPosition * mScrollDistance);
                        mCurrentPosition = 0;
                        invalidate();
                    }
                    break;
                case ORIENTATION_RIGHT:
                    if (mCurrentPosition <= 0) {
                        fastScroll((mItemCount - 1) * mScrollDistance);
                        mCurrentPosition = mItemCount - 1;
                        invalidate();
                    }
                    break;
            }
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
                    postInvalidate();
                    break;
                case ORIENTATION_DOWN:
                    mCurrentPosition--;
                    if (mCurrentPosition < 0) {
                        mCurrentPosition = 1;
                        fastScroll(-getScrollY());
                    } else {
                        smoothScroll(-mScrollDistance);
                    }
                    postInvalidate();
                    break;
                case ORIENTATION_LEFT:
                    mCurrentPosition++;
                    if (mCurrentPosition >= mItemCount) {
                        mCurrentPosition = 0;
                        fastScroll(-getScrollX());
                    } else {
                        smoothScroll(mScrollDistance);
                    }
                    postInvalidate();
                    break;
                case ORIENTATION_RIGHT:
                    mCurrentPosition--;
                    if (mCurrentPosition < 0) {
                        mCurrentPosition = 1;
                        fastScroll(-getScrollX());
                    } else {
                        smoothScroll(-mScrollDistance);
                    }
                    postInvalidate();
                    break;

            }

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
        mTimer.cancel();
        mTimer.purge();
        mTimer = null;
    }

    public void setAdapter(MarqueeLayoutAdapter<String> adapter) {
        final ArrayList<View> views = adapter.getViews();
        if (views == null) {
            return;
        }

        removeAllViews();
        for (View view : views) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addView(view, lp);
        }

        mItemCount = views.size();
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

}
