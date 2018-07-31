package joellc.considermespiritual;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import joellc.considermespiritual.Events.AddToRecyclerViewEvent;
import joellc.considermespiritual.Events.RemoveFromRecyclerViewEvent;
import joellc.considermespiritual.Events.ShowNewQuoteEvent;
import joellc.considermespiritual.Listeners.OnDeleteClickListener;
import joellc.considermespiritual.Listeners.OnHeartClickListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Joseph on 7/15/2018.
 */
public class ShowQuoteFragment extends android.support.v4.app.Fragment {

    // This is to hold the cardView that will be inflated
    public class DynamicCardView {
        @BindView(R.id.quote) TextView quoteTextView;
        @BindView(R.id.author) TextView authorTextView;
        @BindView(R.id.heart_button) LikeButton likeButton;
        @BindView(R.id.delete_button) ImageView deleteButton;

        public DynamicCardView(View view) {
            ButterKnife.bind(this, view);
        }

        public View getview() {
            return getview();
        }

    }

    @BindView(R.id.buttonFindQuote) Button findQuote;
    @BindView(R.id.viewStubQuoteCard) ViewStub cardView;

    DynamicCardView dynamicCardView;

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

        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_quote, container, false);

        ButterKnife.bind(this, view);

        View stubView = cardView.inflate();


        dynamicCardView = new DynamicCardView(stubView);

        Thread t = new Thread() {
            @Override
            public void run() {
                SpiritualToken sp = getNewQuote();
                showNewQuote(sp);
            }
        };

        t.start();

        return view;
    }

    public SpiritualToken getNewQuote() {

        Log.d(TAG, "We are going to start to access the database");

        String quote;
        String author;

        SpiritualToken result;

        Log.d(TAG, "Getting a random quote!");

        result = Database.getDatabase(getActivity().getApplicationContext())
                .spiritualTokenDao().getSpiritualToken();
         Log.d(TAG, "Is the result equal to null? " + result);

         return result;
    }



    /**
     * Method to replace the quote that is show on the screen
     * @param spiritualToken The token that you would like to display
     */
    public void showNewQuote(SpiritualToken spiritualToken) {

        if(spiritualToken != null) {
            String quote = spiritualToken.getQuote();
            String author = spiritualToken.getAuthor();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    dynamicCardView.quoteTextView.setText(quote);
                    dynamicCardView.authorTextView.setText(author);
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
                    dynamicCardView.quoteTextView.setText("Practice doesn't make perfect, Christ makes perfect");
                    dynamicCardView.authorTextView.setText("Brad Wilcox");
                }
            });
        }

        if (spiritualToken != null) {
            // Set the liked button to what we have saved
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dynamicCardView.likeButton.setLiked(spiritualToken.isFavorite());
                }
            });
        }

        // Set the Tag for the listeners
        dynamicCardView.likeButton.setTag(spiritualToken.getID());
        dynamicCardView.deleteButton.setTag(spiritualToken.getID());

        // Set up the like button
        dynamicCardView.likeButton.setOnLikeListener(new OnHeartClickListener());

        // Set up the delete button
        dynamicCardView.deleteButton.setOnClickListener(new OnDeleteClickListener());

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

    @Subscribe()
    public void onEvent(ShowNewQuoteEvent event) {

        Log.d(TAG, "This is getting called");

        SpiritualToken sp = getNewQuote();
        showNewQuote(sp);
    }
}
