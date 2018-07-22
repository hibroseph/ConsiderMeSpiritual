package joellc.considermespiritual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import joellc.considermespiritual.Events.AddToRecyclerViewEvent;

/**
 * Created by Joseph on 7/15/2018.
 */

public class AddQuoteFragment extends Fragment {

    @BindView(R.id.editTextAuthorInput) EditText editTextAuthor;
    @BindView(R.id.editTextQuoteInput) EditText editTextQuote;
    @BindView(R.id.checkBoxFavorite) CheckBox checkBoxFavorite;
    @BindView(R.id.checkboxScripture) CheckBox checkBoxScripture;
    @BindView(R.id.checkBoxUpload) CheckBox checkBoxUpload;

    public AddQuoteFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_quote, container, false);

        ButterKnife.bind(this, view);

        return view;
    }


    @OnClick(R.id.buttonAddQuote)
    public void addQuote() {
        String TAG = "addQuote";

        Log.d(TAG, "Adding quote!");

        // Add to the database
        SpiritualToken sp = new SpiritualToken(
                FireBasePushIdGenerator.generatePushId(),
                editTextAuthor.getText().toString(),
                editTextQuote.getText().toString(),
                checkBoxFavorite.isChecked(),
                checkBoxScripture.isChecked()
        );

        Log.d(TAG, "The ID is: " + sp.getID());

        // Add to the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database.getDatabase(getActivity().getApplicationContext()).spiritualTokenDao()
                        .addSpiritualToken(sp);

            }
        }).start();

        // Toast for user feedback
        Toast.makeText(getActivity(), "Added To Collection", Toast.LENGTH_SHORT).show();

        if(checkBoxFavorite.isChecked()) {
            Log.d(TAG, "If it was favorited, adding to the favorites");

            // Add to the recycler view
            EventBus.getDefault().post(new AddToRecyclerViewEvent(sp));
        }

        if(checkBoxUpload.isChecked()) {
            Log.d(TAG, "Uploading to database");

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("NewQuotes/" + sp.getID());

            reference.setValue(sp).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Successfully uploaded!");
                }
            });
        }

        // Reset the fields
        editTextAuthor.setText("");
        editTextQuote.setText("");
        checkBoxUpload.setChecked(false);
        checkBoxFavorite.setChecked(false);
        checkBoxScripture.setChecked(false);
    }
}
