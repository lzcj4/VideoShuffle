package com.nero.videoshuffle.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.nero.videoshuffle.Activity.PhotoListActivity_;
import com.nero.videoshuffle.Activity.PhotoPreviewActivity;
import com.nero.videoshuffle.Activity.PhotoPreviewActivity_;
import com.nero.videoshuffle.Adapter.VideoAdapter;
import com.nero.videoshuffle.Model.MediaItem;
import com.nero.videoshuffle.MyApplication;
import com.nero.videoshuffle.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
@EFragment(R.layout.fragment_video_view)
public class VideoViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = VideoViewFragment.class.getSimpleName();
    private static final int LOAD_ID = 0;
    public static final int FACEBOOK_LOGIN_REQUEST_CODE = 64206;

    @ViewById(R.id.listview_video)
    ListView mListView;

    VideoAdapter mVideoAdapter;
    LoginButton mBtnLogin;

    @AfterViews
    void onAfterView() {
        setHasOptionsMenu(true);
        registerForContextMenu(mListView);
        mVideoAdapter = new VideoAdapter(getContext());
        //SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getContext(), R.layout.listview_video_item, cursor, columns, new int[]{R.id.imgVideo, R.id.txtVideoName, R.id.txtVideoDuration});
        mListView.setAdapter(mVideoAdapter);
        MyApplication mApp = (MyApplication) getContext().getApplicationContext();
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        getLoaderManager().initLoader(LOAD_ID, null, this);
        iniFacebook();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentIndex = position;
                Intent intent = getNavigateIntent();
                intent.setClass(VideoViewFragment.this.getContext(), PhotoPreviewActivity_.class);
                startActivity(intent);
            }
        });
    }

    CallbackManager mCallbackManager;
    ShareDialog mShareDialog;

    private void iniFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        //AppEventsLogger mLogger = AppEventsLogger.newLogger(this);

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        mShareDialog = new ShareDialog(this);
        mShareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACEBOOK_LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                shareToFacebook();
            }
        }
    }

    int mCurrentIndex = -1;
    @ItemLongClick(R.id.listview_video)
    boolean listviewItemSelected(int pos) {
        mCurrentIndex = pos;
        Log.i(TAG, String.valueOf(pos));
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_video_view, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_video_view, menu);
    }

    private boolean getIsLogined() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void shareToFacebook() {
        final MediaItem videoItem = mVideoAdapter.getItemByPos(mCurrentIndex);
        //  "publish_actions","public_profile", "user_friends"
        Profile profile = Profile.getCurrentProfile();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        ShareVideo shareVideo = new ShareVideo.Builder().setLocalUrl(videoItem.getUri()).build();
        final ShareVideoContent videoContent = new ShareVideoContent.Builder().setVideo(shareVideo).build();

//        ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                .setImageUrl(Uri.parse("http://img3.cache.netease.com/photo/0001/2015-11-17/B8K0P8BQ3R710001.jpg"))
//                .build();
//        if (mShareDialog.canShow(linkContent, ShareDialog.Mode.AUTOMATIC)) {
//            mShareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);
//        }

        if (mShareDialog.canShow(videoContent, ShareDialog.Mode.AUTOMATIC)) {
            mShareDialog.show(videoContent, ShareDialog.Mode.AUTOMATIC);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(videoItem.Data);
            SharePhoto photo = new SharePhoto.Builder().setCaption("New test photo by video shuffle").setBitmap(bitmap).build();
            final SharePhotoContent photoContent = new SharePhotoContent.Builder().setPhotos(Arrays.asList(photo)).build();

            ShareApi.share(videoContent, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Toast.makeText(VideoViewFragment.this.getContext(), String.format("Video uploaded post id:%s", result.getPostId()), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(VideoViewFragment.this.getContext(), "Video uploaded canceled", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(VideoViewFragment.this.getContext(), String.format("Video uploaded error:%s", error.getMessage()), Toast.LENGTH_LONG).show();
                }
            });

            // Toast.makeText(this.getContext(), "No Facebook app installed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_facebook) {
            if (getIsLogined()) {
                shareToFacebook();
            } else {
                LoginManager.getInstance().logInWithPublishPermissions(getActivity(), null);
            }
        } else {
            Intent intent = getNavigateIntent();
            if (item.getItemId() == R.id.action_preview) {
                intent.setClass(this.getContext(), PhotoPreviewActivity_.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.action_list) {
                intent.setClass(this.getContext(), PhotoListActivity_.class);
                startActivity(intent);
            }
        }

        return super.onContextItemSelected(item);
    }

    private Intent getNavigateIntent() {
        ArrayList<MediaItem> list = getContents();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PhotoPreviewActivity.SOURCELIST, list);
        bundle.putInt(PhotoPreviewActivity.CURRENTSELECTEDINDEX, mCurrentIndex);
        intent.putExtras(bundle);
        return intent;
    }

    private ArrayList<MediaItem> getContents() {
        ArrayList<MediaItem> list = new ArrayList<>();
        Cursor cursor = mVideoAdapter.getCursor();
        cursor.moveToFirst();
        do {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            MediaItem mi = new MediaItem(filePath, title, size);
            list.add(mi);
        }
        while (cursor.moveToNext());
        return list;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getContext(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.TITLE);
        //return new CursorLoader(this.getContext(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.TITLE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (LOAD_ID == loader.getId()) {
            mVideoAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVideoAdapter.swapCursor(null);
    }
}
