package com.maxfin.phoenixapp.Managers;

import android.content.Context;
import android.net.Uri;

import com.maxfin.phoenixapp.Models.Contact;
import com.maxfin.phoenixapp.R;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static final String TAG = "Закгружаем список диалогов";
    private List<Contact> mContactList;
    private static MessageManager sMessageManager;
    private Context mContext;

    private MessageManager(Context context) {
        mContext = context;
        mContactList = new ArrayList<>();
        Contact contact = new Contact();
        contact.setJId("maxfin2@jabber.ru");
        contact.setName("Max");
        Uri path = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_balance);
        contact.setPhoto(path);


        mContactList.add(contact);
    }

    public static MessageManager get(Context context) {
        if (sMessageManager == null)
            sMessageManager = new MessageManager(context);
        return sMessageManager;
    }

    public void uploadMessageList(Contact contact) {

        mContactList.add(contact);
    }

    public List<Contact> getContactList() {
        return mContactList;
    }
}
