package joellc.considermespiritual;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Joseph on 2/27/2018.
 * Class that loads data that is stored in the resource files into the database.
 */
public class loadDataIntoDatabase implements Runnable {

    Context context;

    List<String> authors1;
    List<String> topics1;

    String TAG = "loadDataIntoDatabase";

    public List<String> getAuthors() {
        return authors1;
    }

    public List<String> getTopics() {
        return topics1;
    }

    public loadDataIntoDatabase(Context c) {
        context = c;
    }

    private void populateWithTestData(AppDatabase db) {

        Resources res = context.getResources();

        // Make a temporary spiritual token to hold the information that will be put into the database
        SpiritualToken st = new SpiritualToken();

        // Get the string arrays from the resource files
        String[] authors = res.getStringArray(R.array.Authors);
        String[] topics = res.getStringArray(R.array.Topics);
        String[] quotes = res.getStringArray(R.array.Quotes);
        Log.d(TAG, "There are " + authors.length + " in the authors array" );
        Log.d(TAG, "There are " + topics.length + " in the topics array");
        Log.d(TAG, "There are " + quotes.length + " in the quotes array");

        // Create the spiritual tokens and add them to the database
        for (int i = 0; i < authors.length; i++) {
            // Set the SpiritualToken
            st.setAuthor(authors[i]);
            st.setTopic(topics[i]);
            st.setTalk(FALSE);
            st.setScripture(TRUE);
            st.setQuote(quotes[i]);

            Log.d(TAG, st.getQuote());

            // Add the spiritual token to the database (db = database st = SpiritualToken)
            db.spiritualTokenDao().addSpiritualToken(st);
        }
    }

    // Method to add the SpiritualToken to the database
    private static void addSpiritualToken(final AppDatabase db, SpiritualToken st) {
        db.spiritualTokenDao().addSpiritualToken(st);
    }

    // Method to get the SpiritualToken from the database
    private static void getSpiritualTokenAuthors(final AppDatabase db) {
        db.spiritualTokenDao().getAuthors();
    }

    @Override
    public void run() {
        // Create the database
        AppDatabase db = Database.getInstance(context.getApplicationContext());

        // Get rid of everything per I hate it all and it needs to die
        db.spiritualTokenDao().nukeTable();

        // Generate Test Data
        if (db.spiritualTokenDao().getAuthors().isEmpty()) {
            Log.d(TAG, "Populating with new data per getAuthors is empty");
            populateWithTestData(db);
        } else {
            Log.d(TAG, "Authors is not empty");
        }
        authors1 = db.spiritualTokenDao().getAuthors();
        // Load the database topics
        topics1 = db.spiritualTokenDao().getTopics();
    }

}
