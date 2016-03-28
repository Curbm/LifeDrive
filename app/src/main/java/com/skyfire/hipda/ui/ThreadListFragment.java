package com.skyfire.hipda.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.skyfire.hipda.R;
import com.skyfire.hipda.bean.ThreadListItem;
import com.skyfire.hipda.lib.WrapImageLoader;
import com.skyfire.hipda.misc.PrefHelper;
import com.skyfire.hipda.misc.Util;
import com.skyfire.hipda.ui.abs.AbsListFragment;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.skyfire.hipda.api.ApiList.api;

public class ThreadListFragment extends AbsListFragment<List<ThreadListItem>> {

  List<ThreadListItem> mList = new ArrayList<>();
  int mForumId;
  int mPageCount;
  boolean mAllLoaded;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mForumId = getArguments().getInt("id");
    refresh();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
  Bundle savedInstanceState) {
    return inflater.inflate(R.layout.thread_list_fragment, container, false);
  }

  @Override
  protected RecyclerView.ItemDecoration createItemDecoration() {
    return new HorizontalDividerItemDecoration.Builder(getContext())
        .colorResId(R.color.colorDivider)
        .size(1)
        .margin((int) Util.dp(getContext(), 72), (int) Util.dp(getContext(), 16))
        .build();
  }

  @Override
  protected void onItemClick(RecyclerView parent, View view, int position) {
    ThreadListItem item = mList.get(position);
    Intent intent = new Intent(getContext(), PostListActivity.class);
    intent.putExtra("id", item.getId());
    startActivity(intent);
  }

  @Override
  protected RecyclerView.Adapter createAdapter() {
    return new RecyclerView.Adapter<ItemHolder>() {
      @Override
      public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.thread_list_item, parent,
            false);
        return new ItemHolder(view);
      }

      @Override
      public void onBindViewHolder(ItemHolder holder, int position) {
        ThreadListItem item = mList.get(position);

        WrapImageLoader.get(getContext())
            .load(item.getAvatarUrl())
            .cacheInMemory()
            .cacheOnDisk()
            .loadingImage(R.drawable.default_avatar)
            .into(holder.mAvatarIV);
        holder.mSubjectTV.setText(item.getSubject());
        holder.mAuthorTV.setText(item.getAuthor());
        String timeStr = DateUtils.getRelativeTimeSpanString(item.getPublishTime().getTime(),
            System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS).toString();
        holder.mPublishTimeTV.setText(timeStr);
        String metaStr = getString(R.string.thread_list_meta, item.getCommentNum(), item
            .getViewNum());
        holder.mMetaTV.setText(metaStr);


        bindItemClickListener(holder.itemView);
      }

      @Override
      public int getItemCount() {
        return mList.size();
      }

    };
  }

  static class ItemHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.subject_tv)
    TextView mSubjectTV;
    @Bind(R.id.avatar_iv)
    ImageView mAvatarIV;
    @Bind(R.id.meta_tv)
    TextView mMetaTV;
    @Bind(R.id.author_tv)
    TextView mAuthorTV;
    @Bind(R.id.publish_time_tv)
    TextView mPublishTimeTV;


    public ItemHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  @Override
  protected Observable<List<ThreadListItem>> createLoadNewObservable() {
    return api().getThreadList(mForumId, 1)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(this.<List<ThreadListItem>>bindOnDestroy());
  }

  @Override
  protected Observable<List<ThreadListItem>> createLoadOldObservable() {
    return api().getThreadList(mForumId, mPageCount + 1)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(this.<List<ThreadListItem>>bindOnDestroy());
  }

  @Override
  protected void onPostLoadOld(List<ThreadListItem> list) {
    mList.addAll(list);
    mAllLoaded = list.size() < PrefHelper.getThreadPageCount();
    mPageCount++;
  }

  @Override
  protected void onPostLoadNew(List<ThreadListItem> list) {
    mList.clear();
    mList.addAll(list);
    mPageCount = 1;
    mAllLoaded = list.size() < PrefHelper.getThreadPageCount();
  }

  @Override
  protected boolean allLoaded() {
    return mAllLoaded;
  }
}
