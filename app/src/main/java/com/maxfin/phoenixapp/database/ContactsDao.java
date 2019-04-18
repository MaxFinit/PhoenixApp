package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

public interface ContactsDao {

    @Query("SELECT * FROM contactschema")
    List<ContactsChema> getAll();

    @Query("SELECT * FROM contactschema WHERE id = :id")
    ContactsChema getById(long id);

    @Insert
    void insert(ContactsChema employee);

    @Update
    void update(ContactsChema employee);

    @Delete
    void delete(ContactsChema employee);

}
