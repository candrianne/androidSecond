package com.example.androidtests.ui;

import android.content.pm.ActivityInfo;
import android.util.Log;

import androidx.fragment.app.Fragment;

public abstract class RotateFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        Log.i("test", "resume" + this.toString());
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("test", "pause" + this.toString());
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
