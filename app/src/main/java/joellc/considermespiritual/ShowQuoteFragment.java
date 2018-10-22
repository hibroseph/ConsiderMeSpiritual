package joellc.considermespiritual;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.robertlevonyan.views.chip.Chip;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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
import static joellc.considermespiritual.Database.getDatabase;

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
        @BindView(R.id.tagViewHorizontalLayout) LinearLayout tagViewLayout;
        public DynamicCardView(View view) {
            ButterKnife.bind(this, view);
        }

        public View getview() {
            return getview();
        }

    }

    @BindView(R.id.buttonFindQuote) Button findQuote;
    @BindView(R.id.viewStubQuoteCard) ViewStub cardView;
    @BindView(R.id.buttonAddTag) Button buttonAddTag;
    @BindView(R.id.autoCompleteTextViewTag) AutoCompleteTextView editTextAddTag;


    DynamicCardView dynamicCardView;
    String quoteId = null;

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

        buttonAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "You pressed the add tag button");

                String tag = editTextAddTag.getText().toString();

                Log.d(TAG, "The tag you want to insert is: " + tag);

                TagAbstractionLayer.addTag(getContext(), quoteId, tag);

                editTextAddTag.setText("");
            }
        });

        View stubView = cardView.inflate();


        dynamicCardView = new DynamicCardView(stubView);

        Thread t = new Thread() {
            @Override
            public void run() {
                SpiritualToken sp = getNewQuote();
                Log.d(TAG, "updating the quoteId");
                quoteId = sp.getID();
                Log.d(TAG, "quoteId:" + quoteId);
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

        result = getDatabase(getActivity().getApplicationContext())
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

            quoteId = spiritualToken.getID();



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

            // Set the Tag for the listeners
            dynamicCardView.likeButton.setTag(spiritualToken.getID());
            dynamicCardView.deleteButton.setTag(spiritualToken.getID());

        }


        // Set up the like button
        dynamicCardView.likeButton.setOnLikeListener(new OnHeartClickListener());

        // Set up the delete button
        dynamicCardView.deleteButton.setOnClickListener(new OnDeleteClickListener());

    }

    // TODO: IMPLEMENT AUTOFILL
    /*
    private ArrayAdapter<String> getTagsAdapter(Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get all the tags from the Database
                List<Tags> tags = Database.getDatabase(getActivity().getApplicationContext()).tagsDao().getTags();

                List <String> stringTags = null;

                // Move all the string tags into
                for (Tags x : tags) {
                    stringTags.add(x.getTag());
                }

            }
        }).start();


    }
    */

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
                SpiritualToken sp = getDatabase(getActivity().getApplicationContext())
                        .spiritualTokenDao().getSpiritualToken();


                showNewQuote(sp);


                // Load the tags from the database
                List<Integer> listTagIndexes = Database.getDatabase(getActivity().getApplicationContext()).tagMapDao()
                        .getTagMap(sp.getID());

                showNewTags(sp);

                // Check to see if we returned any tags
                if (listTagIndexes.isEmpty()) {
                    // zero tags
                    Log.d(TAG, "There are no tags!");
                } else {
                    // WE HAVE TAGS
                    // LETS GET THEM

                    List<Tags> tagz = Database.getDatabase(getActivity().getApplicationContext()).tagsDao().getSpecificTags(listTagIndexes);

                    Log.d(TAG, "You have " + listTagIndexes.size() + " tags!");

                    // Let's loop through them to display them
                    for (Tags x : tagz) {
                        Log.d(TAG, "TAG: " + x.getTag());
                    }

                    // TODO: Add some displaying mechanic for that tags
                }
            }
        }).start();
    }

    private void getTags(SpiritualToken sp) {

        Log.d(TAG, "getting the tags");

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Integer> listOfTagIndices = getDatabase(getContext()).tagMapDao().getTagMap(sp.getID());

                // TODO: Will only show 1 tag, I don't know you
                if (!listOfTagIndices.isEmpty()) {
                    List<Tags> tags = getDatabase(getContext()).tagsDao().getSpecificTags(listOfTagIndices);

                    // Change the views, this has to run on the UI thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "I am so deeeeep");

                            // Run through all the tags
                            for (Tags x : tags) {
                                Log.d(TAG, "We are adding another textview");
                                final TextView texty = new TextView(getContext());

                                texty.setText(x.getTag());

                                dynamicCardView.tagViewLayout.addView(texty);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * This method will show the new tags, just pass it the spiritual token that you want to tags for
     * @param sp The spiritual token that you want the tags for
     */
    private void showNewTags(SpiritualToken sp) {

        // Check to see if views exist inside and delete them if so
        if (dynamicCardView.tagViewLayout.getChildCount() > 0) {
            Log.d(TAG, "There are some views, let's delete them");

            // To remove all the views, we need to remove them from the UI thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dynamicCardView.tagViewLayout.removeAllViews();
                }
            });

        }

        Log.d(TAG, "Calling getTags");
        getTags(sp);

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

        Log.d(TAG, "Calling showNewTags");
        showNewTags(sp);
    }
}
