package com.maxfin.phoenixapp.managers;

import android.util.Log;

import com.maxfin.phoenixapp.Application;
import com.maxfin.phoenixapp.database.dao.DialogsDao;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static final String TAG = "MessageManager";
    private List<Contact> mContactList;
    private ContactsDatabase mContactsDatabase;
    private DialogsDao mDialogsDao;

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
        mDialogsDao = mContactsDatabase.mContactsDao();
        mDialogsDao.insert(contact);
    }

    public void deleteFromMessageList(Contact contact) {
        mDialogsDao = mContactsDatabase.mContactsDao();
        mDialogsDao.delete(contact);
    }


    public List<Contact> getContactList() {
        mDialogsDao = mContactsDatabase.mContactsDao();
        try {
            mContactList = mDialogsDao.getAll();
        } catch (Exception e) {
            Log.d(TAG, "fail");
            e.getStackTrace();
        }
        return mContactList;
    }


    public void updateContact(Contact contact) {
        mDialogsDao = mContactsDatabase.mContactsDao();
        mDialogsDao.update(contact);
    }


}
