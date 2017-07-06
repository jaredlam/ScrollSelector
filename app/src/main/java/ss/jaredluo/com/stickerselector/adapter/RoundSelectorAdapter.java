package ss.jaredluo.com.stickerselector.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ss.jaredluo.com.stickerselector.R;
import ss.jaredluo.com.stickerselector.layout.SelectorLayoutManager;
import ss.jaredluo.com.stickerselector.utils.ScreenUtils;

/**
 * Created by admin on 2017/7/4.
 */

public class RoundSelectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_DATA = 0x10;
    private static final int ITEM_TYPE_PLACE_HOLDER = 0x11;

    private final List<String> mData;

    public RoundSelectorAdapter(List<String> data) {
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_PLACE_HOLDER) {
            View view = new View(parent.getContext());
            view.setBackgroundResource(android.R.color.transparent);
            int width = ScreenUtils.getScreenWidth();
            view.setLayoutParams(new ViewGroup.LayoutParams(width / 2 + 50 / 2, 1));
            return new ViewHolderPlaceHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.round_selector_item, parent, false);
            view.setScaleX(SelectorLayoutManager.INIT_SCALE);
            view.setScaleY(SelectorLayoutManager.INIT_SCALE);
            return new ViewHolderData(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount() - 1) {
            return ITEM_TYPE_PLACE_HOLDER;
        } else {
            return ITEM_TYPE_DATA;
        }
    }


    @Override
    public int getItemCount() {
        return mData.size() + 2;
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {

        public ViewHolderData(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolderPlaceHolder extends RecyclerView.ViewHolder {

        public ViewHolderPlaceHolder(View itemView) {
            super(itemView);
        }
    }
}
