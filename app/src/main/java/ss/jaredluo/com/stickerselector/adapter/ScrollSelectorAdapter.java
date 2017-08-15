package ss.jaredluo.com.stickerselector.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import ss.jaredluo.com.stickerselector.R;
import ss.jaredluo.com.stickerselector.layout.SelectorLayoutManager;
import ss.jaredluo.com.stickerselector.utils.ScreenUtils;
import ss.jaredluo.com.stickerselector.view.PlaceholderView;
import ss.jaredluo.com.stickerselector.view.ScrollSelectorView;

/**
 * Created by admin on 2017/7/4.
 */


public abstract class ScrollSelectorAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_DATA = 0x10;
    private static final int ITEM_TYPE_PLACE_HOLDER = 0x11;

    private final List<T> mData;
    private final SelectorLayoutManager mLayoutManager;
    private int mDataItemWidth;
    private int mDataItemHeight;
    private int mFullHeight;

    public ScrollSelectorAdapter(List<T> data, SelectorLayoutManager layoutManager) {
        mData = data;
        mLayoutManager = layoutManager;

    }

    public SelectorLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        View dataView = LayoutInflater.from(recyclerView.getContext()).inflate(getItemResourceId(), recyclerView, false);
        mFullHeight = (int) (mLayoutManager.getMaxScale() * dataView.getLayoutParams().height);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = mFullHeight;
        recyclerView.setLayoutParams(params);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View dataView = LayoutInflater.from(parent.getContext()).inflate(getItemResourceId(), parent, false);
        if (viewType == ITEM_TYPE_PLACE_HOLDER) {
            PlaceholderView view = new PlaceholderView(parent.getContext());
            view.setBackgroundResource(android.R.color.transparent);
            int width = ScreenUtils.getScreenWidth();
            mDataItemWidth = dataView.getLayoutParams().width;
            mDataItemHeight = dataView.getLayoutParams().height;
            view.setLayoutParams(new ViewGroup.LayoutParams((int) (width / 2 + mDataItemWidth * mLayoutManager.getMaxScale() / 2 + mLayoutManager.getMarginToCenter()), 1));
            return new ViewHolderPlaceHolder(view);
        } else {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) dataView.getLayoutParams();
            int margin = (mFullHeight - layoutParams.height) / 2;
            layoutParams.topMargin = margin;
            layoutParams.bottomMargin = margin;
            dataView.setLayoutParams(layoutParams);
            return createViewHolder(dataView);
        }

    }

    @Override
    final public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder != null) {
            int type = getItemViewType(position);
            if (type == ITEM_TYPE_DATA) {
                holder.itemView.setTranslationX(0);
                onBindData((VH) holder, position);
            }
        }
    }

    public boolean isCurrentPosition(int position) {
        return position == mLayoutManager.getCurrentPosition();
    }

    public abstract RecyclerView.ViewHolder createViewHolder(View dataView);

    public abstract int getItemResourceId();

    public abstract void onBindData(VH holder, int position);

    public int getRealItemCount() {
        return mData.size();
    }

    @Override
    final public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount() - 1) {
            return ITEM_TYPE_PLACE_HOLDER;
        } else {
            return ITEM_TYPE_DATA;
        }
    }


    @Override
    final public int getItemCount() {
        return mData.size() + 2;
    }


    private static class ViewHolderPlaceHolder extends RecyclerView.ViewHolder {
        private ViewHolderPlaceHolder(View itemView) {
            super(itemView);
        }
    }
}
