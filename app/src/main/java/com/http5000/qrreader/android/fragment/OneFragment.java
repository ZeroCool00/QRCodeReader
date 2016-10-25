package com.http5000.qrreader.android.fragment;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.http5000.qrreader.android.R;
import com.http5000.qrreader.android.helper.Model;
import com.http5000.qrreader.android.helper.PointsOverlayView;
import com.http5000.qrreader.android.helper.RealmController;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.io.text.VCardWriter;
import io.realm.Realm;


public class OneFragment extends DialogFragment implements QRCodeReaderView.OnQRCodeReadListener, View.OnClickListener {
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    FloatingActionButton backButton;
    Model model;
    CameraManager cameraManager;
    ArrayList<Model> models = new ArrayList<Model>();
    Context context;
    private QRCodeReaderView qrCodeReaderView;
    private TextView resultTextView;
    private PointsOverlayView pointsOverlayView;
    private CheckBox flashlightCheckBox;
    private Realm realm;
    private AppCompatButton btnSearch, btnShare, btnNewScan;

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

        this.realm = RealmController.with(getActivity()).getRealm();

        qrCodeReaderView = (QRCodeReaderView) v.findViewById(R.id.qrdecoderview);
        resultTextView = (TextView) v.findViewById(R.id.result_text_view);
        pointsOverlayView = (PointsOverlayView) v.findViewById(R.id.points_overlay_view);
        flashlightCheckBox = (CheckBox) v.findViewById(R.id.flashlight_checkbox);
        btnSearch = (AppCompatButton) v.findViewById(R.id.btnSearchWeb);
        btnShare = (AppCompatButton) v.findViewById(R.id.btnShareLink);
        btnNewScan = (AppCompatButton) v.findViewById(R.id.btnReload);
        backButton = (FloatingActionButton) v.findViewById(R.id.back_button);
        backButton.setOnClickListener(this);

        flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
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
        pointsOverlayView.setPoints(points);

        if (text.length() > 0 && !text.isEmpty()) {
            resultTextView.setText(text);
            qrCodeReaderView.stopCamera();
            resultTextView.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            btnNewScan.setVisibility(View.VISIBLE);

            btnNewScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    OneFragment newFragment = OneFragment.newInstance();
                    newFragment.show(ft, "dialog");

                    getDialog().dismiss();


                }
            });

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

                    if (checkandRequestPermission()) {

                        VCard vCard = Ezvcard.parse(text).first();

                        File vcfFile = new File(getActivity().getExternalFilesDir(null), "generated.vcf");


                        VCardWriter writer = null;
                        try {
                            writer = new VCardWriter(vcfFile, vCard.getVersion());
                            writer.write(vCard);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (writer != null) {
                                try {
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(vcfFile), "text/x-vcard");
                        startActivity(i);
                    }
                }
            });

            model = new Model();
            model.setQrText(resultTextView.getText().toString());
            model.setDate(getDateTime().toString());
            realm.beginTransaction();
            realm.copyToRealm(model);
            realm.commitTransaction();
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private boolean checkandRequestPermission() {

        int readphoestate = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        int storageread = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int storagewrite = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readcontact = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        int writecontact = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS);


        List<String> listpermissionNeeded = new ArrayList<>();

        if (readphoestate != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (storageread != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storagewrite != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readcontact != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (writecontact != PackageManager.PERMISSION_GRANTED) {
            listpermissionNeeded.add(Manifest.permission.WRITE_CONTACTS);
        }
        if (!listpermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listpermissionNeeded.toArray(new String[listpermissionNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_button) {
            getDialog().dismiss();
        }
    }
}