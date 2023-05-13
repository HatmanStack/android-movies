package gemenielabs.movies;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import gemenielabs.movies.Database.ReviewDetails;


public class LiveDataReviewModel extends ViewModel {

    private MutableLiveData<List<ReviewDetails>> mReviews;

    public MutableLiveData<List<ReviewDetails>> getReviews(){
        if(mReviews == null){
            mReviews = new MutableLiveData<>();
        }
        Log.i("TAG MUTABLELIVEDATA", "getReviews");
        return mReviews;
    }
}