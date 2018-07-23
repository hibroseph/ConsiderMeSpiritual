package joellc.considermespiritual;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Joseph Ridgley on 2/26/2018.
 * Updated by Joseph Ridgley on 6/6/2018.
 *
 * This 'dao' (Direct Access Object) are the methods that are available to you to access and
 * manipulate the database.
 */
@Dao
public interface SpiritualTokenDao {

    // Get how many rows exist to help debug issue with Firebase
    @Query("SELECT COUNT(*) FROM SpiritualToken")
    int getSize();

    // to update a favorite
    @Query("UPDATE SpiritualToken SET favorite = :favorite WHERE ID = :id")
    void updateFavorite(String id, boolean favorite);

    // To get the favorites
    @Query("SELECT * FROM SpiritualToken WHERE favorite = 1")
    List<SpiritualToken> getFavoriteSpiritualTokens();

    @Query("SELECT DISTINCT author FROM SpiritualToken WHERE author IS NOT NULL AND scripture = 0")
    List<String> getUniqueAuthors();

    @Query("SELECT quote FROM SpiritualToken")
    List<String> getQuotes();

    @Query("SELECT quote FROM SpiritualToken ORDER BY RANDOM() LIMIT 1")
    String getQuote();

    // Selects a spiritual token by random
    @Query("SELECT * FROM SpiritualToken ORDER BY RANDOM() LIMIT 1")
    SpiritualToken getSpiritualToken();

    // Selects a token with a specific author and topic
    @Query("SELECT * FROM SpiritualToken WHERE author = :requestedAuthor LIMIT 1")
    SpiritualToken getSpecificSpiritualToken(String requestedAuthor);

    // Selects a token with a specific author
    @Query("SELECT * From SpiritualToken WHERE author = :requestedAuthor ORDER BY RANDOM() LIMIT 1")
    SpiritualToken getSpiritualTokenWithAuthor(String requestedAuthor);

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
