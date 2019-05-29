package com.maxfin.phoenixapp.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class BlockContact {

    @PrimaryKey(autoGenerate = true)
    private int mId;
    private String mName;
    private String mNumber;
    private String mPhoto;
    private String mDate;

    public BlockContact(String name, String number, String photo, String date) {
        mName = name;
        mNumber = number;
        mPhoto = photo;
        mDate = date;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
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

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
