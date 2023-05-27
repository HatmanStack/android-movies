package gemenielabs.movies;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import gemenielabs.movies.Database.ReviewDetails;


public class LiveDataReviewModel extends ViewModel {

    private MutableLiveData<List<ReviewDetails>> mReviews;

    /**
     * Get the LiveData object for reviews.
     * If it doesn't exist, create a new MutableLiveData instance.
     *
     * @return The LiveData object for reviews.
     */
    public MutableLiveData<List<ReviewDetails>> getReviews() {
        if (mReviews == null) {
            mReviews = new MutableLiveData<>();
        }
        return mReviews;
    }
}
