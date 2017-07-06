package ss.jaredluo.com.stickerselector;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ss.jaredluo.com.stickerselector.adapter.ScrollSelectorAdapter;

/**
 * Created by admin on 2017/7/6.
 */

public class MainAdapter extends ScrollSelectorAdapter<String, MainAdapter.ViewHolderData> {

    public MainAdapter(List<String> data) {
        super(data);
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
    public void onBindData(ViewHolderData holder, int position) {
        holder.textView.setText("Sticker " + position);
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolderData(View dataView) {
            super(dataView);
            textView = (TextView) dataView.findViewById(R.id.name);
        }
    }
}
