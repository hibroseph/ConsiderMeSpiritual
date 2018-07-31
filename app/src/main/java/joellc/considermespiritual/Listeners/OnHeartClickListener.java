package joellc.considermespiritual.Listeners;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import org.greenrobot.eventbus.EventBus;

import joellc.considermespiritual.Database;
import joellc.considermespiritual.Events.AddToRecyclerViewEvent;
import joellc.considermespiritual.Events.RemoveFromRecyclerViewEvent;
import joellc.considermespiritual.SpiritualToken;

import static android.content.ContentValues.TAG;

/**
 * Created by Joseph on 7/25/2018.
 * This class handles what happens when the heart button is clicked. This allows us to reuse this
 * code many times to simplify all of the files using it.
 */
public class OnHeartClickListener implements OnLikeListener {



    @Override
    public void liked(LikeButton likeButton) {

        if (likeButton.getTag() != null) {
            // Update the database that you like it
            Thread updateFavorite = new Thread(new Runnable() {
                @Override

                public void run() {
                    Database.getDatabase(likeButton.getContext())
                            .spiritualTokenDao().updateFavorite((String) likeButton.getTag(), true);

                    Log.d(TAG, "Database updated");

                    likeButton.setLiked(true);
                }
            });

            // Start the thread
            updateFavorite.start();

            // Wait for this thread to finish
            try {
                updateFavorite.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Thread to update the recycler view
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SpiritualToken spiritualToken = Database.getDatabase(likeButton.getContext())
                            .spiritualTokenDao().getSpiritualToken((String) likeButton.getTag());

                    // Add to the recycler view
                    EventBus.getDefault().post(new AddToRecyclerViewEvent(spiritualToken));
                }
            }).start();

            // For user feedback
            Toast.makeText(likeButton.getContext(), "Added To Favorites",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(likeButton.getContext(), "Error: Unable To Add To Favorites",
                    Toast.LENGTH_SHORT).show();

            Log.e(TAG, "likeButton.getTag() == null");
        }
    }

    @Override
    public void unLiked(LikeButton likeButton) {

        Log.d(TAG, "Uh-oh, you unliked this quote");

        if (likeButton.getTag() != null) {
            Log.d(TAG, "Going to update the database that you do not like this quote");

            // Updates the database that you do not like the quote anymore
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Database.getDatabase(likeButton.getContext())
                            .spiritualTokenDao().updateFavorite((String) likeButton.getTag(), false);

                    Log.d(TAG, "Database updated!");
                }
            }).start();

            // Removes the quote from the recycler view
            new Thread(new Runnable() {
                @Override
                public void run() {

                    SpiritualToken spiritualToken = Database.getDatabase(likeButton.getContext())
                            .spiritualTokenDao().getSpiritualToken((String) likeButton.getTag());

                    EventBus.getDefault().post(new RemoveFromRecyclerViewEvent(spiritualToken));
                }
            }).start();

            // For user feedback
            Toast.makeText(likeButton.getContext(), "Removed From Favorites",
                    Toast.LENGTH_SHORT).show();
        } else {
            // This means that likeButton.getTag() was equal to null, this isn't good
            Log.e(TAG, "likebutton.getTAg() == null");

            // For user feedback
            Toast.makeText(likeButton.getContext(), "Unable To Remove From Favorites",
                    Toast.LENGTH_SHORT).show();
        }
    }
 }

