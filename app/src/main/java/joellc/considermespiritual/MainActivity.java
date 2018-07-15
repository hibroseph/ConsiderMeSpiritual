package joellc.considermespiritual;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    NestedScrollView nsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        String TAG = "MainActivity";

        Log.d(TAG, "OnCreate");

        // Set the reference to the nested scroll view
        nsv = findViewById(R.id.NestedScrollView);

        // Get the fragment manager
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the fragment
        fragmentTransaction.add(R.id.FrameLayout, new SearchForQuoteFragment(), "frag1")
                .commit();

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


                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            Log.d(TAG, "A KEY: " + postSnapshot.getKey());

                            // Get child at specific address
                            SpiritualToken st = dataSnapshot.child(postSnapshot.getKey()).getValue(SpiritualToken.class);

                            //TODO: Add exception handling for the setFirebaseID
                            st.setFirebaseID(postSnapshot.getKey());
                            Log.d(TAG, "Quote: " + st.getQuote());
                            Log.d(TAG, "Firebase ID: " + st.getFirebaseID());

                            // Check to see if you have a scripture
                            if (st.getAuthor().matches(".*\\d.*")) {
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

        FloatingActionButton fab = findViewById(R.id.floatingActionButtonScrollToTop);

        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "You clicked the FAB");

                // Top?
                nsv.scrollTo(0,0);
            }
        });

        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d(TAG, "You are scrolling");
                Log.d(TAG, "You are at position " + scrollY);
                if (scrollY > 639 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                } else if (scrollY <639 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                }
            }
        });
    }

}
