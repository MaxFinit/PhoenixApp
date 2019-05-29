package com.maxfin.phoenixapp.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.maxfin.phoenixapp.models.Call;
import com.maxfin.phoenixapp.models.Contact;

import java.util.List;

@Dao
public interface CallsDao {

    @Query("SELECT * FROM Call")
    List<Call> loadHistory();

    @Query("SELECT * FROM Call WHERE id = :contactId")
    Call getCall(String contactId);


    @Query("DELETE FROM Call")
    void clearHistory();

    @Insert
    void insertCall(Call call);

    @Delete
    void deleteCall(Call call);

    @Update
    void updateCall(Call call);




}
