package com.maxfin.phoenixapp.managers;

import com.maxfin.phoenixapp.Application;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.database.dao.BlackListDao;
import com.maxfin.phoenixapp.models.BlockContact;
import com.maxfin.phoenixapp.models.Contact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BlackListManagers {

    private static BlackListManagers sBlackListManagers;
    private BlackListDao mBlackListDao;
    private List<BlockContact> mBlockContacts;


    private BlackListManagers() {
        ContactsDatabase contactsDatabase = Application.getInstance().getDatabase();
        mBlackListDao = contactsDatabase.mBlockContactDao();
    }

    public static BlackListManagers getBlackListManagers() {
        if (sBlackListManagers == null) {
            sBlackListManagers = new BlackListManagers();
        }
        return sBlackListManagers;
    }

    public List<BlockContact> getBlackList() {
        return mBlockContacts = mBlackListDao.getAllBlockContacts();
    }

    public boolean addToBlackList(Contact contact) {
        if (!checkForEquals(contact)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm", Locale.getDefault());
            String date = dateFormat.format(Calendar.getInstance().getTime());
            BlockContact blockContact = new BlockContact(contact.getName(), contact.getNumber(), contact.getPhoto(), date);
            mBlackListDao.insertToBlackList(blockContact);
            return true;
        }
        if (mBlockContacts.size() == 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm",Locale.getDefault());
            String date = dateFormat.format(Calendar.getInstance().getTime());
            BlockContact blockContact = new BlockContact(contact.getName(), contact.getNumber(), contact.getPhoto(), date);
            mBlackListDao.insertToBlackList(blockContact);
            return true;
        }

        return false;
    }

    public void deleteFromBlackList(BlockContact blockContact) {
        mBlackListDao.deleteFromBlackList(blockContact);
    }

    private boolean checkForEquals(Contact contact) {
        for (BlockContact item : getBlackList()) {
            if (item.getNumber().equals(contact.getNumber())) {
                return true;
            }
        }
        return false;
    }
}