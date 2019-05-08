package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.maxfin.phoenixapp.models.Contact;

import java.util.List;

@Dao
public interface ContactsDao {

    @Query("SELECT * FROM Contact")
    List<Contact> getAll();

    @Query("SELECT * FROM Contact WHERE mJId = :mJId")
    Contact getById(String mJId);


    @Insert
    void insert(Contact contact);

    @Update
    void update(Contact contact);


    @Delete
    void delete(Contact contact);

}
