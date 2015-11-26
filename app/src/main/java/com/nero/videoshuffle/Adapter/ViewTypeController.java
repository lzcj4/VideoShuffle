package com.nero.videoshuffle.Adapter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nlang on 11/19/2015.
 */
public class ViewTypeController {

    private List<Integer> mViews = new ArrayList<>();

    public ViewTypeController(@NonNull List<Integer> views) {
        mViews = views;
    }

    public int getViewTypeCount() {
        return mViews.size();
    }

    public int getItemViewType(int pos) {
        return getViewTypeCount() == 0 ? 0 : pos % getViewTypeCount();

    }

    public int getViewResourceId(int pos) {
        return mViews.get(pos);
    }
}
