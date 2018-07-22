package joellc.considermespiritual.Events;

import joellc.considermespiritual.SpiritualToken;

/**
 * This POJO is based off of the library EventBus to assist in handling events. This was specifically
 * made for the recyclerview and updating the database when someone unlikes a quote
 */
public class UpdateDatabaseFavoriteQuoteEvent {
    private SpiritualToken sp;

    public UpdateDatabaseFavoriteQuoteEvent(SpiritualToken sp) {
        this.sp = sp;
    }

    public SpiritualToken getSpiritualToken() {
        return sp;
    }
}

