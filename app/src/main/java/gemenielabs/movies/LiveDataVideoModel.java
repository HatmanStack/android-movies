package gemenielabs.movies;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import gemenielabs.movies.Database.VideoDetails;

public class LiveDataVideoModel extends ViewModel {

    private MutableLiveData<List<VideoDetails>> mVideoReviews;

    /**
     * Get the LiveData object for video reviews.
     * If it doesn't exist, create a new MutableLiveData instance.
     *
     * @return The LiveData object for video reviews.
     */
    public MutableLiveData<List<VideoDetails>> getVideos() {
        if (mVideoReviews == null) {
            mVideoReviews = new MutableLiveData<>();
        }
        return mVideoReviews;
    }
}
