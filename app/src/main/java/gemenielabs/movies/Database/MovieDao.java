package gemenielabs.movies.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import java.util.List;


@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MovieDetails movieDetails);

    @Query("SELECT * FROM movie_details WHERE id = :id")
    MovieDetails loadMovieID(int id);

    @Query("SELECT * FROM movie_details WHERE favorite = 1")
    List<MovieDetails> loadFavorites();

    @Query("SELECT * FROM movie_details WHERE popular = 1")
    List<MovieDetails> loadPopular();

    @Query("SELECT * FROM movie_details WHERE toprated = 1")
    List<MovieDetails> loadTopRated();

    @Delete
    void delete(MovieDetails movieDetails);

    @Query("SELECT * FROM movie_details")
    List<MovieDetails> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideoDetails(VideoDetails videoDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReviewDetails(ReviewDetails reviewDetails);

    @Query("SELECT * FROM video_details WHERE id = :id")
    List<VideoDetails> getVideosDetails(int id);

    @Query("SELECT * FROM review_details WHERE id = :id")
    List<ReviewDetails> getReviewDetails(int id);

    @Query("SELECT * FROM video_details WHERE type = 'Trailer'  AND id = :id")
    List<VideoDetails> loadVideo(int id);

}
