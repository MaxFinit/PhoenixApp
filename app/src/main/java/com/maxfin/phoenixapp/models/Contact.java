package com.maxfin.phoenixapp.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;

@Entity(indices = {
        @Index("mName"),
        @Index("mNumber")
})
public class Contact {

    @PrimaryKey(autoGenerate = true)
    public long id;

    private String mPhoto;
    private String mName;
    private String mNumber;
    private String mContactId;
    private String mJId;

    public long getId() {
        return id;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
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


}
