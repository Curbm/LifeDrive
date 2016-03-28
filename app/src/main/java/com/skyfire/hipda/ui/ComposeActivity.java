package com.skyfire.hipda.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.skyfire.hipda.R;
import com.skyfire.hipda.ui.abs.AbsActivity;
import com.skyfire.hipda.util.Toaster;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.skyfire.hipda.api.ApiList.api;

/**
 * Created by Neil
 */
public class ComposeActivity extends AbsActivity {

  @Bind(R.id.content_et)
  EditText mContentET;

  private int mThreadId;
  private int mForumId;
  private int mPostId;
  private int mUid;
  private String mAuthor;
  private int mFloor;
  private String mSummary;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.compose_activity);

    mThreadId = getIntent().getIntExtra("threadId", -1);
    mForumId = getIntent().getIntExtra("forumId", -1);
    mPostId = getIntent().getIntExtra("postId", -1);
    mUid = getIntent().getIntExtra("uid", -1);
    mAuthor = getIntent().getStringExtra("author");
    mFloor = getIntent().getIntExtra("floor", -1);
    mSummary = getIntent().getStringExtra("summary");
  }

  @OnClick({R.id.submit_btn})
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.submit_btn:
        String content = mContentET.getText().toString();
        api().replyPost(mForumId, mThreadId, mPostId, mFloor, mUid, mAuthor, mSummary, content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(this.<Void>bindOnDestroy())
            .subscribe(new Action1<Void>() {
              @Override
              public void call(Void v) {
                Toaster.showShort(ComposeActivity.this, R.string.submit_succeed);
                finish();
              }
            }, new Action1<Throwable>() {
              @Override
              public void call(Throwable throwable) {
                Toaster.showShort(ComposeActivity.this, throwable.getCause().getMessage());
              }
            });
        break;
    }
  }


}
