package com.skyfire.hipda.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.skyfire.hipda.R;
import com.skyfire.hipda.bean.AccountInfo;
import com.skyfire.hipda.bean.Forum;
import com.skyfire.hipda.db.DbHelper;
import com.skyfire.hipda.lib.WrapImageLoader;
import com.skyfire.hipda.misc.PrefHelper;
import com.skyfire.hipda.ui.abs.AbsActivity;
import com.skyfire.hipda.util.Toaster;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.RxLifecycle;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.util.List;

import static com.skyfire.hipda.api.ApiList.api;

/**
 * Created by Neil
 */
public class SettingActivity extends AbsActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setting_activity);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  public static class SettingFragment extends PreferenceFragmentCompat implements
      SharedPreferences.OnSharedPreferenceChangeListener {

    final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      addPreferencesFromResource(R.xml.setting);
      getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
      String key = preference.getKey();
      switch (key) {
        case "logout":
          onLogoutClick();
          return true;
        case "update_forum_setting":
          onUpdateForumSettingClick();
          return true;
        case "clear_cache":
          onClearCacheClick();
          return true;
        case "about":
          return true;
        default:
          return super.onPreferenceTreeClick(preference);
      }
    }

    private void onClearCacheClick() {
      WrapImageLoader.get(getContext()).getLoader().clearDiskCache();
      Toaster.showShort(getContext(), R.string.clear_cache_succeed);
    }

    private void onUpdateForumSettingClick() {
      api().getAccountInfo()
          .map(new Func1<AccountInfo, Void>() {
            @Override
            public Void call(AccountInfo info) {
              PrefHelper.setPostPageCount(info.getPostPerPage());
              PrefHelper.setThreadPageCount(info.getThreadPerPage());
              PrefHelper.setForumHash(info.getForumHash());
              return null;
            }
          })
          .flatMap(new Func1<Void, Observable<List<Forum>>>() {
            @Override
            public Observable<List<Forum>> call(Void aVoid) {
              return api().getForumList();
            }
          })
          .map(new Func1<List<Forum>, Void>() {
            @Override
            public Void call(List<Forum> list) {
              DbHelper.getInstance().updateForumList(list);
              return null;
            }
          })
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .compose(this.<Void>bindOnDestroy())
          .subscribe(new Action1<Void>() {
            @Override
            public void call(Void v) {
              Toaster.showShort(getContext(), R.string.forum_setting_update_succeed);
            }
          }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
          });
    }

    private void onLogoutClick() {
      new AlertDialog.Builder(getContext())
          .setTitle(R.string.title_confirm)
          .setMessage(R.string.logout_confirm)
          .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              DbHelper.getInstance().removeAllCookie();
              Intent intent = new Intent(getContext(), LoginActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            }
          })
          .setNegativeButton(android.R.string.cancel, null)
          .show();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onDestroy() {
      lifecycleSubject.onNext(FragmentEvent.DESTROY);
      super.onDestroy();
    }

    public final <T> Observable.Transformer<T, T> bindOnDestroy() {
      return RxLifecycle.bindUntilFragmentEvent(lifecycleSubject, FragmentEvent.DESTROY);
    }
  }
}
