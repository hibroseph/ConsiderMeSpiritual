package joellc.considermespiritual.Events;

import joellc.considermespiritual.SpiritualToken;

/**
 * Created by Joseph on 7/21/2018.
 */

public class AddToRecyclerViewEvent {

    SpiritualToken sp;

    public AddToRecyclerViewEvent(SpiritualToken sp) {
        this.sp = sp;
    }

    public SpiritualToken getSpiritualToken() {
        return sp;
    }
}

