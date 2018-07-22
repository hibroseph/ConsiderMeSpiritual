package joellc.considermespiritual.Events;

import joellc.considermespiritual.SpiritualToken;

public class RemoveFromRecyclerViewEvent {
    SpiritualToken sp;

    public RemoveFromRecyclerViewEvent(SpiritualToken sp) { this.sp = sp; }

    public SpiritualToken getSpiritualToken() {
        return sp;
    }
}
