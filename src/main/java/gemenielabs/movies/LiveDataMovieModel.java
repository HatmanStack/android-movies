package gemenielabs.movies;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import gemenielabs.movies.Database.MovieDetails;

public class LiveDataMovieModel extends ViewModel {

    private MutableLiveData<List<MovieDetails>> mMovies;

    public MutableLiveData<List<MovieDetails>> getMovies() {
        if(mMovies == null){
            mMovies = new MutableLiveData<>();
        }
        return mMovies;
    }

}
