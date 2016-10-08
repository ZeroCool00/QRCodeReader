package com.http5000.qrreader.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.http5000.qrreader.android.R;

public class OneFragment extends android.support.v4.app.DialogFragment {
    QRCodeReaderView qrCodeReaderView;

    public static OneFragment newInstance() {
        OneFragment fragmentFullScreen = new OneFragment();
        return fragmentFullScreen;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        v.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));

        qrCodeReaderView = (QRCodeReaderView) v.findViewById(R.id.qrdecoderview);

        return v;
    }
 
}