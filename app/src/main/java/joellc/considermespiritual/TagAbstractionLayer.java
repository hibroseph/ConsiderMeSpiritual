package joellc.considermespiritual;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static joellc.considermespiritual.Database.getDatabase;

public class TagAbstractionLayer {

    static String TAG = "TagAL";
    /**
     * * Handles all of the specific SQL database queries to add a new tag to the database. This is
     * used to hide specific implementation details from the programmer
     * @param SpiritualTokenId The id of the spiritual token that needs to attached to the tag
     * @param Tag The string of the word that will be used as a tag
     */
    public static void addTag(Context context, String SpiritualTokenId, String Tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "The quoteId inside of TagAL is " +SpiritualTokenId);

                // Check to see if the tag exists
                getDatabase(context).tagsDao().addTag(new Tags(Tag));

                Integer tagId = Database.getDatabase(context).tagsDao().getTagId(Tag);

                getDatabase(context).tagMapDao().addTagMap(new TagMap(tagId, SpiritualTokenId));

                // By this point the tag should have been inserted into the database, let's check
                List<SpiritualToken> sp = getDatabase(context).spiritualTokenDao().getSpiritualTokenWithTags(Arrays.asList(Tag));

                for (SpiritualToken x : sp) {
                    Log.d(TAG, "Quote with tag: " + Tag + " :" + x.getQuote());
                }
            }
        }).start();

    }

    // TODO: Implement a method to see if tags already exist
    /*
    public static Boolean tagExists(Context context, String SpirtualTokenId, String Tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDatabase(context).
            }
        })
    }
    */

}
