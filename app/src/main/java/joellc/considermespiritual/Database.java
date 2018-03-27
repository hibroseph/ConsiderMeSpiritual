package joellc.considermespiritual;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.view.ContextMenu;

import static java.sql.Types.NULL;

/**
 * Representing the database  that will store all of the key and values
 */
class Database {
    private static AppDatabase db;
    private static Context context;

    public Database(Context c) {
        getDatabase(c);
    };

    /**
     *  If the database is built, then it returns that database. If the database isn't instanciated
     *  yet, then it will create the database and then return it.
     * @param c
     * @return The instance of the database.
     */
    public static AppDatabase getInstance(Context c) {
        if (db == null) {
            db = Room.databaseBuilder(c, AppDatabase.class, "database-name").build();
        }
        return db;
    }


    public static AppDatabase getDatabase(Context c) {
        if (context == null) {
            context = c;
        }

        if (db == null) {
            db = Room.databaseBuilder(context,
                    AppDatabase.class, "database-name").build();
        }
        return db;
    }

}
