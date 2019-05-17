package com.maxfin.phoenixapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

import java.util.List;

@Dao
public interface CallDao {

    @Query("SELECT * FROM Call")
    List<Call> loadHistory();

    @Query("SELECT * FROM Contact WHERE mJId = :contactId")
    Contact getContact(String contactId);

    @Query("DELETE FROM Call")
    void clearHistory();

    @Insert
    void insertCall(Call call);

    @Delete
    void deleteCall(Call call);


}
