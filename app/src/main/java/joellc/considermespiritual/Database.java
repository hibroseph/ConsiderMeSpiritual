package joellc.considermespiritual;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.view.ContextMenu;

import static java.sql.Types.NULL;

/**
 * Created by Joseph Ridgley on 2/26/2018
 * Updated by Joseph Ridgley on 6/2/2018
 * Representing the database that will store all of the key and values. Based on the singleton
 * pattern so only one database is instantiated.
 */
class Database {
    private static AppDatabase db;
    private static Context context;

    public Database(Context c) {
        getDatabase(c);
    };

    /**
     *  If the database is built, then it returns that database. If the database isn't instantiated
     *  yet, then it will create the database and then return it.
     * @param c Current context of the application
     * @return The instance of the database.
     */
    public static AppDatabase getInstance(Context c) {
        if (db == null) {
            db = Room.databaseBuilder(c, AppDatabase.class, "database-name").build();
        }
        return db;
    }

    /**
     * If the database is built, it returns that built database. If the database isn't instantiated,
     * then it will create the database and return it.
     * @param c Current context of the application
     * @return AppDatabase (The actual database)
     */
    public static AppDatabase getDatabase(Context c) {
        // See if our database has a context
        if (context == null) {
            context = c;
        }

        // See if the database is equal to null
        if (db == null) {
            // Database is equal to null, ie, doesn't exist, Let's create it!
            db = Room.databaseBuilder(context,
                    AppDatabase.class, "database-name").build();
        }
        // Time to return that database
        return db;
    }

}
