package com.dbvertex.company.h1bq;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDao {
    @Query("SELECT postid FROM postpolllist WHERE postid=:postid")
    String getPostId(String postid);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleMovies (User arrayListist);

    @Update
    void updateMultipleMovies (List<User> arrayListist);


    @Query("SELECT * FROM postpolllist WHERE bucketid=:bucket")
    public List<User> getUsers(String bucket);


    @Query("SELECT * FROM postpolllist WHERE postid=:postid" )
    public List<User> getUsersDetail(String postid);

//
    @Query("DELETE FROM postpolllist  WHERE bucketid=:bucket")
    public void deleteTable(String bucket);
}
