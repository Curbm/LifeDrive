package com.skyfire.hipda.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.skyfire.hipda.R;
import com.skyfire.hipda.bean.AccountInfo;
import com.skyfire.hipda.bean.Forum;
import com.skyfire.hipda.db.DbHelper;
import com.skyfire.hipda.misc.PrefHelper;
import com.skyfire.hipda.ui.abs.AbsActivity;
import com.skyfire.hipda.util.Toaster;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.skyfire.hipda.api.ApiWrapper.api;

public class LoginActivity extends AbsActivity {

  @Bind(R.id.username_et)
  EditText mUsernameET;
  @Bind(R.id.pwd_et)
  EditText mPwdET;
  @Bind(R.id.login_btn)
  Button mLoginBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (DbHelper.getInstance().isAuthCookieValid()) {
      Intent intent = new Intent(LoginActivity.this, MainActivity.class);
      startActivity(intent);
      finish();
      return;
    }

    setContentView(R.layout.login_activity);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    mUsernameET.addTextChangedListener(mTextChangeListener);
    mPwdET.addTextChangedListener(mTextChangeListener);

  }


  TextWatcher mTextChangeListener = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      mLoginBtn.setEnabled(mUsernameET.length() > 0 && mPwdET.length() > 0);
    }
  };


  @OnClick(R.id.login_btn)
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.login_btn:
        final ProgressDialog dialog = ProgressDialog.show(this, null, getString(R.string
            .login_loading), true, false);

        api().getLoginForumHash()
            .flatMap(new Func1<String, Observable<Boolean>>() {
              @Override
              public Observable<Boolean> call(String hash) {
                Map<String, String> extraParams = new HashMap<>();
                extraParams.put("loginfield", "username");
                extraParams.put("loginsubmit", "true");
                extraParams.put("cookietime", "2592000");
                extraParams.put("formHash", hash);

                String userName = mUsernameET.getText().toString();
                String pwd = mPwdET.getText().toString();

                return api().login(userName, pwd, extraParams);
              }
            })
            .flatMap(new Func1<Boolean, Observable<AccountInfo>>() {
              @Override
              public Observable<AccountInfo> call(Boolean s) {
                return api().getAccountInfo();
              }
            })
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
                dialog.dismiss();
                finish();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
              }
            }, new Action1<Throwable>() {
              @Override
              public void call(Throwable throwable) {
                dialog.dismiss();
                Toaster.showShort(LoginActivity.this, throwable.getCause().getMessage());
              }
            });
        break;
    }
  }


}
