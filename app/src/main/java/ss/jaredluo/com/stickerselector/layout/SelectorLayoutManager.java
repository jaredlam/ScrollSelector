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
import android.widget.RelativeLayout;
import android.widget.TextView;

import ss.jaredluo.com.stickerselector.model.Nearest;
import ss.jaredluo.com.stickerselector.utils.ScreenUtils;
import ss.jaredluo.com.stickerselector.view.PlaceholderView;

/**
 * Created by admin on 2017/7/6.
 */

public class SelectorLayoutManager extends LinearLayoutManager {

    private float mMaxScale = 1.5f;

    private OnItemScaleChangeListener mOnItemScaleChangeListener;

    private Point recyclerCenter = new Point();
    private int mChildMaxWidth;
    private Context mContext;
    private int mChildStartWidth;
    private boolean isLayout;

    public SelectorLayoutManager(Context context) {
        super(context);
        this.mContext = context;
    }

    public SelectorLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mContext = context;
    }

    public SelectorLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
    }

    public float getMaxScale() {
        return mMaxScale;
    }

    public void setMaxScale(float maxScale) {
        mMaxScale = maxScale;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        updateRecyclerDimensions();
        initChildSize(recycler);
//        applyItemTransformToChildren();
    }


    private void initChildSize(RecyclerView.Recycler recycler) {
        if(!isLayout) {
            View child = recycler.getViewForPosition(1);
            measureChildWithMargins(child, 0, 0);
            mChildStartWidth = child.getMeasuredWidth();
            mChildMaxWidth = (int) (mChildStartWidth * mMaxScale);
            isLayout = true;
        }
    }

    private void updateRecyclerDimensions() {
        recyclerCenter.set(getWidth() / 2, getHeight() / 2);
    }

    private float getCenterRelativePositionOf(View v) {
        RelativeLayout layout = (RelativeLayout) v;
        if (((TextView) layout.getChildAt(0)).getText().equals("Sticker 2")) {
            Log.i("Jared", "now position 2 width is: " + layout.getWidth());
        }
        return getDecoratedLeft(v) + v.getWidth() / 2 - recyclerCenter.x;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int result = super.scrollHorizontallyBy(dx, recycler, state);
        Log.i("Jared", "scrollHorizontallyBy: " + result);
        applyItemTransformToChildren();
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
        return new Nearest(nearestPosition);
    }

    private void scrollViewToCenter(final Nearest nearest) {
        SelectorLinearSmoothScroller smoothScroller = new SelectorLinearSmoothScroller(mContext);
        smoothScroller.setTargetPosition(nearest.getNearestPosition());
        startSmoothScroll(smoothScroller);
    }

    public void smoothScrollToPosition(int position) {
        View child = findViewByPosition(position);
        if (!(child instanceof PlaceholderView)) {
            scrollViewToCenter(new Nearest(position));
        }

    }

    private class SelectorLinearSmoothScroller extends LinearSmoothScroller {

        public SelectorLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDxToMakeVisible(View view, int snapPreference) {
            return (int) (-getCenterRelativePositionOf(view));
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

}
