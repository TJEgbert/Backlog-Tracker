package com.example.backlogtracker.JSONclasses;

public class SearchResults {
    // Holds the name/title of the game
    private String name;
    // Holds the GiantBomb API ID of the game
    private final String guid;
    // The search the user did to get the results
    private final String searchTitle;

    /**
     * The constructor that sets the attributes from the passed in parameters
     * @param name: The name/title of the game
     * @param guid: GiantBombs API ID of the game
     * @param searchTitle: The search term the user used
     */
    public SearchResults(String name, String guid, String searchTitle) {
        this.name = name;
        this.guid = guid;
        this.searchTitle = searchTitle;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public String getSearchTitle() {
        return searchTitle;
    }

}
