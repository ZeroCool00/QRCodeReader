package com.http5000.qrreader.android.helper;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by cn on 10/10/2016.
 */
@RealmClass
public class Model extends RealmObject {


    private String qrText;
    private String date;

    public Model() {
        super();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQrText() {
        return qrText;
    }

    public void setQrText(String qrText) {
        this.qrText = qrText;
    }

}
