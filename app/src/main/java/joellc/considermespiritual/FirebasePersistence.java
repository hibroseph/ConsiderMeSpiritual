package joellc.considermespiritual;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Joseph on 6/28/2018.
 * This class is used to set the persistence of Firebase once and for all in the app.
 */
public class FirebasePersistence extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
