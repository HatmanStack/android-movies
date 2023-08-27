package gemenielabs.movies.Database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MovieDetails.class, VideoDetails.class, ReviewDetails.class}, exportSchema = false, version = 1)
public abstract class MovieDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();
}
