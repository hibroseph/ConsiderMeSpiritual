package joellc.considermespiritual;

import android.content.Intent;
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

import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * Created by Joseph on 2/16/2018.
 * Updated by Joseph Ridgley on 6/6/2018.
 * Displays the menu to choose different options to specify your random quote generation so it
 * isn't so random. This is the starting activity for the app.
 */


public class FindQuoteActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        String TAG = "onStart";

        Log.d(TAG, "onStart was called");

        Spinner spinnerAuthor = (Spinner) findViewById(R.id.spinnerAuthor);

        Spinner spinnerTopic = (Spinner) findViewById(R.id.spinnerTopic);

        LoadSpinners();
        // A way to see if the user selects something on the spinners
//        spinnerAuthor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, "An item was selected");
//                // update the topic spinner
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        spinnerTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, "A new topic was selected");
//
//                // update the author spinner accordingly
//                loadTopicSpinner((String)spinnerTopic.getSelectedItem());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        LoadSpinners();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // For log messages
        String TAG = "onCreate";

        Log.d(TAG, "Let's make sure this message displays, ya know, to see if its working");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_quote);

        setSupportActionBar(findViewById(R.id.ActivityFindQuoteToolbar));

        // Make Firebase accessible offline
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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
                        // This is where the data should get saved into the database.

                        Log.d(TAG, "The path quotes has: " + dataSnapshot.getChildrenCount() +
                                " children. Let's loop through them all to download them");

                        // Find out how many we need to loop through
                        int childrenCount = (int) dataSnapshot.getChildrenCount();


                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                            //Log.d(TAG, "A KEY: " + postSnapshot.getKey());

                            // Get child at specific address
                            SpiritualToken st = dataSnapshot.child(postSnapshot.getKey()).getValue(SpiritualToken.class);

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
        Button findQuote = findViewById(R.id.buttonFindQuote);

        // Attach a onClickListner to the button to find a quote when it's pressed
        findQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = "startShowQuoteActivity";

                final String selectedAuthor = ((Spinner) findViewById(R.id.spinnerAuthor)).getSelectedItem().toString();
                final String selectedTopic = ((Spinner) findViewById(R.id.spinnerTopic)).getSelectedItem().toString();

                Log.d(TAG, "Your selected author is: " + selectedAuthor);
                Log.d(TAG, "Your selected author is: " + selectedTopic);

                // When the database has accessed the object that we need it to
                OnDatabaseDone getSpiritualToken = new OnDatabaseDone() {
                    @Override
                    public void onSuccess(Object result) {

                        // Create the new intent and give it the quote to display
                        Intent intent = new Intent(FindQuoteActivity.this, ShowQuoteActivity.class);

                        Log.d(TAG, "Let's hope this thing has an ID: " + ((SpiritualToken)result).getID());

                        // We can assume here that the results will be spiritual tokens.
                        intent.putExtra("QUOTE", ((SpiritualToken)result).getQuote());
                        intent.putExtra("AUTHOR", ((SpiritualToken)result).getAuthor());
                        // The String value of is neccessary because getID returns an int
                        intent.putExtra("ID", String.valueOf(((SpiritualToken)result).getID()));

                        // Start that bad boy
                        startActivity(intent);
                        //finish();
                    }
                };

                // Create a thread to get the Randomly generated quote.
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "We are going to start to access the database");

                        SpiritualToken result;

                        if(selectedAuthor.contains("All")) {
                            Log.d(TAG, "The selected Author was all, getting all authors");
                            result = Database.getDatabase(getApplicationContext())
                                    .spiritualTokenDao().getSpiritualTokenWithTopic(selectedTopic);
                        } else {
                            Log.d(TAG, "The selected Author was not all, getting author: " + selectedAuthor);
                            result = Database.getDatabase(getApplicationContext())
                                    .spiritualTokenDao().getSpecificSpiritualToken(selectedAuthor, selectedTopic);
                        }

                        Log.d(TAG, "This shouldn't display till the result has been returned, let's see");
                        Log.d(TAG, "Result Author: " + result.getAuthor() + " - " + result.getQuote());

                        // Notify that we have gotten the spiritual token
                        getSpiritualToken.onSuccess(result);
                    }
                };
                t.start();
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

    /**
     * A listener made to help when when data is downloaded. This was made directly for Firebase
     * downloading quotes but this might be made more abstract much later when I see more purpose
     * for this listener
     */
    public interface OnDataDownloaded {
        void onSuccess();
        void onFailure();
    }

}
