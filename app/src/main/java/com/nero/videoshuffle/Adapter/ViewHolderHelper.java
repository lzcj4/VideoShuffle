package com.nero.videoshuffle.adapter;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by nlang on 11/19/2015.
 */
public class ViewHolderHelper {
    private final String TAG = this.getClass().getSimpleName();
    SparseArray<View> mViews = new SparseArray<>();
    View mParentView;

    public ViewHolderHelper(@NonNull View parentView) {
        mParentView = parentView;
    }

    protected <T extends View> T findView(int viewId,Class<T> clazz) throws IllegalArgumentException {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mParentView.findViewById(viewId);
            if (null != view) {
                mViews.put(viewId, view);
            }
        }
        T result = (T) view;
        if (null == view) {
            Log.w(TAG, String.format("findView View:%s ,id:%s is null",clazz.getSimpleName(),  viewId));
        }

        return result;
    }

    public void setTextView(int viewId, String txt) {
        TextView txtView = findView(viewId,TextView.class);
        if (null != txtView) {
            txtView.setText(txt);
        }
    }

    public void setEditText(int viewId, String txt) {
        EditText editTxt = findView(viewId,EditText.class);
        if (null != editTxt) {
            editTxt.setText(txt);
        }
    }

    public void setImageView(int viewId, @NonNull String pathUri, @NonNull ImageExtractor extractor) {
        ImageView view = findView(viewId,ImageView.class);
        if (null != extractor && null != view) {
            extractor.load(view, pathUri);
        }
    }

    public void setImageViewAsync(int viewId, @NonNull String pathUri, @NonNull ImageExtractor extractor) {
        ImageView view = findView(viewId,ImageView.class);
        if (null != extractor && null != view) {
            extractor.loadAsync(view, pathUri);
        }
    }

    public interface ImageExtractor {
        void load(ImageView imageView, String pathUri);

        void loadAsync(ImageView imageView, String pathUri);
    }

}
