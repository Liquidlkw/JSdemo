package com.example.myapplication;

/**
 * Created by Administrator on 2016/7/8.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以直接添加子view的view
 *
 * @version V1.0
 * @author: hjy
 * @date: 2015年4月25日 上午11:29:58
 */
public class FlowLayout extends ViewGroup {

    final String TAG = FlowLayout.class.getSimpleName();

    /**
     * 存储所有的View，按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();

//    int dp10;
//    int dp15;

    private int mVerticalGap = 0;
    private int mHorizontalGap = 0;

    Context mContext;

    public boolean isMoreViewLine() {
        return isMoreViewLine;
    }

    public void setMoreViewLine(boolean moreViewLine) {
        isMoreViewLine = moreViewLine;
    }

    //总数是否超过可见行数
    private boolean isMoreViewLine;
    /**
     * 记录设置最大行数量
     */
    private int mMaxLineNumbers = -1;
    /**
     * 是否还有数据没显示
     */
    private boolean mHasMoreData;
    /**
     * 记录当前行数
     */
    private int mCount;

    protected boolean viewClick = true;

    public FlowLayout(Context context) {
        super(context);
        mContext = context;
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mHorizontalGap = dip2px(context, 15);
        mVerticalGap = dip2px(context, 15);
    }


    public void setMarginLeft(int dpMarginLeft) {
        mHorizontalGap = dip2px(mContext, dpMarginLeft);
    }

    public void setVerticalPadding(int verticalFading) {
        mVerticalGap = dip2px(mContext, verticalFading);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        Log.e(TAG, sizeWidth + "," + sizeHeight);
        mCount = 0;
        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        int cCount = getChildCount();

        // 遍历每个子元素
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childWidth = child.getMeasuredWidth() + mHorizontalGap;
            int childHeight = child.getMeasuredHeight() + mVerticalGap;
            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
             */
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth);// 取最大的
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 叠加当前高度，
                height += lineHeight;
                // 开启记录下一行的高度
                lineHeight = childHeight;
                mCount++;
                if (mCount >= mMaxLineNumbers && mMaxLineNumbers != -1) {
                    setMoreViewLine(true);
                    setHasMoreData(i + 1, cCount);
                    break;
                }
            } else
            // 否则累加值lineWidth,lineHeight取最大高度
            {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }

        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        mCount = 0;
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<View>();
        int cCount = getChildCount();
        // 遍历所有的孩子
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (viewClick) {
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnTagClickListener != null) {
                            mOnTagClickListener.OnTagClickListener(v);
                        }
                    }
                });
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // 如果已经需要换行
            if (childWidth + mHorizontalGap + lineWidth > width) {
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                lineWidth = 0;// 重置行宽
                lineViews = new ArrayList<View>();
                mCount++;
                if (mCount >= mMaxLineNumbers && mMaxLineNumbers != -1) {
                    setHasMoreData(i + 1, cCount);
                    break;
                }
            }

            // 如果不需要换行，则累加
            lineWidth += childWidth + mHorizontalGap;
            lineHeight = Math.max(lineHeight, childHeight + mVerticalGap);

            lineViews.add(child);
        }
        // 记录最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        int left = 0;
        int top = 0;
        // 得到总行数
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);

            Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
            Log.e(TAG, "第" + i + "行， ：" + lineHeight);

            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

//                int lc = left + dp10;
//                int tc = top + dp15;
                int lc = left;
                int tc = top;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + mHorizontalGap;
            }
            left = 0;
            top += lineHeight;
        }
    }

    private OnTagClickListener mOnTagClickListener;

    public interface OnTagClickListener {
        void OnTagClickListener(View view);
    }


    public void setOnTagClickListener(OnTagClickListener listener) {
        mOnTagClickListener = listener;
    }


    /**
     * 设置最多显示的行数
     *
     * @param number
     */
    public void setMaxLines(int number) {
        mMaxLineNumbers = number;
        requestLayout();
    }


    /**
     * 判断是否还有跟多View未展示
     *
     * @param i     当前展示的View
     * @param count 总共需要展示的View
     */
    private void setHasMoreData(int i, int count) {
        if (i <= count) {
            mHasMoreData = true;
            if (mHasMoreDataListerner != null) {
                mHasMoreDataListerner.callBack(true);
            }
        }
    }

    /**
     * 是否还有更多数据未显示
     *
     * @return true 还有未显示数据 false 完全显示
     */
    public boolean hasMoreData() {
        return mHasMoreData;
    }


    public interface HasMoreDataListerner {
        void callBack(boolean mHasMoreData);
    }

    public void setHasMoreDataListerner(HasMoreDataListerner mHasMoreDataListerner) {
        this.mHasMoreDataListerner = mHasMoreDataListerner;
    }

    private HasMoreDataListerner mHasMoreDataListerner;

}
