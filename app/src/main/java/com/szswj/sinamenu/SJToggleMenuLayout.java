package com.szswj.sinamenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;


/**
 * Created by WayneSuper on 18/01/2016.
 */
public class SJToggleMenuLayout extends ViewGroup implements View.OnClickListener {
    private static final String tag = "SJToggleMenuLayout";
    private static final int MARGIN_BOTTOM = 200;
    private static final long OPEN_DURATION = 500;
    private final AnticipateInterpolator anticipation;
    private final OvershootInterpolator overshoot;
    private View mCloseButton;
    private ArrayList<View> mChildViews;
    private int childSize;
    private SJToggleMenuOverlay mParent;
    private ArrayList<Translation> mLocations;


    private static final float INTERPOLATOR_WEIGHT = 2.5f;
    private boolean mAnimating;
    private boolean mExpanded = true;
    private SJToggleMenuOverlay.OnItemClickListener listener;


    public SJToggleMenuLayout(Context context) {
        this(context, null);
    }

    public SJToggleMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SJToggleMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        anticipation = new AnticipateInterpolator(INTERPOLATOR_WEIGHT);
        overshoot = new OvershootInterpolator(INTERPOLATOR_WEIGHT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMenuOverlay(SJToggleMenuOverlay overlay) {
        this.mParent = overlay;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflateSubViews();
    }

    private void inflateSubViews() {
        int num = getChildCount();
        if (num == 0) throw new IllegalArgumentException("the parent is empty");
        mCloseButton = getChildAt(0);
        mCloseButton.setOnClickListener(this);
        mChildViews = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            View childView = getChildAt(i);
            mChildViews.add(childView);
            childView.setOnClickListener(this);
        }
        if (mChildViews.size() > 6) {
            throw new IllegalArgumentException("the child can't  >6");
        }
        mCloseButton.bringToFront();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            //设置关闭按钮的位置
            Translation ct = layoutCloseButton(l, t, r, b);
            int nL,nT,col,row,columnCount,marginX,marginY;
            int num = mChildViews.size();
            mLocations = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                childSize = mChildViews.get(0).getMeasuredWidth();
                View childView = mChildViews.get(i);
                switch (num) {
                    case 2:  //两个的排列方式不同
                    case 4:
                        columnCount = 2;
                        row = i / columnCount;
                        col = i % columnCount;
                        break;
                    case 5:
                        if (i < 3) {
                            columnCount = 2;
                            if (i == 2) {
                                row = 1;
                                col = 0;
                                columnCount = 3;
                            } else {
                                row = i / columnCount;
                                col = i % columnCount;
                            }
                        } else {
                            columnCount = 3;
                            row = i / columnCount;
                            col = i % columnCount+1;
                        }
                        break;
                    default:
                        columnCount = 3;
                        row = i / columnCount;
                        col = i % columnCount;
                        break;
                }
                marginX = (getMeasuredWidth() - columnCount * childSize) / (columnCount + 1);
                marginY = childSize;

                nL = marginX + col * (childSize + marginX);
                nT = b - MARGIN_BOTTOM - mCloseButton.getMeasuredWidth() - 2 * childSize - row * (childSize + marginY);
                childView.layout(nL, nT, nL + childSize, nT + childSize);

                Translation tt = new Translation(nL - ct.getX(), nT - ct.getY());
                mLocations.add(tt);

            }
            ViewHelper.setRotation(mCloseButton, 45);
        }
    }

    private Translation layoutCloseButton(int l, int t, int r, int b) {
        int nL = (r - mCloseButton.getMeasuredWidth()) / 2;
        int nt = b - mCloseButton.getMeasuredHeight() - MARGIN_BOTTOM;
        mCloseButton.layout(nL, nt, nL + mCloseButton.getMeasuredWidth(), nt + mCloseButton.getMeasuredHeight());
        Translation ct = new Translation(nL, nt);
        return ct;
    }


    private void expandAnimation() {
        for (int i = 0; i < mChildViews.size(); i++) {
            int TRANSLATION_Y = mLocations.get(i).getY();
            int TRANSLATION_X = mLocations.get(i).getX();
            ViewPropertyAnimator.animate(mChildViews.get(i)).setDuration(OPEN_DURATION).
                    translationYBy(TRANSLATION_Y).translationXBy(TRANSLATION_X).setInterpolator(anticipation).setListener(ON_EXPAND_COLLAPSE_LISTENER);
            ViewPropertyAnimator.animate(mChildViews.get(i)).alpha(1.0f).setDuration(OPEN_DURATION).start();
        }
        ViewPropertyAnimator.animate(mCloseButton).rotation(45).setDuration(OPEN_DURATION).start();
    }

    private void closeAnimation() {
        for (int i = 0; i < mChildViews.size(); i++) {
            int TRANSLATION_Y = mLocations.get(i).getY();
            int TRANSLATION_X = mLocations.get(i).getX();
            ViewPropertyAnimator.animate(mChildViews.get(i)).setDuration(OPEN_DURATION).
                    translationYBy(-TRANSLATION_Y).translationXBy(-TRANSLATION_X).setInterpolator(overshoot).setListener(ON_EXPAND_COLLAPSE_LISTENER);
            ViewPropertyAnimator.animate(mChildViews.get(i)).alpha(0.0f).setDuration(OPEN_DURATION).start();
        }
        ViewPropertyAnimator.animate(mCloseButton).rotation(-90).setDuration(OPEN_DURATION).start();
    }

    public void onShowing() {
        if (!mExpanded) {
            expandAnimation();
        }
    }

    @Override
    public void onClick(View v) {
        if (mCloseButton == v) {
            toggle();
        }else {
            if (listener!=null) listener.onItemClick(v);
        }
    }

    /**
     * Toggle the expandable menu button, expanding or collapsing it
     */
    public void toggle() {
        if (!mAnimating) {
            mAnimating = true;
            if (mExpanded) {
                closeAnimation();
            } else {
                expandAnimation();
            }
        }
    }


    private int ANIMATION_COUNTER;
    /**
     * Listener for expand and collapse animations
     */
    private Animator.AnimatorListener ON_EXPAND_COLLAPSE_LISTENER = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (ANIMATION_COUNTER == 1) {
                mCloseButton.setEnabled(false);
                for (int i = 0; i < mChildViews.size(); i++) {
                    mChildViews.get(i).setEnabled(false);
                }
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            ANIMATION_COUNTER++;
            if (ANIMATION_COUNTER == mChildViews.size()) {
                mExpanded = !mExpanded;
                if (mExpanded) {
                    for (int i = 0; i < mChildViews.size(); i++) {
                        mChildViews.get(i).setEnabled(true);
                    }
                } else {
                    for (int i = 0; i < mChildViews.size(); i++) {
                        mChildViews.get(i).setEnabled(false);
                    }
                    mParent.dismiss();
                }
                mAnimating = false;

                mCloseButton.setEnabled(true);
                ANIMATION_COUNTER = 0;

            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setOnItemClickListener(SJToggleMenuOverlay.OnItemClickListener listener) {
        this.listener = listener;
    }

    private class Translation {
        int x;
        int y;

        public Translation(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getY() {
            return y;
        }
    }


}
