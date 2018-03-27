package joellc.considermespiritual;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Joseph on 2/26/2018.
 */

@Entity
public class SpiritualToken {
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
