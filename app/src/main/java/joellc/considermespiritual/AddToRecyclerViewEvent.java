package joellc.considermespiritual;

/**
 * Created by Joseph on 7/21/2018.
 */

public class AddToRecyclerViewEvent {

    SpiritualToken sp;

    AddToRecyclerViewEvent(SpiritualToken sp) {
        this.sp = sp;
    }

    public SpiritualToken getSpiritualToken() {
        return sp;
    }
}

