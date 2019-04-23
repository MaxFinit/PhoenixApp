package com.maxfin.phoenixapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Contact.class, parentColumns = "mJId", childColumns = "contact_id", onDelete = CASCADE),
        indices = {@Index("contact_id")})
public class Message {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "contact_id")
    private String mContactId;
    private String mTextMessage;
    private boolean mTypeMessage;
    private String mDateMessage;


    public String getContactId() {
        return mContactId;
    }

    public void setContactId(String contactId) {
        mContactId = contactId;
    }

    public Message(String textMessage, boolean typeMessage, String dateMessage, String contactId) {
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
