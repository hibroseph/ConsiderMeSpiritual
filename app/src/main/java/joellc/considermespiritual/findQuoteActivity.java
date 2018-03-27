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

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static joellc.considermespiritual.Database.getDatabase;

/**
 * Created by Joseph on 2/16/2018.
 * Displays the menu to choose different options to specify your random quote generation so it
 * isn't so random.
 */
public class findQuoteActivity extends AppCompatActivity {

    private AppDatabase quoteDatabase;
    private Database database;
    private Button findQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // For log messages
        String TAG = "onCreate";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_quote);

        // Preload the database with some test data
        loadDataIntoDatabase LDID = new loadDataIntoDatabase(getApplicationContext());

        // Lets get a database that we can work directly with
        quoteDatabase = database.getInstance(getApplicationContext());

        // Create a new thread to populate the database with data
        Thread databaseThread = new Thread(LDID);

        // Start the thread
        Log.d(TAG, "Starting the thread");
        databaseThread.start();

        // While the thread was starting for loading the spinners
        findQuote = findViewById(R.id.buttonFindQuote);

        // Attach a onClickListner to the button to find a quote when it's pressed
        findQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = "startShowQuoteActivity";

                // String to test as the quote
                final String[] quote = new String[500];

                // Load the Data Access Object to something direct that we can use
                // STD = Spiritual Token DAO, not Sexually Transmitted Disease
                final SpiritualTokenDao STD = quoteDatabase.spiritualTokenDao();

                // Create a thread to get the Randomly generated quote.
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        quote[0] = STD.getQuote();
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
                intent.putExtra("QUOTE", quote[0]);

                // Start that bad boy
                startActivity(intent);
                finish();
            }
        });

        // Wait for the thread to finish
        Log.d(TAG, "Waiting for the thread to finish before loading spinners");
        try {
            databaseThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Thread has finished, loading spinners");

        Log.d(TAG, "Number of authors: " + LDID.getAuthors());
        Log.d(TAG, "Number of topics: " + LDID.getTopics());

        // Get the topic spinner
        Resources res = getResources();
        String[] Topics = res.getStringArray(R.array.Topics);

        // Find the topic spinner
        Spinner spinnerTopic = (Spinner) findViewById(R.id.spinnerTopic);

        // Create an adapter to display the information
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, LDID.getTopics());

        spinnerTopic.setAdapter(adapter);

        // Get the author spinner
        String[] Authors = res.getStringArray(R.array.Authors);
        Spinner spinnerAuthor = (Spinner) findViewById(R.id.spinnerAuthor);

        // Create an adapter for the author spinner
        ArrayAdapter<String> authorAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, LDID.getAuthors());

        spinnerAuthor.setAdapter(authorAdapter);
    }

 }



