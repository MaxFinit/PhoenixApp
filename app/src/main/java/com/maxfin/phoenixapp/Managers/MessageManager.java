package com.maxfin.phoenixapp.Managers;

import android.content.Context;

import com.maxfin.phoenixapp.Models.Contact;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static final String TAG = "Закгружаем список диалогов";
    private List<Contact> mContactList;
    private static MessageManager sMessageManager;
    private Context mContext;

    private MessageManager(Context context) {
        mContext = context;
    }

    public static MessageManager getMessageManager(Context context) {
        if (sMessageManager == null)
            sMessageManager = new MessageManager(context);
        return sMessageManager;
    }

    public void uploadMessageList(Contact contact) {

        mContactList = new ArrayList<>();
        mContactList.add(contact);


    }

    public List<Contact> getContactList() {
        return mContactList;
    }
}
