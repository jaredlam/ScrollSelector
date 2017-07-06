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

import ss.jaredluo.com.stickerselector.model.Nearest;

/**
 * Created by admin on 2017/7/6.
 */

public class SelectorLayoutManager extends LinearLayoutManager {

    private static final float MAX_SCALE_DIFF = 0.4f;
    public static final float INIT_SCALE = 0.6f;

    private Point recyclerCenter = new Point();
    private int mChildHalfWidth;
    private Context mContext;

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

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        updateRecyclerDimensions();
        initChildSize(recycler);
        applyItemTransformToChildren();
    }

    private void initChildSize(RecyclerView.Recycler recycler) {
        View child = recycler.getViewForPosition(1);
        addView(child);
        measureChildWithMargins(child, 0, 0);

        mChildHalfWidth = getDecoratedMeasuredWidth(child) / 2;
    }

    private void updateRecyclerDimensions() {
        recyclerCenter.set(getWidth() / 2, getHeight() / 2);
    }

    private float getCenterRelativePositionOf(View v) {
        float distanceFromCenter = getDecoratedLeft(v) + mChildHalfWidth - recyclerCenter.x;

        return distanceFromCenter;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int result = super.scrollHorizontallyBy(dx, recycler, state);
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
            if (i == 0 || i == getChildCount() - 1) {
                continue;
            }
            View child = getChildAt(i);
            float absDistance = Math.abs(getCenterRelativePositionOf(child));

            float scale = INIT_SCALE;
            float centerWidth = mChildHalfWidth * 2;
            if (absDistance <= centerWidth) {
                float closeFactorToCenter = 1 - absDistance / centerWidth;
                scale += MAX_SCALE_DIFF * closeFactorToCenter;
                if (i == 2 && scale < 1) {
                    Log.i("Jared", "scale: " + scale);
                    Log.i("Jared", "closeFactorToCenter: " + closeFactorToCenter);
                }
            }

            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }

    private void onScrollIdle() {
        Nearest nearest = getNearestScrollOffset();
        Log.i("JARED", "Nearest offset: " + nearest.getNearestOffset());
        scrollViewToCenter(nearest);
    }

    private Nearest getNearestScrollOffset() {
        float nearestOffset = 0;
        int nearestPosition = 0;
        float shortestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < getChildCount(); i++) {
            if (i == 0 || i == getChildCount() - 1) {
                continue;
            }
            View child = getChildAt(i);
            float distance = getCenterRelativePositionOf(child);
            float absDistance = Math.abs(distance);
            if (absDistance < shortestDistance) {
                shortestDistance = absDistance;
                nearestOffset = distance;
                nearestPosition = i;
            }
        }
        return new Nearest(nearestPosition, nearestOffset);
    }

    private void scrollViewToCenter(final Nearest nearest) {
        SelectorLinearSmoothScroller smoothScroller = new SelectorLinearSmoothScroller(mContext, nearest.getNearestOffset());
        smoothScroller.setTargetPosition(nearest.getNearestPosition());
        startSmoothScroll(smoothScroller);
    }

    private class SelectorLinearSmoothScroller extends LinearSmoothScroller {
        private float pendingScroll = 0;

        public SelectorLinearSmoothScroller(Context context, float pendingScroll) {
            super(context);
            this.pendingScroll = pendingScroll;
        }

        @Override
        public int calculateDxToMakeVisible(View view, int snapPreference) {
            return (int) -pendingScroll;
        }

        @Override
        public int calculateDyToMakeVisible(View view, int snapPreference) {
            return 0;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            int maxScrollRange = getWidth() / 2;
            float dist = Math.min(Math.abs(dx), maxScrollRange);
            return (int) (dist / maxScrollRange * 500);
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return new PointF(pendingScroll, 0);
        }
    }


}