package joellc.considermespiritual;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    NestedScrollView nsv;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences prefs;

    private int[] tabIcons = {
            R.drawable.chat_icon_24,
            R.drawable.thumb_up_24,
            R.drawable.add_icon_24
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
        String TAG = "MainActivity";

        // Get the shared preferences and make sure they can edit

        // CURRENT DEBUGGING SECTION
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // Put this just to see where we can start
        SharedPreferences.Editor editor= prefs.edit();
        editor.putString("Last_Downloaded", "-LGOEw6mcW-R4N1wK5us").apply();

        String startAtDownload = prefs.getString("Last_Downloaded", "");

        if (startAtDownload != null) {
            Query DownloadNewQuotesQuery = db.getReference("Quotes").orderByChild("id").startAt(startAtDownload);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownloadNewQuotesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String lastDownloaded = null;
                            for (DataSnapshot snappy : dataSnapshot.getChildren()) {

                                Log.d(TAG, "An Object");
                                SpiritualToken sp = snappy.getValue(SpiritualToken.class);
                                Log.d(TAG, sp.getID());
                                Log.d(TAG, sp.getQuote());
                                lastDownloaded = sp.getID();
                            }

                            Log.d(TAG, "The last downloaded id was: " + lastDownloaded);

                            // See if anything was downloaded
                            if (lastDownloaded != null) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("Last_Downloaded", lastDownloaded).apply();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }).start();
        } else {
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


                            String lastDownloaded = null;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                Log.d(TAG, "A KEY: " + postSnapshot.getKey());

                                // Get child at specific address
                                SpiritualToken st = dataSnapshot.child(postSnapshot.getKey()).getValue(SpiritualToken.class);

                                lastDownloaded = st.getID();
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

                            // Store in the sharedPreferences the last downloaded item
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("Last_Downloaded", lastDownloaded).apply();

                            // Let's tell the fragment that we are done downloading
//                        SearchForQuoteFragment searchForQuoteFragment1 = (SearchForQuoteFragment) getSupportFragmentManager().findFragmentByTag("frag1");
//                        searchForQuoteFragment1.onSuccess();

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


//        FloatingActionButton fab = findViewById(R.id.floatingActionButtonScrollToTop);

//        fab.hide();

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "You clicked the FAB");
//
//                // Top?
//                nsv.scrollTo(0,0);
//            }
//        });
//
//        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Log.d(TAG, "You are scrolling");
//                Log.d(TAG, "You are at position " + scrollY);
//                if (scrollY > 639 && fab.getVisibility() != View.VISIBLE) {
//                    fab.show();
//                } else if (scrollY <639 && fab.getVisibility() == View.VISIBLE) {
//                    fab.hide();
//                }
//            }
//        });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = Database.getDatabase(getApplicationContext()).spiritualTokenDao().getSize();

                Log.d(TAG, "The size of the table is: " + size);
            }
        }).start();

    }

    // Set up tab icons lol like the name
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    // Setups the ViewPager and adds the fragments
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ShowQuoteFragment(), "QUOTE");
        adapter.addFragment(new FavoriteQuotesFragment(), "FIND");
        adapter.addFragment(new AddQuoteFragment(), "ADD");
        viewPager.setAdapter(adapter);
    }

    // Adapter for the view pager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
            //            return mFragmentTitleList.get(position);
        }
    }
}