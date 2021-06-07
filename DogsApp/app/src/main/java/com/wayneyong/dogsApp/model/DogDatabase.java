package com.wayneyong.dogsApp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Room database
@Database(entities = {DogBreed.class}, version = 1)
public abstract class DogDatabase extends RoomDatabase {
    /*
     * In software engineering, the singleton pattern is a software design pattern that restricts the instantiation of a class to one "single" instance.
     * This is useful when exactly one object is needed to coordinate actions across the system. The term comes from the mathematical concept of a singleton.
     * */
    //Database class to be singleton, to avoid it be created from multiple part of code
    private static DogDatabase instance;

    public static DogDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(), //single context may be destroyed at any point oby system, application context is maintained through out the lifecycle
                    DogDatabase.class,
                    "dogdatabase") //check database at "Device FIle Explorer" (data -> data -> package name -> dogdatabase)
                    .build();
        }
        return instance;
    }

    public abstract DogDao dogDao();

}
