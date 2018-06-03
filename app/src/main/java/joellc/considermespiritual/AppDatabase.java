package joellc.considermespiritual;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Joseph Ridgley on 2/26/2018.
 * Updated by Joseph Ridgley on 6/2/2018.
 * This is to assist in creating a room database to store all the quotes that will be needed for the
 * app. Created by following the structure on the app development page
 * https://developer.android.com/training/data-storage/room/
 */
@Database(entities = {SpiritualToken.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SpiritualTokenDao spiritualTokenDao();
}
