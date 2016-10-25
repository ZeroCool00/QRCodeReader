package com.http5000.qrreader.android.adapter;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.http5000.qrreader.android.R;
import com.http5000.qrreader.android.helper.Model;
import com.http5000.qrreader.android.helper.Prefs;
import com.http5000.qrreader.android.helper.RealmController;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class BooksAdapter extends RealmRecyclerViewAdapter<Model> {

    Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public BooksAdapter(Activity context) {

        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();

        final Model book = getItem(position);
        final CardViewHolder holder = (CardViewHolder) viewHolder;
        holder.textTitle.setText(book.getQrText());
        holder.txtDate.setText(book.getDate());

    }



    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitle;
        public TextView txtDate;
        public ImageView overflow;

        public CardViewHolder(final View itemView) {
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.qrText);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            txtDate = (TextView) itemView.findViewById(R.id.date);

            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String result = getItemName(getAdapterPosition());
                    PopupMenu popup = new PopupMenu(overflow.getContext(), itemView);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    deleteItem(getAdapterPosition());
                                    return true;
                                case R.id.search:
                                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                                    search.putExtra(SearchManager.QUERY, result.toString());
                                    context.startActivity(search);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.menu_main);
                    popup.setGravity(Gravity.RIGHT);
                    try {
                        Field mFieldPopup = popup.getClass().getDeclaredField("mPopup");
                        mFieldPopup.setAccessible(true);
                        MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popup);
                        mPopup.setForceShowIcon(true);
                    } catch (Exception e) {

                    }
                    popup.show();
                }
            });
        }

        public String getItemName(int position) {
            RealmResults<Model> results = realm.where(Model.class).findAll();

            Model b = results.get(position);
            String title = b.getQrText();
            return title;
        }

        public void deleteItem(int position) {
            RealmResults<Model> results = realm.where(Model.class).findAll();
            Model b = results.get(position);
            String title = b.getQrText();
            realm.beginTransaction();
            results.remove(position);
            realm.commitTransaction();

            if (results.size() == 0) {
                Prefs.with(context).setPreLoad(false);
            }

            notifyDataSetChanged();
        }
    }
}