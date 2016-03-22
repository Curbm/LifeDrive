package com.skyfire.hipda.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.skyfire.hipda.R;
import com.skyfire.hipda.bean.Post;
import com.skyfire.hipda.bean.content.*;
import com.skyfire.hipda.lib.ImageLoadingTracker;
import com.skyfire.hipda.lib.ImageLoadingTracker.TrackRecord;
import com.skyfire.hipda.lib.WrapImageLoader;
import com.skyfire.hipda.misc.PrefHelper;
import com.skyfire.hipda.misc.Util;
import com.skyfire.hipda.widget.CircleProgressBar;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.skyfire.hipda.api.ApiWrapper.api;

public class PostListActivity extends AbsActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new
        InnerFragment()).commit();

  }

  public static class InnerFragment extends AbsListFragment<List<Post>> {

    @Bind(R.id.tool_bar)
    Toolbar mToolbar;

    List<Post> mList = new ArrayList<>();
    int mPageCount;
    boolean mAllLoaded;
    int mThreadId;
    ImageLoadingTracker mImageLoadingTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mImageLoadingTracker = new ImageLoadingTracker(getContext(), getResources()
          .getDimensionPixelSize(R.dimen.post_image_max_size) / 2);
      mThreadId = getActivity().getIntent().getIntExtra("id", -1);
      refresh();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
      return inflater.inflate(R.layout.post_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
      ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      mImageLoadingTracker.destroy();
    }

    @Override
    protected RecyclerView.ItemDecoration createItemDecoration() {
      return new HorizontalDividerItemDecoration.Builder(getContext())
          .colorResId(android.R.color.transparent)
          .size((int) Util.dp(getContext(), 4))
          .showLastDivider()
          .build();
    }

    @Override
    protected void onItemClick(RecyclerView parent, View view, int position) {
      Post item = mList.get(position);

    }


    @Override
    protected Adapter createAdapter() {
      return new RecyclerView.Adapter<ViewHolder>() {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          View v;
          switch (viewType) {
            case 0:
              v = LayoutInflater.from(getContext())
                  .inflate(R.layout.post_list_item_first, parent, false);
              return new FirstItemHolder(v);
            case 1:
              v = LayoutInflater.from(getContext())
                  .inflate(R.layout.post_list_item_normal, parent, false);
              return new NormalItemHolder(v);
          }
          return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
          Post item = mList.get(position);

          switch (getItemViewType(position)) {
            case 0: {
              FirstItemHolder firstHolder = (FirstItemHolder) holder;
              firstHolder.mTitleTV.setText(item.getTitle());
              firstHolder.mAuthorTV.setText(item.getAuthor());
              WrapImageLoader.get(getContext())
                  .load(item.getAvatarUrl())
                  .cacheInMemory()
                  .cacheOnDisk()
                  .loadingImage(R.drawable.default_avatar)
                  .into(firstHolder.mAvatarIV);


              String publishTimeStr = DateUtils.getRelativeTimeSpanString(item.getPublishTime()
                  .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
              firstHolder.mPublishTimeTV.setText(publishTimeStr);


              if (item.getModifyTime() == null) {
                firstHolder.mModifyTimeTV.setVisibility(View.GONE);
              } else {
                firstHolder.mModifyTimeTV.setVisibility(View.VISIBLE);
                String modifiedTimeStr = DateUtils.getRelativeTimeSpanString(item
                    .getModifyTime().getTime(), System.currentTimeMillis(), DateUtils
                    .MINUTE_IN_MILLIS).toString();
                firstHolder.mModifyTimeTV.setText(getString(R.string.modified_time_text,
                    modifiedTimeStr));
              }

              bindContents(firstHolder.mContentContainer, item.getContentList(), position);
              break;
            }
            case 1: {
              NormalItemHolder normalHolder = (NormalItemHolder) holder;
              normalHolder.mAuthorTV.setText(item.getAuthor());
              WrapImageLoader.get(getContext())
                  .load(item.getAvatarUrl())
                  .cacheInMemory()
                  .cacheOnDisk()
                  .loadingImage(R.drawable.default_avatar)
                  .into(normalHolder.mAvatarIV);


              String publishTimeStr = DateUtils.getRelativeTimeSpanString(item.getPublishTime()
                  .getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
              normalHolder.mPublishTimeTV.setText(publishTimeStr);

              if (item.getModifyTime() == null) {
                normalHolder.mModifyTimeTV.setVisibility(View.GONE);
              } else {
                normalHolder.mModifyTimeTV.setVisibility(View.VISIBLE);
                String modifiedTimeStr = DateUtils.getRelativeTimeSpanString(item
                    .getModifyTime().getTime(), System.currentTimeMillis(), DateUtils
                    .MINUTE_IN_MILLIS).toString();
                normalHolder.mModifyTimeTV.setText(getString(R.string.modified_time_text,
                    modifiedTimeStr));
              }

              bindContents(normalHolder.mContentContainer, item.getContentList(), position);

              // Fix TextView measurement problem with image span
//              normalHolder.mContentTV.setIncludeFontPadding(false);
//              normalHolder.mContentTV.setIncludeFontPadding(true);
              break;
            }
          }


          bindItemClickListener(holder.itemView);
        }

        @Override
        public int getItemCount() {
          return mList.size();
        }

        @Override
        public int getItemViewType(int position) {
          return position == 0 ? 0 : 1;
        }
      };
    }

    private void bindContents(LinearLayout container, List<Content> list, int position) {
      container.removeAllViewsInLayout();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      for (Content content : list) {
        if (content instanceof TextContent) {
          TextView tv = (TextView) inflater.inflate(R.layout.post_item_content_text,
              container, false);
          tv.setText(((TextContent) content).getText());
          tv.setMovementMethod(Util.getLinkMovementMethod());
          container.addView(tv);
        } else if (content instanceof ImageContent) {
          View view = inflater.inflate(R.layout.post_item_content_image, container, false);
          ImageView imgView = (ImageView) view.findViewById(R.id.img_view);
          final LinearLayout placeHolderView =
              (LinearLayout) view.findViewById(R.id.loading_place_holder);
          final CircleProgressBar progressBar = (CircleProgressBar) placeHolderView
              .findViewById(R.id.progress_bar);
          TextView infoTV = (TextView) placeHolderView.findViewById(R.id.info_tv);

          if (content instanceof UploadImageContent) {
            infoTV.setVisibility(View.VISIBLE);
            infoTV.setText(((UploadImageContent) content).getSizeText());
          } else {
            infoTV.setVisibility(View.GONE);
          }

          ImageLoadingProgressListener progressListener = new ImageLoadingProgressListener() {

            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
              progressBar.setProgress(current / (float) total);
            }
          };

          ImageLoadingListener listener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
              placeHolderView.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
              placeHolderView.setVisibility(View.GONE);
            }
          };

          String imgUrl = ((ImageContent) content).getUrl();
          TrackRecord trackRecord = mImageLoadingTracker.track(imgUrl, imgView, listener,
              progressListener);

          if (!trackRecord.isLoading()) {
            WrapImageLoader.get(getContext())
                .load(imgUrl)
                .progressListener(mImageLoadingTracker)
                .loadingListener(mImageLoadingTracker)
                .cacheOnDisk()
                .cacheInMemory()
                .into(trackRecord.getImageAwareForLoad());
          }

          container.addView(view);
        } else if (content instanceof ReplyContent) {
          Post post = getPostById(((ReplyContent) content).getReplyId(), position);
          if (post == null) {
            continue;
          }
          SpannableStringBuilder text = new SpannableStringBuilder(post.getAuthor() + ": ");
          text.append(getContentSummary(post.getContentList()));
          View view = inflater.inflate(R.layout.post_item_content_refer, container, false);
          TextView contentTV = (TextView) view.findViewById(R.id.content_tv);
          contentTV.setText(text);
          contentTV.setMovementMethod(Util.getLinkMovementMethod());
          container.addView(view);
        } else if (content instanceof QuoteContent) {
          QuoteContent quoteContent = (QuoteContent) content;
          SpannableStringBuilder text = quoteContent.getQuoteText();
          Post post = getPostById(quoteContent.getQuoteId(), position);
          if (post != null) {
            text = new SpannableStringBuilder(post.getAuthor() + ": ").append(text);
          }
          View view = inflater.inflate(R.layout.post_item_content_refer, container, false);
          TextView contentTV = (TextView) view.findViewById(R.id.content_tv);
          contentTV.setText(text);
          contentTV.setMovementMethod(Util.getLinkMovementMethod());
          container.addView(view);
        }
      }
    }

    private Post getPostById(int id, int lastIndex) {
      for (int i = lastIndex - 1; i >= 0; i--) {
        Post post = mList.get(i);
        if (post.getId() == id) {
          return post;
        }
      }
      return null;
    }

    private SpannableStringBuilder getContentSummary(List<Content> list) {
      final int limit = 90;
      SpannableStringBuilder builder = new SpannableStringBuilder();
      for (Content content : list) {
        if (content instanceof TextContent) {
          builder.append(((TextContent) content).getText());
          if (builder.length() > limit) {
            SpannableStringBuilder builder2 = (SpannableStringBuilder) builder.subSequence(0,
                limit);
            return builder2.append(" ...");
          }
        }
      }
      return builder;
    }

    static class FirstItemHolder extends RecyclerView.ViewHolder {

      @Bind(R.id.title_tv)
      TextView mTitleTV;
      @Bind(R.id.avatar_iv)
      ImageView mAvatarIV;
      @Bind(R.id.author_tv)
      TextView mAuthorTV;
      @Bind(R.id.publish_time_tv)
      TextView mPublishTimeTV;
      @Bind(R.id.modify_time_tv)
      TextView mModifyTimeTV;
      @Bind(R.id.content_container)
      LinearLayout mContentContainer;

      public FirstItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }

    static class NormalItemHolder extends RecyclerView.ViewHolder {

      @Bind(R.id.avatar_iv)
      ImageView mAvatarIV;
      @Bind(R.id.author_tv)
      TextView mAuthorTV;
      @Bind(R.id.publish_time_tv)
      TextView mPublishTimeTV;
      @Bind(R.id.modify_time_tv)
      TextView mModifyTimeTV;
      @Bind(R.id.content_container)
      LinearLayout mContentContainer;

      public NormalItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }

    @Override
    protected Observable<List<Post>> createLoadNewObservable() {
      return api().getPostList(mThreadId, 1)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .compose(this.<List<Post>>bindOnDestroy());
    }

    @Override
    protected Observable<List<Post>> createLoadOldObservable() {
      return api().getPostList(mThreadId, mPageCount + 1)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .compose(this.<List<Post>>bindOnDestroy());
    }

    @Override
    protected void onPostLoadOld(List<Post> list) {
      mList.addAll(list);
      mAllLoaded = list.size() < PrefHelper.getPostPageCount();
      mPageCount++;
    }

    @Override
    protected void onPostLoadNew(List<Post> list) {
      mList.clear();
      mList.addAll(list);
      mPageCount = 1;
      mAllLoaded = list.size() < PrefHelper.getPostPageCount();
    }

    @Override
    protected boolean allLoaded() {
      return mAllLoaded;
    }
  }
}
