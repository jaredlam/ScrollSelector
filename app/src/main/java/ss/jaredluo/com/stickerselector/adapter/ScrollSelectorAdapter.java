package ss.jaredluo.com.stickerselector.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

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
    private final SparseArray<Float> mScaleMap;
    private int mDataItemWidth;
    private int mDataItemHeight;
    private int mFullHeight;

    public ScrollSelectorAdapter(List<T> data, SelectorLayoutManager layoutManager) {
        mData = data;
        mLayoutManager = layoutManager;
        mScaleMap = new SparseArray<>();
        for (int i = 0; i < mData.size(); i++) {
            mScaleMap.put(i, 1.0f);
        }
        mLayoutManager.setOnItemScaleChangeListener(new SelectorLayoutManager.OnItemScaleChangeListener() {
            @Override
            public void onScale(int position, float scale) {
                mScaleMap.put(position, scale);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });
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
            return createViewHolder(dataView);
        }

    }

    @Override
    final public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder != null) {
            int type = getItemViewType(position);
            if (type == ITEM_TYPE_DATA) {
                float scale = mScaleMap.get(position, 1f);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.width = (int) (mDataItemWidth * scale);
                layoutParams.height = (int) (mDataItemHeight * scale);
                int margin = (mFullHeight - layoutParams.height) / 2;
                layoutParams.topMargin = margin;
                layoutParams.bottomMargin = margin;
                holder.itemView.setLayoutParams(layoutParams);
                onBindData((VH) holder, position);
            }
        }
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
