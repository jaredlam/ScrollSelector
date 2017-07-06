package ss.jaredluo.com.stickerselector.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ss.jaredluo.com.stickerselector.layout.SelectorLayoutManager;
import ss.jaredluo.com.stickerselector.utils.ScreenUtils;
import ss.jaredluo.com.stickerselector.view.PlaceholderView;

/**
 * Created by admin on 2017/7/4.
 */

public abstract class ScrollSelectorAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_DATA = 0x10;
    private static final int ITEM_TYPE_PLACE_HOLDER = 0x11;

    private final List<T> mData;

    public ScrollSelectorAdapter(List<T> data) {
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View dataView = LayoutInflater.from(parent.getContext()).inflate(getItemResourceId(), parent, false);
        if (viewType == ITEM_TYPE_PLACE_HOLDER) {
            PlaceholderView view = new PlaceholderView(parent.getContext());
            view.setBackgroundResource(android.R.color.transparent);
            int width = ScreenUtils.getScreenWidth();
            int dataItemWidth = dataView.getLayoutParams().width;
            view.setLayoutParams(new ViewGroup.LayoutParams(width / 2 + dataItemWidth / 2, 1));
            return new ViewHolderPlaceHolder(view);
        } else {
            dataView.setScaleX(SelectorLayoutManager.getInitScale());
            dataView.setScaleY(SelectorLayoutManager.getInitScale());
            return createViewHolder(dataView);
        }

    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            int type = getItemViewType(position);
            if (type == ITEM_TYPE_DATA) {
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
