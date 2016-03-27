package com.skyfire.hipda.ui.abs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.skyfire.hipda.R;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AbsActivity extends AppCompatActivity {

  @Nullable
  @Bind(R.id.tool_bar)
  protected Toolbar mToolbar;

  final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onDestroy() {
    lifecycleSubject.onNext(ActivityEvent.DESTROY);
    super.onDestroy();
  }

  @Override
  public void onContentChanged() {
    super.onContentChanged();
    ButterKnife.bind(this);

    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
    }
  }

  // Suppress IDE warning
  @NonNull
  @Override
  public ActionBar getSupportActionBar() {
    return super.getSupportActionBar();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public final <T> Observable.Transformer<T, T> bindOnDestroy() {
    return RxLifecycle.bindUntilActivityEvent(lifecycleSubject, ActivityEvent.DESTROY);
  }


}
