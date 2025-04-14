package com.example.backlogtracker;

import android.util.Log;

import com.example.backlogtracker.JSONclasses.GameResult;
import com.example.backlogtracker.JSONclasses.SearchResults;
import com.example.backlogtracker.db.Authorization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class APIAccess {

    /**
     * Gets the list of games from the GiantBomb API based on the passed search
     * @param search: The title the user want to look for
     * @return: List of SearchResults
     */
    public List<SearchResults> fetchResults(String search) {
        List<SearchResults> results = null;
        try {
            // Creates the URL string and sends the request
            URL url = new URL("https://www.giantbomb.com/api/search/?api_key=" + Authorization.AUTH_TOKEN + "&format=json&query=" + search + "&resources=game&field_list=name,guid&limit=100");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Checks return status code
            int status = connection.getResponseCode();
            if(status == 200)
            {
                // Reads in the returning JSON and creates a JSON object
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String returnJson = bufferedReader.readLine();
                JSONObject returnObject = new JSONObject(returnJson);

                // Parse the JSON and returns the results
                results = parseSearchResults(returnObject, search);
            }
            else
            {
                Log.d("APIAccess", "Got status code: " + status);
            }

        } catch (Exception e) {
            Log.d("APIAccess", "Error fetching game list: ");
        }
        return results;
    }

    /**
     * Gets a specific game from the GiantBomb API
     * @param guid: The GiantBomb game id
     * @return: GameResults that have the information about the game
     */
    public GameResult fetchGame(String guid)
    {
        GameResult game = new GameResult();
        try {
            // Creates the URL string and sends the request
            URL url = new URL("https://www.giantbomb.com/api/game/"+guid +"/?api_key=" + Authorization.AUTH_TOKEN + "&format=json&field_list=name,developers,genres,platforms,publishers");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int status = connection.getResponseCode();
            if(status == 200)
            {
                // Reads in the returning JSON and creates a JSON object
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String returnJson = bufferedReader.readLine();
                JSONObject returnObject = new JSONObject(returnJson);

                // Parse the JSON and returns the results
                parseGameResult(returnObject, game);
            }
            else
            {
                Log.d("APIAccess", "Got status code: " + status);
            }

        } catch (Exception e) {
            Log.d("APIAccess", "Error fetching game list: " + e);
        }

        return  game;
    }

    /**
     * Breaks down the jsonObject and stores the information in GameResult object
     * @param jsonObject: JSONObject containing the game information
     * @param game: GameResult that is used to fill out SearchCreateFragment
     * @throws JSONException: Throws error if there's a problem with the JSON object
     */
    private void parseGameResult(JSONObject jsonObject, GameResult game) throws JSONException
    {
        // Breaks the JSON object down into the different fields
        JSONObject results = jsonObject.getJSONObject("results");
        game.setTitle(results.getString("name"));
        JSONArray platforms = results.getJSONArray("platforms");
        JSONArray developers = results.getJSONArray("developers");
        JSONArray genres = results.getJSONArray("genres");
        JSONArray publishers = results.getJSONArray("publishers");

        // Loops through adding a platforms name to the platFormList
        List<String> platformList = new ArrayList<>();
        for (int i = 0; i < platforms.length(); i++)
        {
            platformList.add(platforms.getJSONObject(i).getString("name"));
        }

        // Loops through adding a developer name to the developerList
        List<String> developerList = new ArrayList<>();
        for (int i = 0; i < developers.length(); i++)
        {
            developerList.add(developers.getJSONObject(i).getString("name"));
        }

        // Loops through adding a genre to the genresList
        List<String> genresList = new ArrayList<>();
        for (int i = 0; i < genres.length(); i++)
        {
            genresList.add(genres.getJSONObject(i).getString("name"));
        }

        // Loops through adding a publishers name to the publisherList
        List<String> publisherList = new ArrayList<>();
        for (int i = 0; i < publishers.length(); i++)
        {
            publisherList.add(publishers.getJSONObject(i).getString("name"));
        }

        // Updates the game object with the list
        game.setPlatforms(platformList);
        game.setDevelopers(developerList);
        game.setGenres(genresList);
        game.setPublishers(publisherList);
    }

    /**
     * Takes the json and breaks it down into SearchResults objects.
     * @param json: The JSON object containing the information for the SearchResults
     * @param search: The search term the user used to get the results
     * @return List of SearchResults Objects
     * @throws JSONException: Throws error if there's a problem with the JSON object
     */
    private List<SearchResults> parseSearchResults(JSONObject json, String search) throws JSONException {
        List<SearchResults> returnList = new ArrayList<>();
        JSONArray games = json.getJSONArray("results");
        // Loops through JSONArray getting the name and guid of each game
        for (int i = 0; i < games.length(); i++)
        {
            SearchResults temp = new SearchResults(games.getJSONObject(i).getString("name"),
                    games.getJSONObject(i).getString("guid"), search);
            returnList.add(temp);
        }

        return returnList;
    }

}
