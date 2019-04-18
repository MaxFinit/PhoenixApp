package com.maxfin.phoenixapp.managers;

import android.content.Context;



import com.maxfin.phoenixapp.models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

public class DialogManager {
    private Message mMessage;
    private List<Message> mMessageList;
    private static DialogManager sDialogManager;


    private DialogManager(Context context) {
        mMessageList = new ArrayList<>();
    }

    public static DialogManager getDialogManager(Context context) {
        if (sDialogManager == null)
            sDialogManager = new DialogManager(context);
        return sDialogManager;
    }


    public void addMessage(String messagesText, boolean messageType) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dMMMHH:mm");

        String date = dateFormat.format(Calendar.getInstance().getTime());


        mMessage = new Message(messagesText, messageType, date);

        mMessageList.add(mMessage);
    }


    public List<Message> getMessageList() {
        return mMessageList;

    }

}