package com.nero.videoshuffle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.nero.videoshuffle.R;
import com.nero.videoshuffle.fragment.UploadFragment;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {
    UploadFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        List<String> list = new ArrayList<String>() {{
            add("a");
            add("b");
        }};
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportFragmentManager().findFragmentByTag(this.getClass().getSimpleName()) != null) {
            return;
        }
        mFragment = new UploadFragment();
        Bundle bundle = getIntent().getExtras();
        mFragment.setArguments(bundle);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.layout_upload_root, mFragment, this.getClass().getSimpleName());
        trans.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
