package com.maxfin.phoenixapp.models;

import android.net.Uri;

public class Contact {


    private Uri mPhoto;
    private String mName;
    private String mNumber;
    private String mContactId;
    private String mJId;
    private Message mMessageHistory;

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

    public String getContactId() {
        return mContactId;
    }

    public void setContactId(String contactId) {
        mContactId = contactId;
    }


    public String getJId() {
        return mJId;
    }

    public void setJId(String JId) {
        mJId = JId;
    }

    public Message getMessageHistory() {
        return mMessageHistory;
    }
}
