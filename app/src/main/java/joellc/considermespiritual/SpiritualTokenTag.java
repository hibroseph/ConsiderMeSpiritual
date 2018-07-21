package joellc.considermespiritual;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Joseph on 7/18/2018.
 */

@Entity
public class SpiritualTokenTag {

    // Default Constructor
    SpiritualTokenTag() {

    }

    @PrimaryKey
    private String Title;

    @PrimaryKey
    private int ID;

}
