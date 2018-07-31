package joellc.considermespiritual.Listeners;

import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import joellc.considermespiritual.Database;
import joellc.considermespiritual.Events.RemoveFromRecyclerViewEvent;
import joellc.considermespiritual.Events.ShowNewQuoteEvent;
import joellc.considermespiritual.ShowQuoteFragment;
import joellc.considermespiritual.SpiritualToken;

/**
 * Created by Joseph on 7/25/2018.
 * This class is used when the delete button is clicked. This deletes an entry from the database
 */
public class OnDeleteClickListener implements View.OnClickListener {
    String TAG = getClass().getSimpleName();

    @Override
    public void onClick(View view) {
         Log.d(TAG, "You clicked the delete button");

        // Confirm with the user they want to delete the entry
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());

        alertDialogBuilder
                .setMessage("Are you sure you would like to delete this quote forever?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "We shall delete the quote");

                        if(view.getTag() != null) {

                        // Delete from the database

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // Delete from the database
                                    // Get the spiritual token from the database
                                    SpiritualToken sp = Database.getDatabase(view.getContext())
                                            .spiritualTokenDao().getSpiritualToken((String) view.getTag());

                                    // Let's first check to see if it is a favorite
                                    if(sp.isFavorite()) {
                                        // It is a favorite, we need to remove it from the recycler view
                                        EventBus.getDefault().post(new RemoveFromRecyclerViewEvent(sp));
                                    }

                                    // Remove it from the find quote fragment
                                    EventBus.getDefault().post(new ShowNewQuoteEvent());

                                    // Erase that bad boy
                                    Database.getDatabase(view.getContext()).spiritualTokenDao()
                                            .deleteSpiritualToken(sp);

                                }
                            }).start();

                            Toast.makeText(view.getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                        } else {
                        Log.e(TAG, "view.getTag is equal to null");

                        Toast.makeText(view.getContext(), "Unable to Delete",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "We are going to cancel this dialog table");
                    }
                }).create().show();
    }
}

