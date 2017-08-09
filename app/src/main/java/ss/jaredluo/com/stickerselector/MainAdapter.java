package ss.jaredluo.com.stickerselector;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ss.jaredluo.com.stickerselector.adapter.ScrollSelectorAdapter;
import ss.jaredluo.com.stickerselector.layout.SelectorLayoutManager;
import ss.jaredluo.com.stickerselector.view.CircleSelectorSelector;

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
        final CircleSelectorSelector selector = holder.selector;

        selector.setProgressWithoutAnimation(100);
        selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutManager().smoothScrollToPosition(holder.getAdapterPosition());
            }
        });

        holder.posTv.setText(position + "");
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {

        private final TextView posTv;
        private final CircleSelectorSelector selector;

        public ViewHolderData(View dataView) {
            super(dataView);
            posTv = (TextView) dataView.findViewById(R.id.position);
            selector = (CircleSelectorSelector) dataView.findViewById(R.id.circle_selector);
        }
    }
}
