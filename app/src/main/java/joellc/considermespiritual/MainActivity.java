package joellc.considermespiritual;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {


    OnDataDownloaded download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        String TAG = "MainActivity";

        Log.d(TAG, "OnCreate");

        // Create an instance of the SearchForQuoteFragment
        SearchForQuoteFragment searchForQuoteFragment = new SearchForQuoteFragment();

        // Get the fragment manager
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the fragment
        fragmentTransaction.add(R.id.FrameLayout, new SearchForQuoteFragment(), "frag1")
                .commit();

        Button findQuote = findViewById(R.id.   buttonFragFindQuote);

        Log.d(TAG, "Setting up the RecyclerView");

        RecyclerView rv = findViewById(R.id.recyclerView);

        // This is to make the scrolling smooth
        rv.setNestedScrollingEnabled(false);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

        rv.setLayoutManager(llm);

        ArrayList<SpiritualToken> spiritualTokens = new ArrayList<>();

        RVAdapter adapter = new RVAdapter(spiritualTokens);

        rv.setAdapter(adapter);


        // Let's clear the table so that we know we have a clean playing field
        // DESTROY THE TABLE
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database.getDatabase(getApplicationContext()).spiritualTokenDao().nukeTable();
            }
        }).start();


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
                        // This is where the data should get saved into the database.

                        Log.d(TAG, "The path quotes has: " + dataSnapshot.getChildrenCount() +
                                " children. Let's loop through them all to download them");

                        // Find out how many we need to loop through
                        int childrenCount = (int) dataSnapshot.getChildrenCount();


                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                            Log.d(TAG, "A KEY: " + postSnapshot.getKey());

                            // Get child at specific address
                            SpiritualToken st = dataSnapshot.child(postSnapshot.getKey()).getValue(SpiritualToken.class);

                            st.setFirebaseID(postSnapshot.getKey());
                            Log.d(TAG, "Quote: " + st.getQuote());
                            Log.d(TAG, "Firebase ID: " + st.getFirebaseID());

                            // Check to see if you have a scripture
                            if(st.getAuthor().matches(".*\\d.*")) {
                                Log.d(TAG, "THIS CONTAINS A NUMBER. SCRIPTURE");
                                Log.d(TAG, "See if I am wrong, is it a scripture? " + st.getAuthor());
                                st.setScripture(TRUE);
                            }

                            Thread loadTokensIntoDatabase = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Database.getDatabase(getApplicationContext()).spiritualTokenDao().addSpiritualToken(st);
                                }
                            });

                            loadTokensIntoDatabase.start();
                        }

                        /*
                        // Loop through all the children to download them
                        for (Integer i = 0; i < childrenCount; i++) {

                            Log.d(TAG, "I would be downloading child: " + i.toString());
                            SpiritualToken st = dataSnapshot.child(i.toString()).getValue(SpiritualToken.class);

                            Log.d(TAG, "The quote for child: " + i.toString() + " is: " +
                                    st.getQuote());

                            Log.d(TAG, "Whats the Firebase id? " + st.getFirebaseID());
                            // If it contains a number, it's a scripture (if this doesn't work later on
                            // I can make it a number and a colon :)
                            if(st.getAuthor().matches(".*\\d.*")) {
                                Log.d(TAG, "THIS CONTAINS A NUMBER. SCRIPTURE");
                                Log.d(TAG, "See if I am wrong, is it a scripture? " + st.getAuthor());
                                st.setScripture(TRUE);
                            }

                            // Add them to the database on the phone in a new thread.
                            Thread loadTokensIntoDatabase = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Database.getDatabase(getApplicationContext()).spiritualTokenDao().addSpiritualToken(st);
                                }
                            });

                            loadTokensIntoDatabase.start();

                        }
                        */
                        // By time the code runs here, it should be done.
                        Log.d(TAG, "I think the firebase has finished loading data");

                        // Let's tell the fragment that we are done downloading
                        SearchForQuoteFragment searchForQuoteFragment1 = (SearchForQuoteFragment) getSupportFragmentManager().findFragmentByTag("frag1");
                        searchForQuoteFragment1.onSuccess();

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

        // Load the individual spinners
        loadAuthorSpinner();
        loadTopicSpinner();


    }

    public void loadAuthorSpinner() {
        String TAG = "loadAuthorSpinner";

        // Run a new thread to get all of the authors from the database
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Get a unique list of authors from the database
                List<String> authors = Database.getDatabase(getApplicationContext()).spiritualTokenDao().getUniqueAuthors();

                // Sort the authors to be in alphabetical order
                java.util.Collections.sort(authors);

                // Add an option for all authors in the spinner
                authors.add(0, "All");

                Log.d(TAG, "Updating the spinner. Fingers crossed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Spinner spinnerAuthor = (Spinner) findViewById(R.id.spinnerAuthor);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                                android.R.layout.simple_spinner_item, authors);

                        //adapter.notifyDataSetChanged();

                        spinnerAuthor.setAdapter(adapter);
                    }
                });

            }
        }).start();

    }

    public void loadTopicSpinner() {
        String TAG = "loadTopicSpinner";

        Spinner spinnerTopic = (Spinner) findViewById(R.id.spinnerTopic);

        // Run a new thread to get all of the topics from the database
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Get a unique list of authors from the database
                List<String> topics = Database.getDatabase(getApplicationContext()).spiritualTokenDao().getUniqueTopics();
                // Sort the list
                java.util.Collections.sort(topics);

                // Add an option for all topics in the spinner if it isn't already there
                if (!topics.contains("All")) {
                    Log.d(TAG, "Topics does not contain the world All, adding all");
                    topics.add(0, "All");
                }

                Log.d(TAG, "Does the topic list contain lol hi");

                if (topics.contains("lol hi")) {
                    Log.d(TAG, "IT DOES CONTAIN LOL HI, THIS MEANS WE ARE DOING SUMTHIN RITE");
                } else {
                    Log.d(TAG, "k idk if we are doing this right");
                }

                Log.d(TAG, "Let's update the spinner!");

                // Attach a listener to the spinner to know when the user selects something


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Find the topic spinneR
                        Spinner spinnerTopic = (Spinner) findViewById(R.id.spinnerTopic);

                        // Create an adapter to display the information
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                                android.R.layout.simple_spinner_item, topics);

                        spinnerTopic.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }


}
