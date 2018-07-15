package joellc.considermespiritual;


/**
 * A listener made to help when when data is downloaded. This was made directly for Firebase
 * downloading quotes but this might be made more abstract much later when I see more purpose
 * for this listener
 */
public interface OnDataDownloaded {
    void onSuccess();
    void onFailure();
}

