package com.skyfire.hipda.ui.abs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.skyfire.hipda.R;
import com.skyfire.hipda.lib.WrapImageLoader;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public abstract class AbsListFragment<T extends List> extends AbsFragment {

  private SwipeRefreshLayout mSwipeLayout;
  private RecyclerView mRecyclerView;
  private View mEmptyView;
  private View mFooterLoadMore;

  private RecyclerView.Adapter mAdapter;
  private boolean mListEmpty;

  private Subscription mLoadNewSubscription;
  private Subscription mLoadOldSubscription;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAdapter = createAdapter();
  }

  SwipeRefreshLayout.OnRefreshListener mRefreshListener =
      new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
          loadNew();
        }
      };

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    mFooterLoadMore = view.findViewById(R.id.load_more_footer_view);
    mEmptyView = view.findViewById(R.id.empty_view);

    mSwipeLayout.setOnRefreshListener(mRefreshListener);
    mSwipeLayout.setColorSchemeResources(R.color.colorAccent);
    mSwipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setOnScrollListener(mScrollListener);
    RecyclerView.ItemDecoration decor = createItemDecoration();
    if (decor != null) {
      mRecyclerView.addItemDecoration(decor);
    }

    if (!isStopped(mLoadNewSubscription)) {
      mSwipeLayout.post(new Runnable() {
        @Override
        public void run() {
          mSwipeLayout.setRefreshing(true);
        }
      });
    }
    if (!isStopped(mLoadOldSubscription)) {
      if (mFooterLoadMore != null) {
        mFooterLoadMore.setVisibility(View.VISIBLE);
      }
    }
    if (!mListEmpty) {
      if (mEmptyView != null) {
        mEmptyView.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mEmptyView = null;
    mRecyclerView = null;
    mSwipeLayout = null;
    mFooterLoadMore = null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  protected void refresh() {
    loadNew();
  }

  private void loadNew() {
    if (!isStopped(mLoadNewSubscription)) {
      return;
    }
    Observable<T> observable = createLoadNewObservable();
    mLoadNewSubscription = observable.subscribe(new Subscriber<T>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        if (mSwipeLayout != null) {
          mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
              mSwipeLayout.setRefreshing(false);
            }
          });
        }
      }

      @Override
      public void onNext(T t) {
        onPostLoadNew(t);
        mAdapter.notifyDataSetChanged();
        mListEmpty = t.isEmpty();

        if (mEmptyView != null) {
          mEmptyView.setVisibility(mListEmpty ? View.VISIBLE : View.GONE);
        }
        if (mSwipeLayout != null) {
          mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
              mSwipeLayout.setRefreshing(false);
            }
          });
        }
      }
    });
  }

  private void loadOld() {
    if (!isStopped(mLoadOldSubscription)) {
      return;
    }
    mFooterLoadMore.setVisibility(View.VISIBLE);
    Observable<T> observable = createLoadOldObservable();
    mLoadOldSubscription = observable.subscribe(new Subscriber<T>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        if (mFooterLoadMore != null) {
          mFooterLoadMore.setVisibility(View.GONE);
        }
      }

      @Override
      public void onNext(T t) {
        onPostLoadOld(t);
        mAdapter.notifyDataSetChanged();
        if (mFooterLoadMore != null) {
          mFooterLoadMore.setVisibility(View.GONE);
        }
      }
    });
  }

  private boolean isStopped(Subscription s) {
    return s == null || s.isUnsubscribed();
  }

  protected RecyclerView.ItemDecoration createItemDecoration() {
    return null;
  }

  protected void bindItemClickListener(View child) {
    child.setOnClickListener(mClickListener);
  }

  protected abstract RecyclerView.Adapter createAdapter();

  protected abstract Observable<T> createLoadNewObservable();

  protected abstract Observable<T> createLoadOldObservable();

  protected abstract void onPostLoadOld(T list);

  protected abstract void onPostLoadNew(T list);

  protected abstract boolean allLoaded();

  protected void onItemClick(RecyclerView parent, View view, int position) {
  }

  View.OnClickListener mClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      int position = mRecyclerView.getChildAdapterPosition(v);
      if (position != RecyclerView.NO_POSITION) {
        onItemClick(mRecyclerView, v, position);
      }
    }
  };

  RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      if (newState == SCROLL_STATE_IDLE) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVis = manager.findLastVisibleItemPosition();
        int count = manager.getItemCount();
        if (lastVis != RecyclerView.NO_POSITION && lastVis == count - 1 && !allLoaded()) {
          loadOld();
        }
        WrapImageLoader.get(getContext()).getLoader().resume();
      } else {
        WrapImageLoader.get(getContext()).getLoader().pause();
      }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
    }
  };

}
