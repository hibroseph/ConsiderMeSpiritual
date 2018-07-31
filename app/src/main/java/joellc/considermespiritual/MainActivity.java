package joellc.considermespiritual;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//TODO: Adding settings to give options like changing color and clearing the database
//TODO: Add a way to delete quotes from your database

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences prefs;

    // For debugging
    private String TAG;

    private FirebaseDatabase firebaseDatabase;


    // The icons for the tabs
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

        firebaseDatabase = FirebaseDatabase.getInstance();

        // Get from the Shared preferences the last downloaded ID and find how many quotes there are to download
        findNumOfQuotesToDownload((prefs.getString("LAST_DOWNLOADED", null)));

        // Displays the size of the table
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = Database.getDatabase(getApplicationContext()).spiritualTokenDao().getSize();

                Log.d(TAG, "The size of the table is: " + size);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    // This assists us in creating the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Log.d(TAG, "You pressed the settings in the menu");
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return true;
    }

    /**
     * Increments the ID that is passed to the function by 1. This is so we only download new items.
     * This is because the ID that is saved in the shared preferences is already in the phone data-
     * base
     * @param Id The string ID that you want to increase by 1
     * @return The incremented ID
     */
    public String incrementPushId(String Id) {

        Log.d(TAG, "The original Id is: " + Id);

        // TODO: Handle incrementing where last letter is z

        // Some preliminary checks
        // If it was null, return null.
        if (Id == null) {
            return null;
        }

        // If there was nothing, don't increase anything.
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

    /**
     * Downloads quotes from the database taking the ID of the last downloaded quote from shared
     * preferences
     */
    private void downloadQuotes() {
        String lastDownloadedQuote = incrementPushId(prefs.getString("LAST_DOWNLOADED", null));

        // TODO: Test to download quotes with .startAt(null);
        Query query = firebaseDatabase.getReference("Quotes").orderByChild("id").startAt(lastDownloadedQuote);

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
                Log.e(TAG, "Oppa, there was an error" + databaseError.getDetails());
            }
        });
    }

    /**
     * Prompts the user to download the quotes
     * @param numQuotesToDownload The number of quotes to download
     */
    private void promptUserToDownloadQuotes(long numQuotesToDownload) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setPositiveButton("Download Quotes", new DialogInterface.OnClickListener() {
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
        }).setMessage("You have " + numQuotesToDownload + " new quotes to download").create().show();
    }

    /**
     * This function returns the number of quotes that exist to download according to the last id
     * stored that was downloaded in the shared preferences
     * @param lastDownloadedString The string of the last downloaded ID
     */
    private void findNumOfQuotesToDownload(String lastDownloadedString) {

        // Increase the ID by 1 so it will count only new items in Firebase
        lastDownloadedString = incrementPushId(lastDownloadedString);

        // There is no previous download history
        if (lastDownloadedString == null) {
            lastDownloadedString = "";
        }

        Log.d(TAG, "lastDownloadedString: " + lastDownloadedString);

        //firebaseDatabase = FirebaseDatabase.getInstance();

        Query quoteReference = firebaseDatabase.getReference("Quotes").orderByChild("id").startAt(lastDownloadedString);

        quoteReference.addValueEventListener(new ValueEventListener() {
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
                Log.e(TAG, "There was a database Error" + databaseError.getDetails());
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

    /**
     * Removes a value from the shared preferences with the specified key
     * @param key
     */
    private void removeFromSharedPreferences(String key) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(key).apply();

        Log.d(TAG, key + " successfully removed from SharedPreferences");
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