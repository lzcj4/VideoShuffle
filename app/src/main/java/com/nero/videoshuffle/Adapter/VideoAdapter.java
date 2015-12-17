package com.nero.videoshuffle.adapter;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nero.videoshuffle.model.MediaItem;
import com.nero.videoshuffle.MyApplication;
import com.nero.videoshuffle.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by nlang on 11/12/2015.
 */
public class VideoAdapter extends CursorAdapter {
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        setViewContent(view, cursor);
    }

    LayoutInflater mInflater;
    Context mContext;
    MyApplication mApp;

    @SuppressWarnings("deprecation")
    public VideoAdapter(Context context) {
        super(context, null);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mApp = (MyApplication) mContext.getApplicationContext();
    }


    public MediaItem getItemByPos(int position) {
        MediaItem result = null;
        Cursor cursor = (Cursor) super.getItem(position);
        if (cursor == null) {
            return result;
        }
        result = getVideoItem(cursor);
        return result;
    }

    private MediaItem getVideoItem(Cursor cursor) {
        MediaItem result = null;
//        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
//        String title = cursor.getString(getCursor().getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
//        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String title = cursor.getString(getCursor().getColumnIndex(MediaStore.Images.Media.TITLE));
        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));

        result = new MediaItem(id, filePath, title, size);
        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        getItem(position);
        if (null == convertView) {
            convertView = newView(mContext, getCursor(), parent);
        }
        setViewContent(convertView, getCursor());
        return convertView;
    }

    private View setViewContent(View convertView, Cursor cursor) {
        if (null == convertView ||
                convertView.getTag() == null ||
                !(convertView.getTag() instanceof ViewHolder)) {
            throw new IllegalArgumentException();
        }
        final MediaItem videoItem = getVideoItem(cursor);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        String filePath = ImageDownloader.Scheme.FILE.wrap(videoItem.Data);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .build();
        ((ViewGroup) convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        viewHolder.mImgVideo.setTag(filePath);
        viewHolder.mImgVideo.setImageBitmap(null);
        //Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoItem.Data, 0);
        //viewHolder.mImgVideo.setImageBitmap(thumbnail);
        ImageLoader.getInstance().loadImage(filePath, new ImageSize(100, 100), options, new VideoImageListener(viewHolder.mImgVideo));
        //viewHolder.mTxtName.setText(videoItem.Title);
        viewHolder.mTxtSize.setText(videoItem.Size);

        Spanned htmlUrl = Html.fromHtml(String.format("<a href='http://www.163.com'>%s</a>", videoItem.Title));
        viewHolder.mTxtName.setText(htmlUrl);
        // viewHolder.mTxtName.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.mViewStub.setVisibility(View.VISIBLE);


        final ImageView imgView = viewHolder.mImgVideo;
        imgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData cd = ClipData.newRawUri("lable", videoItem.getUri());
                View.DragShadowBuilder dsb = new View.DragShadowBuilder(imgView);
                imgView.startDrag(cd, dsb, videoItem, 0);
                return true;
            }
        });


        return convertView;
    }

    //  FadeInBitmapDisplayer fadeinBD = new FadeInBitmapDisplayer(1000);

    public class VideoImageListener implements ImageLoadingListener {

        ImageView mImageView;

        public VideoImageListener(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            String tagPath = (String) mImageView.getTag();
            if (tagPath.equals(imageUri)) {
                mImageView.setImageBitmap(loadedImage);
                // fadeinBD.display(loadedImage, new ImageViewAware(mImageView), null);
            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.listview_video_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    private class ViewHolder {
        public ImageView mImgVideo;
        public TextView mTxtName;
        public TextView mTxtSize;
        public ViewStub mViewStub;

        private ViewHolder(View parentView) {
            if (null == parentView) {
                throw new IllegalArgumentException();
            }

            mImgVideo = (ImageView) parentView.findViewById(R.id.imgVideo);
            mTxtName = (TextView) parentView.findViewById(R.id.txt_video_title);
            mTxtSize = (TextView) parentView.findViewById(R.id.txt_video_size);
            mViewStub = (ViewStub) parentView.findViewById(R.id.viewstub_video_play);
        }
    }
}
