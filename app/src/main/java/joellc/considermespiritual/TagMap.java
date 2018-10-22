package joellc.considermespiritual;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class TagMap {

    @PrimaryKey @NonNull
    String SpiritualTokenId;

    int TagId;

    TagMap(int TagId, String SpiritualTokenId) {
        this.SpiritualTokenId = SpiritualTokenId;
        this.TagId = TagId;
    }

}
