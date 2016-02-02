package com.nero.videoshuffle.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nero.videoshuffle.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nlang on 1/26/2016.
 */
public class SpanAdapter extends BaseAdapter {
    String[] mItems;
    Context mContext;

    public SpanAdapter(Context context, String[] items) {
        super();
        mItems = items;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder vh;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.list_item, null, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.mTxtTitle.setText((String)getItem(position));

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.txt_title)
        TextView mTxtTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
