package com.maxfin.phoenixapp.managers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.maxfin.phoenixapp.App;
import com.maxfin.phoenixapp.database.ContactsDao;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.R;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static final String TAG = "MessageManager";
    private List<Contact> mContactList;
    private static MessageManager sMessageManager;
    private ContactsDatabase mContactsDatabase;
    private ContactsDao mContactsDao;

    private MessageManager() {
        mContactList = new ArrayList<>();


        //  mContactList.add(contact);
    }

    public static MessageManager get() {
        if (sMessageManager == null)
            sMessageManager = new MessageManager();
        return sMessageManager;
    }

    public void uploadMessageList(Contact contact) {
        mContactsDatabase = App.getInstance().getDatabase();
        mContactsDao = mContactsDatabase.mContactsDao();

        mContactsDao.insert(contact);


        //       mContactList.add(contact);
    }

    public void deleteFromMessageList(Contact contact) {
        mContactsDatabase = App.getInstance().getDatabase();
        mContactsDao = mContactsDatabase.mContactsDao();
        mContactsDao.delete(contact);

    }


    public List<Contact> getContactList() {

        mContactsDatabase = App.getInstance().getDatabase();
        mContactsDao = mContactsDatabase.mContactsDao();

        try {
            mContactList = mContactsDao.getAll();
        } catch (Exception e) {
            Log.d(TAG, "fail");
            e.getStackTrace();
        }


        return mContactList;
    }


    public void updateConact(Contact contact) {

        mContactsDatabase = App.getInstance().getDatabase();
        mContactsDao = mContactsDatabase.mContactsDao();

        mContactsDao.update(contact);
    }


}
