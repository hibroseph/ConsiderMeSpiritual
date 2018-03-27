package joellc.considermespiritual;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Joseph on 2/26/2018.
 */

@Database(entities = {SpiritualToken.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SpiritualTokenDao spiritualTokenDao();
}
