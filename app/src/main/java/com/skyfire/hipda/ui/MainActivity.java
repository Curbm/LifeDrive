package com.skyfire.hipda.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import com.skyfire.hipda.R;
import com.skyfire.hipda.ui.abs.AbsActivity;

public class MainActivity extends AbsActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {

  private NavigationDrawerFragment mNavigationDrawerFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
        .findFragmentById(R.id.navigation_drawer);

    mNavigationDrawerFragment.setUp(mToolbar, R.id.navigation_drawer, (DrawerLayout) findViewById
        (R.id.drawer_layout));

  }

  @Override
  public void onNavigationDrawerItemSelected(int id, final String title) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction trans = fragmentManager.beginTransaction();
    Fragment oldFrag = fragmentManager.findFragmentById(R.id.fragment_container);
    if (oldFrag != null) {
      trans.detach(oldFrag);
    }
    Fragment newFrag = fragmentManager.findFragmentByTag(makeFragmentTag(id));
    if (newFrag == null) {
      newFrag = createFragment(id);
      trans.add(R.id.fragment_container, newFrag, makeFragmentTag(id));
    } else {
      trans.attach(newFrag);
    }
    trans.commit();
  }

  private String makeFragmentTag(int id) {
    return "ForumListFragment" + id;
  }

  private Fragment createFragment(int id) {
    ThreadListFragment fragment = new ThreadListFragment();
    Bundle args = new Bundle();
    args.putInt("id", id);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent;
    switch (item.getItemId()) {
      case R.id.action_settings:
        intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

}
