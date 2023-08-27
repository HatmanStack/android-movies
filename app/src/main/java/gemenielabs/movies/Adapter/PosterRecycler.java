package gemenielabs.movies.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import gemenielabs.movies.Database.MovieDetails;
import gemenielabs.movies.MainActivity;
import gemenielabs.movies.R;

public class PosterRecycler extends RecyclerView.Adapter<PosterRecycler.PosterVH> {

    private List<MovieDetails> mList;
    private final vHClickListener mVHClickListener;

    public PosterRecycler(vHClickListener listener) {
        mVHClickListener = listener;
    }

    @NonNull
    @Override
    public PosterRecycler.PosterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_view, parent, false);
        return new PosterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterRecycler.PosterVH holder, int position) {
        MovieDetails movieDetails = mList.get(position);
        String imageUri = MainActivity.MOVIE_DB_IMAGE_BASE + MainActivity.IMAGE_SIZE + movieDetails.getPosterPath();
        Log.i("TAG onBind", imageUri);
        Picasso.get().load(imageUri).into(holder.posterImage);
    }

    public void setList(List<MovieDetails> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mList == null){
            return 0;
        }
        return mList.size();
    }

    public interface vHClickListener{
        void onPosterClicked(int Id, boolean x, View v);
    }

    class PosterVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView posterImage;

        public PosterVH(View itemView) {
            super(itemView);

            posterImage = itemView.findViewById(R.id.poster_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mVHClickListener.onPosterClicked(mList.get(getAdapterPosition()).getId(),
                    mList.get(getAdapterPosition()).isFavorite(), posterImage);
        }
    }
}



