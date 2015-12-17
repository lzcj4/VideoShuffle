package com.nero.videoshuffle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nlang on 11/19/2015.
 */
public abstract class GenericListAdapter<T> extends BaseAdapter {
    protected List<T> mDataSource = new ArrayList<>();
    protected ViewTypeController mViewController = null;
    private Context mContext;
    private LayoutInflater mLayoutInflator;
    private boolean mIsSupportedFooter = false;

    public GenericListAdapter(@NonNull Context context, @NonNull List<T> list, @NonNull ViewTypeController viewController) {
        mDataSource = list;
        mViewController = viewController;
        mContext = context;
        mLayoutInflator = LayoutInflater.from(context);
    }

    public void setIsSupportFooter(boolean isSupported) {
        mIsSupportedFooter = isSupported;
    }

    public boolean getIsSupportFooter() {
        return mIsSupportedFooter;
    }

    public abstract void convertView(ViewHolderHelper helper, T item);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = createView(position, parent);
        }
        ViewHolderHelper helper = (ViewHolderHelper) convertView.getTag();
        T item = (T) getItem(position);
        convertView(helper, item);
        return convertView;
    }

    protected View createView(int position, ViewGroup parent) {
        View view = mLayoutInflator.inflate(mViewController.getViewResourceId(position), parent, false);
        ViewHolderHelper helper = new ViewHolderHelper(view);
        view.setTag(helper);
        return view;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < mDataSource.size()) {
            return mDataSource.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return mDataSource.size() + (getIsSupportFooter() ? 1 : 0);
    }

    @Override
    public boolean isEmpty() {
        return mDataSource.isEmpty();
    }

    @Override
    public int getViewTypeCount() {
        return mViewController.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mViewController.getItemViewType(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

}
