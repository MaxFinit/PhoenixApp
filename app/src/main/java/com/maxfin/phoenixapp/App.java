package com.maxfin.phoenixapp;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.maxfin.phoenixapp.database.ContactsDatabase;

public class App extends Application {

    public static App instance;

    private ContactsDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, ContactsDatabase.class, "database")
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public ContactsDatabase getDatabase() {
        return database;
    }


}
