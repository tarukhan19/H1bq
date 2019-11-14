package com.dbvertex.company.h1bq;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class},version =2,exportSchema = false)
public abstract class MyAppDatabase extends RoomDatabase {

    public abstract MyDao myDao();
}
