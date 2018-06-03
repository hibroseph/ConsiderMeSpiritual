package joellc.considermespiritual;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Joseph Ridgley on 2/26/2018
 * Updated by Joseph Ridgley on 6/3/2018
 *
 * This is the activty that is used to display the spiritual token to the user
 */
public class showQuoteActivity extends AppCompatActivity {

    TextView showQuote;
    Button findQuote;

    String Quote;

    private AppDatabase quoteDatabase;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quote);

        // Reference the TextView on the .xml file
        showQuote = findViewById(R.id.textViewShowQuote);
        findQuote = findViewById(R.id.buttonFindQuote);

        // Set an onClickListner to the button to generate a new quote
        findQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                String[] quote = new String[1];
//                String[] author = new String[1];
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
                showQuote.setText(ST[0].getQuote() + " " + ST[0].getAuthor());

                // Rinse and repeat
            }
        });

        // Save the quote that was sent over by the findQuoteActivity
        Quote = getIntent().getStringExtra("QUOTE");
        // Change the text of the TextView to the quote
        showQuote.setText(Quote);

        // Put the database in a variable we can use easily
        quoteDatabase = database.getInstance(getApplicationContext());
    }
}
