package joellc.considermespiritual;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    NestedScrollView nsv;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.chat_icon_24,
            R.drawable.thumb_up_24,
            R.drawable.add_icon_24
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
        String TAG = "MainActivity";


        // Let's clear the table so that we know we have a clean playing field
        // DESTROY THE TABLE
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Database.getDatabase(getApplicationContext()).spiritualTokenDao().nukeTable();
//            }
//        }).start();


        // Create a thread to download the quotes and scriptures from Firebase.
        // Currently false to stop it from downloading the same data from Firebase
        if (false) {
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