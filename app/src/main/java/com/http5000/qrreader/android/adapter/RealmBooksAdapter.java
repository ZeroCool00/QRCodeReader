package com.http5000.qrreader.android.adapter;

import android.content.Context;

import com.http5000.qrreader.android.helper.Model;

import io.realm.RealmResults;

public class RealmBooksAdapter extends RealmModelAdapter<Model> {
 
    public RealmBooksAdapter(Context context, RealmResults<Model> realmResults, boolean automaticUpdate) {
 
        super(context, realmResults, automaticUpdate);
    }
}