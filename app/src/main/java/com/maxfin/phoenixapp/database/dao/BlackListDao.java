package com.maxfin.phoenixapp.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.maxfin.phoenixapp.models.BlockContact;

import java.util.List;

@Dao
public interface BlackListDao {

    @Query("SELECT * FROM BlockContact")
    List<BlockContact> getAllBlockContacts();

    @Query("SELECT * FROM BlockContact WHERE mId = :contactId")
    BlockContact getContact(String contactId);

    @Query("DELETE FROM BlockContact")
    void clearHistory();

    @Insert
    void insertToBlackList(BlockContact blockContact);

    @Delete
    void deleteFromBlackList(BlockContact blockContact);


}
