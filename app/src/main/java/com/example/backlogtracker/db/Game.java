package com.example.backlogtracker.db;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Game {
    // Primary key for the game table
    @PrimaryKey(autoGenerate = true)
    private int id;
    // Title of the game
    private String title;
    // Developer of the game
    private String developer;
    // Publisher of the game
    private String publisher;
    // Platform of the game
    private String platform;
    // Genres of the game
    private String genres;
    // The format the user owns
    private String format;
    // Personal rating of the user (1-10)
    private int rating;

    /**
     * Constructor for the Game class
     * @param rating: int
     * @param format: string
     * @param genres: string
     * @param platform: string
     * @param publisher: string
     * @param developer: string
     * @param title: string
     */
    public Game(int rating, String format, String genres, String platform, String publisher, String developer, String title) {
        this.rating = rating;
        this.format = format;
        this.genres = genres;
        this.platform = platform;
        this.publisher = publisher;
        this.developer = developer;
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", developer='" + developer + '\'' +
                ", publisher='" + publisher + '\'' +
                ", platform='" + platform + '\'' +
                ", genres='" + genres + '\'' +
                ", format='" + format + '\'' +
                ", rating=" + rating +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
