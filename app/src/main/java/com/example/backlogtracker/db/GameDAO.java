package com.example.backlogtracker.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameDAO {

    @Query("select * from Game order by title")
    LiveData<List<Game>> getAll();

    @Query("select * from Game")
    List<Game> getAllList();

    @Query("select * from Game where id = :gameID")
    Game getByID(int gameID);

    @Query("select distinct developer from Game")
    List<String> getDevelopers();

    @Query("select distinct publisher from Game")
    List<String> getPublishers();

    @Query("select distinct platform from Game")
    List<String> getPlatforms();

    @Query("select * from Game order by rating desc")
    List<Game> getAllDescByRank();

    @Query("select * from Game where developer = :name")
    List<Game> getGamesByDeveloper(String name);

    @Query("select * from Game where publisher = :name")
    List<Game> getGamesByPublisher(String name);
    @Query("select * from Game where platform = :name")
    List<Game> getGamesByPlatform(String name);
    @Query("select * from Game where format = :name")
    List<Game> getGamesByFormat(String name);
    @Query("select * from Game where rating = :rating")
    List<Game> getGamesByRating(int rating);

    @Update()
    void update(Game game);

    @Delete
    void delete(Game game);

    @Insert
    void insert(Game... games);
}
