package joellc.considermespiritual;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Joseph on 7/18/2018.
 */

@Dao
public interface SpiritualTokenTagDao {

    @Query("SELECT Title FROM SpiritualTokenTag GROUP BY Title")
    List<String> getTags();

    @Insert
    void addSpiritualTokenTag(SpiritualTokenTag stt);


}