package com.maxfin.phoenixapp.managers;

import android.content.Context;
import android.util.Log;


import com.maxfin.phoenixapp.App;
import com.maxfin.phoenixapp.database.ContactsDao;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.database.MessagesDao;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

public class DialogManager {
    private static final String TAG="DialogManager";
    private Message mMessage;
    private List<Message> mMessageList;
    private static DialogManager sDialogManager;
    private ContactsDatabase mContactsDatabase;
    private ContactsDao mContactsDao;
    private MessagesDao mMessagesDao;



    private DialogManager(Context context) {
        mMessageList = new ArrayList<>();
    }

    public static DialogManager getDialogManager(Context context) {
        if (sDialogManager == null)
            sDialogManager = new DialogManager(context);
        return sDialogManager;
    }


    public void addMessage(String messagesText, boolean messageType,String id) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dMMMHH:mm");

        String date = dateFormat.format(Calendar.getInstance().getTime());

        mMessage = new Message(messagesText, messageType, date,id);


        mContactsDatabase = App.getInstance().getDatabase();
        mMessagesDao =mContactsDatabase.mMessagesDao();

        mMessagesDao.insertMessage(mMessage);


    //    mMessageList.add(mMessage);
    }


    public void deleteMessage(Message message){

        mContactsDatabase = App.getInstance().getDatabase();
        mMessagesDao =mContactsDatabase.mMessagesDao();
        mMessagesDao.deleteMessage(message);


    }


    public void deleteMessageList(String id){

        mContactsDatabase = App.getInstance().getDatabase();
        mMessagesDao =mContactsDatabase.mMessagesDao();
        mMessagesDao.clearHistory(id);


    }

    public List<Message> getMessageList(String id) {
        mContactsDatabase = App.getInstance().getDatabase();
        mMessagesDao =mContactsDatabase.mMessagesDao();

        try {
            mMessageList = mMessagesDao.loadHistory(id);
        } catch (Exception e){
            Log.d(TAG,"fail");
            e.getStackTrace();
        }


        return mMessageList;

    }


    public Contact getContact(String id){
        mContactsDatabase = App.getInstance().getDatabase();
        mMessagesDao =mContactsDatabase.mMessagesDao();

        return mMessagesDao.getContact(id);


    }

}