package joellc.considermespiritual;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
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

    // How to access the object
    //private SpiritualTokenDao STD = Database.getDatabase(getApplicationContext()).spiritualTokenDao().;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // For log messages
        String TAG = "onCreate";

        Log.d(TAG, "Let's make sure this message displays, ya know, to see if its working");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_quote);



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
                            Log.d(TAG, "I would be downloading child: " +  i.toString());
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // This shouldn't run but if it does, bad things happened that need to be
                        // researched

                        Log.d(TAG, "BAD STUFF HAPPENED " + databaseError.getDetails() );
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
                // Load the Data Access Object to something direct that we can use
                // STD = Spiritual Token DAO, not Sexually Transmitted Disease
//                final SpiritualTokenDao STD = quoteDatabase.spiritualTokenDao();

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
//        Log.d(TAG, "Waiting for the thread to finish before loading spinners");
//        try {
//            //databaseThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "Thread has finished, loading spinners");
//
//        Log.d(TAG, "Number of authors: " + LDID.getAuthors());
//        Log.d(TAG, "Number of topics: " + LDID.getTopics());

        // Get the topic spinner
        Resources res = getResources();
        String[] Topics = res.getStringArray(R.array.Topics);

        // Find the topic spinner
        Spinner spinnerTopic = (Spinner) findViewById(R.id.spinnerTopic);

//        // Create an adapter to display the information
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, LDID.getTopics());

//        spinnerTopic.setAdapter(adapter);

        // Get the author spinner
        String[] Authors = res.getStringArray(R.array.Authors);
        Spinner spinnerAuthor = (Spinner) findViewById(R.id.spinnerAuthor);

        // Create an adapter for the author spinner
//        ArrayAdapter<String> authorAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, LDID.getAuthors());
//
//        spinnerAuthor.setAdapter(authorAdapter);
    }

 }



