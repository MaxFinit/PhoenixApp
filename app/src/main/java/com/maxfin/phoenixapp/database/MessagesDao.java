package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.maxfin.phoenixapp.models.Contact;
import com.maxfin.phoenixapp.models.Message;

import java.util.List;

@Dao
public interface MessagesDao {

    @Query("SELECT * FROM Message WHERE contact_id = :contactId")
    List<Message> loadHistory(String contactId);

    @Query("DELETE FROM Message WHERE contact_id = :contactId")
    void clearHistory(String contactId);

    @Query("SELECT * FROM Contact WHERE mJId = :contactId")
    Contact getContact(String contactId);

    @Insert
    void insertMessage(Message message);

    @Delete
    void deleteMessage(Message message);

    @Update
    void updateMessage(Message message);


}
