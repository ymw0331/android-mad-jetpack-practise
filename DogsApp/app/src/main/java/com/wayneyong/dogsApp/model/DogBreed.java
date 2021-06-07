package com.wayneyong.dogsApp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

//POJO - Plain old java object
//Entity, as object that goes into database
@Entity
public class DogBreed {

    //SerializedName, specify which variable from back_end api correspond to variable here
    //each of these variable will be column name for database


    @ColumnInfo(name = "breed_id")
    @SerializedName("id")
    public String breedId;

    @ColumnInfo(name = "dog_name")
    @SerializedName("name")
    public String dogBreed;

    @ColumnInfo(name = "life_span")
    @SerializedName("life_span")
    public String lifeSpan;

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    public String breedGroup;

    @ColumnInfo(name = "bred_for")
    @SerializedName("bred_for")
    public String bredFor;

    //    @ColumnInfo(name = "temperament")
    //    @SerializedName("temperament")
    public String temperament;

    @ColumnInfo(name = "dog_url")
    @SerializedName("url")
    public String imageUrl;

    //UUID does not correspond to anything on backend, useful for database system locally
    @PrimaryKey(autoGenerate = true)
    public int uuid;

    //adapter convert POJO/model into a layout

    //constructor, need to be public to retrieve data from back end api
    public DogBreed(String breedId, String dogBreed, String lifeSpan, String breedGroup, String bredFor, String temperament, String imageUrl) {
        this.breedId = breedId;
        this.dogBreed = dogBreed;
        this.lifeSpan = lifeSpan;
        this.breedGroup = breedGroup;
        this.bredFor = bredFor;
        this.temperament = temperament;
        this.imageUrl = imageUrl;
    }
}
