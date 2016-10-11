package com.http5000.qrreader.android.fragment;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.http5000.qrreader.android.R;
import com.http5000.qrreader.android.helper.Model;

import java.util.ArrayList;


public class OneFragment extends android.support.v4.app.DialogFragment implements QRCodeReaderView.OnQRCodeReadListener {
    private QRCodeReaderView qrCodeReaderView;
    private TextView resultTextView;
    private PointsOverlayView pointsOverlayView;
    private CheckBox flashlightCheckBox;
    Model model;
    ArrayList<Model> models = new ArrayList<Model>();

    public static OneFragment newInstance() {
        OneFragment fragmentFullScreen = new OneFragment();
        return fragmentFullScreen;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyMaterialThemeFull);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        v.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));

        qrCodeReaderView = (QRCodeReaderView)v.findViewById(R.id.qrdecoderview);
        resultTextView = (TextView) v.findViewById(R.id.result_text_view);
        pointsOverlayView = (PointsOverlayView) v.findViewById(R.id.points_overlay_view);
        flashlightCheckBox = (CheckBox) v.findViewById(R.id.flashlight_checkbox);

        flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                qrCodeReaderView.setTorchEnabled(isChecked);
            }
        });

        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setBackCamera();

        qrCodeReaderView.startCamera();

        return v;
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        resultTextView.setText(text);
        pointsOverlayView.setPoints(points);

        model = new Model();
        model.setQrText(text);

        models.add(model);

     //   tinyDB.putListObject("allScan", models);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }
}