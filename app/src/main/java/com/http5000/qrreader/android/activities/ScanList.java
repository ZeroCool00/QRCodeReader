package com.http5000.qrreader.android.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.http5000.qrreader.android.R;
import com.http5000.qrreader.android.adapter.BooksAdapter;
import com.http5000.qrreader.android.adapter.RealmBooksAdapter;
import com.http5000.qrreader.android.helper.Model;
import com.http5000.qrreader.android.helper.RealmController;

import io.realm.Realm;
import io.realm.RealmResults;

public class ScanList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BooksAdapter adapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        this.realm = RealmController.with(this).getRealm();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupRecycler();

        RealmController.with(this).refresh();

        setRealmAdapter(RealmController.with(this).getBooks());
    }

    private void setupRecycler() {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BooksAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    public void setRealmAdapter(RealmResults<Model> books) {

        RealmBooksAdapter realmAdapter = new RealmBooksAdapter(this.getApplicationContext(), books, true);
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }
}
