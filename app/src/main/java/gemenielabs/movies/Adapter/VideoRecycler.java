package gemenielabs.movies.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.util.List;

import gemenielabs.movies.Database.VideoDetails;
import gemenielabs.movies.DetailsActivity;
import gemenielabs.movies.R;

public class VideoRecycler extends RecyclerView.Adapter<VideoRecycler.TrailerReviewVH> {

    private List<VideoDetails> mList;
    private final Context mContext;
    private final VideoRecycler.onListClickListener mOnListClickListener;

    public VideoRecycler(VideoRecycler.onListClickListener listener, Context context) {
        mOnListClickListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public VideoRecycler.TrailerReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_review, parent, false);
        return new VideoRecycler.TrailerReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoRecycler.TrailerReviewVH holder, int position) {
            VideoDetails videoDetails = mList.get(position);
            Log.i("TAG onBinD", String.valueOf(videoDetails.getImageURL()));
            Picasso.get().load(videoDetails.getImageURL()).into(holder.image);
            holder.image.setTag(DetailsActivity.TRAILER);
    }

    public void setVideoDetails(List<VideoDetails> list){
        if(!list.isEmpty()) {
            mList = list;
        }
    }


    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    public interface onListClickListener{
        void onTrailerClicked(int clickedPosition, View v);
    }

    class TrailerReviewVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView image;

        public TrailerReviewVH(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.trailer_image);
            image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("TAG Clicked", "TRAILER");
            mOnListClickListener.onTrailerClicked(getAdapterPosition(), v);
        }
    }
}
