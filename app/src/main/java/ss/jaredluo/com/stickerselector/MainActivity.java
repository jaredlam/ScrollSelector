package ss.jaredluo.com.stickerselector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ss.jaredluo.com.stickerselector.adapter.RoundSelectorAdapter;
import ss.jaredluo.com.stickerselector.layout.SelectorLayoutManager;
import ss.jaredluo.com.stickerselector.view.RoundSelectorView;

public class MainActivity extends AppCompatActivity {

    private RoundSelectorView mRecyclerView;
    private SelectorLayoutManager mLayoutManager;
    private RoundSelectorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RoundSelectorView) findViewById(R.id.recycler_view);
        mLayoutManager = new SelectorLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            data.add(i + "");
        }
        mAdapter = new RoundSelectorAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
    }
}
