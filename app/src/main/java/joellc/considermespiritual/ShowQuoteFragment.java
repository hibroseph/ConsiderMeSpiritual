package joellc.considermespiritual;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Joseph on 7/15/2018.
 */

public class ShowQuoteFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.quote) TextView quoteTextView;
    @BindView(R.id.author) TextView authorTextView;
    @BindView(R.id.buttonFindQuote) Button findQuote;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public ShowQuoteFragment() {
        // Thread to get a random entry from the database.

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_quote, container, false);

        ButterKnife.bind(this, view);


        Thread t = new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "We are going to start to access the database");

                String quote;
                String author;

                SpiritualToken result;

                Log.d(TAG, "Getting a random quote!");

                result = Database.getDatabase(getActivity().getApplicationContext())
                        .spiritualTokenDao().getSpiritualToken();

                Log.d(TAG, "Is the result equal to null? " + result);

                if(result != null) {
                    quote = result.getQuote();
                    author = result.getAuthor();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            quoteTextView.setText(quote);
                            authorTextView.setText(author);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Your result to access the database was equal to null");
                            quoteTextView.setText("Practice doesn't make perfect, Christ makes perfect");
                            authorTextView.setText("Brad Wilcox");
                        }
                    });
                }

            }
        };

        t.start();


        return view;
    }

    @OnClick(R.id.buttonFindQuote)
    public void findQuoteShortClick() {
        Log.d(TAG, "You Clicked the Find New Quote button quickly");
    }

    @OnLongClick(R.id.buttonFindQuote)
    public boolean findQuoteLongClick() {
        Log.d(TAG, "You did a looooong click on Find New Quote button");
        return true;
    }

}
