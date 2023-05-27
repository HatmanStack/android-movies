package gemenielabs.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import gemenielabs.movies.Adapter.PosterRecycler;
import gemenielabs.movies.Database.MovieDao;
import gemenielabs.movies.Database.MovieDatabase;
import gemenielabs.movies.Database.MovieDetails;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, PosterRecycler.vHClickListener {

    public static final String RESULTS = "results";
    public static final String MOVIE_ID = "json_string";
    private static final String POSITION = "position";
    public static final String MOVIE_DB_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    public static final String SAVED_STRING = "saved_string";
    public static final String IMAGE_SIZE = "w185";
    public static final String IS_FAVORITE = "is_favorite";

    private LiveDataMovieModel mLiveDataMovieModel;
    private SharedPreferences sharedPreferences;
    private PosterRecycler posterRecycler;

    @BindView(R.id.poster_list)
    RecyclerView posterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Get the default SharedPreferences instance
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Initialize the MovieDatabase
        MovieDatabase db = Room.databaseBuilder(getApplicationContext(), MovieDatabase.class, "moviedatabase").build();
        movieDao = db.movieDao();

        // Create and observe the LiveDataMovieModel
        mLiveDataMovieModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataMovieModel.class);
        mLiveDataMovieModel.getMovies().observe(this, posterObserver);

        // Retrieve movie details from the web and set the poster list
        Executors.newSingleThreadExecutor().execute(() -> {
            GetWebData getWebData = new GetWebData();
            getWebData.getMovieDetails(getString(R.string.moviedb_api_key));
            setPosterList();
        });

        createRecycler();
        getWindow().setExitTransition(new Explode());
    }

    // Create the poster RecyclerView
    public void createRecycler() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        posterList.setLayoutManager(staggeredGridLayoutManager);
        posterRecycler = new PosterRecycler(this);
        posterList.setAdapter(posterRecycler);
        posterList.scrollToPosition(sharedPreferences.getInt(POSITION, 0));
    }

    // Observer for the poster LiveData
    Observer<List<MovieDetails>> posterObserver = movieDetails -> {
        posterRecycler.setList(movieDetails);
    };

    // Set the poster list based on shared preferences
    public void setPosterList() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MovieDetails> list = new ArrayList<>();

            if (sharedPreferences.getBoolean(getString(R.string.popular_key), true)) {
                list.addAll(movieDao.loadPopular());
            }
            if (sharedPreferences.getBoolean(getString(R.string.top_rated_key), true)) {
                list.addAll(movieDao.loadTopRated());
            }
            if (sharedPreferences.getBoolean(getString(R.string.favorites_key), true) && movieDao.loadFavorites().size() > 0) {
                Log.i("TAG FAVORITES", movieDao.loadFavorites().size() + " ");
                list.addAll(movieDao.loadFavorites());
            }

            // Log the movies in the list
            for (MovieDetails movie : list) {
                Log.i("TAG", "list: " + movie.getTitle());
            }

            runOnUiThread(() -> {
                // Log the movies in the setPosterList
                for (MovieDetails movie : list) {
                    Log.i("TAG", "setPosterList: " + movie.getTitle());
                }
                mLiveDataMovieModel.getMovies().setValue(list);
            });
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setPosterList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_STRING, RESULTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPosterClicked(int id, boolean x, View v) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(MOVIE_ID, id);
        intent.putExtra(IS_FAVORITE, x);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "poster");
        startActivity(intent, options.toBundle());
    }
}

