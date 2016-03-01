package com.balloonoffice.balloonapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bluenightz on 2/29/16 AD.
 */
public class TestFragment extends Fragment {

    public static TestFragment getInstance() {
        return new TestFragment();
    }

    public void Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.test_fragment, container, false);


        return v;
    }
}
