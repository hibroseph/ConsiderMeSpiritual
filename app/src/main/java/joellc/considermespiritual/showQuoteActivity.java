package joellc.considermespiritual;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class showQuoteActivity extends AppCompatActivity {

    TextView showQuote;
    String Quote;

    private AppDatabase quoteDatabase;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quote);

        // Reference the TextView on the .xml file
        showQuote = findViewById(R.id.ShowQuoteTextView);
        // Save the quote that was sent over by the findQuoteActivity
        Quote = getIntent().getStringExtra("QUOTE");
        // Change the text of the TextView to the quote
        showQuote.setText(Quote);

        // Put the database in a variable we can use easily
        quoteDatabase = database.getInstance(getApplicationContext());
    }

   public void onClickGetQuote(View view) {

        String[] quote = new String[1];

        Thread t = new Thread() {
            @Override
            public void run() {
                quote[0] = quoteDatabase.spiritualTokenDao().getQuote();
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
        showQuote.setText(quote[0]);

        // Rinse and repeat
    }
}
