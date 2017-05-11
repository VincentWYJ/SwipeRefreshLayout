package com.dylan.swiperefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.swiperefreshlayout)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recyclerview)
    public RecyclerView mRecyclerView;

    private List<String> mDataListS;
    private ListAdapter mListAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private int mLastVisibleItem = 0;
    private Random random = new Random();
    private boolean mIsDown2Up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mDataListS = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            mDataListS.add("item " + i);
        }
        mListAdapter = new ListAdapter(this, mDataListS);
        mListAdapter.setOnItemClickListener(new ListAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, mDataListS.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new ListDecoration(this, ListDecoration.VERTICAL_LIST));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mIsDown2Up && (mLastVisibleItem + 2 == mListAdapter.getItemCount() ||
                            mLastVisibleItem + 1 == mListAdapter.getItemCount())) {
                        getDataByRxJava();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mIsDown2Up = dy > 0 ? true : false;
                mLastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getDataByHandler();
            }
        });
    }

    private void getDataByHandler() {
        final int seed = 100;
        final int index = 0;
        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mDataListS.add(index, "item " + random.nextInt(seed));
                mListAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    private void getDataByRxJava() {
        mListAdapter.updateAdapter();
        int seed1 = 1000, seed2 = 10000;
        final int index = mDataListS.size();
        Observable.from(new Integer[]{seed1, seed2})
                .map(new Func1<Integer, Integer>() {

                    @Override
                    public Integer call(Integer seed) {
//                        double result = 10/0;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return random.nextInt(seed);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {  //onNext

                    @Override
                    public void call(Integer data) {
                        mDataListS.add(index, "item " + data);
                    }
                }, new Action1<Throwable>() {  //onError

                    @Override
                    public void call(Throwable e) {
                        mListAdapter.updateAdapter();
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {  //onComplete

                    @Override
                    public void call() {
                        mListAdapter.updateAdapter();
                        Toast.makeText(MainActivity.this, "Complete!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
