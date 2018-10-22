package joellc.considermespiritual;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Joseph on 7/18/2018.
 */

@Dao
public interface TagsDao {

    @Query("SELECT TagId FROM Tags WHERE Tag = :tagName")
    Integer getTagId(String tagName);

    @Query("SELECT * FROM Tags WHERE TagId = :tagIndices")
    List<Tags> getSpecificTags(List<Integer> tagIndices);

    // This will get all of the tags. This will be used for spinners where all the tags are needed
    @Query("SELECT * FROM Tags GROUP BY Tag")
    List<Tags> getTags();

    // Gives you 5 random tags
    @Query("SELECT * FROM Tags ORDER BY random() LIMIT 5")
    List<Tags> getRandomTags();

    // This will insert a new tag into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTag(Tags tag);
}