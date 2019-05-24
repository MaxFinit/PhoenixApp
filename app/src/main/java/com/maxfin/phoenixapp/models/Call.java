package com.maxfin.phoenixapp.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Call {

    @PrimaryKey(autoGenerate = true)
    public long id;
    private String mName;
    private String mNumber;
    private String mData;
    private String mPhoto;
    private String mContactId;
    private byte mCallType;


    public Call(String name, String number, byte callType, String data, String photo, String contactId) {
        mName = name;
        mNumber = number;
        mCallType = callType;
        mData = data;
        mPhoto = photo;
        mContactId = contactId;
    }

    public byte getCallType() {
        return mCallType;
    }

    public void setCallType(byte callType) {
        mCallType = callType;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
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

    public String getContactId() {
        return mContactId;
    }

    public void setContactId(String contactId) {
        mContactId = contactId;
    }
}
