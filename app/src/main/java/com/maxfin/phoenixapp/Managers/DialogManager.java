package com.maxfin.phoenixapp.Managers;

import android.content.Context;

import com.maxfin.phoenixapp.Models.Message;

import java.util.ArrayList;
import java.util.List;

public class DialogManager {
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

    public void addMessage(Message message) {
        mMessageList.add(message);
    }

    public List<Message> getMessageList() {
        return mMessageList;

    }

}