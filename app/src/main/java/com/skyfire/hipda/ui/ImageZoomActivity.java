package com.skyfire.hipda.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.skyfire.hipda.R;
import com.skyfire.hipda.misc.Util;
import com.skyfire.hipda.ui.abs.AbsActivity;
import com.skyfire.hipda.ui.abs.AbsFragment;

/**
 * Created by Neil
 */
public class ImageZoomActivity extends AbsActivity {

  @Bind(R.id.view_pager)
  ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(R.anim.image_zoom_fade_in, 0);

    setContentView(R.layout.image_zoom_activity);

    final int index = getIntent().getIntExtra("index", 0);
    final String[] imageUris = getIntent().getStringArrayExtra("imageUris");

    mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString("imageUri", imageUris[position]);
        fragment.setArguments(args);
        return fragment;
      }

      @Override
      public int getCount() {
        return imageUris.length;
      }
    });
    mViewPager.setCurrentItem(index);
    mViewPager.setPageMargin((int) Util.dp(this, 8));
  }

  @Override
  public void finish() {
    super.finish();
    overridePendingTransition(0, 0);
  }

  public static class PageFragment extends AbsFragment {

    @Bind(R.id.image_view)
    SubsamplingScaleImageView mImageView;
    @Bind(R.id.error_view)
    View mErrorView;

    String mImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mImageUri = getArguments().getString("imageUri");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
      return inflater.inflate(R.layout.image_zoom_page_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      mImageView.setImage(ImageSource.uri(mImageUri));
      mImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          getActivity().finish();
        }
      });
      mImageView.setOnImageEventListener(new SubsamplingScaleImageView
          .DefaultOnImageEventListener() {
        @Override
        public void onImageLoadError(Exception e) {
          mErrorView.setVisibility(View.VISIBLE);
        }
      });
    }

    @Override
    public void onDestroyView() {
      super.onDestroyView();
      mImageView.recycle();
      mImageView = null;
      mErrorView = null;
    }
  }

}
