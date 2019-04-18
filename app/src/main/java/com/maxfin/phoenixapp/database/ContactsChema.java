package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;


@Entity
public class ContactsChema {

    @PrimaryKey
    public long id;
    public String jId;
    public String numberId;
    public String number;
    public Uri photo;



}
