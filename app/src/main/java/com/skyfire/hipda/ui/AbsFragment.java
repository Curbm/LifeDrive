package com.skyfire.hipda.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import butterknife.ButterKnife;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.RxLifecycle;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class AbsFragment extends Fragment {

  final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override
  public void onDestroyView() {
    lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    lifecycleSubject.onNext(FragmentEvent.DESTROY);
    super.onDestroy();
  }

  public final <T> Observable.Transformer<T, T> bindOnDestroyView() {
    return RxLifecycle.bindUntilFragmentEvent(lifecycleSubject, FragmentEvent.DESTROY_VIEW);
  }

  public final <T> Observable.Transformer<T, T> bindOnDestroy() {
    return RxLifecycle.bindUntilFragmentEvent(lifecycleSubject, FragmentEvent.DESTROY);
  }


}
