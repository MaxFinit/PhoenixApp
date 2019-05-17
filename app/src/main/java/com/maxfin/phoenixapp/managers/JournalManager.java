package com.maxfin.phoenixapp.managers;

import com.maxfin.phoenixapp.App;
import com.maxfin.phoenixapp.database.CallDao;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

import java.util.List;

public class JournalManager {
    private static JournalManager sJournalManager;
    private CallDao mCallDao;
    private List<Call> mCallsList;

    private JournalManager() {
        ContactsDatabase contactsDatabase = App.getInstance().getDatabase();
        mCallDao = contactsDatabase.mCallDao();
    }

    public static JournalManager getJournalManager() {
        if (sJournalManager == null) {
            sJournalManager = new JournalManager();
        }
        return sJournalManager;
    }

    public List<Call> getCalls() {
        mCallsList = mCallDao.loadHistory();
        return mCallsList;
    }

    public Contact getContact(String id) {
        return mCallDao.getContact(id);
    }

    public void clearJournal() {
        mCallDao.clearHistory();
    }

    public void clearCall(Call call) {
        mCallDao.deleteCall(call);
    }

    public void addCall(Call call) {
        mCallDao.insertCall(call);
    }


}
