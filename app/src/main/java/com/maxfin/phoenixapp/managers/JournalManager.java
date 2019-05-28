package com.maxfin.phoenixapp.managers;

import com.maxfin.phoenixapp.Application;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.database.dao.CallsDao;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class JournalManager {
    private CallsDao mCallsDao;

    private static JournalManager sJournalManager;

    private JournalManager() {
        ContactsDatabase contactsDatabase = Application.getInstance().getDatabase();
        mCallsDao = contactsDatabase.mCallDao();
    }

    public static JournalManager getJournalManager() {
        if (sJournalManager == null) {
            sJournalManager = new JournalManager();
        }
        return sJournalManager;
    }

    public List<Call> getCalls() {
        List<Call> calls;
        calls = mCallsDao.loadHistory();
        Collections.reverse(calls);
        return calls;
    }

    public Call getContact(String id) {
        return mCallsDao.getCall(id);
    }

    public void clearJournal() {
        mCallsDao.clearHistory();
    }

    public void clearCall(Call call) {
        mCallsDao.deleteCall(call);
    }

    public void addCall(Call call) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        call.setData(date);
        mCallsDao.insertCall(call);
    }

    public void updateCall(Call call){
        mCallsDao.updateCall(call);
    }


}
