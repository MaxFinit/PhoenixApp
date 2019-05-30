package com.maxfin.phoenixapp.managers;


import android.util.Log;


import com.maxfin.phoenixapp.Application;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.database.dao.MessagesDao;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DialogManager {
    private static final String TAG = "DialogManager";

    private List<Message> mMessageList;
    private MessagesDao mMessagesDao;

    private static DialogManager sDialogManager;

    private DialogManager() {
        mMessageList = new ArrayList<>();
        ContactsDatabase contactsDatabase = Application.getInstance().getDatabase();
        mMessagesDao = contactsDatabase.mMessagesDao();
    }

    public static DialogManager getDialogManager() {
        if (sDialogManager == null)
            sDialogManager = new DialogManager();
        return sDialogManager;
    }


    public void addMessage(String messagesText, boolean messageType, String id) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm", Locale.getDefault());
        String date = dateFormat.format(Calendar.getInstance().getTime());
        Message message = new Message(messagesText, messageType, date, id);
        mMessagesDao.insertMessage(message);
    }

    public void deleteMessage(Message message) {
        mMessagesDao.deleteMessage(message);
    }

    public void deleteMessageList(String id) {
        mMessagesDao.clearHistory(id);
    }

    public List<Message> getMessageList(String id) {
        try {
            mMessageList = mMessagesDao.loadHistory(id);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return mMessageList;
    }

    public Message getLastMessage(String id) {
        try {
            mMessageList = mMessagesDao.loadHistory(id);
            if (mMessageList.size() > 0)
                return mMessageList.get(mMessageList.size() - 1);
        } catch (Exception e) {
            Log.d(TAG, "fail");
            e.getStackTrace();
        }
        return null;
    }

    public Contact getContact(String id) {
        return mMessagesDao.getContact(id);
    }

}