package com.example.backlogtracker;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.backlogtracker.db.AppDatabase;
import com.example.backlogtracker.db.Game;

import java.util.List;

public class AllGameViewModel extends ViewModel {
    // Holds the list of games that can be updated
    private LiveData<List<Game>> gameList;

    /**
     * Returns the gameList and if one doesn't exists it call the database to get the list
     * @param c: context of the App
     * @return: LiveData of a List game from the database
     */
    public LiveData<List<Game>> getGameList(Context c)
    {
        if(gameList != null)
        {
            return gameList;
        }
        return gameList = AppDatabase.getInstance(c).gameDAO().getAll();
    }

}
