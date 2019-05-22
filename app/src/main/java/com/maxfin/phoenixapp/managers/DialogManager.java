package com.maxfin.phoenixapp.managers;


import android.util.Log;


import com.maxfin.phoenixapp.App;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.database.dao.MessagesDao;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

public class DialogManager {
    private static final String TAG = "DialogManager";
    private Message mMessage;
    private List<Message> mMessageList;
    private static DialogManager sDialogManager;
    private MessagesDao mMessagesDao;


    private DialogManager() {
        mMessageList = new ArrayList<>();
        ContactsDatabase contactsDatabase = App.getInstance().getDatabase();
        mMessagesDao = contactsDatabase.mMessagesDao();
    }

    public static DialogManager getDialogManager() {
        if (sDialogManager == null)
            sDialogManager = new DialogManager();
        return sDialogManager;
    }


    public void addMessage(String messagesText, boolean messageType, String id) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        mMessage = new Message(messagesText, messageType, date, id);
        mMessagesDao.insertMessage(mMessage);

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
            Log.d(TAG, "fail");
            e.getStackTrace();
        }
        return mMessageList;
    }

    public Message getLastMessage(String id) {
        try {
            mMessageList = mMessagesDao.loadHistory(id);
        } catch (Exception e) {
            Log.d(TAG, "fail");
            e.getStackTrace();
        }
        if (mMessageList.size() > 0)
            return mMessageList.get(mMessageList.size() - 1);
        return null;
    }

    public Contact getContact(String id) {

        return mMessagesDao.getContact(id);

    }

}