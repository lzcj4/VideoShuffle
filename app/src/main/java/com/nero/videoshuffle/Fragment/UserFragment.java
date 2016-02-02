package com.nero.videoshuffle.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.nero.videoshuffle.R;
import com.nero.videoshuffle.view.CircleImage;
import com.nero.videoshuffle.view.SpanAdapter;
import com.nero.videoshuffle.view.SpanListView;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends ListFragment {

    @Bind(R.id.circleView)
    CircleImage mCircleView;
    @Bind(android.R.id.list)
    SpanListView mListview;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String[] items = new String[10];
        Random r = new Random(100);
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(r.nextInt(100));
        }
        SpanAdapter adapter = new SpanAdapter(this.getActivity(), items);
        mListview.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
