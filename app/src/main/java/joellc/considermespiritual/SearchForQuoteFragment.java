package joellc.considermespiritual;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * This fragment is used to show the neccsary elements to search for an element including the
 * spinners, check buttons, and search button
 */
public class SearchForQuoteFragment extends android.support.v4.app.Fragment implements OnDataDownloaded{

    String TAG = "SearchForQuoteFragment";

    View v;
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstances) {

        Log.d(TAG, "onCreateView: Does this get called everytime to get created");

        v = inflater.inflate(R.layout.fragment_search_for_quote, container, false);

        Button findQuote = v.findViewById(R.id.buttonFragFindQuote);

        loadAuthorSpinner();

        // When someone clicks the button to find a quote
        findQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreateView: Someone clicked the button");

                RVAdapter ra = (RVAdapter) rv.getAdapter();

                if(ra == null)
                    Log.e(TAG, "onClick: RecyclerView adapter should not be equal to null");

                final String selectedAuthor = ((Spinner) v.findViewById(R.id.spinnerAuthor)).getSelectedItem().toString();
                final String selectedTopic = ((Spinner) v.findViewById(R.id.spinnerTopic)).getSelectedItem().toString();

                Log.d(TAG, "Your selected author is: " + selectedAuthor);
                Log.d(TAG, "Your selected author is: " + selectedTopic);

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "We are going to start to access the database");

                        SpiritualToken result = null;

                        if(result.getAuthor().equals("All")) {
                            result = Database.getDatabase(getActivity().getApplicationContext())
                                    .spiritualTokenDao().getSpiritualToken();

                        } else {
                            result = Database.getDatabase(getActivity().getApplicationContext())
                                    .spiritualTokenDao().getSpiritualTokenWithAuthor(selectedAuthor);
                        }

                        ra.insert(result);

                        // Let's notify that the list has changed and to update UI
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ra.notifyDataSetChanged();

//                                NestedScrollView nsv = getActivity().findViewById(R.id.NestedScrollView);

                                // This is used to scroll to the bottom of the view
//                                nsv.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        nsv.fullScroll(View.FOCUS_DOWN);
//                                    }
//                                });
                            }
 });
                    }
                };
                t.start();

            }
        });
        return v;
    }

    /**
     * Loads the spinners when called. The idea is that this will get called once all the data from
     * Firebase has been loading.
     */
    public void LoadSpinners() {
        String TAG = "LoadSpinners";

        Thread getAuthorCount = new Thread(new Runnable() {
            @Override
            public void run() {
                int numOfAuthors = Database.getDatabase(getActivity().getApplicationContext()).spiritualTokenDao()
                        .getAuthorCount();

                int numOfQuotes = Database.getDatabase(getActivity().getApplicationContext()).spiritualTokenDao()
                        .getAuthorCount();

                // Print out how many you have of the authors and of the quotes
                Log.d(TAG, "Number of authors: " + numOfAuthors);
                Log.d(TAG, "Number of quotes: " + numOfQuotes);

            }
        });
        getAuthorCount.start();

        // Load the individual spinners
        loadAuthorSpinner();

    }

    public void loadAuthorSpinner() {
        String TAG = "loadAuthorSpinner";

        // Run a new thread to get all of the authors from the database
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Get a unique list of authors from the database
                List<String> authors = Database.getDatabase(getActivity().getApplicationContext()).spiritualTokenDao().getUniqueAuthors();

                // Sort the authors to be in alphabetical order
                java.util.Collections.sort(authors);

                // Add an option for all authors in the spinner
                authors.add(0, "All");

                Log.d(TAG, "Updating the spinner. Fingers crossed");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Spinner spinnerAuthor = (Spinner) getView().findViewById(R.id.spinnerAuthor);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                                android.R.layout.simple_spinner_item, authors);

                        //adapter.notifyDataSetChanged();

                        spinnerAuthor.setAdapter(adapter);
                    }
                });

            }
        }).start();

    }

    @Override
    public void onSuccess() {
        loadAuthorSpinner();
    }

    @Override
    public void onFailure() {

    }
}
