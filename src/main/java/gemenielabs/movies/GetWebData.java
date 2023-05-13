package gemenielabs.movies;

import static gemenielabs.movies.MainActivity.movieDao;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.Objects;

import gemenielabs.movies.Database.ReviewDetails;
import gemenielabs.movies.Database.VideoDetails;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import gemenielabs.movies.Database.MovieDetails;

public class GetWebData {
    private  static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String IMAGE_BASE = "https://www.googleapis.com/youtube/v3/videos?id=";
    private static final String IMAGE_END = "&part=snippet,contentDetails,statistics,status";
    private final String[] terms = {"popular", "top_rated"};

    public List<MovieDetails> getMovieDetails(String key) {
        if(movieDao.getAll().isEmpty()) {
            for (int i = 0; i < 2; i++) {
                try {
                    String url = MOVIE_DB_BASE + terms[i] + "?api_key=" + key;
                    Log.i("TAG", "URL: " + url);
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    Gson gson = new GsonBuilder()
                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create();
                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                    JSONArray arr = jsonObject.getJSONArray("results");
                    for (int j = 0; j < arr.length(); j++) {
                        //Something weird with gson not parsing the json file correctly
                        //MovieDetails movieDetails = gson.fromJson(arr.get(j).toString(), MovieDetails.class);
                        MovieDetails movieDetails = new MovieDetails(false, false, false, 0, 0, 0, "test", "test", 0, "test", "test'", "test'");
                        movieDetails.setId(arr.getJSONObject(j).getInt("id"));
                        movieDetails.setVoteCount(arr.getJSONObject(j).getInt("vote_count"));
                        movieDetails.setPopularity(arr.getJSONObject(j).getInt("popularity"));
                        movieDetails.setTitle(arr.getJSONObject(j).getString("title"));
                        movieDetails.setOverview(arr.getJSONObject(j).getString("overview"));
                        movieDetails.setVoteAverage(arr.getJSONObject(j).getInt("vote_average"));
                        movieDetails.setReleaseDate(arr.getJSONObject(j).getString("release_date"));
                        movieDetails.setPosterPath(arr.getJSONObject(j).getString("poster_path"));
                        movieDetails.setOriginalLanguage(arr.getJSONObject(j).getString("original_language"));

                        Log.i("TAG JSON", arr.get(j).toString());
                        Log.i("TAG Toprated", "MovieTitle: " + movieDetails.getVoteAverage());
                        if (i == 0) {
                            movieDetails.setPopular(true);
                        } else {

                            movieDetails.setToprated(true);
                        }
                        movieDao.insertAll(movieDetails);
                        MovieDetails testDetails = movieDao.loadMovieID(arr.getJSONObject(j).getInt("id"));
                        Log.i("TAG TESTDETAILS", testDetails.getReleaseDate());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return movieDao.getAll();
    }

    public List<VideoDetails> getVideoDetails(String movieKey, String youtubeKey, int id) {
        OkHttpClient client = new OkHttpClient();
        try {
            String videoUrl = MOVIE_DB_BASE + id + "/" + "videos" + "?api_key=" + movieKey;
            Request request = new Request.Builder()
                    .url(videoUrl)
                    .get()
                    .build();
            Response videoResponse = client.newCall(request).execute();
            /**Gson gson = new GsonBuilder()
             .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
             .create();**/
            JSONObject videojsonObject = new JSONObject(Objects.requireNonNull(videoResponse.body()).string());
            JSONArray arr = videojsonObject.getJSONArray("results");
            for (int j = 0; j < arr.length(); j++) {
                VideoDetails videoDetails = new VideoDetails(0, "", "", "",
                        "", "", "");
                videoDetails.setId(id);
                videoDetails.setIso_639_1(arr.getJSONObject(j).getString("iso_639_1"));
                videoDetails.setIso_3166_1(arr.getJSONObject(j).getString("iso_3166_1"));
                videoDetails.setKey(arr.getJSONObject(j).getString("key"));
                videoDetails.setSite(arr.getJSONObject(j).getString("site"));
                videoDetails.setSize(arr.getJSONObject(j).getString("size"));
                videoDetails.setType(arr.getJSONObject(j).getString("type"));
                Log.i("TAG VIDEOREVIEWDETAILS", "JSON" + arr.get(j).toString());
                if (videoDetails.getType().equals("Trailer")) {
                    String youtubeUrl = IMAGE_BASE + videoDetails.getKey() + "&key=" + youtubeKey + IMAGE_END;
                    Log.i("TAG VIDEOREVIEWDETAILS", "YOUTUBEURL" + youtubeUrl);
                    Request youTubeRequest = new Request.Builder()
                            .url(youtubeUrl)
                            .get()
                            .build();
                    Response youTubeResponse = client.newCall(youTubeRequest).execute();
                    JSONObject obj = new JSONObject(Objects.requireNonNull(youTubeResponse.body()).string());
                    JSONArray imageArr = obj.getJSONArray("items");
                    String image = imageArr.getJSONObject(0).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                    videoDetails.setImageURL(image);
                    movieDao.insertVideoDetails(videoDetails);
                }


            }
        } catch(Exception e){
                e.printStackTrace();
        }
        return movieDao.getVideosDetails(id);
    }

    public List<ReviewDetails> getReviewDetails(String movieKey, int id) {
        Log.i("TAG REVIEWDETAILS", "MOVIEDAO" + movieDao.loadVideo(id));
        OkHttpClient client = new OkHttpClient();
        try{
        String reviewUrl = MOVIE_DB_BASE + id + "/" + "reviews" + "?api_key=" + movieKey;
        Request reviewRequest = new Request.Builder()
                .url(reviewUrl)
                .get()
                .build();
        Response reviewResponse = client.newCall(reviewRequest).execute();
        JSONObject reviewjsonObject = new JSONObject(Objects.requireNonNull(reviewResponse.body()).string());
        JSONArray reviewArr = reviewjsonObject.getJSONArray("results");
        ReviewDetails reviewDetails = new ReviewDetails(0,"","");
        for (int j = 0; j < reviewArr.length(); j++) {
            reviewDetails.setReviewId(id);
            reviewDetails.setAuthor(reviewArr.getJSONObject(j).getString("author"));
            reviewDetails.setContent(reviewArr.getJSONObject(j).getString("content"));
            movieDao.insertReviewDetails(reviewDetails);
            List<ReviewDetails> test = movieDao.getReviewDetails(id);
            Log.i("TAG GETDATA REVIEW", test.get(0).getAuthor());
        }

        } catch(Exception e){
            e.printStackTrace();
        }

    return movieDao.getReviewDetails(id);
    }
}

