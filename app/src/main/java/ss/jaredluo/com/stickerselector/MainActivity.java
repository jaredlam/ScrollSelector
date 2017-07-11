package ss.jaredluo.com.stickerselector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ss.jaredluo.com.stickerselector.adapter.ScrollSelectorAdapter;
import ss.jaredluo.com.stickerselector.layout.SelectorLayoutManager;
import ss.jaredluo.com.stickerselector.utils.ScreenUtils;
import ss.jaredluo.com.stickerselector.view.ScrollSelectorView;

public class MainActivity extends AppCompatActivity {

    private ScrollSelectorView mRecyclerView;
    private SelectorLayoutManager mLayoutManager;
    private ScrollSelectorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (ScrollSelectorView) findViewById(R.id.recycler_view);
        mRecyclerView.setSpace((int) ScreenUtils.convertDpToPx(10));
        mLayoutManager = new SelectorLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setMaxScale(1.5f);
        mLayoutManager.setMarginToCenter(ScreenUtils.convertDpToPx(20));
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            data.add(i + "");
        }
        mAdapter = new MainAdapter(data, mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
