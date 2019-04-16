package com.maxfin.phoenixapp.Models;

import android.net.Uri;

import java.net.URI;

public class Contact {


    private Uri mPhoto;
    private String mName;
    private String mNumber;
    private String mId;

    public Uri getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Uri photo) {
        mPhoto = photo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
