package joellc.considermespiritual;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Ridgley on 2/26/2018
 * Updated by Joseph Ridgley on 6/3/2018
 *
 * This is the activity that is used to display the spiritual token to the user
 */
public class ShowQuoteActivity extends AppCompatActivity {

    TextView quoteTextView;
    TextView authorTextView;

    Button buttonFindQuote;
    Button buttonHelpSort;

    // Attributes of the Spiritual Token
    String Quote;
    String Author;
    String ID;

    private AppDatabase quoteDatabase;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quote);

        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);

        final String TAG = "onCreate";

        // Reference the TextView on the .xml file
        quoteTextView = findViewById(R.id.textViewQuote);
        authorTextView = findViewById(R.id.textViewAuthor);

        // Get the information that was sent over the intent (such as the quote and author)
        Quote = getIntent().getStringExtra("QUOTE");
        Author = getIntent().getStringExtra("AUTHOR");
        ID = getIntent().getStringExtra("ID");

        Log.d(TAG, "What does the ID equal here" + ID);

        // Change the text of the TextView's to the information provided
        quoteTextView.setText(Quote);
        authorTextView.setText(Author);

        // Put the database in a variable we can use easily
        quoteDatabase = database.getInstance(getApplicationContext());
    }

    public void generateSuggestTopicDialog() {
        String TAG = "generateDialogBox";

        Log.d(TAG, "You pressed the help button");

        // Let's inflate the custom view that is needed to get the input from the user
        View dialogView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.dialog_submit_topic, null);

        // Here you will generate a dialog box with an option to enter text
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowQuoteActivity.this);



        // Create and add items to the complete text
        // First get a list of all the topics the database contains

        AutoCompleteTextView autoCompleteText =  dialogView.findViewById(R.id.autoCompleteTextView);


        final List <String> topics = new ArrayList<>();
//                topics.add("Love");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(dialogView.getContext(),
                android.R.layout.simple_list_item_1, topics);

        autoCompleteText.setThreshold(1);

        autoCompleteText.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                topics.addAll(Database.getDatabase(getApplicationContext()).spiritualTokenDao()
                        .getUniqueTopics());

                adapter.notifyDataSetChanged();

            }
        }).start();

        builder.setView(dialogView)
                .setTitle("Thanks For Helping!")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // This probs mean the user wants to close the dialog box
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Update the database with what the user inputted

                        String topic = ((EditText) dialogView.findViewById(R.id.autoCompleteTextView))
                                .getText().toString();

                        Log.d(TAG, "The user thinks the topic is: " + topic);
                        Log.d(TAG, "The ID of this quote is: " + ID);

                        // Send to Firebase to be reviewed to see if it is a correct topic
                        DatabaseReference mTopicRef = FirebaseDatabase.getInstance().getReference("PossibleTopic");

                        mTopicRef.child(ID).child("topic").setValue(topic);
                        mTopicRef.child(ID).child("quote").setValue(Quote);

                        // Because we like user feedback
                        Toast.makeText(ShowQuoteActivity.this, "Thanks for helping!", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();


        Log.d(TAG, "Does this show in the dialog scope?");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_show_quote, menu);
        return true;
    }

    public void generateNewQuote() {
        SpiritualToken[] ST = new SpiritualToken[1];

        Thread t = new Thread() {
            @Override
            public void run() {
//                        quote[0] = quoteDatabase.spiritualTokenDao().getQuote();
//                        author[0] = quoteDatabase.spiritualTokenDao().
                ST[0] = quoteDatabase.spiritualTokenDao().getSpiritualToken();
            }
        };
        t.start();

        // Wait till the thread ends
        try {
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the text of textView to the new quote
        quoteTextView.setText(ST[0].getQuote());
        authorTextView.setText(ST[0].getAuthor());
        ID = ST[0].getID();
        Quote = ST[0].getQuote();
        // Rinse and repeat
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshQuote:
                generateNewQuote();
                return true;

            case R.id.submitTopic:
                // Generate the dialog box
                generateSuggestTopicDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
