package com.nero.videoshuffle.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.nero.videoshuffle.Adapter.GenericListAdapter;
import com.nero.videoshuffle.Adapter.ViewHolderHelper;
import com.nero.videoshuffle.Adapter.ViewTypeController;
import com.nero.videoshuffle.Model.MediaItem;
import com.nero.videoshuffle.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;
import java.util.List;

@EActivity(R.layout.activity_photo_list)
public class PhotoListActivity extends AppCompatActivity {

    @ViewById(R.id.listview)
    ListView mListView;

    @AfterViews
    void onAfterView() {
        Intent intent = getIntent();
        List<MediaItem> list = intent.getParcelableArrayListExtra(PhotoPreviewActivity.SOURCELIST);

        mListView.setAdapter(new GenericListAdapter<MediaItem>(this, list, new ViewTypeController(Arrays.asList(R.layout.listview_video_item, R.layout.photo_preview_item)) {
            @Override
            public int getViewResourceId(int pos) {
                return super.getViewResourceId(pos % 2);
            }
        }) {
            @Override
            public void convertView(ViewHolderHelper helper, MediaItem item) {
                helper.setTextView(R.id.txt_video_title, item.Title);
                helper.setTextView(R.id.txt_video_size, item.Size);
                String uri = ImageDownloader.Scheme.FILE.wrap(item.Data);
                helper.setImageViewAsync(R.id.imgVideo, uri, imgExtractor);
                helper.setImageViewAsync(R.id.photoview_detail, uri, imgExtractor);
            }
        });
    }

    ViewHolderHelper.ImageExtractor imgExtractor = new ViewHolderHelper.ImageExtractor() {
        @Override
        public void load(ImageView imageView, String pathUri) {

        }

        SimpleImageLoadingListener sill = new SimpleImageLoadingListener() {

        };

        @Override
        public void loadAsync(final ImageView imageView, String pathUri) {
            imageView.setTag(pathUri);
            ImageLoader.getInstance().loadImage(pathUri, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (((String) imageView.getTag()) == imageUri) {
                        imageView.setImageBitmap(loadedImage);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    };

}


