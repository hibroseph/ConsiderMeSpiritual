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
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import org.greenrobot.eventbus.EventBus;

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
    @BindView(R.id.like_button) LikeButton likeButton;

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

                showNewQuote(result);

            }
        };

        t.start();


        return view;
    }

    /**
     * Method to replace the quote that is show on the screen
     * @param spiritualToken The token that you would like to display
     */
    public void showNewQuote(SpiritualToken spiritualToken) {

        // Set the liked button to what we have saved
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                likeButton.setLiked(spiritualToken.isFavorite());
            }
        });


        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Log.d(TAG, "You liked this quote");
                Log.d(TAG, "Let's just make sure it was the correct quote");
                Log.d(TAG, "Quote: " + spiritualToken.getQuote());

                // Update the database that you like it
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Database.getDatabase(getActivity().getApplicationContext())
                                .spiritualTokenDao().updateFavorite(spiritualToken.getID(), true);

                        Log.d(TAG, "Database updated");

                        spiritualToken.setFavorite(true);

                        EventBus.getDefault().post(new AddToRecyclerViewEvent(spiritualToken));

                    }
                }).start();

                Toast.makeText(getActivity(), "Added To Favorites",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Log.d(TAG, "Uh-oh, you unliked this quote");

                Log.d(TAG, "Going to update the database that you do not like this quote");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new RemoveFromRecyclerViewEvent(spiritualToken));

                        Database.getDatabase(getActivity().getApplicationContext())
                                .spiritualTokenDao().updateFavorite(spiritualToken.getID(), false);



                        Log.d(TAG, "Database updated!");
                    }
                }).start();

                // Remove it from the recycler view


                Toast.makeText(getActivity(), "Removed From Favorites",
                        Toast.LENGTH_SHORT).show();
            }
        });

        if(spiritualToken != null) {
            String quote = spiritualToken.getQuote();
            String author = spiritualToken.getAuthor();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    quoteTextView.setText(quote);
                    authorTextView.setText(author);
                }
            });
        } else {
            // Here is a default text to display just incase we can't get one from the database
            Log.d(TAG, "Your result to access the database was equal to null");

            // Little error message just to notify you
            Log.e(TAG, "Was unable to get token from database, might want to see if either " +
                    "Firebase is saving the data to the database or if the dao is correct");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    quoteTextView.setText("Practice doesn't make perfect, Christ makes perfect");
                    authorTextView.setText("Brad Wilcox");
                }
            });
        }
    }

    /**
     * When the button on the street is short clicked
     */
    @OnClick(R.id.buttonFindQuote)
    public void findQuoteShortClick() {
        Log.d(TAG, "You Clicked the Find New Quote button quickly");

        // Thread to grab new quote
        new Thread(new Runnable() {
            @Override
            public void run() {
                SpiritualToken sp = Database.getDatabase(getActivity().getApplicationContext())
                        .spiritualTokenDao().getSpiritualToken();

                showNewQuote(sp);
            }
        }).start();
    }

    /**
     * When there is a long click. The idea is to bring up the filtering menu
     * @return I really don't know what this is for, the code made me
     */
    @OnLongClick(R.id.buttonFindQuote)
    public boolean findQuoteLongClick() {
        Log.d(TAG, "You did a looooong click on Find New Quote button");
        return true;
    }

}
