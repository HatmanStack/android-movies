package gemenielabs.movies;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import gemenielabs.movies.Database.VideoDetails;

public class LiveDataVideoModel extends ViewModel {

        private MutableLiveData<List<VideoDetails>> mVideoReviews;


        public MutableLiveData<List<VideoDetails>> getVideos(){
            if(mVideoReviews == null){
                mVideoReviews = new MutableLiveData<>();
            }
            Log.i("TAG MUTABLELIVEDATA", "getVideos");
            return mVideoReviews;
        }

}
