package com.example.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李振伟 on 2019/10/19 0019.
 */

public class Flowlayout extends ViewGroup {

    private static final String TAG = "Flowlayout";

    public Flowlayout(Context context) {
        super(context);
    }

    public Flowlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Flowlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 每一行的高度
     */
    private List<Integer> rowHeights = new ArrayList<>();

    /**
     * 每一行的子View
     */
    private List<List<View>> rowViews = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");

        rowHeights.clear();
        rowViews.clear();

        //这里我们可以获取到父布局的宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        int widthmode = MeasureSpec.getMode(widthMeasureSpec);
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);

        //当前控件的宽高
        int measureWidth = 0;
        int measureHeight = 0;

        //当前行的宽高，因为存在多行，下一行数据要放到下方，行的宽高需要保存
        int curLineWidth = 0;
        int curLineHeight = 0;

        if (widthmode == MeasureSpec.EXACTLY && heightmode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
            measureHeight = heightSize;
        } else {
            int childWidth = 0;
            int childHeight = 0;

            //获取子View的数量用于迭代
            int childCount = getChildCount();

            //当前行的子View集合
            List<View> viewList = new ArrayList<>();

            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                //1、测量子View自己
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                //2、获取getLayoutParams 即XML资源
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
                //3、获得实际宽度和高度(MARGIN+WIDTH)
                childWidth = childView.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                childHeight = childView.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;

                //4、判断是否需要换行
                if (curLineWidth + childWidth > widthSize) {//需要换行

                    //记录当前行的最大宽度，高度累加
                    measureWidth = Math.max(measureWidth, curLineWidth);
                    measureHeight += curLineHeight;

                    //保存当前行的数据和高度
                    rowHeights.add(curLineHeight);
                    rowViews.add(viewList);

                    curLineWidth = childWidth;
                    curLineHeight = childHeight;

                    viewList = new ArrayList<>();
                    viewList.add(childView);

                } else {//不换行的情况下
                    //记录当前行的总宽度和最大高度
                    curLineWidth += childWidth;
                    curLineHeight = Math.max(curLineHeight, childHeight);
                    //将子View添加到当前行的List中
                    viewList.add(childView);
                }

                if (i == childCount - 1) {
                    measureWidth = Math.max(measureWidth, curLineWidth);
                    measureHeight += curLineHeight;

                    //保存当前行的数据和高度
                    rowHeights.add(curLineHeight);
                    rowViews.add(viewList);
                }
            }
        }

        //确认保存自己的宽高
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left, top, right, bottom;
        int curTop = 0;//当前顶部距离
        int curLeft = 0;//当前左边距离

        for (int i = 0; i < rowViews.size(); i++) {
            List<View> viewList = rowViews.get(i);
            for (int j = 0; j < viewList.size(); j++) {
                View child = viewList.get(j);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();

                left = marginLayoutParams.leftMargin + curLeft;
                top = marginLayoutParams.topMargin + curTop;
                right = left + child.getMeasuredWidth();
                bottom = top + child.getMeasuredHeight();

                child.layout(left, top, right, bottom);

                curLeft += child.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            curLeft = 0;
            curTop += rowHeights.get(i);
        }

        rowHeights.clear();
        rowHeights.clear();
    }
}
