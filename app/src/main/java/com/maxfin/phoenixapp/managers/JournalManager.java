package com.maxfin.phoenixapp.managers;

import com.maxfin.phoenixapp.Application;
import com.maxfin.phoenixapp.database.dao.CallsDao;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

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
        return mCallsDao.loadHistory();
    }

    public Contact getContact(String id) {
        return mCallsDao.getContact(id);
    }

    public void clearJournal() {
        mCallsDao.clearHistory();
    }

    public void clearCall(Call call) {
        mCallsDao.deleteCall(call);
    }

    public void addCall(Call call) {
        mCallsDao.insertCall(call);
    }


}
