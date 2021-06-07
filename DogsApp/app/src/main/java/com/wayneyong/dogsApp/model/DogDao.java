package com.wayneyong.dogsApp.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/*Dao always created as interface, need to define all the methods to access to database*/

@Dao
public interface DogDao {

    /* ... zero or more String objects (or a single array of them) may be passed as the argument(s) for that method*/
    @Insert
    List<Long> insertAll(DogBreed... dogs);

    @Query("SELECT * FROM dogbreed")
    List<DogBreed> getAllDogs();

    @Query("SELECT * FROM dogbreed WHERE uuid = :dogId")
    DogBreed getDog(int dogId);

    @Query("DELETE FROM dogbreed")
    void deleteAllDogs();
}
