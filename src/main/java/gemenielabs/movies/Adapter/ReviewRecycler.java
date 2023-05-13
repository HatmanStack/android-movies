package gemenielabs.movies.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gemenielabs.movies.Database.ReviewDetails;
import gemenielabs.movies.R;

public class ReviewRecycler extends RecyclerView.Adapter<ReviewRecycler.ReviewVH> {

    private List<ReviewDetails> rList;

    @NonNull
    @Override
    public ReviewRecycler.ReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_review, parent, false);
        return new ReviewRecycler.ReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewRecycler.ReviewVH holder, int position) {
            ReviewDetails reviewDetails = rList.get(position);
            String review = reviewDetails.getContent() + "\n\n" + reviewDetails.getAuthor();
            holder.review.setText(review);
    }

    public void setReviewDetails(List<ReviewDetails> list){
        if(!list.isEmpty()) {
            rList = list;
        }
    }

    @Override
    public int getItemCount() {
        if (rList == null) {
            return 0;
        }
        return rList.size();
    }

    class ReviewVH extends RecyclerView.ViewHolder {

        @BindView(R.id.review_text) TextView review;

        public ReviewVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
