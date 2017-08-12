package ss.jaredluo.com.stickerselector.layout;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import ss.jaredluo.com.stickerselector.model.Nearest;
import ss.jaredluo.com.stickerselector.view.PlaceholderView;

/**
 * Created by admin on 2017/7/6.
 */


public class SelectorLayoutManager extends LinearLayoutManager {

    private static final int MSG_ON_SELECTION = 0x01;
    private static final int NO_SELECTION = -1;
    private static final int NO_CANCELED_SELECTION = -2;

    private float mMaxScale = 1.5f;

    private float mMarginToCenter = 100;

    private OnItemSelectedListener mOnItemSelectedListener;

    private Point recyclerCenter = new Point();
    private int mChildMaxWidth;
    private Context mContext;
    private int mChildStartWidth;
    private boolean isLayout;
    private boolean mIsReverse;
    private int mTargetPosition;
    private int mCurrentPosition = NO_SELECTION;
    private boolean mIsShowingUnSelected = true;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalListener;
    private SelectionEventHandler mHandler;

    private SparseArray<Float> mScaleMap;
    private RecyclerView mRecyclerView;
    private float mLastChildTranslationX;
    private float mFirstChildTranslationX;
    private int mCanceledPosition = -1;

    public SelectorLayoutManager(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public SelectorLayoutManager(Context context, int orientation, boolean reverseLayout, RecyclerView recyclerView) {
        super(context, orientation, reverseLayout);
        this.mContext = context;
        this.mRecyclerView = recyclerView;
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

    private float getCenterRelativeDistanceOf(View v) {

        float scaledWidth = v.getWidth() * v.getScaleX();
        float offset = (mChildMaxWidth - scaledWidth) / 2f;
        float afterTransCenter = v.getLeft() - (scaledWidth - v.getMeasuredWidth()) + scaledWidth / 2;
        float result = afterTransCenter - offset - recyclerCenter.x;
        Log.i("JaredTest", "position:" + getPosition(v) + "offset result:" + result);
        return result;
    }

    private float getCenterRelativeRealDistanceOf(View v) {

        float scaledWidth = v.getWidth() * v.getScaleX();
        float afterTransCenter = v.getLeft() - (scaledWidth - v.getMeasuredWidth()) + scaledWidth / 2;
        return afterTransCenter - recyclerCenter.x;
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
            applyItemTransformToChildren();
        }
        return result;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
        } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
            onScrollIdle();
        } else if (state == RecyclerView.SCROLL_STATE_SETTLING) {
        }
    }


    private void initScaleMap() {
        if (mScaleMap == null || mScaleMap.size() == 0) {
            mScaleMap = new SparseArray<>();
            for (int i = 1; i <= getItemCount() - 2; i++) {
                mScaleMap.put(i, 1.0f);
            }
        }
    }

    private float getScale(View child) {

//        float absDistance = Math.abs(getCenterRelativeDistanceOf(child));
        float absDistance = Math.abs(getCenterRelativeRealDistanceOf(child));
        float scale = 1f;
        float centerWidth = mChildMaxWidth / 2 + mChildStartWidth / 2;
        if (absDistance <= centerWidth) {
            float closeFactorToCenter = 1 - absDistance / centerWidth;
            scale += (mMaxScale - 1f) * closeFactorToCenter;
        }

        return scale;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);
        initScaleMap();
    }

    private void applyItemTransformToChildren() {

        boolean hasSelection = false;
        float fullOffset = (mMaxScale - 1f) * mChildStartWidth;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof PlaceholderView)) {

                float scale = getScale(child);

                int position = getPosition(child);

                Log.i("JARED", "position:" + position + ", scale:" + scale);

                child.setPivotX(child.getWidth() / 2f);
                child.setPivotY(child.getHeight() / 2f);
                child.setScaleX(scale);
                child.setScaleY(scale);

                float offset = (scale * mChildStartWidth - mChildStartWidth) / 2;
                Log.i("JaredLuo", "position:" + position + ", offset:" + offset);
                float targetTrans = -offset;
                if (targetTrans > 0) {
                    targetTrans = 0;
                }

                child.setTranslationX(targetTrans);

                if (child.getTranslationX() < 0) {
                    for (int j = 0; j < i; j++) {
                        View preChild = getChildAt(j);
                        if (!(preChild instanceof PlaceholderView)) {
                            float preTargetTrans = preChild.getTranslationX() - offset * 2;
                            if (preTargetTrans < -fullOffset) {
                                preTargetTrans = -fullOffset;
                            }
                            preChild.setTranslationX(preTargetTrans);
                        }
                    }
                }


                if (mCurrentPosition == position && mCanceledPosition != position) {
                    mCanceledPosition = position;
                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onCancelSelection(mCanceledPosition);
                    }
                }

                mScaleMap.put(position, scale);

                BigDecimal roundScale = new BigDecimal(scale).setScale(1, BigDecimal.ROUND_HALF_UP);

                if (roundScale.floatValue() > 1f || child.getLeft() < recyclerCenter.x) {
                    hasSelection = true;
                }

            }
        }

        if (!hasSelection && mCurrentPosition != NO_SELECTION) {
            postSelectionMsg(NO_SELECTION);
        }
    }

    private void select(int position) {
        if (mCurrentPosition != position) {
            if (mOnItemSelectedListener != null) {
                postSelectionMsg(position);
            }
        }
    }

    private void postSelectionMsg(int position) {
        mCurrentPosition = position;
        mCanceledPosition = NO_CANCELED_SELECTION;
        if (mHandler != null) {
            Message msg = new Message();
            msg.what = MSG_ON_SELECTION;
            msg.arg1 = position;
            mHandler.removeMessages(msg.what);
            mHandler.sendMessageDelayed(msg, 300);
        }
    }

    public SparseArray<Float> getScaleMap() {
        return mScaleMap;
    }

    private void onScrollIdle() {
        Nearest nearest = getNearestScrollOffset();
        scrollViewToCenter(nearest);
    }

    private Nearest getNearestScrollOffset() {
        int nearestPosition = 0;
        float shortestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof PlaceholderView)) {
                float distance = getCenterRelativeDistanceOf(child);
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
        if (mTargetPosition > nearest.getNearestPosition()) {
            mIsReverse = true;
        } else if (mTargetPosition < nearest.getNearestPosition()) {
            mIsReverse = false;
        }
        mTargetPosition = nearest.getNearestPosition();
        smoothScroller.setTargetPosition(mTargetPosition);
        startSmoothScroll(smoothScroller);
    }

    public void smoothScrollToPosition(int position) {
        View child = findViewByPosition(position);
        if (!(child instanceof PlaceholderView)) {
            scrollViewToCenter(new Nearest(position));
        }

    }

    public int getTargetPosition() {
        return mTargetPosition;
    }

    private class SelectorLinearSmoothScroller extends LinearSmoothScroller {

        public SelectorLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDxToMakeVisible(View view, int snapPreference) {
            int nearestOffset;
            if (mTargetPosition != 0) {
                nearestOffset = (int) (-getCenterRelativeDistanceOf(view));
                if (nearestOffset == 0) {
                    select(getPosition(view));
                }
            } else {
                //回到起始位置
                nearestOffset = (int) (-getStartRelativePositionOf(view));
            }
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

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
        mHandler = new SelectionEventHandler(mOnItemSelectedListener);
    }

    public interface OnItemSelectedListener {
        void onSelected(int position);

        void onNoSelection();

        void onCancelSelection(int position);
    }

    private static class SelectionEventHandler extends Handler {

        private final WeakReference<OnItemSelectedListener> mListener;

        SelectionEventHandler(OnItemSelectedListener listener) {
            mListener = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_ON_SELECTION) {
                OnItemSelectedListener listener = mListener.get();
                if (listener != null) {
                    int position = msg.arg1;
                    if (position == NO_SELECTION) {
                        listener.onNoSelection();
                    } else {
                        listener.onSelected(position);
                    }
                }
            }
        }
    }

}
