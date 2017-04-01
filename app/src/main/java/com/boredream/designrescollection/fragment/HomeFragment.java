package com.boredream.designrescollection.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boredream.bdcodehelper.adapter.LoadMoreAdapter;
import com.boredream.bdcodehelper.entity.ListResponse;
import com.boredream.bdcodehelper.net.ObservableDecorator;
import com.boredream.bdcodehelper.utils.DisplayUtils;
import com.boredream.bdcodehelper.utils.TitleBuilder;
import com.boredream.bdcodehelper.view.GridSpacingDecorator;
import com.boredream.designrescollection.R;
import com.boredream.designrescollection.adapter.DesignResAdapter;
import com.boredream.designrescollection.base.BaseFragment;
import com.boredream.designrescollection.constants.CommonConstants;
import com.boredream.designrescollection.entity.DesignRes;
import com.boredream.designrescollection.net.HttpRequest;
import com.boredream.designrescollection.net.SimpleSubscriber;

import java.util.ArrayList;

import rx.Observable;

public class HomeFragment extends BaseFragment {

    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private int mCurrentPage = 1;
    private ArrayList<DesignRes> mData = new ArrayList<>();
    private LoadMoreAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = View.inflate(activity, R.layout.frag_home, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        new TitleBuilder(mRootView).setTitleText(getString(R.string.tab1));

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.srl);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(1);
            }
        });

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rv);
        mRecyclerView.addItemDecoration(new GridSpacingDecorator(DisplayUtils.dp2px(activity, 8)));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new LoadMoreAdapter(mRecyclerView,
                new DesignResAdapter(activity, mData),
                new LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        loadData(mCurrentPage + 1);
                    }
                });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        showProgressDialog();
        loadData(1);
    }

    private void loadData(final int page) {
        Observable<ListResponse<DesignRes>> observable = HttpRequest.getDesignRes(page);
        ObservableDecorator.decorate(observable).subscribe(
                new SimpleSubscriber<ListResponse<DesignRes>>(activity) {
                    @Override
                    public void onNext(ListResponse<DesignRes> response) {
                        mCurrentPage = page;

                        mSwipeRefreshLayout.setRefreshing(false);
                        dismissProgressDialog();

                        if (page == 1) {
                            mData.clear();
                        }
                        setResponse(response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);

                        mSwipeRefreshLayout.setRefreshing(false);
                        dismissProgressDialog();
                    }
                });
    }

    private void setResponse(ListResponse<DesignRes> response) {
        mData.addAll(response.getResults());

        // 设置是否已加载完全部数据状态
        mAdapter.setStatus(response.getResults().size() == CommonConstants.COUNT_OF_PAGE ? LoadMoreAdapter.STATUS_HAVE_MORE : LoadMoreAdapter.STATUS_LOADED_ALL);
        mAdapter.notifyDataSetChanged();
    }
}
