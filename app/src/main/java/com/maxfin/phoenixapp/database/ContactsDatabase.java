package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.maxfin.phoenixapp.database.dao.BlackListDao;
import com.maxfin.phoenixapp.database.dao.CallsDao;
import com.maxfin.phoenixapp.database.dao.DialogsDao;
import com.maxfin.phoenixapp.database.dao.MessagesDao;
import com.maxfin.phoenixapp.models.BlockContact;
import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;

@Database(entities = {Contact.class, Message.class, Call.class, BlockContact.class}, version = 1, exportSchema = false)
public abstract class ContactsDatabase extends RoomDatabase {

    public abstract DialogsDao mContactsDao();

    public abstract MessagesDao mMessagesDao();

    public abstract CallsDao mCallDao();

    public abstract BlackListDao mBlockContactDao();



}
