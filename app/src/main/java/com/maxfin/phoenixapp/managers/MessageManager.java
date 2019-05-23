package com.maxfin.phoenixapp.managers;

import android.util.Log;

import com.maxfin.phoenixapp.Application;
import com.maxfin.phoenixapp.database.dao.ContactsDao;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static final String TAG = "MessageManager";
    private List<Contact> mContactList;
    private ContactsDatabase mContactsDatabase;
    private ContactsDao mContactsDao;

    private static MessageManager sMessageManager;

    private MessageManager() {
        mContactList = new ArrayList<>();
        mContactsDatabase = Application.getInstance().getDatabase();
    }

    public static MessageManager get() {
        if (sMessageManager == null)
            sMessageManager = new MessageManager();
        return sMessageManager;
    }

    public void uploadMessageList(Contact contact) {
        mContactsDao = mContactsDatabase.mContactsDao();
        mContactsDao.insert(contact);
    }

    public void deleteFromMessageList(Contact contact) {
        mContactsDao = mContactsDatabase.mContactsDao();
        mContactsDao.delete(contact);
    }


    public List<Contact> getContactList() {
        mContactsDao = mContactsDatabase.mContactsDao();
        try {
            mContactList = mContactsDao.getAll();
        } catch (Exception e) {
            Log.d(TAG, "fail");
            e.getStackTrace();
        }
        return mContactList;
    }


    public void updateContact(Contact contact) {
        mContactsDao = mContactsDatabase.mContactsDao();
        mContactsDao.update(contact);
    }


}
