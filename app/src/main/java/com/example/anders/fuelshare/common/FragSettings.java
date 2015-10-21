package com.example.anders.fuelshare.common;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anders.fuelshare.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragSettings extends Fragment {

    public FragSettings() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_settings, container, false);
    }
}
