package com.maxfin.phoenixapp.Models;

import android.net.Uri;

import java.util.Date;

public class Contact {


    private Uri mPhoto;
    private String mName;
    private String mNumber;
    private String mId;
    private String mJId;
    private Date mTime;
    private String mPreviewMessage;
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

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date time) {
        mTime = time;
    }

    public String getPreviewMessage() {
        return mPreviewMessage;
    }

    public void setPreviewMessage(String previewMessage) {
        mPreviewMessage = previewMessage;
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
