package joellc.considermespiritual;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TokenViewHolder> {

    List<SpiritualToken> spiritualTokens;

    RVAdapter(List<SpiritualToken> spiritualTokens) {
        this.spiritualTokens = spiritualTokens;
    }

    public static class TokenViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView quote;
        TextView author;

        public TokenViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            quote = itemView.findViewById(R.id.quote);
            author = itemView.findViewById(R.id.author);
        }

    }

    public void insert(SpiritualToken spiritualToken) {
        spiritualTokens.add(spiritualToken);

    }

    @Override
    public TokenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_quote, parent, false);
        TokenViewHolder tvh = new TokenViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(TokenViewHolder holder, int position) {
        holder.author.setText(spiritualTokens.get(position).getAuthor());
        holder.quote.setText(spiritualTokens.get(position).getQuote());
    }

    @Override
    public int getItemCount() {
        return spiritualTokens.size();
    }
}
