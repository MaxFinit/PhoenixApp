package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ContactsChema.class},version = 1)
public abstract class ContactsDatabase extends RoomDatabase {

    public abstract ContactsDao mContactsDao();

}
