package com.maxfin.phoenixapp.Models;


public class Message {
    private String mTextMessage;
    private boolean mTypeMessage;
    private String mDateMessage;

    public Message(String textMessage, boolean typeMessage, String dateMessage) {
        mTextMessage = textMessage;
        mTypeMessage = typeMessage;
        mDateMessage = dateMessage;
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
