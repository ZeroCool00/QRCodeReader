package com.http5000.qrreader.android.fragment;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.AppCompatButton;
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

    private AppCompatButton btnSearch, btnShare, btnSave;
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
        btnSearch = (AppCompatButton) v.findViewById(R.id.btnSearchWeb);
        btnSave = (AppCompatButton) v.findViewById(R.id.btnSaveToContact);
        btnShare = (AppCompatButton) v.findViewById(R.id.btnShareLink);

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
    public void onQRCodeRead(final String text, PointF[] points) {
        resultTextView.setText(text);
        pointsOverlayView.setPoints(points);

        if(text.length() > 0){
            btnSearch.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);

            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY, text);
                    startActivity(search);
                }
            });

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareCompat.IntentBuilder
                            .from(getActivity()) // getActivity() or activity field if within Fragment
                            .setText(text)
                            .setType("text/plain") // most general text sharing MIME type
                            .setChooserTitle("Share QR-Code Content")
                            .startChooser();
                }
            });
        }

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