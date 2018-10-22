package joellc.considermespiritual;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Ridgley on 9/19/18
 *
 * This 'dao' is meant to connect the Tags and the Spiritual Tokens.
 */
@Dao
public interface TagMapDao {



    @Query("SELECT TagId FROM TagMap WHERE SpiritualTokenId = :SpId")
    List<Integer> getTagMap(String SpId);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTagMap(TagMap tm);

}
