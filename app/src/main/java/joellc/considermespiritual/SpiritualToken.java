package joellc.considermespiritual;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Joseph Ridgley on 2/26/2018.
 * Updated by Joseph Ridgley on 6/2/2018.
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
        topic = null;
        talk = false;
        scripture = false;
    }

    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo
    private String topic;

    @ColumnInfo
    private String author;

    @ColumnInfo
    private String quote;

    @ColumnInfo
    private Boolean talk;

    @ColumnInfo(name = "scripture")
    private Boolean scripture;

    public int getID() {
        return ID;
    }

    public void setID(int id) { ID = id;}

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

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

    public Boolean getTalk() { return talk; }

    public void setTalk(Boolean talk) { this.talk = talk; }

    public Boolean getScripture() { return scripture; }

    public void setScripture(Boolean scripture) { this.scripture = scripture; }
}
