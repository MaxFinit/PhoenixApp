package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.maxfin.phoenixapp.models.Message;

import java.util.List;

@Dao
public interface MessagesDao {

    @Query("SELECT * FROM Message WHERE contact_id = :contactId")
    List<Message> loadHistory(long contactId);


    @Insert
    void insertMessage(Message message);


}
