package joellc.considermespiritual;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Ridgley on 2/26/2018.
 * Updated by Joseph Ridgley on 7/18/2018.
 * A SpiritualToken represents a piece of spiritual advise that usually comes in the form of a
 * quote from a general authority or a scripture.
 *
 * Represents a table within the room database.
 * This was created by following the instructions on how to create a room database
 * https://developer.android.com/training/data-storage/room/
 */

@Entity
public class SpiritualToken {

    SpiritualToken() {
        scripture = false;
        quote = "NA";
        author = "NA";
        favorite = false;
        tags = new ArrayList<>();
    }

    // This will be the Firebase ID
    @PrimaryKey @NonNull
    private String ID;

    @ColumnInfo
    private String author;

    @ColumnInfo
    private String quote;

    @ColumnInfo
    private Boolean scripture;

    @ColumnInfo
    private boolean favorite;

    // I don't want the table that will be holding the spiritual tokens to hold the list of tags
    // The tags will be held on a different table.
    @Ignore
    private List<String> tags;

    public List<String> getTags() { return tags; }

    @NonNull
    public void addTag(String tag) { tags.add(tag); }

    @NonNull
    public String getID() { return ID; }

    public void setID(@NonNull String id) { ID = id;}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public Boolean getScripture() { return scripture; }

    public void setScripture(Boolean scripture) { this.scripture = scripture; }

    public boolean isFavorite() { return favorite; }

    public void setFavorite(boolean favorite) { this.favorite = favorite; }

}
