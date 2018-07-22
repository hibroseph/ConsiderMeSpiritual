package joellc.considermespiritual;

public class RemoveFromRecyclerViewEvent {
    SpiritualToken sp;

    RemoveFromRecyclerViewEvent(SpiritualToken sp) { this.sp = sp; }

    public SpiritualToken getSpiritualToken() {
        return sp;
    }
}
