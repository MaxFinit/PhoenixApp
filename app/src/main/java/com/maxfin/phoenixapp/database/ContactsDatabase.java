package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;

@Database(entities = {Contact.class, Message.class, Call.class}, version = 1, exportSchema = false)
public abstract class ContactsDatabase extends RoomDatabase {

    public abstract ContactsDao mContactsDao();

    public abstract MessagesDao mMessagesDao();

    public abstract CallDao mCallDao();

}
