package joellc.considermespiritual;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import java.util.List;
/**
 * Created by Joseph on 2/26/2018.
 */

@Dao
public interface SpiritualTokenDao {
    @Query("SELECT topic FROM SpiritualToken")
    List<String> getTopics();

    @Query("SELECT author FROM SpiritualToken")
    List<String> getAuthors();

    @Query("SELECT quote FROM SpiritualToken")
    List<String> getQuotes();

    @Query("SELECT quote FROM SpiritualToken ORDER BY RANDOM() LIMIT 1")
    String getQuote();

    @Query("DELETE FROM SpiritualToken")
    public void nukeTable();

    @Insert
    void addSpiritualToken(SpiritualToken st);

    @Delete
    void deleteSpiritualTokenByAuthor(SpiritualToken st);


}
