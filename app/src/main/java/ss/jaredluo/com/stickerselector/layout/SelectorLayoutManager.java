package ss.jaredluo.com.stickerselector.layout;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import ss.jaredluo.com.stickerselector.model.Nearest;
import ss.jaredluo.com.stickerselector.view.PlaceholderView;

/**
 * Created by admin on 2017/7/6.
 */

public class SelectorLayoutManager extends LinearLayoutManager {

    private float mMaxScale = 1.5f;

    private float mMarginToCenter = 100;

    private OnItemScaleChangeListener mOnItemScaleChangeListener;
    private OnItemSelectedListener mOnItemSelectedListener;

    private Point recyclerCenter = new Point();
    private int mChildMaxWidth;
    private Context mContext;
    private int mChildStartWidth;
    private boolean isLayout;
    private boolean mIsReverse;
    private int mCurrentPosition;
    private boolean mIsShowingUnSelected = true;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalListener;

    public SelectorLayoutManager(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public SelectorLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mContext = context;
        init();
    }

    public SelectorLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init();
    }

    @Override
    public void onAttachedToWindow(final RecyclerView view) {
        super.onAttachedToWindow(view);
//        if (mGlobalListener == null) {
//            mGlobalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    int firstDataChildLeft = 0;
//                    for (int i = 0; i < view.getChildCount(); i++) {
//                        View child = view.getChildAt(i);
//                        ViewCompat.setZ(child, view.getChildCount() - i);
//                        if (i == 1) {
//                            firstDataChildLeft = child.getLeft();
//                        } else if (i > 1) {
//                            int left = firstDataChildLeft + (int) ((i - 1) * ScreenUtils.convertDpToPx(10));
//                            int deltaX = left - child.getLeft();
//                            child.setTranslationX(deltaX);
//                        }
//                    }
//                    mCanScroll = false;
//                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//            };
//            view.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalListener);
//        }

    }

    private void init() {

    }

    public float getMaxScale() {
        return mMaxScale;
    }

    public void setMaxScale(float maxScale) {
        mMaxScale = maxScale;
    }

    public float getMarginToCenter() {
        return mMarginToCenter;
    }

    public void setMarginToCenter(float mMarginToCenter) {
        this.mMarginToCenter = mMarginToCenter;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        updateRecyclerDimensions();
        initChildSize(recycler);
    }

    private void initChildSize(RecyclerView.Recycler recycler) {
        if (!isLayout && getItemCount() > 1) {
            View child = recycler.getViewForPosition(1);
            measureChildWithMargins(child, 0, 0);
            mChildStartWidth = child.getMeasuredWidth();
            mChildMaxWidth = (int) (mChildStartWidth * mMaxScale);
            isLayout = true;
        }
    }

    public void hideUnSelected() {
        mIsShowingUnSelected = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (i != mCurrentPosition) {
                View child = findViewByPosition(i);
                if (child != null) {
                    child.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public boolean isShowingUnSelected() {
        return mIsShowingUnSelected;
    }

    public void showUnSelected() {
        mIsShowingUnSelected = true;
        for (int i = 0; i < getItemCount(); i++) {
            if (i != mCurrentPosition) {
                View child = findViewByPosition(i);
                if (child != null) {
                    child.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void updateRecyclerDimensions() {
        recyclerCenter.set(getWidth() / 2, getHeight() / 2);
    }

    private float getCenterRelativePositionOf(View v) {
        int offset = (mChildMaxWidth - v.getWidth()) / 2;
        if (!mIsReverse && getPosition(v) != 1) {
            offset = -offset;
        }

        //处理最后一个Item继续向右滑动的情况
        offset = dealLastItemOverScroll(v, offset);

        Log.i("Jared", "vPosition: " + getPosition(v) + "  , negative offset:" + offset + " , mIsReverse: " + mIsReverse);

        int distance = v.getLeft() + v.getWidth() / 2 + offset - recyclerCenter.x;
        Log.i("Jared", "now position: " + getPosition(v) + ". distance is: " + distance + " , mIsReverse: " + mIsReverse);
        return distance;
    }

    private int dealLastItemOverScroll(View v, int offset) {
        if (getPosition(v) == getItemCount() - 2) {
            boolean isAtLast = false;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != null && child != v && getPosition(child) != 0 && getPosition(child) != getItemCount() - 1) {
                    if (child.getWidth() > mChildStartWidth) {
                        isAtLast = false;
                        break;
                    } else {
                        isAtLast = true;
                    }
                }
            }
            if (isAtLast) {
                offset = -offset;
            }
        }

        return offset;
    }


    private float getStartRelativePositionOf(View v) {
        return -((RecyclerView) v.getParent()).computeHorizontalScrollOffset();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int result = 0;
        if (mIsShowingUnSelected) {
            result = super.scrollHorizontallyBy(dx, recycler, state);
            Log.i("Jared", "scrollHorizontallyBy: " + result + ", offset:" + computeHorizontalScrollOffset(state));
            applyItemTransformToChildren();
        }
        return result;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            Log.i("Jared", "SCROLL_STATE_DRAGGING");
        } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
            Log.i("Jared", "SCROLL_STATE_IDLE");
            onScrollIdle();
        } else if (state == RecyclerView.SCROLL_STATE_SETTLING) {
            Log.i("Jared", "SCROLL_STATE_SETTLING");
        }
    }

    private void applyItemTransformToChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof PlaceholderView)) {
                float absDistance = Math.abs(getCenterRelativePositionOf(child));
                Log.i("Jared", "Position is " + getPosition(child) + ", absDistance is: " + absDistance);

                float scale = 1f;
                float centerWidth = mChildMaxWidth / 2 + mChildStartWidth / 2;
                if (absDistance <= centerWidth) {
                    float closeFactorToCenter = 1 - absDistance / centerWidth;
                    scale += (mMaxScale - 1f) * closeFactorToCenter;
                }
                if (mOnItemScaleChangeListener != null) {
                    int position = getPosition(child);
                    Log.i("Jared", "Position: " + position + ", apply scale: " + scale + " , absDistance: " + absDistance);
                    mOnItemScaleChangeListener.onScale(position, scale);
                }
                if (scale == mMaxScale) {
                    if (mOnItemSelectedListener != null) {
                        int position = getPosition(child);
                        mOnItemSelectedListener.onSelected(position);
                    }
                }
//
//                ViewGroup.LayoutParams layoutParam = child.getLayoutParams();
//                layoutParam.width = (int) (layoutParam.width * scale);
//                layoutParam.height = (int) (layoutParam.height * scale);
//                child.setLayoutParams(layoutParam);
//                child.getParent().requestLayout();
//                child.setScaleX(scale);
//                child.setScaleY(scale);

            }
        }
    }

    private void onScrollIdle() {
        Nearest nearest = getNearestScrollOffset();
        Log.i("JARED", "Nearest position: " + nearest.getNearestPosition());
        scrollViewToCenter(nearest);
    }

    private Nearest getNearestScrollOffset() {
        int nearestPosition = 0;
        float shortestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof PlaceholderView)) {
                float distance = getCenterRelativePositionOf(child);
                float absDistance = Math.abs(distance);
                if (absDistance < shortestDistance) {
                    shortestDistance = absDistance;
                    nearestPosition = getPosition(child);
                }
            }
        }
        //回到起始位置
        if (nearestPosition == 1) {
            if (shortestDistance > 0 && shortestDistance > (mChildMaxWidth + mChildStartWidth) / 2) {
                nearestPosition = 0;
            }
        }
        return new Nearest(nearestPosition);
    }

    private void scrollViewToCenter(final Nearest nearest) {
        SelectorLinearSmoothScroller smoothScroller = new SelectorLinearSmoothScroller(mContext);
        if (mCurrentPosition > nearest.getNearestPosition()) {
            mIsReverse = true;
        } else if (mCurrentPosition < nearest.getNearestPosition()) {
            mIsReverse = false;
        }
        mCurrentPosition = nearest.getNearestPosition();
        Log.i("Jared", "Current Position changed: " + mCurrentPosition + ", mIsReverse: " + mIsReverse);
        smoothScroller.setTargetPosition(mCurrentPosition);
        startSmoothScroll(smoothScroller);
    }

    public void smoothScrollToPosition(int position) {
        View child = findViewByPosition(position);
        if (!(child instanceof PlaceholderView)) {
            scrollViewToCenter(new Nearest(position));
        }
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    private class SelectorLinearSmoothScroller extends LinearSmoothScroller {

        public SelectorLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDxToMakeVisible(View view, int snapPreference) {
            int nearestOffset;
            if (mCurrentPosition != 0) {
                nearestOffset = (int) (-getCenterRelativePositionOf(view));
            } else {
                //回到起始位置
                nearestOffset = (int) (-getStartRelativePositionOf(view));
            }
            Log.i("Jared", "nearest Offset: " + nearestOffset);
            return nearestOffset;
        }

        @Override
        public int calculateDyToMakeVisible(View view, int snapPreference) {
            return 0;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            int maxScrollRange = getWidth() / 2;
            float dist = Math.min(Math.abs(dx), maxScrollRange);
            return (int) (dist / maxScrollRange * 200);
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return new PointF(0, 0);
        }
    }

    public void setOnItemScaleChangeListener(OnItemScaleChangeListener onItemScaleChangeListener) {
        this.mOnItemScaleChangeListener = onItemScaleChangeListener;
    }

    public interface OnItemScaleChangeListener {
        void onScale(int position, float scale);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onSelected(int position);
    }

//    public void startExpandAnim(RecyclerView view, int expandPosition) {
//        LinearLayoutManager layoutManager = (LinearLayoutManager) view.getLayoutManager();
//        final int firstVisible = layoutManager.findFirstVisibleItemPosition();
//        final int lastVisible = layoutManager.findLastVisibleItemPosition();
//        if (firstVisible == -1 || lastVisible == -1) {
//            return;
//        }
//        for (int i = firstVisible; i <= lastVisible; i++) {
//            final View child = layoutManager.findViewByPosition(i);
//            //final int startPosition = expandPosition - child.getWidth() * i;
//            final int startPosition = expandPosition - child.getLeft();
//
//            ScaleAnimation scale = new ScaleAnimation(.5f, 1f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            TranslateAnimation keepTransPosition = new TranslateAnimation(startPosition, startPosition, 0, 0);
//            AnimationSet set = new AnimationSet(true);
//            set.setInterpolator(new AccelerateInterpolator());
//            set.setDuration(1000);
//            set.addAnimation(scale);
//            set.addAnimation(keepTransPosition);
//            child.startAnimation(set);
//
//
//            set.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    TranslateAnimation anim = new TranslateAnimation(startPosition, 0, 0, 0);
//                    anim.setDuration(1000);
//                    anim.setInterpolator(new OvershootInterpolator());
//                    child.startAnimation(anim);
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//
//        }
//    }

}
