package com.maxfin.phoenixapp.managers;

import com.maxfin.phoenixapp.Application;
import com.maxfin.phoenixapp.database.ContactsDatabase;
import com.maxfin.phoenixapp.database.dao.BlackListDao;
import com.maxfin.phoenixapp.models.BlockContact;
import com.maxfin.phoenixapp.models.Contact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class BlackListManagers {

    private static BlackListManagers sBlackListManagers;
    private BlackListDao mBlackListDao;


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
        return mBlackListDao.getAllBlockContacts();
    }

    public void addToBlackList(Contact contact) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        BlockContact blockContact = new BlockContact(contact.getName(), contact.getNumber(), contact.getPhoto(), date);
        mBlackListDao.insertToBlackList(blockContact);
    }

    public void deleteFromBlackList(BlockContact blockContact) {
        mBlackListDao.deleteFromBlackList(blockContact);
    }

}
