package joellc.considermespiritual;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Ridgley on 2/26/2018.
 * Updated by Joseph Ridgley on 6/3/2018.
 *
 * This 'dao' (Direct Access Object) are the methods that are available to you to access and
 * manipulate the database.
 */
@Dao
public interface SpiritualTokenDao {

    // All of these are pretty obvious what they do.
    @Query("SELECT topic FROM SpiritualToken")
    List<String> getTopics();

    @Query("SELECT author FROM SpiritualToken")
    List<String> getAuthors();

    @Query("SELECT quote FROM SpiritualToken")
    List<String> getQuotes();

    @Query("SELECT quote FROM SpiritualToken ORDER BY RANDOM() LIMIT 1")
    String getQuote();

    // Selects a spiritual token by random
    @Query("SELECT * FROM SpiritualToken ORDER BY RANDOM() LIMIT 1")
    SpiritualToken getSpiritualToken();

    // This deletes the whole database. PLEASE BE CAREFUL. THERE AINT NO TURNING BACK FROM THIS.
    @Query("DELETE FROM SpiritualToken")
    public void nukeTable();

    // Get the number of authors
    @Query("SELECT count(author) FROM SpiritualToken")
    public int getAuthorCount();

    // Get the number of quotes
    @Query("SELECT count(quote) FROM SpiritualToken")
    public int getQuoteCount();

    // How a quote or scripture is added to the database
    @Insert
    void addSpiritualToken(SpiritualToken st);

    // You can delete by author
    @Delete
    void deleteSpiritualTokenByAuthor(SpiritualToken st);


}
