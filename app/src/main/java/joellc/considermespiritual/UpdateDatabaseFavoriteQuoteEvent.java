package joellc.considermespiritual;

public class UpdateDatabaseFavoriteQuoteEvent {
    private SpiritualToken sp;

    UpdateDatabaseFavoriteQuoteEvent(SpiritualToken sp) {
        this.sp = sp;
    }

    public SpiritualToken getSpiritualToken() {
        return sp;
    }
}
