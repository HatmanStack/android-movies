package gemenielabs.movies.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "movie_details")
public class MovieDetails {
    @PrimaryKey
    int id;

    @ColumnInfo(name = "favorite")
    public boolean favorite;

    @ColumnInfo(name = "toprated")
    public boolean toprated;

    @ColumnInfo(name = "popular")
    public boolean popular;

    @ColumnInfo(name = "vote_count")
    public int voteCount;

    @ColumnInfo(name = "popularity")
    public float popularity;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "overview")
    public String overview;

    @ColumnInfo(name = "vote_average")
    public int voteAverage;

    @ColumnInfo(name = "release_date")
    public String releaseDate;

    @ColumnInfo(name = "poster_path")
    public String posterPath;

    @ColumnInfo(name = "original_language")
    public String originalLanguage;

    public MovieDetails(boolean favorite, boolean popular, boolean toprated, int id, int voteCount, float popularity, String title, String overview, int voteAverage, String releaseDate, String posterPath, String originalLanguage) {
        this.favorite = favorite;
        this.popular = popular;
        this.toprated = toprated;
        this.id = id;
        this.voteCount = voteCount;
        this.popularity = popularity;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.originalLanguage = originalLanguage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public boolean isToprated() {
        return toprated;
    }

    public void setToprated(boolean toprated) {
        this.toprated = toprated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String plot) {
        this.overview = plot;
    }

    public String getVoteAverage() {
        return String.valueOf(voteAverage);
    }

    public void setVoteAverage(int rating) {
        this.voteAverage = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String date) {
        this.releaseDate = date;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }

}

