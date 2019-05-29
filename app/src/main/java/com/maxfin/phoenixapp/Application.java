package com.maxfin.phoenixapp;

import android.arch.persistence.room.Room;

import com.maxfin.phoenixapp.database.ContactsDatabase;

public class Application extends android.app.Application {

    public static Application instance;
    private ContactsDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, ContactsDatabase.class, "database").allowMainThreadQueries()
                .build();
    }

    public static Application getInstance() {
        return instance;
    }

    public ContactsDatabase getDatabase() {
        return database;
    }


}
