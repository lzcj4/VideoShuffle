package com.nero.videoshuffle.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.MediaController;
import android.widget.VideoView;

import com.nero.videoshuffle.R;
import com.nero.videoshuffle.activity.PhotoPreviewActivity;
import com.nero.videoshuffle.model.MediaItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideoPlaybackFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoPlaybackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@EFragment(R.layout.fragment_video_playback)
public class VideoPlaybackFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LIST = "video_list";
    private static final String ARG_INDEX = "current_index";

    // TODO: Rename and change types of parameters
    private ArrayList<MediaItem> mContentList;
    private int mCurrentIndex;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoPlaybackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoPlaybackFragment newInstance(Bundle bundle) {
        VideoPlaybackFragment_ fragment = new VideoPlaybackFragment_();
        fragment.setArguments(bundle);
        return fragment;
    }

    @ViewById(R.id.videoview_playback)
    VideoView mVideoView;

    @AfterViews
    public void onAfterView() {
        if (getArguments() != null) {
            mContentList = getArguments().getParcelableArrayList(PhotoPreviewActivity.SOURCELIST);
            mCurrentIndex = getArguments().getInt(PhotoPreviewActivity.CURRENT_SELECTED_INDEX);
            if (mContentList == null || mContentList.isEmpty() ||
                    mCurrentIndex < 0 || mCurrentIndex >= mContentList.size()) {
                throw new IllegalArgumentException();
            }
        }

        MediaItem currentItem = mContentList.get(mCurrentIndex);
        mVideoView.setVideoURI(currentItem.getUri());

        MediaController mediaController = new MediaController(getContext());
        mVideoView.setMediaController(mediaController);
        mVideoView.start();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
