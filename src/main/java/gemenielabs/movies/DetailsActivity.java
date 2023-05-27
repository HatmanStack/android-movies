package gemenielabs.movies;

import static gemenielabs.movies.MainActivity.movieDao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import gemenielabs.movies.Adapter.ReviewRecycler;
import gemenielabs.movies.Adapter.VideoRecycler;
import gemenielabs.movies.Database.MovieDetails;
import gemenielabs.movies.Database.ReviewDetails;
import gemenielabs.movies.Database.VideoDetails;


public class DetailsActivity extends AppCompatActivity implements
        VideoRecycler.onListClickListener{

    public static final String TRAILER = "trailer";
    @BindView(R.id.plot_text) TextView plotTX;
    @BindView(R.id.rating_text) TextView ratingTX;
    @BindView(R.id.date_text) TextView dateTX;
    @BindView(R.id.poster_image) ImageView imageView;
    @BindView(R.id.trailer_list) RecyclerView trailerList;
    @BindView(R.id.review_list) RecyclerView reviewList;
    @BindView(R.id.movie_title) TextView movieTitle;
    private ImageView favoriteButton;
    @BindViews({R.id.plot_text, R.id.rating_text, R.id.date_text})List<TextView> textViews;


    private MovieDetails movieDetails;
    private ReviewRecycler reviewRecycler;
    private VideoRecycler videoRecycler;

    private LiveDataVideoModel mLiveDataVideoModel;
    private LiveDataReviewModel mLiveDataReviewModel;
    private int movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        favoriteButton = findViewById(R.id.favorite_button);
        ButterKnife.bind(this);

        // Get movie ID and set favorite button icon
        movieID = getIntent().getIntExtra(MainActivity.MOVIE_ID, 0);
        if(getIntent().getBooleanExtra(MainActivity.IS_FAVORITE, false)){
            favoriteButton.setImageDrawable(getDrawable(R.drawable.gold));
        } else {
            favoriteButton.setImageDrawable(getDrawable(R.drawable.white));
        }

        // Initialize LiveData models
        mLiveDataVideoModel = new ViewModelProvider(this).get(LiveDataVideoModel.class);
        mLiveDataReviewModel = new ViewModelProvider(this).get(LiveDataReviewModel.class);
        setLiveData();

        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if video and review details are already available in the database
            List<VideoDetails> videoDetails = movieDao.getVideosDetails(movieID);
            List<ReviewDetails> reviewDetails = movieDao.getReviewDetails(movieID);
            if (videoDetails.size() < 1) {
                // Fetch video and review details from web if not available in the database
                GetWebData getWebData = new GetWebData();
                videoDetails = getWebData.getVideoDetails(getString(R.string.moviedb_api_key),
                        getString(R.string.google_youtube_api_key), movieID);
                reviewDetails = getWebData.getReviewDetails(getString(R.string.moviedb_api_key), movieID);
            }

            // Update LiveData models with the fetched details
            final List<VideoDetails> videoValue = videoDetails;
            final List<ReviewDetails> reviewValue = reviewDetails;
            runOnUiThread(() -> {
                mLiveDataVideoModel.getVideos().setValue(videoValue);
                mLiveDataReviewModel.getReviews().setValue(reviewValue);
            });
        });

        // Create recycler views
        reviewRecycler = new ReviewRecycler();
        videoRecycler = new VideoRecycler(this, this);
        createRecycler(reviewList, "not video");
        createRecycler(trailerList, "video");

        // Populate UI with movie details
        populateUI();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Populate UI with movie details
    public void populateUI() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Load movie details from the database
            movieDetails = movieDao.loadMovieID(movieID);
            runOnUiThread(() -> {
                // Update UI elements with movie details
                Picasso.get()
                        .load(MainActivity.MOVIE_DB_IMAGE_BASE + MainActivity.IMAGE_SIZE + movieDetails.getPosterPath())
                        .noFade()
                        .noPlaceholder()
                        .into(imageView);

                plotTX.setText(movieDetails.getOverview());
                ratingTX.setText(movieDetails.getVoteAverage());
                dateTX.setText(movieDetails.getReleaseDate());
                movieTitle.setText(movieDetails.getTitle());
                if (movieDetails.isFavorite()) {
                    favoriteButton.setActivated(true);
                }
            });
        });
    }

    // Create a recycler view with the specified type
    public void createRecycler(RecyclerView recyclerView, String type) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (type.equals("video")) {
            recyclerView.setAdapter(videoRecycler);
        } else {
            recyclerView.setAdapter(reviewRecycler);
        }
    }

    // Add or remove the movie from favorites
    public void addToFavorites(View v) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Load movie details from the database
            MovieDetails movieDetails = movieDao.loadMovieID(movieID);
            boolean favorited = false;
            if (movieDetails.isFavorite()) {
                movieDetails.setFavorite(false);
            } else {
                movieDetails.setFavorite(true);
                favorited = true;
            }
            // Delete and insert movie details to update the favorite status
            movieDao.delete(movieDetails);
            movieDao.insertAll(movieDetails);
            final boolean imageset = favorited;
            runOnUiThread(() -> {
                // Update favorite button icon
                if (imageset) {
                    favoriteButton.setImageDrawable(getDrawable(R.drawable.gold));
                } else {
                    favoriteButton.setImageDrawable(getDrawable(R.drawable.white));
                }
            });
        });
    }

    // Handle option item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Uncomment this if block if it's required to navigate up from the activity
        // if (item.getItemId() == R.id.home) {
        //     NavUtils.navigateUpFromSameTask(this);
        // }
        return super.onOptionsItemSelected(item);
    }

    // Handle trailer clicks
    @Override
    public void onTrailerClicked(final int clickedPosition, View v) {
        if (v.getTag().toString().equals(TRAILER)) {
            final Activity activity = this;
            Executors.newSingleThreadExecutor().execute(() -> {
                // Load video key from the database
                final String key = movieDao.loadVideo(movieID).get(clickedPosition).getKey();
                runOnUiThread(() -> {
                    // Play the video using YouTubeStandalonePlayer
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity,
                            getString(R.string.google_youtube_api_key), key);
                    startActivity(intent);
                });
            });
        }
    }

    // Set up observers for LiveData models
    public void setLiveData() {
        final Observer<List<VideoDetails>> videoObserver = videoReviewDetails -> {
            videoRecycler.setVideoDetails(videoReviewDetails);
            videoRecycler.notifyDataSetChanged();
        };
        final Observer<List<ReviewDetails>> reviewObserver = reviewDetails -> {
            if (!reviewDetails.isEmpty()) {
                Log.i("TAG SETReviewLIVEDATA", reviewDetails.get(0).getAuthor());
            }
            reviewRecycler.setReviewDetails(reviewDetails);
            reviewRecycler.notifyDataSetChanged();
        };

        mLiveDataVideoModel.getVideos().observe(this, videoObserver);
        mLiveDataReviewModel.getReviews().observe(this, reviewObserver);
    }
}