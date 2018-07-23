package joellc.considermespiritual;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    NestedScrollView nsv;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences prefs;

    // For debugging
    private String TAG;

    private FirebaseDatabase firebaseDatabase;

    private int[] tabIcons = {
            R.drawable.chat_icon_24,
            R.drawable.thumb_up_24,
            R.drawable.add_icon_24
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the TAG for logging
        TAG = this.getLocalClassName();

        // Get the preference Manager.
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

//        Log.d(TAG, "Nuking Table");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Database.getDatabase(getApplicationContext()).spiritualTokenDao().nukeTable();
//            }
//        }).start();

        firebaseDatabase = FirebaseDatabase.getInstance();

        // Get from the Shared preferences the last downloaded ID and find how many quotes there are to download
        findNumOfQuotesToDownload((prefs.getString("LAST_DOWNLOADED", null)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = Database.getDatabase(getApplicationContext()).spiritualTokenDao().getSize();

                Log.d(TAG, "The size of the table is: " + size);
            }
        }).start();
    }

    // Increments a push id that was saved from Firebase by 1
    public String incrementPushIdBy1(String Id) {

        Log.d(TAG, "The original Id is: " + Id);

        // TODO: Handle incrementing where last letter is z

        // Some prelimaray checks
        if (Id == null) {
            return null;
        }

        if (Id.equals("")) {
            return "";
        }

        // Get the position of the last char
        int posOfLastChar = (Id.length() - 1);

        // Get that char
        char temp = Id.charAt(posOfLastChar);

        // Get a charArray to be able to change the last value
        char charArray[] = Id.toCharArray();

        // Increment the last char by 1 and add it to the char array
        charArray[posOfLastChar] = (char)(temp + 1);


        Log.d(TAG, "The new incremented id is: " + new String(charArray));

        // Return a new string
        return new String(charArray);
    }

    private void downloadQuotes() {
        String lastDownloadedQuote = incrementPushIdBy1(prefs.getString("LAST_DOWNLOADED", null));

        Query query = firebaseDatabase.getReference("Quotes").orderByChild("id").startAt(lastDownloadedQuote).limitToFirst(5);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "We are going to download " + dataSnapshot.getChildrenCount() + " children");

                String ID = null;

                for (DataSnapshot snappy : dataSnapshot.getChildren()) {
                    SpiritualToken st = snappy.getValue(SpiritualToken.class);

                    Log.d(TAG, "Id: " + st.getID() );
                    Log.d(TAG, "Quote: " + st.getQuote());
                    Log.d(TAG, "Author: " + st.getAuthor());

                    // Save the ID, in hopes to save the last one so we can store it in shared preferences
                    ID = st.getID();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Database.getDatabase(getApplicationContext()).spiritualTokenDao().addSpiritualToken(st);
                        }
                    }).start();
                }

                addToSharedPreferences("LAST_DOWNLOADED", ID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void promptUserToDownloadQuotes(long quotesToDownload) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setPositiveButton("Download 5", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "You clicked the download button");

                downloadQuotes();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "You pressed cancel");
            }
        }).setMessage("You have " + quotesToDownload + " new quotes to download").create().show();

    }

    // This function returns the number of quotes that exist to download according to the last id
    // stored that was downloaded
    private void findNumOfQuotesToDownload(String lastDownloadedString) {

        lastDownloadedString = incrementPushIdBy1(lastDownloadedString);

        // There is no previous download history
        if (lastDownloadedString == null) {
            lastDownloadedString = "";
        }

        //Query DownloadNewQuotesQuery = firebaseDatabase.getReference("Quotes").orderByChild("id").startAt(lastDownloadedString);

        Log.d(TAG, "lastDownloadedString: " + lastDownloadedString);

        firebaseDatabase = FirebaseDatabase.getInstance();

        Query ref = firebaseDatabase.getReference("Quotes").orderByChild("id").startAt(lastDownloadedString);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "You have " + dataSnapshot.getChildrenCount() + " children to download");

                // There are more than 15 quotes to download, prompt the user to download the new ones
                if (dataSnapshot.getChildrenCount() > 15) {
                    promptUserToDownloadQuotes(dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * Adds the parameters passed to shared preferences. This was made in attempt to size down and
     * simplify the onCreate function
     * @param key The key to be placed in the sharedPreferences
     * @param value The value to be placed at the key in the sharedPreferences
     */
    private void addToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value).apply();

    }

    private void removeFromSharedPreferences(String key) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(key).apply();

        Log.d(TAG, key + " succesfully removed from SharedPreferences");
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