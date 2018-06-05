package joellc.considermespiritual;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static joellc.considermespiritual.Database.getDatabase;

/**
 * Created by Joseph on 2/16/2018.
 * Updated by Joseph Ridgley on 6/3/2018.
 * Displays the menu to choose different options to specify your random quote generation so it
 * isn't so random. This is the starting activity for the app.
 */
public class findQuoteActivity extends AppCompatActivity {

    private AppDatabase quoteDatabase;
    private Database database;
    private Button findQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // For log messages
        String TAG = "onCreate";

        Log.d(TAG, "Let's make sure this message displays, ya know, to see if its working");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_quote);

        // Let's clear the table so that we know we have a clean playing field
        // DESTROY THE TABLE
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database.getDatabase(getApplicationContext()).spiritualTokenDao().nukeTable();
            }
        }).start();


        // This is used to notify us when the data is downloaded per threads are failing me
        final OnDataDownloaded firebaseFinishListener = new OnDataDownloaded() {
            String TAG = "OnDataDownloaded";

            @Override
            public void onSuccess() {
                Log.d(TAG, "All the data has been downloaded from Firebase!");
                LoadSpinners();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "Something went amiss! Uh-Oh! I haven't programmed to print out " +
                        "problems yet so I'm sorry! Start searching for your bug");
            }
        };

        // Create a thread to download the quotes and scriptures from Firebase.
        Thread downloadQuotesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // Get a reference to the Firebase where the quotes are stored.
                DatabaseReference databaseRef = database.getReference("Quotes");

                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This is where the data should be get saved into the database.
                        // I am going to test around with the dataSnapshot to see if works like
                        // I think it does according to the documentation

                        Log.d(TAG, "The path quotes has: " + dataSnapshot.getChildrenCount() +
                                " children. Let's loop through them all to download them");

                        // Find out how many we need to loop through
                        int childrenCount = (int) dataSnapshot.getChildrenCount();

                        // Loop through all the children to download them
                        for (Integer i = 0; i < childrenCount; i++) {
                            Log.d(TAG, "I would be downloading child: " + i.toString());
                            SpiritualToken st = dataSnapshot.child(i.toString()).getValue(SpiritualToken.class);

                            Log.d(TAG, "The quote for child: " + i.toString() + " is: " +
                                    st.getQuote());


                            // Add them to the database on the phone in a new thread.
                            Thread loadTokensIntoDatabase = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Database.getDatabase(getApplicationContext()).spiritualTokenDao().addSpiritualToken(st);
                                }
                            });

                            loadTokensIntoDatabase.start();

                        }

                        // By time the code runs here, it should be done.
                        Log.d(TAG, "I think the firebase has finished loading data");
                        firebaseFinishListener.onSuccess();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // This shouldn't run but if it does, bad things happened that need to be
                        // researched

                        Log.d(TAG, "BAD STUFF HAPPENED " + databaseError.getDetails());
                    }

                });
            }
        });

        // Start the thread
        Log.d(TAG, "Starting the thread");
        downloadQuotesThread.start();


        // While the thread was starting for loading the spinners
        findQuote = findViewById(R.id.buttonFindQuote);

        // Attach a onClickListner to the button to find a quote when it's pressed
        findQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = "startShowQuoteActivity";

                // String to test as the quote
                final SpiritualToken[] ST = new SpiritualToken[1];

                // Create a thread to get the Randomly generated quote.
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        ST[0] = Database.getDatabase(getApplicationContext()).spiritualTokenDao().getSpiritualToken();
                    }
                };

                t.start();

                // Wait for the thread to finish.
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Create the new intent and give it the quote to display
                Intent intent = new Intent(findQuoteActivity.this, showQuoteActivity.class);
                intent.putExtra("QUOTE", ST[0].getQuote());
                intent.putExtra("AUTHOR", ST[0].getAuthor());

                // Start that bad boy
                startActivity(intent);
                finish();
            }
        });

        // Wait for the thread to finish
        Log.d(TAG, "Waiting for the thread to finish before loading spinners");

        Log.d(TAG, "Is the downloadQuotesThread alive? " + downloadQuotesThread.isAlive());

        try {
            downloadQuotesThread.join();
            Log.d(TAG, "Is the downloadQuotesThread alive? " + downloadQuotesThread.isAlive());
            Log.d(TAG, "Downloading the quotes thread has finished!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the spinners when called. The idea is that this will get called once all the data from
     * Firebase has been loading.
     */
    public void LoadSpinners() {
        String TAG = "LoadSpinners";

        Thread getAuthorCount = new Thread(new Runnable() {
            @Override
            public void run() {
                int numOfAuthors = Database.getDatabase(getApplicationContext()).spiritualTokenDao()
                        .getAuthorCount();

                int numOfQuotes = Database.getDatabase(getApplicationContext()).spiritualTokenDao()
                        .getAuthorCount();

                // Print out how many you have of the authors and of the quotes
                Log.d(TAG, "Number of authors: " + numOfAuthors);
                Log.d(TAG, "Number of quotes: " + numOfQuotes);

            }
        });
        getAuthorCount.start();


//        // Find the topic spinner
//        Spinner spinnerTopic = (Spinner) findViewById(R.id.spinnerTopic);
//
//        // Create an adapter to display the information
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, LDID.getTopics());
//        spinnerTopic.setAdapter(adapter);

//        Handler handler = new Handler() {
//            @Override
//            public void updateAuthorSpinner(List<String> authors) {
//                Spinner spinnerAuthor = (Spinner) findViewById(R.id.spinnerAuthor);
//
//                Log.d(TAG, "Updating the spinner LETS SEE IF THIS WORKS");
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
//                        android.R.layout.simple_spinner_item, authors);
//
//                adapter.notifyDataSetChanged();
//
//                spinnerAuthor.setAdapter(adapter);
//            }
//
//        };
//
//        // Run a new thread to get all of the authors from the database
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                List<String> authors = Database.getDatabase(getApplicationContext()).spiritualTokenDao().getAuthors();
//
//                Log.d(TAG, "Sending the authors over to the handler to update the spinner");
//                handler.updateAuthorSpinner(authors);
//            }
//        }).start();
    }

    /**
     * A listener made to help when when data is downloaded. This was made directly for Firebase
     * downloading quotes but this might be made more abstract much later when I see more purpose
     * for this listener
     */
    public interface Handler {
        void updateAuthorSpinner(List<String> authors);
    }

    public interface OnDataDownloaded {
        void onSuccess();
        void onFailure();
    }
}
