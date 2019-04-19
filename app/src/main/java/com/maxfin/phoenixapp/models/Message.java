package com.maxfin.phoenixapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Contact.class, parentColumns = "id", childColumns = "contact_id", onDelete = CASCADE))
public class Message {
    @PrimaryKey(autoGenerate = true)
    public long id;
    private String mTextMessage;
    private boolean mTypeMessage;
    private String mDateMessage;
    @ColumnInfo(name = "contact_id")
    private long mContactId;

    public long getContactId() {
        return mContactId;
    }

    public void setContactId(long contactId) {
        mContactId = contactId;
    }

    public Message(String textMessage, boolean typeMessage, String dateMessage, long contactId) {
        mTextMessage = textMessage;
        mTypeMessage = typeMessage;
        mDateMessage = dateMessage;
        mContactId = contactId;
    }

    public String getTextMessage() {
        return mTextMessage;
    }

    public void setTextMessage(String textMessage) {
        this.mTextMessage = textMessage;
    }

    public boolean isTypeMessage() {
        return mTypeMessage;
    }

    public void setTypeMessage(boolean typeMessage) {
        mTypeMessage = typeMessage;
    }

    public String getDateMessage() {
        return mDateMessage;
    }

    public void setDateMessage(String dateMessage) {
        mDateMessage = dateMessage;
    }
}
