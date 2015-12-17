package com.nero.videoshuffle.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nero.videoshuffle.model.MediaItem;
import com.nero.videoshuffle.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

@EActivity(R.layout.activity_photo_preview)
public class PhotoPreviewActivity extends AppCompatActivity {

    public static final String SOURCELIST = "source_list";
    public static final String CURRENT_SELECTED_INDEX = "current_select_index";

    @ViewById(R.id.viewpager_photo_preview)
    ViewPager mPreviewViewPager;
    @ViewById(R.id.layoutParent)
    ViewGroup mLayoutParent;

    ArrayList<MediaItem> mDataSource;
    int mCurrentIndex;

    @AfterViews
    void onAfterView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        mDataSource = bundle.getParcelableArrayList(SOURCELIST);
        mCurrentIndex = bundle.getInt(CURRENT_SELECTED_INDEX);
        mPreviewViewPager.setAdapter(new PhotoPageAdapter());
        mPreviewViewPager.setCurrentItem(mCurrentIndex);
    }


    private class PhotoPageAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MediaItem item = mDataSource.get(position);
            View view = getLayoutInflater().inflate(R.layout.photo_preview_item, null, false);
            final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoview_detail);
            String filePath = item.Data;
            String uri = ImageDownloader.Scheme.FILE.wrap(filePath);
            DisplayImageOptions options = new DisplayImageOptions.Builder().
                    cacheInMemory(true).cacheOnDisk(true)
                    .showImageOnLoading(R.mipmap.ic_launcher)
                    .build();
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mLayoutParent.getLayoutParams();
            int imgWidth = metrics.widthPixels - mLayoutParent.getPaddingLeft() - mLayoutParent.getPaddingRight() - layoutParams.leftMargin - layoutParams.rightMargin;
            int imgHeight = metrics.heightPixels - mLayoutParent.getPaddingTop() - mLayoutParent.getPaddingBottom() - layoutParams.topMargin - layoutParams.bottomMargin;

            ImageLoader.getInstance().loadImage(uri, new ImageSize(imgWidth, imgHeight), options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    photoView.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            boolean isSame = view == object;
            return isSame;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            //super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return mDataSource.indexOf(object);
        }

        @Override
        public int getCount() {
            return mDataSource.size();
        }
    }
}
