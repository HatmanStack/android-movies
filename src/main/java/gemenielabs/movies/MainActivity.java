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


public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        PosterRecycler.vHClickListener{

    public static final String RESULTS = "results";
    public static final String MOVIE_ID = "json_string";
    private static final String POSITION = "position";
    public static final String MOVIE_DB_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    public static final String SAVED_STRING = "saved_string";
    public static final String IMAGE_SIZE = "w185";
    public static final String IS_FAVORITE = "is_favorite";
    private LiveDataMovieModel mLiveDataMovieModel;
    public static MovieDao movieDao;
    SharedPreferences s;
    private PosterRecycler posterRecycler;

    @BindView(R.id.poster_list) RecyclerView posterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);

        s = PreferenceManager.getDefaultSharedPreferences(this);
        s.registerOnSharedPreferenceChangeListener(this);
        MovieDatabase db = Room.databaseBuilder(getApplicationContext(), MovieDatabase.class, "moviedatabase").build();
        movieDao = db.movieDao();
        mLiveDataMovieModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataMovieModel.class);
        mLiveDataMovieModel.getMovies().observe(this, posterObserver);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                GetWebData getWebData = new GetWebData();
                getWebData.getMovieDetails(getString(R.string.moviedb_api_key));
                setPosterList();
            }
        });

        setPosterList();
        createRecycler();
        getWindow().setExitTransition(new Explode());

    }

    public void createRecycler() {

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        posterList.setLayoutManager(staggeredGridLayoutManager);
        posterRecycler = new PosterRecycler(this);
        posterList.setAdapter(posterRecycler);
        posterList.scrollToPosition(s.getInt(POSITION, 0));

    }

    Observer<List<MovieDetails>> posterObserver = new Observer<List<MovieDetails>>() {
        @Override
        public void onChanged(@Nullable List<MovieDetails> movieDetails) {

            posterRecycler.setList(movieDetails);
        }
    };

    public void setPosterList(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

            List<MovieDetails> list = movieDao.getAll();
            list.clear();
            if (s.getBoolean(getString(R.string.popular_key), true)) {

                list.addAll(movieDao.loadPopular());
            }
            if (s.getBoolean(getString(R.string.top_rated_key), true)) {

                list.addAll(movieDao.loadTopRated());
            }
            if (s.getBoolean(getString(R.string.favorites_key), true) && movieDao.loadFavorites().size() > 0) {
                Log.i("TAG FAVORITES", movieDao.loadFavorites().size() + " ");
                list.addAll(movieDao.loadFavorites());
            }
                for(MovieDetails movie: list){
                    Log.i("TAG", "list: " + movie.getTitle());
                }
            final List<MovieDetails> setPosterList = list;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(MovieDetails movie: setPosterList){
                        Log.i("TAG", "setPosterList: " + movie.getTitle());
                    }
                    mLiveDataMovieModel.getMovies().setValue(setPosterList);
                }
            });

            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences s, String key) {
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
