package joellc.considermespiritual;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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


public class SearchForQuoteFragment extends android.support.v4.app.Fragment implements OnDataDownloaded{

    String TAG = "SearchForQuoteFragment";

    View view;
    RecyclerView rv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstances) {

        Log.d(TAG, "onCreateView: Does this get called everytime to get created");

        if (getActivity() instanceof OnDataDownloaded) {
            Log.d(TAG, "There is an instance of it");
            OnDataDownloaded dataDownloaded = (OnDataDownloaded) getActivity();
        }

        view = inflater.inflate(R.layout.fragment_search_for_quote, container, false);

        Button findQuote = view.findViewById(R.id.buttonFragFindQuote);

        rv = getActivity().findViewById(R.id.recyclerView);

        // When someone clicks the button to find a quote
        findQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onCreateView: Someone clicked the button");

                RVAdapter ra = (RVAdapter) rv.getAdapter();

                if(ra == null)
                    Log.e(TAG, "onClick: RecyclerView adapter should not be equal to null");

                final String selectedAuthor = ((Spinner) getView().findViewById(R.id.spinnerAuthor)).getSelectedItem().toString();
                final String selectedTopic = ((Spinner) getView().findViewById(R.id.spinnerTopic)).getSelectedItem().toString();

                Log.d(TAG, "Your selected author is: " + selectedAuthor);
                Log.d(TAG, "Your selected author is: " + selectedTopic);

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "We are going to start to access the database");

                        SpiritualToken result;

                        if(selectedAuthor.contains("All")) {
                            Log.d(TAG, "The selected Author was all, getting all authors");
                            result = Database.getDatabase(getActivity().getApplicationContext())
                                    .spiritualTokenDao().getSpiritualTokenWithTopic(selectedTopic);
                        } else {
                            Log.d(TAG, "The selected Author was not all, getting author: " + selectedAuthor);
                            result = Database.getDatabase(getActivity().getApplicationContext())
                                    .spiritualTokenDao().getSpecificSpiritualToken(selectedAuthor, selectedTopic);
                        }

                        Log.d(TAG, "This shouldn't display till the result has been returned, let's see");
                        Log.d(TAG, "Result Author: " + result.getAuthor() + " - " + result.getQuote());

                        // Notify that we have gotten the spiritual token

                        ra.insert(result);

                        // Let's notify that the list has changed and to update UI
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ra.notifyDataSetChanged();
                            }
                        });
                    }
                };
                t.start();

//                ra.insert(new SpiritualToken());
            }
        });
        return view;
    }

    public void loadSpinnerz() {
        Log.d(TAG, "Here we would load the spinnerz");
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
        loadTopicSpinner();


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
                        Spinner spinnerAuthor = (Spinner) view.findViewById(R.id.spinnerAuthor);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                                android.R.layout.simple_spinner_item, authors);

                        //adapter.notifyDataSetChanged();

                        spinnerAuthor.setAdapter(adapter);
                    }
                });

            }
        }).start();

    }

    public void loadTopicSpinner() {
        String TAG = "loadTopicSpinner";

        Spinner spinnerTopic = (Spinner) view.findViewById(R.id.spinnerTopic);

        // Run a new thread to get all of the topics from the database
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Get a unique list of authors from the database
                List<String> topics = Database.getDatabase(getActivity().getApplicationContext()).spiritualTokenDao().getUniqueTopics();
                // Sort the list
                java.util.Collections.sort(topics);

                // Add an option for all topics in the spinner if it isn't already there
                if (!topics.contains("All")) {
                    Log.d(TAG, "Topics does not contain the world All, adding all");
                    topics.add(0, "All");
                }

                Log.d(TAG, "Does the topic list contain lol hi");

                if (topics.contains("lol hi")) {
                    Log.d(TAG, "IT DOES CONTAIN LOL HI, THIS MEANS WE ARE DOING SUMTHIN RITE");
                } else {
                    Log.d(TAG, "k idk if we are doing this right");
                }

                Log.d(TAG, "Let's update the spinner!");

                // Attach a listener to the spinner to know when the user selects something


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Find the topic spinneR
                        Spinner spinnerTopic = (Spinner) view.findViewById(R.id.spinnerTopic);

                        // Create an adapter to display the information
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
                                android.R.layout.simple_spinner_item, topics);

                        spinnerTopic.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }


    @Override
    public void onSuccess() {
        loadAuthorSpinner();
        loadTopicSpinner();
    }

    @Override
    public void onFailure() {

    }
}
