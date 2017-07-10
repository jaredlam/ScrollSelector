package ss.jaredluo.com.stickerselector;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ss.jaredluo.com.stickerselector.adapter.ScrollSelectorAdapter;
import ss.jaredluo.com.stickerselector.layout.SelectorLayoutManager;
import ss.jaredluo.com.stickerselector.view.CircleSelector;

/**
 * Created by admin on 2017/7/6.
 */

public class MainAdapter extends ScrollSelectorAdapter<String, MainAdapter.ViewHolderData> {

    public MainAdapter(List<String> data, SelectorLayoutManager layoutManager) {
        super(data, layoutManager);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View dataView) {
        return new MainAdapter.ViewHolderData(dataView);
    }

    @Override
    public int getItemResourceId() {
        return R.layout.round_selector_item;
    }

    @Override
    public void onBindData(final ViewHolderData holder, int position) {
        final CircleSelector selector = (CircleSelector) holder.itemView;
        if (position == getLayoutManager().getCurrentPosition()) {
            selector.setProgressWithoutAnimation(0);
            selector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selector.setProgress(100);
                }
            });
            selector.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SelectorLayoutManager layoutManager = getLayoutManager();
                    if (layoutManager.isHideUnSelected()) {
                        layoutManager.hideUnSelected();
                    } else {
                        layoutManager.showUnSelected();
                    }
                    return true;
                }
            });
        } else {
            selector.setProgressWithoutAnimation(100);
            selector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLayoutManager().smoothScrollToPosition(holder.getAdapterPosition());
                }
            });
        }
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {

        public ViewHolderData(View dataView) {
            super(dataView);
        }
    }
}
