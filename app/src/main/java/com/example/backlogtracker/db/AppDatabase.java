package com.example.backlogtracker.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Game.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    // Holds the instance of the database
    private static AppDatabase instance;

    /**
     * Returns the instance of the database
     * @param context: Context of the app
     * @return the instance of the database
     */
    public static AppDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context, AppDatabase.class, "user-database")
                    .build();
        }

        return instance;
    }

    /**
     * Access the Game table in the database
     * @return the Game table
     */
    public abstract GameDAO gameDAO();
}
