package com.example.backlogtracker.JSONclasses;

import java.util.ArrayList;
import java.util.List;

public class GameResult {

    // Holds the title of the
    private String title;
    // Holds the name of the platforms that the game released on
    private List<String> platforms;
    // Holds the name of the publishers that published the game
    private List<String> publishers;
    // Holds the developers that worked on the game
    private List<String> developers;
    // Holds the genres of the game
    private List<String> genres;

    /**
     * Constructs a GameResults object with everything set to empty
     */
    public GameResult() {
        this.title = "";
        platforms = new ArrayList<>();
        developers =  new ArrayList<>();
        genres =  new ArrayList<>();
        publishers = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
        this.developers = developers;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }
}
