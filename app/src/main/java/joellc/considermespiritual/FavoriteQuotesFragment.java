package joellc.considermespiritual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Joseph on 7/20/2018.
 * This fragment will be used to display the users favorite quotes.
 */
public class FavoriteQuotesFragment extends Fragment {

    RVAdapter rvAdapter;

    @BindView(R.id.recyclerView_favorite_quotes) RecyclerView recycler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite_quotes, container, false);

        // Bind the views
        ButterKnife.bind(this, view);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<SpiritualToken> list = Database.getDatabase(getActivity().getApplicationContext()).spiritualTokenDao()
                        .getFavoriteSpiritualTokens();

                rvAdapter = new RVAdapter(list);

                // TODO: Fix bug here, crashes every so often
                recycler.setAdapter(rvAdapter);
            }
        }).start();

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateDatabaseFavoriteQuoteEvent event) {
        String TAG = "onEvent";

        Log.d(TAG, "Let's update the database");
        String ID = event.getSpiritualToken().getID();
        boolean favorite = event.getSpiritualToken().isFavorite();
        Log.d(TAG, "Updating entry: " + ID + " with favorite: " + favorite);

        // Update database
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database.getDatabase(getActivity().getApplicationContext()).spiritualTokenDao()
                        .updateFavorite(ID, favorite);
            }
        }).start();

        // Remove from the recycler view
        rvAdapter.remove(event.getSpiritualToken());
        rvAdapter.notifyDataSetChanged();

    }

    // Name describes it all, adds a spiritual token to the likes
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AddToRecyclerViewEvent event) {
        String TAG = "onEvent:Add";

        Log.d(TAG, "Inserting spiritualToken into list");
        rvAdapter.insert(event.getSpiritualToken());

        // Notify that the list has changed
        rvAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RemoveFromRecyclerViewEvent event) {
        String TAG = "onEvent:Remove";

        Log.d(TAG, "Removing spiritualToken from list");

        Log.d(TAG, "The ID of the token we will remove is: " + event.getSpiritualToken().getID());

        rvAdapter.remove(event.getSpiritualToken());

        // Notify that the list has changed
        rvAdapter.notifyDataSetChanged();
    }
}




