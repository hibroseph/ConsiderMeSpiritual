package joellc.considermespiritual;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Joseph on 7/18/2018.
 */

@Entity
public
class Tags {

    // This will be the Tag that is referenced with a tag
    @NonNull
    private String Tag;

    // This will be the Tag Id
    @PrimaryKey(autoGenerate = true) @NonNull
    private int TagId;

    // Default Constructor
    Tags(String Tag) {
        this.Tag = Tag;
    }

    @NonNull
    public String getTag() {
        return Tag;
    }

    public void setTag(@NonNull String tag) {
        Tag = tag;
    }

    @NonNull
    public int getTagId() {
        return TagId;
    }

    public void setTagId(@NonNull int tagId) {
        TagId = tagId;
    }


}
