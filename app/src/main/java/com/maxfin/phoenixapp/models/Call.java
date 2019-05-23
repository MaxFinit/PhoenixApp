package com.maxfin.phoenixapp.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Contact.class, parentColumns = "mJId", childColumns = "contact_id", onDelete = CASCADE),
        indices = {@Index("contact_id")})
public class Call {

    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "contact_id")
    private String mContactId;
    private byte mCallType;
    private String mData;

    public String getContactId() {
        return mContactId;
    }

    public void setContactId(String contactId) {
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
}
