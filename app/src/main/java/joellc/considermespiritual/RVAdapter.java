package joellc.considermespiritual;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TokenViewHolder> {

    List<SpiritualToken> spiritualTokens;

    RVAdapter(List<SpiritualToken> spiritualTokens) {
        this.spiritualTokens = spiritualTokens;
    }

    Context parentConext;

    public static class TokenViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView quote;
        TextView author;
        LikeButton heart;

        public TokenViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            quote = itemView.findViewById(R.id.quote);
            author = itemView.findViewById(R.id.author);
            heart = itemView.findViewById(R.id.heart_button);
        }

    }

    public void insert(SpiritualToken spiritualToken) {
        spiritualTokens.add(spiritualToken);

    }

    public void remove(SpiritualToken spiritualToken) {
        String TAG = "remove";

        // We are going to remove by ID per other methods does not work
        for(int i = 0; i < spiritualTokens.size(); i++) {
            if (spiritualTokens.get(i).getID().equals(spiritualToken.getID())) {
                Log.d(TAG, "The ID's equal at index: " + i);

                spiritualTokens.remove(i);

                // Only one should exist so if we hit this point, we can break out of the loop
                break;
            }
        }

    }

    @Override
    public TokenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_quote, parent, false);

        parentConext = parent.getContext();

        TokenViewHolder tvh = new TokenViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(TokenViewHolder holder, int position) {
        String TAG = "onBindViewHolder";

        // Set the elements of the card to the values in the list.
        holder.author.setText(spiritualTokens.get(position).getAuthor());
        holder.quote.setText(spiritualTokens.get(position).getQuote());
        holder.heart.setLiked(spiritualTokens.get(position).isFavorite());

        holder.heart.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Log.d(TAG, "You liked the quote " + spiritualTokens.get(position).getQuote());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Log.d(TAG, "You unliked the quote " + spiritualTokens.get(position).getQuote());

                spiritualTokens.get(position).setFavorite(false);

                // Send the event to the listener
                EventBus.getDefault().post(new UpdateDatabaseFavoriteQuoteEvent(spiritualTokens.get(position)));

//                spiritualTokens.remove(position);
            }

        });
    }

    @Override
    public int getItemCount() {
        return spiritualTokens.size();
    }
}

