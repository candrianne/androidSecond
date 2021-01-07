package com.example.androidtests.ui;

import android.content.pm.ActivityInfo;

import androidx.fragment.app.Fragment;

public abstract class RotateFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
